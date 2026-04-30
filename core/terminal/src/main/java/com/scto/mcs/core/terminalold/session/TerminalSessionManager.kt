package com.scto.mcs.core.terminalold.session

import com.scto.mcs.core.terminal.session.TerminalSessionManager as ModernTerminalSessionManager
import kotlinx.uuid.UUID
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Wrapper für den TerminalSessionManager, der auf die moderne Implementierung verweist.
 */
@Singleton
class TerminalSessionManager @Inject constructor(
    private val modernManager: ModernTerminalSessionManager
) {
    val sessions = modernManager.sessions
    val activeSessionId = modernManager.activeSessionId

    fun createNewSession(title: String = "Session") {
        modernManager.createNewSession(title)
    }

    fun switchSession(id: UUID) {
        modernManager.switchSession(id)
    }

    fun closeSession(id: UUID) {
        modernManager.closeSession(id)
    }
}
