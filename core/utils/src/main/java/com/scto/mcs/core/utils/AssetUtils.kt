package com.scto.mcs.core.utils

import android.content.Context
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream

/**
 * Hilfsklasse zum Verwalten von Dateien aus dem Assets-Ordner.
 */
object AssetUtils {

    /**
     * Kopiert eine Datei aus den Assets in den internen Cache-Speicher,
     * da Checkstyle oft physische Pfade für Konfigurationsdateien benötigt.
     */
    fun getAssetFile(context: Context, fileName: String): File {
        val cacheFile = File(context.cacheDir, fileName)
        if (!cacheFile.exists()) {
            context.assets.open(fileName).use { input ->
                FileOutputStream(cacheFile).use { output ->
                    input.copyTo(output)
                }
            }
        }
        return cacheFile
    }
}