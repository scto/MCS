package com.scto.mcs.core.utils

import android.content.Context
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import java.io.File

/**
 * Verwaltet die Logik zur Wiederherstellung von ungespeicherten Entwürfen nach einem Absturz.
 */
object RecoveryManager {

    /**
     * Prüft, ob ein Entwurf existiert und bietet dem Nutzer die Wiederherstellung an.
     * * @param context Der Context für den Dialog
     * @param originalFilePath Der Pfad der eigentlich zu öffnenden Datei
     * @param onRestore Callback, wenn der Nutzer den Entwurf laden möchte
     * @param onDiscard Callback, wenn der Nutzer den Entwurf löschen möchte
     */
    fun checkAndRestore(
        context: Context,
        originalFilePath: String,
        onRestore: (String) -> Unit,
        onDiscard: () -> Unit
    ) {
        val fileName = File(originalFilePath).name
        val draftContent = FileManager.loadDraft(context, fileName)

        if (draftContent != null) {
            // Vergleiche den Entwurf mit der Originaldatei (falls diese existiert)
            val originalFile = File(originalFilePath)
            val originalContent = if (originalFile.exists()) originalFile.readText() else ""

            // Nur fragen, wenn der Entwurf sich tatsächlich vom Original unterscheidet
            if (draftContent != originalContent) {
                showRecoveryDialog(context, fileName, draftContent, onRestore, onDiscard)
            } else {
                // Entwurf ist identisch, einfach löschen
                FileManager.clearDraft(context, fileName)
                onDiscard()
            }
        } else {
            onDiscard()
        }
    }

    private fun showRecoveryDialog(
        context: Context,
        fileName: String,
        draftContent: String,
        onRestore: (String) -> Unit,
        onDiscard: () -> Unit
    ) {
        MaterialAlertDialogBuilder(context)
            .setTitle("Wiederherstellung verfügbar")
            .setMessage("Es wurde ein ungespeicherter Entwurf für '$fileName' gefunden. Möchtest du diesen wiederherstellen?")
            .setCancelable(false)
            .setPositiveButton("Wiederherstellen") { _, _ ->
                onRestore(draftContent)
            }
            .setNegativeButton("Verwerfen") { _, _ ->
                FileManager.clearDraft(context, fileName)
                onDiscard()
            }
            .show()
    }
}