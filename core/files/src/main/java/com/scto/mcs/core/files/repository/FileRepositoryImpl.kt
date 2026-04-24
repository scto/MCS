package com.scto.mcs.core.files.repository

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
 * Implementierung des FileRepository für JVM/Android.
 * Nutzt Dispatchers.IO für alle Festplattenoperationen.
 */
class FileRepositoryImpl @Inject constructor() : FileRepository {

    override fun getFiles(path: String): Flow<List<List<FileItem>>> = flow {
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
        emit(listOf(files))
    }.flowOn(Dispatchers.IO)

    override suspend fun create(parentPath: String, name: String, isDirectory: Boolean): Result<FileItem> = 
        withContext(Dispatchers.IO) {
            runCatching {
                val newFile = File(parentPath, name)
                if (isDirectory) newFile.mkdirs() else newFile.createNewFile()
                FileItem(name = name, path = newFile.absolutePath, isDirectory = isDirectory)
            }
        }

    override suspend fun delete(path: String): Result<Unit> = withContext(Dispatchers.IO) {
        runCatching {
            val file = File(path)
            if (file.isDirectory) file.deleteRecursively() else file.delete()
            Unit
        }
    }

    override suspend fun move(sourcePath: String, targetPath: String): Result<Unit> = withContext(Dispatchers.IO) {
        runCatching {
            val source = File(sourcePath)
            val target = File(targetPath, source.name)
            source.renameTo(target)
            Unit
        }
    }

    override suspend fun rename(path: String, newName: String): Result<FileItem> = withContext(Dispatchers.IO) {
        runCatching {
            val file = File(path)
            val newFile = File(file.parent, newName)
            file.renameTo(newFile)
            FileItem(name = newName, path = newFile.absolutePath, isDirectory = file.isDirectory)
        }
    }

    override suspend fun copy(sourcePath: String, targetPath: String): Result<Unit> = withContext(Dispatchers.IO) {
        runCatching {
            File(sourcePath).copyTo(File(targetPath, File(sourcePath).name), overwrite = true)
            Unit
        }
    }
}