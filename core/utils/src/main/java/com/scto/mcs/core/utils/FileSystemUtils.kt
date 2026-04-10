package com.scto.mcs.core.utils

import android.content.Context
import androidx.core.content.FileProvider
import android.net.Uri
import dagger.hilt.android.qualifiers.ApplicationContext
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FileSystemUtils @Inject constructor(
    @ApplicationContext private val context: Context
) {
    // Funktionen für Dateizugriffe im mcs Pfad
    // FileProvider Setup Unterstützung
    
    fun getMcsDirectory(): File {
        return File(context.filesDir, "mcs")
    }

    fun ensureDirectoryExists(path: String): File {
        val file = File(getMcsDirectory(), path)
        if (!file.exists()) {
            file.mkdirs()
        }
        return file
    }

    fun listFiles(path: String): List<File> {
        val dir = File(getMcsDirectory(), path)
        return dir.listFiles()?.toList() ?: emptyList()
    }

    fun getUriForFile(file: File): Uri {
        // Annahme: FileProvider Authority ist "com.scto.mcs.provider"
        return FileProvider.getUriForFile(context, "com.scto.mcs.provider", file)
    }
}
