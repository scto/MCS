package com.scto.mcs.feature.terminal

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.scto.mcs.core.terminal.TerminalService
import com.scto.mcs.core.terminal.config.TerminalConfig
import com.scto.mcs.core.terminal.session.TerminalSessionManager
import com.scto.mcs.core.terminal.setup.TerminalSetupService
import com.scto.mcs.core.terminal.setup.TerminalSetupService.SetupState
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.io.File
import javax.inject.Inject
import com.scto.mcs.core.resources.getString
import com.scto.mcs.core.resources.strings

@HiltViewModel
class TerminalViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val terminalService: TerminalService,
    private val setupService: TerminalSetupService,
    val sessionManager: TerminalSessionManager
) : ViewModel() {

    private val _setupState = MutableStateFlow<SetupState?>(null)
    val setupState: StateFlow<SetupState?> = _setupState.asStateFlow()

    private val _textSize = MutableStateFlow(12)
    val textSize: StateFlow<Int> = _textSize.asStateFlow()

    private val _isExecuting = MutableStateFlow(false)
    val isExecuting: StateFlow<Boolean> = _isExecuting.asStateFlow()

    init {
        checkInstallationStatus()
    }

    /**
     * Prüft, ob das Terminal bereits installiert ist.
     */
    private fun checkInstallationStatus() {
        val rootFs = TerminalConfig.getRootFsDir(context)
        val proot = File(File(context.filesDir, TerminalConfig.TERMINAL_ROOT_DIR), "${TerminalConfig.BIN_DIR}/proot")
        
        if (!rootFs.exists() || !proot.exists()) {
            // Setup erforderlich
            _setupState.value = null 
        } else {
            _setupState.value = SetupState.Completed
            if (sessionManager.sessions.value.isEmpty()) {
                sessionManager.createNewSession("Terminal")
            }
        }
    }

    fun startInstallation() {
        viewModelScope.launch {
            setupService.runFullSetup(context).collect { state ->
                _setupState.value = state
                if (state is SetupState.Completed) {
                    sessionManager.createNewSession("Terminal")
                }
            }
        }
    }

    fun runCommand(command: String) {
        val activeId = sessionManager.activeSessionId.value ?: return
        if (command.isBlank() || _isExecuting.value) return

        viewModelScope.launch {
            _isExecuting.value = true
            appendLineToActiveSession("> $command")

            terminalService.execute(command).collect { line ->
                appendLineToActiveSession(line)
            }
            
            _isExecuting.value = false
        }
    }

    private fun appendLineToActiveSession(line: String) {
        val activeId = sessionManager.activeSessionId.value ?: return
        val currentSessions = sessionManager.sessions.value.toMutableList()
        val index = currentSessions.indexOfFirst { it.id == activeId }
        
        if (index != -1) {
            val session = currentSessions[index]
            val updatedOutput = session.output.toMutableList().apply { add(line) }
            currentSessions[index] = session.copy(output = updatedOutput)
            // Hinweis: Der Manager sorgt normalerweise für das State-Update
        }
    }

    fun setTextSize(size: Int) {
        _textSize.value = size.coerceIn(8, 24)
    }
}
