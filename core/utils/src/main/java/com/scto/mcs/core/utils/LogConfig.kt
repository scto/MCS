package com.scto.mcs.core.utils

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore

import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow

import java.io.File
import java.text.SimpleDateFormat
import java.util.*

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
     * ✅ Kern-Fix: Verwendung von combine zum Zusammenführen der DataStore- und WorkspaceManager-Flows
     * Dadurch wird hier neu berechnet, egal ob die Log-Einstellungen oder das Arbeitsverzeichnis geändert wurden
     */
    val logConfigFlow: Flow<LogConfigState> = context.dataStore.data
        .combine(WorkspaceManager.getWorkspacePathFlow(context)) { preferences, workspacePath ->

            // 1. Dynamisches Arbeitsverzeichnis abrufen
            // workspacePath wird in Echtzeit vom Flow übergeben

            // 2. Standard-Logverzeichnis erstellen: Arbeitsverzeichnis/logs
            val defaultLogPath = File(workspacePath, "logs").absolutePath

            // 3. Endgültigen Pfad bestimmen:
            // Wenn der Benutzer den Pfad manuell im DataStore angegeben hat (savedPath ist nicht leer), diesen priorisieren
            // Wenn nicht manuell angegeben (null oder leer), automatisch dem Arbeitsverzeichnis folgen
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

            // Optionale Optimierung: Wenn der vom Benutzer gespeicherte Pfad mit dem aktuellen Standardpfad übereinstimmt, als null/empty speichern,
            // damit der Log-Pfad später automatisch folgen kann, wenn das Arbeitsverzeichnis geändert wird.
            val currentWorkspace = WorkspaceManager.getWorkspacePath(context)
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

object LogCatcher {

    // ✅ Nach dem Fix wird eine einfachere LogConfigState Typenreferenz verwendet
    private var logConfig: LogConfigState? = null

    @Volatile
    private var isInitialized = false

    private val _logFlow = MutableSharedFlow<LogEntry>(extraBufferCapacity = 1000)
    val logFlow = _logFlow.asSharedFlow()

    // Wird verwendet, um den Verlauf der Build-Logs zu speichern
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
        i("LogCatcher", "Log-System konfiguriert - Aktiviert: ${config.isLogEnabled}, Pfad: ${config.logFilePath}")
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
        
        // Wenn es sich um ein Build-bezogenes Log handelt, im Verlauf speichern
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
                android.util.Log.e("LogCatcher", "Fehler beim Schreiben in die Log-Datei: ${e.message}")
            }
        }
    }

}