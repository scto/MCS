package com.scto.mcs.core.editor.intelligent

import android.view.KeyEvent
import com.scto.mcs.core.editor.Editor
import io.github.rosemoe.sora.event.EditorKeyEvent

/**
 * Sorgt dafür, dass in Markdown-Dateien Listen automatisch fortgesetzt werden, 
 * wenn man Enter drückt.
 */
object BulletContinuation : IntelligentFeature() {
    override val id: String = "md.bullet_continuation"

    // Unterstützte Dateiendungen für dieses Feature
    override val supportedExtensions = listOf("md", "markdown")

    private val QUOTE_REGEX = Regex("^> ")
    private val LIST_WHITESPACE_REGEX = Regex("^\\s*([-+*]|[0-9]+[.)]) +(\\[[ x]] +)?")
    private val LIST_REGEX = Regex("^([-+*]|[0-9]+[.)])( +\\[[ x]])?\$")
    private val UL_LIST_REGEX = Regex("^((\\s*[-+*] +)(\\[[ x]] +)?)")
    private val OL_LIST_REGEX = Regex("^(\\s*)([0-9]+)([.)])( +)((\\[[ x]] +)?)")

    override fun handleKeyEvent(event: EditorKeyEvent, editor: Editor) {
        if (event.action != KeyEvent.ACTION_DOWN) return

        if (event.keyCode == KeyEvent.KEYCODE_ENTER && event.modifiers == 0) {
            onEnter(editor) { event.markAsConsumed() }
        } else if (event.keyCode == KeyEvent.KEYCODE_TAB && !event.isCtrlPressed && !event.isAltPressed) {
            onTab(editor, event.isShiftPressed) { event.markAsConsumed() }
        }
    }

    private fun onTab(editor: Editor, shiftPressed: Boolean, consumeEvent: () -> Unit) {
        if (editor.cursor.leftLine != editor.cursor.rightLine) return
        val lineIndexBefore = editor.cursor.leftLine
        val columnIndexBefore = editor.cursor.leftColumn

        val line = editor.text.getLine(lineIndexBefore)
        val lineToCursor = line.take(columnIndexBefore)

        val listMatch = LIST_WHITESPACE_REGEX.find(line)
        if (listMatch != null && (lineToCursor.endsWith(listMatch.value) || editor.isTextSelected)) {
            if (!shiftPressed) {
                editor.indentLines(false)
            } else {
                editor.unindentSelection()
            }
            consumeEvent()
        }
    }

    private fun onEnter(editor: Editor, consumeEvent: () -> Unit) {
        if (editor.isTextSelected) return
        val lineIndexBefore = editor.cursor.leftLine
        val columnIndexBefore = editor.cursor.leftColumn

        val line = editor.text.getLine(lineIndexBefore).toString()
        val lineToCursor = line.take(columnIndexBefore)

        // Zitate (Blockquotes) behandeln
        if (QUOTE_REGEX.find(line) != null) {
            if (line.trim() == ">") {
                editor.text.delete(lineIndexBefore, 0, lineIndexBefore, line.length)
            } else {
                editor.text.insert(lineIndexBefore, columnIndexBefore, "\n> ")
            }
            consumeEvent()
            return
        }

        // Leere Listen-Elemente beim Enter löschen
        if (LIST_REGEX.matchEntire(line.trim()) != null) {
            editor.text.delete(lineIndexBefore, 0, lineIndexBefore, line.length)
            consumeEvent()
            return
        }

        // Ungeordnete Listen fortführen
        val ulMatch = UL_LIST_REGEX.find(lineToCursor)
        if (ulMatch != null) {
            val prefix = ulMatch.groupValues[1]
            val nextItem = "\n" + prefix.replace("[x]", "[ ]")
            editor.text.insert(lineIndexBefore, columnIndexBefore, nextItem)
            consumeEvent()
            return
        }

        // Geordnete Listen (nummeriert) fortführen
        val olMatch = OL_LIST_REGEX.find(lineToCursor)
        if (olMatch != null) {
            val leading = olMatch.groupValues[1]
            val number = olMatch.groupValues[2].toInt() + 1
            val delimiter = olMatch.groupValues[3]
            val trailing = olMatch.groupValues[4]
            val check = olMatch.groupValues[5].replace("[x]", "[ ]")

            val nextItem = "\n$leading$number$delimiter$trailing$check"
            editor.text.insert(lineIndexBefore, columnIndexBefore, nextItem)
            consumeEvent()
        }
    }
}
