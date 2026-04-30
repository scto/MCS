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
            if (!source.rename