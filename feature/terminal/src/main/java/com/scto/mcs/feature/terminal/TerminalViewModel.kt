package com.scto.mcs.feature.terminal

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.scto.mcs.core.resources.R
import com.scto.mcs.core.terminal.TerminalService
import com.scto.mcs.core.terminal.config.TerminalConfig
import com.scto.mcs.core.terminal.session.TerminalSessionManager
import com.scto.mcs.core.terminal.setup.TerminalSetupService
import com.scto.mcs.core.terminal.setup.TerminalSetupService.SetupState
import com.termux.terminal.TerminalSession
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.io.File
import javax.inject.Inject

/**
 * Modernisiertes ViewModel für die Terminal-Funktionalität.
 * Verwaltet echte Terminal-Sitzungen und den Setup-Prozess.
 */
@HiltViewModel
class TerminalViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val setupService: TerminalSetupService,
    private val terminalService: TerminalService,
    val sessionManager: TerminalSessionManager
) : ViewModel() {

    private val _setupState = MutableStateFlow<SetupState?>(null)
    val setupState = _setupState.asStateFlow()

    // Liste der aktiven Termux-Sessions
    private val _sessions = MutableStateFlow<List<TerminalSession>>(emptyList())
    val sessions = _sessions.asStateFlow()

    private val _activeSessionId = MutableStateFlow<String?>(null)
    val activeSessionId = _activeSessionId.asStateFlow()

    // Kombinierter State für die aktuell angezeigte Session
    val activeSession = combine(_sessions, _activeSessionId) { sessions, id ->
        sessions.find { it.mHandle == id }
    }.stateIn(viewModelScope, SharingStarted.Lazily, null)

    init {
        checkInstallationStatus()
    }

    /**
     * Prüft die Existenz der RootFS und Binärdateien.
     */
    private fun checkInstallationStatus() {
        val rootFs = TerminalConfig.getRootFsDir(context)
        val proot = File(
            File(context.filesDir, TerminalConfig.TERMINAL_ROOT_DIR), 
            "${TerminalConfig.BIN_DIR}/proot"
        )
        
        if (!rootFs.exists() || !proot.exists()) {
            _setupState.value = null // Setup erforderlich
        } else {
            _setupState.value = SetupState.Completed
            loadSessions()
        }
    }

    /**
     * Startet das komplette Setup der Linux-Umgebung.
     */
    fun startInstallation() {
        viewModelScope.launch {
            setupService.runFullSetup(context).collect { state ->
                _setupState.value = state
                if (state is SetupState.Completed) {
                    loadSessions()
                }
            }
        }
    }

    /**
     * Erstellt eine neue interaktive Shell-Sitzung.
     */
    fun createNewSession(name: String? = null) {
        viewModelScope.launch {
            val sessionName = name ?: "Session #${_sessions.value.size + 1}"
            // Erstellt eine neue Session über den Core-Dienst
            val newSession = terminalService.createTerminalSession(sessionName)
            
            val updatedList = _sessions.value.toMutableList().apply { add(newSession) }
            _sessions.value = updatedList
            _activeSessionId.value = newSession.mHandle
        }
    }

    fun switchSession(id: String?) {
        _activeSessionId.value = id
    }

    fun removeSession(id: String?) {
        val current = _sessions.value.toMutableList()
        val sessionToRemove = current.find { it.mHandle == id }
        
        sessionToRemove?.let {
            it.finishIfRunning()
            current.remove(it)
            _sessions.value = current
            
            if (_activeSessionId.value == id) {
                _activeSessionId.value = current.firstOrNull()?.mHandle
            }
        }
    }

    /**
     * Sendet einen Befehl an die aktuell aktive Session.
     */
    fun runCommand(command: String) {
        activeSession.value?.let { session ->
            session.write(command + "\n")
        }
    }

    private fun loadSessions() {
        if (_sessions.value.isEmpty()) {
            createNewSession()
        }
    }
}
