package com.scto.mcs.core.terminal

import com.scto.mcs.core.terminal.config.TerminalConfig
import com.scto.mcs.feature.settings.Settings
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TerminalSessionFactory @Inject constructor() {

    fun createEnv(): Map<String, String> {
        val env = TerminalConfig.DEFAULT_ENV.toMutableMap()
        env["HOME"] = Settings.sandbox
        return env
    }
}
