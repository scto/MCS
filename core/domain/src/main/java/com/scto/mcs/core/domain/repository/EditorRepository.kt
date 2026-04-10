package com.scto.mcs.core.domain.repository

import java.io.File

interface EditorRepository {
    fun readFile(file: File): String
    fun saveFile(file: File, content: String)
}
