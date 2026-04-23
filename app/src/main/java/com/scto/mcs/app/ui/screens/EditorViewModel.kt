package com.srvhive.app.ui.screens

import android.content.Context
import android.net.Uri
import androidx.compose.runtime.*
import androidx.lifecycle.ViewModel
import io.github.rosemoe.sora.text.Content
import io.github.rosemoe.sora.text.ContentIO
import java.io.OutputStreamWriter

class EditorViewModel : ViewModel() {
    val openFiles = mutableStateListOf<EditorFile>()
    var activeFileIndex by mutableStateOf(-1)
    
    val activeFile: EditorFile?
        get() = if (activeFileIndex in openFiles.indices) openFiles[activeFileIndex] else null

    fun openFileFromUri(context: Context, uri: Uri) {
        val existing = openFiles.indexOfFirst { it.uri == uri }
        if (existing != -1) {
            activeFileIndex = existing
            return
        }

        try {
            context.contentResolver.openInputStream(uri)?.use { stream ->
                val content = ContentIO.createFromReader(stream.bufferedReader())
                // Name aus URI extrahieren
                val fileName = uri.path?.split("/")?.lastOrNull() ?: "Unbenannt"
                
                val newFile = EditorFile(uri, fileName, content)
                openFiles.add(newFile)
                activeFileIndex = openFiles.lastIndex
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /**
     * Speichert den aktuellen Inhalt in die bestehende Datei (URI).
     */
    fun saveCurrentFile(context: Context) {
        val file = activeFile ?: return
        val uri = file.uri ?: return // "Speichern unter" falls kein URI vorhanden
        
        saveToUri(context, uri, file)
    }

    /**
     * Schreibt den Content in einen URI und aktualisiert den Dateistatus.
     */
    fun saveToUri(context: Context, uri: Uri, file: EditorFile) {
        try {
            context.contentResolver.openOutputStream(uri, "wt")?.use { outputStream ->
                OutputStreamWriter(outputStream).use { writer ->
                    writer.write(file.content.toString())
                    file.isDirty.value = false
                    // Falls "Speichern unter", URI und Name aktualisieren
                    if (file.uri != uri) {
                        // In einer realen App würde man hier den Namen aus dem neuen URI extrahieren
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun closeTab(index: Int) {
        if (index in openFiles.indices) {
            openFiles.removeAt(index)
            if (activeFileIndex >= openFiles.size) activeFileIndex = openFiles.size - 1
        }
    }
}