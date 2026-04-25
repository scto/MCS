package com.scto.mcs.core.domain.repository

import com.scto.mcs.core.domain.model.FileItem
import kotlinx.coroutines.flow.Flow
import java.io.File

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

    /** Liefert den Namen der Datei/des Ordners aus dem Pfad. */
    fun getName(path: String): String

    /** Liefert die Dateiendung (z.B. "kt" oder "json"). */
    fun getExtension(path: String): String

    /** Liefert einen lesbaren Typ (z.B. "Ordner", "Kotlin Datei"). */
    fun getType(path: String): String

    /** IDE Standard-Verzeichnisse **/
    suspend fun getPrivateDir(): Result<FileItem>
    
    suspend fun getCacheDir(): Result<FileItem>
    
    suspend fun getLocalDir(): Result<FileItem>
    
    suspend fun getBinDir(): Result<FileItem>
    
    suspend fun getLibDir(): Result<FileItem>
    
    suspend fun getSandboxDir(): Result<FileItem>
    
    suspend fun getSandboxHomeDir(): Result<FileItem>
    
    suspend fun getRunnerDir(): Result<FileItem>
    
    suspend fun getThemeDir(): Result<FileItem>
    
    suspend fun getTempDir(): Result<FileItem>
}