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

class RedoCommand(commandContext: CommandContext) : EditorCommand(commandContext) {
    override val id: String = "editor.redo"

    override fun getLabel(): String = strings.redo.getString()

    override fun action(editorActionContext: EditorActionContext) {
        val editor = editorActionContext.editor
        if (editor.canRedo()) editor.redo()
        editorActionContext.editorTab.editorState.updateUndoRedo()
    }

    override fun isEnabled(editorNonActionContext: EditorNonActionContext): Boolean {
        val editorState = editorNonActionContext.editorTab.editorState
        return editorState.editable && editorState.canRedo
    }

    override fun getIcon(): Icon = Icon.DrawableRes(drawables.redo)

    override val defaultKeybinds: KeyCombination = KeyCombination(keyCode = KeyEvent.KEYCODE_Y, ctrl = true)
}
