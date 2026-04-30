package com.scto.mcs.core.terminal.session

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import com.scto.mcs.feature.settings.Settings
import com.termux.terminal.TerminalSession
import kotlinx.uuid.UUID
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Verwaltet aktive Terminal-Sitzungen und deren Arbeitsverzeichnisse.
 */
@Singleton
class TerminalSessionManager @Inject constructor(
    private val tabManager: TabManager
) {
    val sessions = mutableStateListOf<TerminalSession>()
    val activeSessionId = mutableStateOf<String?>(null)

    fun createNewSession(title: String = "Session") {
        val newSession = TerminalSession() 
        sessions.add(newSession)
        activeSessionId.value = newSession.mHandle
    }

    fun switchSession(id: UUID) {
        activeSessionId.value = id.toString()
    }

    fun closeSession(id: UUID) {
        val session = sessions.find { it.mHandle == id.toString() }
        session?.let {
            it.finishIfRunning()
            sessions.remove(it)
        }
    }

    /**
     * Ermittelt das aktuelle Arbeitsverzeichnis.
     * Wenn Settings.project_as_pwd aktiv ist, wird der Pfad des aktuellen Editor-Tabs verwendet.
     */
    fun getPwd(): String {
        return if (Settings.project_as_pwd) {
            tabManager.getCurrentTabPath()?.let { path ->
                // Extrahiere Parent-Verzeichnis der Datei
                java.io.File(path).parent ?: path
            } ?: System.getProperty("user.home") ?: "/"
        } else {
            System.getProperty("user.home") ?: "/"
        }
    }
}
