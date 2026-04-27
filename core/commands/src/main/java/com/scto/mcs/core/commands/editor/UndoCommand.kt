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

class UndoCommand(commandContext: CommandContext) : EditorCommand(commandContext) {
    override val id: String = "editor.undo"

    override fun getLabel(): String = strings.undo.getString()

    override fun action(editorActionContext: EditorActionContext) {
        val editor = editorActionContext.editor
        if (editor.canUndo()) editor.undo()
        editorActionContext.editorTab.editorState.updateUndoRedo()
    }

    override fun isEnabled(editorNonActionContext: EditorNonActionContext): Boolean {
        val editorState = editorNonActionContext.editorTab.editorState
        return editorState.editable && editorState.canUndo
    }

    override fun getIcon(): Icon = Icon.DrawableRes(drawables.undo)

    override val defaultKeybinds: KeyCombination = KeyCombination(keyCode = KeyEvent.KEYCODE_Z, ctrl = true)
}
