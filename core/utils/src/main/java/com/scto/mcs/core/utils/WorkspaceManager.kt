package com.scto.mcs.core.utils

import android.content.Context
import android.content.SharedPreferences

import androidx.core.content.edit

import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

import java.io.File

object WorkspaceManager {
    private const val PREFS_NAME = "mcs_prefs"
    private const val KEY_WORKSPACE_PATH = "workspace_path"
    private const val KEY_IS_CONFIGURED = "is_workspace_configured"

    fun getDefaultPath(context: Context): String {
        val dir = context.getExternalFilesDir(null)
        return dir?.absolutePath ?: context.filesDir.absolutePath
    }

    /**
     * Gibt das Arbeitsverzeichnis zurück (mit automatischer Fehlerkorrektur)
     */
    fun getWorkspacePath(context: Context): String {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val savedPath = prefs.getString(KEY_WORKSPACE_PATH, null)

        // 1. Wenn nichts gespeichert wurde, Standard zurückgeben
        if (savedPath.isNullOrBlank()) {
            return getDefaultPath(context)
        }

        // 🔥🔥🔥 Fix 2: Robustere Logik zur Pfadüberprüfung 🔥🔥🔥
        // Vorherige Logik hing von absoluten Pfaden ab, was durch Abweichungen zwischen /sdcard und /storage/emulated/0 leicht zu Fehlern führte
        // Aktuelle Logik: Wenn der Pfad "Android/data" enthält, prüfen, ob er den Paketnamen der aktuellen App enthält
        if (savedPath.contains("/Android/data/")) {
            val packageName = context.packageName
            // Wenn der Pfad nicht einmal den Paketnamen enthält, gehört er sicher einer anderen App (oder einem alten Paketnamen). Da wir keine Berechtigung haben, muss er zurückgesetzt werden
            if (!savedPath.contains(packageName)) {
                android.util.Log.e("WorkspaceManager", "Ungültiger Pfad erkannt (Paketname stimmt nicht überein): $savedPath, wird auf Standard zurückgesetzt")
                val validPath = getDefaultPath(context)
                saveWorkspacePath(context, validPath) // Korrigierten Pfad automatisch speichern
                return validPath
            }
        }

        return savedPath
    }

    fun isWorkspaceConfigured(context: Context): Boolean {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        // Solange dieser Wert true ist, bedeutet dies, dass der Benutzer auf "Bestätigen und fortfahren" geklickt hat
        return prefs.getBoolean(KEY_IS_CONFIGURED, false)
    }

    fun getWorkspacePathFlow(context: Context): Flow<String> = callbackFlow {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val listener = SharedPreferences.OnSharedPreferenceChangeListener { _, key ->
            if (key == KEY_WORKSPACE_PATH) {
                trySend(getWorkspacePath(context))
            }
        }
        prefs.registerOnSharedPreferenceChangeListener(listener)
        trySend(getWorkspacePath(context))
        awaitClose { prefs.unregisterOnSharedPreferenceChangeListener(listener) }
    }

    fun saveWorkspacePath(context: Context, path: String) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        prefs.edit {
            putString(KEY_WORKSPACE_PATH, path)
            // ✅ Wichtig: Auf true setzen, um anzuzeigen, dass der Benutzer den Initialisierungsassistenten abgeschlossen hat
            putBoolean(KEY_IS_CONFIGURED, true)
        }
        ensurePathExists(context, path)
    }

    fun ensurePathExists(context: Context, path: String): Boolean {
        val file = File(path)
        if (file.exists() && file.isDirectory) return true

        try {
            if (path.contains(context.packageName)) {
                return file.mkdirs() || file.exists()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return file.mkdirs() || file.exists()
    }
}