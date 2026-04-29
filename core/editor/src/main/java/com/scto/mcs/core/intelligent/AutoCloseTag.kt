package com.scto.mcs.core.editor.intelligent

import com.scto.mcs.core.editor.Editor

/**
 * Automatische Vervollständigung von HTML/XML Tags.
 */
object AutoCloseTag {
    private val TAG_REGEX = Regex("<([_a-zA-Z][a-zA-Z0-9:\\-_.]*)(?:\\s+[^<>]*)*(/|>)$")

    fun handleInsert(char: Char, editor: Editor) {
        if (char != '>' || editor.cursor.isSelected) return

        val line = editor.text.getLine(editor.cursor.leftLine)
        val before = line.take(editor.cursor.leftColumn)
        
        val match = TAG_REGEX.find(before) ?: return
        val tagName = match.groupValues[1]
        val closing = match.groupValues[2]

        if (closing == ">") {
            editor.text.insert(editor.cursor.leftLine, editor.cursor.leftColumn, "</$tagName>")
            editor.setSelection(editor.cursor.leftLine, editor.cursor.leftColumn)
        }
    }
}
