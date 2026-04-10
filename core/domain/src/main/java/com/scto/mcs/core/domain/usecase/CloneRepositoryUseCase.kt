package com.scto.mcs.core.domain.usecase

import com.scto.mcs.core.domain.repository.GitRepository
import java.io.File
import javax.inject.Inject

class CloneRepositoryUseCase @Inject constructor(
    private val gitRepository: GitRepository
) {
    suspend operator fun invoke(url: String, destination: File): Result<Unit> {
        return gitRepository.cloneRepository(url, destination)
    }
}
