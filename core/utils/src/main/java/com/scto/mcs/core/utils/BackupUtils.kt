package com.scto.mcs.core.utils

import android.content.Context

import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.zip.ZipEntry
import java.util.zip.ZipOutputStream

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

object BackupUtils {
    // Die 5 aktuellsten Backups behalten
    private const val MAX_BACKUP_COUNT = 5

    /**
      * Backup-Projekt: In eine ZIP-Datei packen und in einem privaten Verzeichnis speichern
     */
    suspend fun backupProject(context: Context, projectPath: String): String = withContext(Dispatchers.IO) {
        val projectDir = File(projectPath)
        if (!projectDir.exists()) return@withContext "Das Projekt existiert nicht."

        val folderName = projectDir.name
        // Privates Verzeichnis: /data/data/package_name/files/project_backups/project_name/
        val backupRootDir = File(context.filesDir, "project_backups/$folderName")
        if (!backupRootDir.exists()) backupRootDir.mkdirs()

        // Benennung: project_name_timestamp.zip
        val timestamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val backupFile = File(backupRootDir, "${folderName}_$timestamp.zip")

        try {
            zipFolder(projectDir, backupFile) { file ->
                val path = file.absolutePath
                // Filtert die Verzeichnisse build, .git und .gradle
                !path.contains("/build/") && !path.contains("/.git/") && !path.contains("/.gradle/")
            }
            cleanOldBackups(backupRootDir)
            return@withContext backupFile.absolutePath
        } catch (e: Exception) {
            e.printStackTrace()
            return@withContext "Fail: ${e.message}"
        }
    }

    private fun zipFolder(srcFolder: File, destZipFile: File, filter: (File) -> Boolean) {
        ZipOutputStream(FileOutputStream(destZipFile)).use { zos ->
            addFolderToZip(srcFolder, srcFolder, zos, filter)
        }
    }

    private fun addFolderToZip(rootFolder: File, srcFolder: File, zos: ZipOutputStream, filter: (File) -> Boolean) {
        val files = srcFolder.listFiles() ?: return
        for (file in files) {
            if (!filter(file)) continue
            if (file.isDirectory) {
                addFolderToZip(rootFolder, file, zos, filter)
            } else {
                val relPath = file.toRelativeString(rootFolder)
                zos.putNextEntry(ZipEntry(relPath))
                FileInputStream(file).use { fis -> fis.copyTo(zos) }
                zos.closeEntry()
            }
        }
    }

    private fun cleanOldBackups(backupDir: File) {
        val files = backupDir.listFiles { _, name -> name.endsWith(".zip") } ?: return
        if (files.size > MAX_BACKUP_COUNT) {
            files.sortBy { it.lastModified() }
            // Lösche die ältesten Einträge
            files.take(files.size - MAX_BACKUP_COUNT).forEach { it.delete() }
        }
    }
}