package com.scto.mcs.core.domain.usecase

import com.scto.mcs.core.domain.repository.EditorRepository
import java.io.File
import javax.inject.Inject

class LoadFileContentUseCase @Inject constructor(
    private val editorRepository: EditorRepository
) {
    operator fun invoke(file: File): String {
        return editorRepository.readFile(file)
    }
}
