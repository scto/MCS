package com.scto.mcs.core.files.repository

import android.content.Context

import com.scto.mcs.core.domain.model.FileItem
import com.scto.mcs.core.domain.repository.FileRepository

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext

import java.io.File

import javax.inject.Inject

/**
 * Implementierung des FileRepository für Android.
 * Integriert die Logik zur Verzeichnisverwaltung direkt in die Repository-Methoden.
 */
class FileRepositoryImpl @Inject constructor(
    private val context: Context
) : FileRepository {

    // --- Hilfs-Erweiterungen für File-Handling ---
    private fun File.ensureDir(): File {
        if (!this.exists()) this.mkdirs()
        return this
    }

    private fun File.child(name: String): File = File(this, name)

    // --- Standard Datei-Operationen ---

    override fun getFiles(path: String): Flow<List<FileItem>> = flow {
        val root = File(path)
        val files = root.listFiles()?.map { file ->
            FileItem(
                name = file.name,
                path = file.absolutePath,
                isDirectory = file.isDirectory,
                size = file.length(),
                lastModified = file.lastModified(),
                isHidden = file.isHidden
            )
        } ?: emptyList()
        emit(files)
    }.flowOn(Dispatchers.IO)

    override suspend fun create(parentPath: String, name: String, isDirectory: Boolean): Result<FileItem> = 
        withContext(Dispatchers.IO) {
            runCatching {
                val newFile = File(parentPath, name)
                if (isDirectory) newFile.mkdirs() else newFile.createNewFile()
                FileItem(
                    name = name, 
                    path = newFile.absolutePath, 
                    isDirectory = isDirectory,
                    lastModified = newFile.lastModified()
                )
            }
        }

    override suspend fun delete(path: String): Result<Unit> = withContext(Dispatchers.IO) {
        runCatching {
            val file = File(path)
            if (file.isDirectory) file.deleteRecursively() else file.delete()
            Unit
        }
    }

    override suspend fun rename(path: String, newName: String): Result<FileItem> = withContext(Dispatchers.IO) {
        runCatching {
            val file = File(path)
            val newFile = File(file.parent, newName)
            if (file.renameTo(newFile)) {
                FileItem(name = newName, path = newFile.absolutePath, isDirectory = file.isDirectory)
            } else {
                throw Exception("Umbenennen fehlgeschlagen")
            }
        }
    }

    override suspend fun copy(sourcePath: String, targetPath: String): Result<Unit> = withContext(Dispatchers.IO) {
        runCatching {
            val source = File(sourcePath)
            val target = File(targetPath).child(source.name)
            source.copyRecursively(target, overwrite = true)
            Unit
        }
    }

    override suspend fun move(sourcePath: String, targetPath: String): Result<Unit> = withContext(Dispatchers.IO) {
        runCatching {
            val source = File(sourcePath)
            val target = File(targetPath).child(source.name)
            if (!source.renameTo(target)) {
                // Fallback falls Partitionen unterschiedlich sind
                source.copyRecursively(target, overwrite = true)
                source.deleteRecursively()
            }
            Unit
        }
    }

    fun getName(path: String): String {
        return File(path).name
    }

    override fun getExtension(path: String): String {
        val file = File(path)
        return if (file.isDirectory) "" else file.extension
    }

    override fun getType(path: String): String {
        val file = File(path)
        if (file.isDirectory) return "Ordner"
        
        val ext = file.extension.lowercase(Locale.ROOT)
        return when (ext) {
            "kt", "kts" -> "Kotlin Datei"
            "java" -> "Java Datei"
            "py" -> "Python Datei"
            "js", "ts" -> "JavaScript/TypeScript Datei"
            "json" -> "JSON Konfiguration"
            "xml" -> "XML Dokument"
            "md", "markdown" -> "Markdown Dokument"
            "html", "htm" -> "HTML Dokument"
            "css" -> "Stylesheets"
            "sh", "bash" -> "Shell Skript"
            "txt" -> "Text Dokument"
            "" -> "Datei"
            else -> "${ext.uppercase(Locale.ROOT)} Datei"
        }
    }
    
    // --- Implementierung der Pfad-Konstanten ---

    //override fun getPrivateDir(): File = context.filesDir.ensureDir()
    override fun getPrivateDir(): Result<FileItem> = withContext(Dispatchers.IO) {
        runCatching {
            val privateDir = context.filesDir.ensureDir()
            FileItem(
                name = getName(privateDir), 
                path = privateDir.absolutePath, 
                isDirectory = privateDir.isDirectory,
                lastModified = privateDir.lastModified()
            )
        }
    }
        
    //override fun getCacheDir(): File = context.cacheDir.ensureDir()
    override fun getCacheDir(): Result<FileItem> = withContext(Dispatchers.IO) {
        runCatching {
            val cacheDir = context.cacheDir.ensureDir()
            FileItem(
                name = getName(cacheDir), 
                path = cacheDir.absolutePath, 
                isDirectory = cacheDir.isDirectory,
                lastModified = cacheDir.lastModified()
            )
        }
    }
    
    //override fun getLocalDir(): File = getPrivateDir().child("local").ensureDir()
    override fun getLocalDir(): Result<FileItem> = withContext(Dispatchers.IO) {
        runCatching {
            val localDir = getPrivateDir().child("local").ensureDir()
            FileItem(
                name = getName(localDir), 
                path = localDir.absolutePath, 
                isDirectory = localDir.isDirectory,
                lastModified = localDir.lastModified()
            )
        }
    }
    
    //override fun getBinDir(): File = getLocalDir().child("bin").ensureDir()
    override fun getBinDir(): Result<FileItem> = withContext(Dispatchers.IO) {
        runCatching {
            val binDir = getLocalDir().child("bin").ensureDir()
            FileItem(
                name = getName(binDir), 
                path = binDir.absolutePath, 
                isDirectory = binDir.isDirectory,
                lastModified = binDir.lastModified()
            )
        }
    }

    //override fun getLibDir(): File = getLocalDir().child("lib").ensureDir()
    override fun getLibDir(): Result<FileItem> = withContext(Dispatchers.IO) {
        runCatching {
            val libDir = getLocalDir().child("lib").ensureDir()
            FileItem(
                name = getName(libDir), 
                path = libDir.absolutePath, 
                isDirectory = libDir.isDirectory,
                lastModified = libDir.lastModified()
            )
        }
    }

    //override fun getSandboxDir(): File = getLocalDir().child("sandbox").ensureDir()
    override fun getSandboxDir(): Result<FileItem> = withContext(Dispatchers.IO) {
        runCatching {
            val sandboxDir = getLocalDir().child("sandbox").ensureDir()
            FileItem(
                name = getName(sandboxDir), 
                path = sandboxDir.absolutePath, 
                isDirectory = sandboxDir.isDirectory,
                lastModified = sandboxDir.lastModified()
            )
        }
    }

    //override fun getSandboxHomeDir(): File = getLocalDir().child("home").ensureDir()
    override fun getSandboxHomeDir(): Result<FileItem> = withContext(Dispatchers.IO) {
        runCatching {
            val sandboxHomeDir = getLocalDir().child("home").ensureDir()
            FileItem(
                name = getName(sandboxHomeDir), 
                path = sandboxHomeDir.absolutePath, 
                isDirectory = sandboxHomeDir.isDirectory,
                lastModified = sandboxHomeDir.lastModified()
            )
        }
    }

    //override fun getRunnerDir(): File = getLocalDir().child("runners").ensureDir()
    override fun getRunnerDir(): Result<FileItem> = withContext(Dispatchers.IO) {
        runCatching {
            val runnerDir = getLocalDir().child("runners").ensureDir()
            FileItem(
                name = getName(runnerDir), 
                path = runnerDir.absolutePath, 
                isDirectory = runnerDir.isDirectory,
                lastModified = runnerDir.lastModified()
            )
        }
    }

    //override fun getThemeDir(): File = getLocalDir().child("themes").ensureDir()
    override fun getThemeDir(): Result<FileItem> = withContext(Dispatchers.IO) {
        runCatching {
            val themeDir = getLocalDir().child("themes").ensureDir()
            FileItem(
                name = getName(themeDir), 
                path = themeDir.absolutePath, 
                isDirectory = themeDir.isDirectory,
                lastModified = themeDir.lastModified()
            )
        }
    }

    //override fun getTempDir(): File = getCacheDir().child("tempFiles").ensureDir()
    override fun getTempDir(): Result<FileItem> = withContext(Dispatchers.IO) {
        runCatching {
            val tempDir = getCacheDir().child("tempFiles").ensureDir()
            FileItem(
                name = getName(tempDir), 
                path = tempDir.absolutePath, 
                isDirectory = isDirectory,
                lastModified = tempDir.lastModified()
            )
        }
    }
}