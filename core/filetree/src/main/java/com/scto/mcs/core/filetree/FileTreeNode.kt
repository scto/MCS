package com.scto.mcs.core.filetree

import com.scto.mcs.core.files.FileObject

data class FileTreeNode(val file: FileObject, val isFile: Boolean, val isDirectory: Boolean, val name: String)
