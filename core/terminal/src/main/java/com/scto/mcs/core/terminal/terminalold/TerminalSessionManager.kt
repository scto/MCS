package com.scto.mcs.core.terminal.terminalold

import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TerminalSessionManager @Inject constructor() {
    private val sessions = mutableMapOf<String, ProotProcessWrapper>()

    fun createSession(sessionId: String, config: TerminalConfig): ProotProcessWrapper {
        val wrapper = ProotProcessWrapper(config)
        sessions[sessionId] = wrapper
        return wrapper
    }

    fun getSession(sessionId: String): ProotProcessWrapper? = sessions[sessionId]

    fun closeSession(sessionId: String) {
        sessions[sessionId]?.stop()
        sessions.remove(sessionId)
    }
}
