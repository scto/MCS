package com.scto.mcs.core.terminal.terminalold

import javax.inject.Inject

class SessionService @Inject constructor(
    private val sessionManager: TerminalSessionManager
) {
    fun sendInput(sessionId: String, input: String) {
        val session = sessionManager.getSession(sessionId)
        session?.let {
            // Logic to write to the process output stream
        }
    }
}
