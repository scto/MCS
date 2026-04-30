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
import java.util.Locale
import javax.inject.Inject

/**
 * Implementierung des FileRepository für Android.
 */
class FileRepositoryImpl @Inject constructor(
    private val context: Context
) : FileRepository {

    private fun File.ensureDir(): File {
        if (!this.exists()) this.mkdirs()
        return this
    }

    private fun File.child(name: String): File = File(this, name)

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
                source.copyRecursively(target, overwrite = true)
                source.deleteRecursively()
            }
            Unit
        }
    }

    override suspend fun saveInternalScript(name: String, content: String): Result<FileItem> = withContext(Dispatchers.IO) {
        runCatching {
            val binDir = getBinDir().getOrThrow()
            val file = File(binDir.path, name)
            file.writeText(content)
            file.setExecutable(true)
            FileItem(name = name, path = file.absolutePath, isDirectory = false)
        }
    }

    override suspend fun ensureDirectoryStructure(paths: List<String>): Result<Unit> = withContext(Dispatchers.IO) {
        runCatching {
            paths.forEach { File(it).ensureDir() }
            Unit
        }
    }

    override suspend fun readAsset(path: String): String = withContext(Dispatchers.IO) {
        context.assets.open(path).bufferedReader().use { it.readText() }
    }

    override fun getName(path: String): String = File(path).name

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

    override suspend fun getPrivateDir(): Result<FileItem> = wrapDir(context.filesDir)
    override suspend fun getCacheDir(): Result<FileItem> = wrapDir(context.cacheDir)
    override suspend fun getLocalDir(): Result<FileItem> = wrapDir(File(context.filesDir, "local"))
    override suspend fun getBinDir(): Result<FileItem> = wrapDir(File(File(context.filesDir, "local"), "bin"))
    override suspend fun getLibDir(): Result<FileItem> = wrapDir(File(File(context.filesDir, "local"), "lib"))
    override suspend fun getSandboxDir(): Result<FileItem> = wrapDir(File(File(context.filesDir, "local"), "sandbox"))
    override suspend fun getSandboxHomeDir(): Result<FileItem> = wrapDir(File(File(context.filesDir, "local"), "home"))
    override suspend fun getRunnerDir(): Result<FileItem> = wrapDir(File(File(context.filesDir, "local"), "runners"))
    override suspend fun getThemeDir(): Result<FileItem> = wrapDir(File(File(context.filesDir, "local"), "themes"))
    override suspend fun getTempDir(): Result<FileItem> = wrapDir(File(context.cacheDir, "tempFiles"))

    private suspend fun wrapDir(file: File): Result<FileItem> = withContext(Dispatchers.IO) {
        runCatching {
            file.ensureDir()
            FileItem(
                name = file.name, 
                path = file.absolutePath, 
                isDirectory = file.isDirectory,
                lastModified = file.lastModified()
            )
        }
    }
}