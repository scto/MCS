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
import com.scto.mcs.core.editor.tabs.editor.EditorTab

class ToggleReadOnlyCommand(commandContext: CommandContext) : EditorCommand(commandContext) {
    override val id: String = "editor.editable"

    override fun getLabel(): String {
        val editorTab = commandContext.mainViewModel.currentTab as? EditorTab
        return if (editorTab?.editorState?.editable == true) {
            strings.read_mode.getString()
        } else {
            strings.edit_mode.getString()
        }
    }

    override fun action(editorActionContext: EditorActionContext) {
        val editorState = editorActionContext.editorTab.editorState
        editorActionContext.editorTab.removeNotice("binary_file")
        editorState.editable = !editorState.editable
    }

    override fun isEnabled(editorNonActionContext: EditorNonActionContext): Boolean {
        return editorNonActionContext.editorTab.file.canWrite()
    }

    override fun getIcon(): Icon {
        val editorTab = commandContext.mainViewModel.currentTab as? EditorTab
        return if (editorTab?.editorState?.editable == true) {
            Icon.DrawableRes(drawables.lock)
        } else {
            Icon.DrawableRes(drawables.edit)
        }
    }

    override val defaultKeybinds: KeyCombination = KeyCombination(keyCode = KeyEvent.KEYCODE_E, ctrl = true)
}
