package com.scto.mcs.core.file

import java.io.File

data class FileNode(
    val file: File,
    val isDirectory: Boolean,
)
