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

class DuplicateLineCommand(commandContext: CommandContext) : EditorCommand(commandContext) {
    override val id: String = "editor.duplicate_line"

    override fun getLabel(): String = strings.duplicate_line.getString()

    override fun action(editorActionContext: EditorActionContext) {
        editorActionContext.editor.duplicateLine()
    }

    override fun isEnabled(editorNonActionContext: EditorNonActionContext): Boolean {
        return editorNonActionContext.editorTab.editorState.editable
    }

    override fun getIcon(): Icon = Icon.DrawableRes(drawables.duplicate_line)

    override val defaultKeybinds: KeyCombination = KeyCombination(keyCode = KeyEvent.KEYCODE_D, ctrl = true)
}
