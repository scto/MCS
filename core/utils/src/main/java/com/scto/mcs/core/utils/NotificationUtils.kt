package com.scto.mcs.core.utils

import android.view.View
import android.widget.EditText
import com.google.android.material.snackbar.Snackbar

object NotificationUtils {

    /**
     * Zeigt den Fehler an und bietet eine Aktion zum "Springen zur Zeile" an.
     */
    fun showFormattingErrorWithAction(
        rootView: View, 
        editor: EditText, 
        result: FormattingResult.Error
    ) {
        val snackbar = Snackbar.make(
            rootView, 
            "Fehler: ${result.message.take(50)}...", 
            Snackbar.make(rootView, "", 0).duration // Nutzt Standard-Dauer
        )

        if (result.line > 0) {
            snackbar.setAction("ZEIGE ZEILE ${result.line}") {
                EditorUtils.highlightErrorLine(editor, result.line)
            }
        }
        
        snackbar.setBackgroundTint(0xFFD32F2F.toInt())
        snackbar.setTextColor(0xFFFFFFFF.toInt())
        snackbar.setActionTextColor(0xFFFFFF00.toInt()) // Gelb für die Aktion
        snackbar.show()
    }
}