package com.scto.mcs.core.terminal.terminalold

import com.scto.mcs.core.domain.repository.FileRepository
import javax.inject.Inject
import javax.inject.Singleton
import java.io.File

@Singleton
class TerminalSetupService @Inject constructor(
    private val fileRepository: FileRepository
) {
    suspend fun setupEnvironment(): Result<Unit> {
        val sandboxResult = fileRepository.getSandboxDir()
        
        return if (sandboxResult.isSuccess) {
            val sandboxDir = sandboxResult.getOrNull()
            if (sandboxDir != null) {
                val file = File(sandboxDir.path)
                if (!file.exists()) {
                    file.mkdirs()
                }
                Result.success(Unit)
            } else {
                Result.failure(Exception("Sandbox directory path is null"))
            }
        } else {
            Result.failure(Exception("Failed to retrieve sandbox directory: ${sandboxResult.exceptionOrNull()?.message}"))
        }
    }
}
