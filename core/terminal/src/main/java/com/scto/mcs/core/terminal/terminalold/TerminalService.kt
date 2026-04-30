package com.scto.mcs.core.terminal.terminalold

import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TerminalService @Inject constructor(
    private val sessionManager: TerminalSessionManager,
    private val setupService: TerminalSetupService
) {
    suspend fun initialize() {
        setupService.setupEnvironment()
    }

    fun startSession(sessionId: String, config: TerminalConfig) {
        sessionManager.createSession(sessionId, config).start()
    }

    fun stopSession(sessionId: String) {
        sessionManager.closeSession(sessionId)
    }
}
