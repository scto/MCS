package com.scto.mcs.core.utils

import android.content.Context

import java.io.File
import java.io.IOException

/**
 * Hilfsklasse für den Lese- und Schreibzugriff auf Dateien.
 */
object FileManager {

    /**
     * Speichert den Inhalt in eine Datei im internen Speicher oder auf der SD-Karte.
     */
    fun saveFile(path: String, content: String): Boolean {
        return try {
            val file = File(path)
            // Erstelle Verzeichnisse, falls sie nicht existieren
            file.parentFile?.mkdirs()
            file.writeText(content)
            true
        } catch (e: IOException) {
            e.printStackTrace()
            false
        }
    }

    /**
     * Speichert einen Entwurf (Draft) für den Fall eines Absturzes.
     */
    fun saveDraft(context: Context, fileName: String, content: String) {
        val draftFile = File(context.cacheDir, "draft_$fileName")
        try {
            draftFile.writeText(content)
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    /**
     * Lädt einen gespeicherten Entwurf.
     */
    fun loadDraft(context: Context, fileName: String): String? {
        val draftFile = File(context.cacheDir, "draft_$fileName")
        return if (draftFile.exists()) draftFile.readText() else null
    }

    /**
     * Löscht den Entwurf nach erfolgreichem manuellem Speichern.
     */
    fun clearDraft(context: Context, fileName: String) {
        File(context.cacheDir, "draft_$fileName").delete()
    }
}