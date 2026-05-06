package com.mobilecodespace.core.utils

import org.apache.commons.compress.archivers.ArchiveEntry
import org.apache.commons.compress.archivers.sevenz.SevenZFile
import org.apache.commons.compress.archivers.tar.TarArchiveInputStream
import org.apache.commons.compress.compressors.gzip.GzipCompressorInputStream
import java.io.*
import java.nio.file.Files
import java.util.zip.ZipEntry
import java.util.zip.ZipInputStream

object ArchiveUtils {
    fun extract(archivePath: String, destDir: String): Boolean {
        val file = File(archivePath)
        val ext = archivePath.substringAfterLast(".").lowercase()
        return when (ext) {
            "zip" -> unzip(file, File(destDir))
            "gz" -> if (archivePath.contains(".tar.gz")) extractTarGz(file, File(destDir)) else ungzip(file, File(destDir))
            "tar" -> extractTar(file, File(destDir))
            "7z" -> extract7z(file, File(destDir))
            else -> false
        }
    }

    fun extract7z(archiveFile: File, destDir: File): Boolean {
        return try {
            SevenZFile(archiveFile).use { sevenZFile ->
                var entry = sevenZFile.nextEntry
                while (entry != null) {
                    val curFile = File(destDir, entry.name)
                    if (entry.isDirectory) {
                        curFile.mkdirs()
                    } else {
                        curFile.parentFile?.mkdirs()
                        FileOutputStream(curFile).use { out ->
                            val content = ByteArray(8192)
                            var n: Int
                            while (sevenZFile.read(content).also { n = it } != -1) {
                                out.write(content, 0, n)
                            }
                        }
                    }
                    entry = sevenZFile.nextEntry
                }
            }
            true
        } catch (e: Exception) { e.printStackTrace(); false }
    }

    fun extractTarGz(archiveFile: File, destDir: File): Boolean {
        return try {
            FileInputStream(archiveFile).use { fis ->
                GzipCompressorInputStream(fis).use { gzis ->
                    TarArchiveInputStream(gzis).use { taris ->
                        var entry: ArchiveEntry? = taris.nextEntry
                        while (entry != null) {
                            val destFile = File(destDir, entry.name)
                            if (entry.isDirectory) {
                                destFile.mkdirs()
                            } else {
                                destFile.parentFile?.mkdirs()
                                Files.copy(taris, destFile.toPath())
                            }
                            entry = taris.nextEntry
                        }
                    }
                }
            }
            true
        } catch (e: Exception) { e.printStackTrace(); false }
    }

    fun extractTar(archiveFile: File, destDir: File): Boolean {
        return try {
            TarArchiveInputStream(FileInputStream(archiveFile)).use { taris ->
                var entry: ArchiveEntry? = taris.nextEntry
                while (entry != null) {
                    val destFile = File(destDir, entry.name)
                    if (entry.isDirectory) {
                        destFile.mkdirs()
                    } else {
                        destFile.parentFile?.mkdirs()
                        Files.copy(taris, destFile.toPath())
                    }
                    entry = taris.nextEntry
                }
            }
            true
        } catch (e: Exception) { e.printStackTrace(); false }
    }

    fun unzip(zipFile: File, destDir: File): Boolean {
        return try {
            ZipInputStream(BufferedInputStream(FileInputStream(zipFile))).use { zis ->
                var entry: ZipEntry? = zis.nextEntry
                while (entry != null) {
                    val newFile = File(destDir, entry.name)
                    if (entry.isDirectory) {
                        newFile.mkdirs()
                    } else {
                        newFile.parentFile?.mkdirs()
                        FileOutputStream(newFile).use { fos -> zis.copyTo(fos) }
                    }
                    zis.closeEntry()
                    entry = zis.nextEntry
                }
            }
            true
        } catch (e: Exception) { e.printStackTrace(); false }
    }

    fun ungzip(srcFile: File, destFile: File): Boolean {
        return try {
            GzipCompressorInputStream(FileInputStream(srcFile)).use { gzis ->
                FileOutputStream(destFile).use { fos -> gzis.copyTo(fos) }
            }
            true
        } catch (e: Exception) { e.printStackTrace(); false }
    }
}
