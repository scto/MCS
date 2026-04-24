package com.scto.mcs.core.domain.model

import kotlinx.uuid.UUID

/**
 * Repräsentiert eine Datei oder einen Ordner im System.
 */
data class FileItem(
    val id: UUID = UUID.generateUUID(),
    val name: String,
    val path: String,
    val isDirectory: Boolean,
    val size: Long = 0,
    val lastModified: Long = 0,
    val isHidden: Boolean = false
)