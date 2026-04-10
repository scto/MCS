package com.scto.mcs.core.data.repository

import com.scto.mcs.core.domain.repository.EditorRepository
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class EditorRepositoryImpl @Inject constructor() : EditorRepository {

    override fun readFile(file: File): String {
        return file.readText()
    }

    override fun saveFile(file: File, content: String) {
        file.writeText(content)
    }
}
