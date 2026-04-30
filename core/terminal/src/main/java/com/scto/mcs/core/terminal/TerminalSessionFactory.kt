package com.scto.mcs.core.terminal

import com.scto.mcs.core.terminal.config.TerminalConfig
import com.scto.mcs.feature.settings.Settings
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TerminalSessionFactory @Inject constructor() {

    /**
     * Erstellt die Umgebungsvariablen für eine neue Terminal-Session.
     * Integriert Pfade aus TerminalConfig und Sandbox-Einstellungen.
     */
    fun createEnv(): Map<String, String> {
        val env = TerminalConfig.DEFAULT_ENV.toMutableMap()
        
        // Setzen der Pfade basierend auf TerminalConfig
        env["HOME"] = Settings.sandbox
        env["PREFIX"] = TerminalConfig.PREFIX
        env["TMPDIR"] = TerminalConfig.TMPDIR
        env["LANG"] = "en_US.UTF-8"
        
        // PATH erweitern, um die Terminal-Binaries einzubinden
        val currentPath = System.getenv("PATH") ?: ""
        env["PATH"] = "${TerminalConfig.BIN_PATH}:$currentPath"
        
        return env
    }
}
