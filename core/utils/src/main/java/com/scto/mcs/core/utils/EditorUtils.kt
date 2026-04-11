package com.scto.mcs.core.utils

import android.graphics.Color
import android.text.Spannable
import android.text.style.BackgroundColorSpan
import android.widget.EditText

/**
 * Hilfsklasse zum Manipulieren des Editor-Inhalts und Markieren von Fehlern.
 */
object EditorUtils {

    private var currentErrorSpan: BackgroundColorSpan? = null

    /**
     * Markiert eine spezifische Zeile im EditText rot.
     * @param editor Der EditText (oder deine Editor-View)
     * @param lineNumber Die Zeilennummer (1-basiert)
     */
    fun highlightErrorLine(editor: EditText, lineNumber: Int) {
        clearHighlight(editor)
        
        val text = editor.text
        val lines = text.split("\n")
        
        if (lineNumber <= 0 || lineNumber > lines.size) return

        // Berechne Start- und End-Index der Zeile im Gesamttext
        var startPos = 0
        for (i in 0 until lineNumber - 1) {
            startPos += lines[i].length + 1 // +1 für das Newline-Zeichen
        }
        val endPos = startPos + lines[lineNumber - 1].length

        val span = BackgroundColorSpan(Color.parseColor("#44FF0000")) // Transparentes Rot
        text.setSpan(span, startPos, endPos, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        currentErrorSpan = span
        
        // Scrolle zur Fehlerzeile
        editor.setSelection(startPos)
    }

    /**
     * Entfernt alle aktiven Fehlermarkierungen.
     */
    fun clearHighlight(editor: EditText) {
        val text = editor.text
        currentErrorSpan?.let {
            text.removeSpan(it)
            currentErrorSpan = null
        }
    }
}