package com.scto.mcs.core.terminal.session

import com.scto.mcs.core.terminal.TerminalService

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.uuid.UUID

import javax.inject.Inject
import javax.inject.Singleton

/**
 * Modell für eine aktive Terminal-Sitzung.
 */
data class TerminalSession(
    val id: UUID = UUID.generateUUID(),
    val title: String,
    val output: MutableList<String> = mutableListOf()
)

/**
 * Verwaltet mehrere parallele Terminal-Sessions.
 */
@Singleton
class TerminalSessionManager @Inject constructor(
    private val terminalService: TerminalService
) {
    private val _sessions = MutableStateFlow<List<TerminalSession>>(emptyList())
    val sessions: StateFlow<List<TerminalSession>> = _sessions

    private val _activeSessionId = MutableStateFlow<UUID?>(null)
    val activeSessionId: StateFlow<UUID?> = _activeSessionId

    fun createNewSession(title: String = "Session ${_sessions.value.size + 1}") {
        val newSession = TerminalSession(title = title)
        _sessions.value = _sessions.value + newSession
        if (_activeSessionId.value == null) {
            _activeSessionId.value = newSession.id
        }
    }

    fun switchSession(id: UUID) {
        _activeSessionId.value = id
    }

    fun closeSession(id: UUID) {
        _sessions.value = _sessions.value.filter { it.id != id }
        if (_activeSessionId.value == id) {
            _activeSessionId.value = _sessions.value.firstOrNull()?.id
        }
    }
}