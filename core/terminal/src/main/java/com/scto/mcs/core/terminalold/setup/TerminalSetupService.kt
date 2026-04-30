package com.scto.mcs.core.terminalold.setup

import android.content.Context
import com.scto.mcs.core.terminal.setup.TerminalSetupService as ModernTerminalSetupService
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Wrapper für den TerminalSetupService, der auf die moderne Implementierung verweist.
 */
@Singleton
class TerminalSetupService @Inject constructor(
    private val modernService: ModernTerminalSetupService
) {
    fun runFullSetup(context: Context): Flow<ModernTerminalSetupService.SetupState> {
        return modernService.runFullSetup(context)
    }
}
