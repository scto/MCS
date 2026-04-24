package com.scto.mcs.core.domain.repository

import com.scto.mcs.core.domain.model.FileItem
import kotlinx.coroutines.flow.Flow

/**
 * Interface für alle Datei-Operationen im System.
 */
interface FileRepository {
    /** Liefert die Liste der Dateien in einem Verzeichnis als Flow. */
    fun getFiles(path: String): Flow<List<FileItem>>

    /** Erstellt eine neue Datei oder einen Ordner. */
    suspend fun create(parentPath: String, name: String, isDirectory: Boolean): Result<FileItem>

    /** Löscht eine Datei oder einen Ordner. */
    suspend fun delete(path: String): Result<Unit>

    /** Benennt eine Datei oder einen Ordner um. */
    suspend fun rename(path: String, newName: String): Result<FileItem>

    /** Kopiert eine Datei an einen neuen Ort. */
    suspend fun copy(sourcePath: String, targetPath: String): Result<Unit>

    /** Verschiebt eine Datei an einen neuen Ort (Cut/Paste). */
    suspend fun move(sourcePath: String, targetPath: String): Result<Unit>
}