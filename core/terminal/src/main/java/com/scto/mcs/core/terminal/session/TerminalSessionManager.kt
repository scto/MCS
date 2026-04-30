package com.scto.mcs.core.terminal.session

import com.scto.mcs.core.terminal.TerminalSessionFactory
import com.scto.mcs.feature.settings.Settings
import com.termux.terminal.TerminalSession
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.io.File
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TerminalSessionManager @Inject constructor(
    private val tabManager: TabManager,
    private val sessionFactory: TerminalSessionFactory
) {
    private val _sessions = MutableStateFlow<List<TerminalSession>>(emptyList())
    val sessions: StateFlow<List<TerminalSession>> = _sessions.asStateFlow()

    fun createNewSession(name: String): TerminalSession {
        val newSession = TerminalSession()
        newSession.mHandle = UUID.randomUUID().toString()
        
        // Umgebungsvariablen setzen
        val env = sessionFactory.createEnv()
        
        // PWD berechnen
        val pwd = getPwd()
        
        // Session initialisieren (Logik aus MkSession)
        // Hier würde normalerweise die TerminalSession mit den Parametern konfiguriert werden
        
        _sessions.value = _sessions.value + newSession
        return newSession
    }

    fun closeSession(handle: String?) {
        _sessions.value = _sessions.value.filter { it.mHandle != handle }
    }

    fun getPwd(): String {
        return if (Settings.project_as_pwd) {
            tabManager.getCurrentTabPath()?.let { path ->
                File(path).parent ?: path
            } ?: System.getProperty("user.home") ?: "/"
        } else {
            System.getProperty("user.home") ?: "/"
        }
    }
}
