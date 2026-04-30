package com.scto.mcs.core.terminal.terminalold

import javax.inject.Inject
import java.io.IOException

class SessionService @Inject constructor(
    private val sessionManager: TerminalSessionManager
) {
    fun sendInput(sessionId: String, input: String) {
        val session = sessionManager.getSession(sessionId)
        try {
            session?.outputStream?.write(input.toByteArray())
            session?.outputStream?.flush()
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }
}
