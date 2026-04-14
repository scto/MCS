package com.scto.mcs.core.debug

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore

import java.io.File
import java.text.SimpleDateFormat
import java.util.*

import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "mcs_log_config")

data class LogEntry(
    val timestamp: Long,
    val level: String,
    val tag: String,
    val message: String
)

data class LogConfigState(
    val isLogEnabled: Boolean = true,
    val logFilePath: String = "",
    val isLoaded: Boolean = false
)

class LogConfigRepository(private val context: Context) {

    private object PreferencesKeys {
        val LOG_ENABLED = booleanPreferencesKey("log_enabled")
        val LOG_FILE_PATH = stringPreferencesKey("log_file_path")
    }

    /**
      * ✅ Kernkorrektur: Verwendung von `combine` zum Zusammenführen von DataStore- und WorkspaceManager-Streams
      * Dies wird unabhängig von Änderungen an den Protokollierungseinstellungen oder dem Arbeitsverzeichnis neu berechnet.
    */
    val logConfigFlow: Flow<LogConfigState> = context.dataStore.data
        .combine(WorkspaceManager.getWorkspacePathFlow(context)) { preferences, workspacePath ->

            // 1. Dynamisches Arbeitsverzeichnis abrufen
            // workspacePath wird in Echtzeit von Flow übergeben
            // 2. Standard-Protokollverzeichnis erstellen: Arbeitsverzeichnis/Protokolle
            val defaultLogPath = File(workspacePath, "logs").absolutePath

            // 3. Den endgültigen Pfad bestimmen:
            // Wenn der Benutzer in DataStore manuell einen Pfad angegeben hat (savedPath ist nicht leer), wird dieser zuerst verwendet.
            // Wenn kein Pfad manuell angegeben wurde (null oder leer), wird automatisch das aktuelle Verzeichnis verwendet.
            val savedPath = preferences[PreferencesKeys.LOG_FILE_PATH]
            val finalPath = if (savedPath.isNullOrEmpty()) defaultLogPath else savedPath

            LogConfigState(
                isLogEnabled = preferences[PreferencesKeys.LOG_ENABLED] ?: true,
                logFilePath = finalPath,
                isLoaded = true
            )
        }

    suspend fun saveLogConfig(isEnabled: Boolean, filePath: String) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.LOG_ENABLED] = isEnabled

            // Optionale Optimierung: Wenn der vom Benutzer gespeicherte Pfad mit dem aktuellen Standardpfad übereinstimmt, wird er als null/leer gespeichert.
            // Dadurch wird sichergestellt, dass der Protokollpfad auch bei zukünftigen Änderungen des Arbeitsverzeichnisses automatisch beibehalten wird.            val currentWorkspace = WorkspaceManager.getWorkspacePath(context)
            val defaultPath = File(currentWorkspace, "logs").absolutePath

            if (filePath == defaultPath) {
                preferences.remove(PreferencesKeys.LOG_FILE_PATH)
            } else {
                preferences[PreferencesKeys.LOG_FILE_PATH] = filePath
            }
        }
    }
    suspend fun resetLogPath() {
        context.dataStore.edit { preferences ->
            preferences.remove(PreferencesKeys.LOG_FILE_PATH)
        }
    }
}
// LogCatcher bleibt unverändert...

object LogCatcher {

    // ✅ Nach der Korrektur sind Typreferenzen in LogConfigState nun einfacher.
    private var logConfig: LogConfigState? = null

    @Volatile
    private var isInitialized = false

    private val _logFlow = MutableSharedFlow<LogEntry>(extraBufferCapacity = 1000)
    val logFlow = _logFlow.asSharedFlow()

    // Historische Datensätze, die zur Speicherung von Build-Protokollen verwendet werden
    private val _buildLogs = Collections.synchronizedList(ArrayList<LogEntry>())

    @JvmStatic
    fun getBuildLogs(): List<LogEntry> {
        synchronized(_buildLogs) {
            return ArrayList(_buildLogs)
        }
    }

    @JvmStatic
    fun clearBuildLogs() {
        _buildLogs.clear()
    }

    @JvmStatic
    fun updateConfig(config: LogConfigState) {
        logConfig = config
        isInitialized = true
        i("LogCatcher", "Das Protokollsystem ist konfiguriert - aktiviert: ${config.isLogEnabled}, Weg: ${config.logFilePath}")
    }

    @JvmStatic
    fun d(tag: String, message: String) {
        android.util.Log.d(tag, message)
        emitLog("DEBUG", tag, message)
        writeToFile("DEBUG", tag, message)
    }

    @JvmStatic
    fun i(tag: String, message: String) {
        android.util.Log.i(tag, message)
        emitLog("INFO", tag, message)
        writeToFile("INFO", tag, message)
    }

    @JvmStatic
    fun w(tag: String, message: String) {
        android.util.Log.w(tag, message)
        emitLog("WARN", tag, message)
        writeToFile("WARN", tag, message)
    }

    @JvmStatic
    @JvmOverloads
    fun e(tag: String, message: String, exception: Exception? = null) {
        android.util.Log.e(tag, message, exception)
        
        val msg = "$message${exception?.let { " - ${it.message}" } ?: ""}"
        emitLog("ERROR", tag, msg)
        writeToFile("ERROR", tag, msg)
    }

    private fun emitLog(level: String, tag: String, message: String) {
        val entry = LogEntry(System.currentTimeMillis(), level, tag, message)
        
        // Falls es sich um ein Build-bezogenes Protokoll handelt, speichere es im Verlauf.
        if (tag == "ApkBuilder" || tag == "Build") {
            _buildLogs.add(entry)
        }
        
        _logFlow.tryEmit(entry)
    }

    @OptIn(DelicateCoroutinesApi::class)
    private fun writeToFile(level: String, tag: String, message: String) {
        val config = logConfig ?: return
        if (!config.isLogEnabled) return

        GlobalScope.launch(Dispatchers.IO) {
            try {
                val logDir = File(config.logFilePath)
                if (!logDir.exists()) {
                    logDir.mkdirs()
                }
                val logFile = File(logDir, "webide.log")
                val timestamp = SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS", Locale.getDefault()).format(Date())
                val logEntry = "[$timestamp] [$level] [$tag] $message\n"
                logFile.appendText(logEntry)
            } catch (e: Exception) {
                android.util.Log.e("LogCatcher", "Fehler beim Schreiben in die Protokolldatei: ${e.message}")
            }
        }
    }
}