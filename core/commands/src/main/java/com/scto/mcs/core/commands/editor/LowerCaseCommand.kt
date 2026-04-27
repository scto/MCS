package com.scto.mcs.core.commands.editor

import com.scto.mcs.core.commands.CommandContext
import com.scto.mcs.core.commands.EditorActionContext
import com.scto.mcs.core.commands.EditorCommand
import com.scto.mcs.core.ui.icons.Icon
import com.scto.mcs.core.resources.drawables
import com.scto.mcs.core.resources.getString
import com.scto.mcs.core.resources.strings

class LowerCaseCommand(commandContext: CommandContext) : EditorCommand(commandContext) {
    override val id: String = "editor.lowercase"

    override fun getLabel(): String = strings.transform_lowercase.getString()

    override fun action(editorActionContext: EditorActionContext) {
        val editor = editorActionContext.editor
        if (editor.isTextSelected) {
            val selectionStart = editor.cursorRange.startIndex
            val selectionEnd = editor.cursorRange.endIndex
            val selectionText = editor.text.substring(selectionStart, selectionEnd)
            editor.text.replace(selectionStart, selectionEnd, selectionText.lowercase())
        }
    }

    override fun getIcon(): Icon = Icon.DrawableRes(drawables.letters)
}
