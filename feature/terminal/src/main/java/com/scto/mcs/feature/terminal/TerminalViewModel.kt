package com.scto.mcs.feature.terminal

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.scto.mcs.core.terminal.TerminalService
import com.scto.mcs.core.terminal.session.TerminalSessionManager
import com.scto.mcs.core.terminal.setup.TerminalSetupService
import com.termux.terminal.TerminalSession
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TerminalViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val setupService: TerminalSetupService,
    private val terminalService: TerminalService,
    val sessionManager: TerminalSessionManager
) : ViewModel() {

    val sessions = sessionManager.sessions
    private val _activeSessionId = MutableStateFlow<String?>(null)
    val activeSessionId = _activeSessionId.asStateFlow()

    val activeSession = combine(sessions, _activeSessionId) { list, id ->
        list.find { it.mHandle == id }
    }.stateIn(viewModelScope, SharingStarted.Lazily, null)

    fun createNewSession() {
        val session = sessionManager.createNewSession("Session")
        _activeSessionId.value = session.mHandle
    }

    fun runCommand(command: String) {
        activeSession.value?.write(command + "\n")
    }
    
    fun removeSession(id: String?) {
        sessionManager.closeSession(id)
    }
}
