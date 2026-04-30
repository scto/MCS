package com.scto.mcs.core.terminal.session

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
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
        // Logik zur Erstellung einer neuen Session
        // Hier würde normalerweise die Terminal-Emulation initialisiert werden
        val newSession = TerminalSession() // Vereinfacht
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

    fun getPwd(): String {
        // Wenn Settings.project_as_pwd wahr ist, frage TabManager ab
        return tabManager.getCurrentTabPath() ?: System.getProperty("user.home") ?: "/"
    }
}
