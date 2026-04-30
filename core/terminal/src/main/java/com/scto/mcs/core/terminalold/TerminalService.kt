package com.scto.mcs.core.terminalold

import android.content.Context
import com.scto.mcs.core.terminal.TerminalService as ModernTerminalService
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Wrapper für den TerminalService, der auf die moderne Implementierung verweist.
 */
@Singleton
class TerminalService @Inject constructor(
    private val modernService: ModernTerminalService
) {
    fun execute(command: String, workingDir: String? = null): Flow<String> {
        return modernService.execute(command, workingDir)
    }
}
