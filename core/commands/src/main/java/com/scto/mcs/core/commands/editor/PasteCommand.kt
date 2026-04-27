package com.scto.mcs.core.commands.editor

import android.view.KeyEvent

import com.scto.mcs.core.commands.CommandContext
import com.scto.mcs.core.commands.EditorActionContext
import com.scto.mcs.core.commands.EditorCommand
import com.scto.mcs.core.commands.EditorNonActionContext
import com.scto.mcs.core.commands.KeyCombination
import com.scto.mcs.core.ui.icons.Icon
import com.scto.mcs.core.resources.drawables
import com.scto.mcs.core.resources.getString
import com.scto.mcs.core.resources.strings

class PasteCommand(commandContext: CommandContext) : EditorCommand(commandContext) {
    override val id: String = "editor.paste"

    override fun getLabel(): String = strings.paste.getString()

    override fun action(editorActionContext: EditorActionContext) {
        editorActionContext.editor.pasteText()
    }

    override fun isEnabled(editorNonActionContext: EditorNonActionContext): Boolean {
        return editorNonActionContext.editorTab.editorState.editable
    }

    override fun getIcon(): Icon = Icon.DrawableRes(drawables.paste)

    override val defaultKeybinds: KeyCombination = KeyCombination(keyCode = KeyEvent.KEYCODE_V, ctrl = true)
}
