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

    /**
     * Erstellt eine neue Terminal-Session.
     * Nutzt die Logik zur Initialisierung der Umgebungsvariablen und des Arbeitsverzeichnisses.
     */
    fun createNewSession(name: String): TerminalSession {
        val newSession = TerminalSession()
        newSession.mHandle = UUID.randomUUID().toString()
        
        // Umgebungsvariablen abrufen
        val env = sessionFactory.createEnv()
        
        // Arbeitsverzeichnis (PWD) berechnen
        val pwd = getPwd()
        
        // Initialisierung der Session
        // Wir verwenden hier die Standard-Termux-Initialisierung:
        // processName: null (Standard), directory: pwd, arguments: ["/system/bin/sh", "-l"], 
        // environment: env-Map, sessionClient: null (wird vom Service gesetzt)
        val envArray = env.map { "${it.key}=${it.value}" }.toTypedArray()
        
        // Hinweis: Die genaue Signatur von initializeSession hängt von der Termux-Version ab.
        // Hier wird die Standard-Signatur angenommen.
        newSession.initializeSession(
            null, 
            pwd, 
            arrayOf("/system/bin/sh", "-l"), 
            envArray, 
            null
        )
        
        _sessions.value = _sessions.value + newSession
        return newSession
    }

    fun closeSession(handle: String?) {
        _sessions.value = _sessions.value.filter { it.mHandle != handle }
    }

    /**
     * Berechnet das Arbeitsverzeichnis basierend auf den Einstellungen und dem aktuellen Tab.
     */
    fun getPwd(): String {
        return if (Settings.project_as_pwd) {
            tabManager.getCurrentTabPath()?.let { path ->
                val file = File(path)
                if (file.isDirectory) path else file.parent ?: path
            } ?: System.getProperty("user.home") ?: "/"
        } else {
            System.getProperty("user.home") ?: "/"
        }
    }
}
