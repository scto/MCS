package com.scto.mcs.core.terminal.terminalold

import com.scto.mcs.core.domain.repository.FileRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TerminalSetupService @Inject constructor(
    private val fileRepository: FileRepository
) {
    suspend fun setupEnvironment(): Result<Unit> {
        // Ensure necessary directories exist using the domain repository
        val sandboxDir = fileRepository.getSandboxDir()
        return if (sandboxDir.isSuccess) {
            Result.success(Unit)
        } else {
            Result.failure(Exception("Failed to setup terminal environment"))
        }
    }
}
