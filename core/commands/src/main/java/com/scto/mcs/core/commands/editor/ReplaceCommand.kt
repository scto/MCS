package com.scto.mcs.core.commands.editor

import android.view.KeyEvent

import com.scto.mcs.core.commands.CommandContext
import com.scto.mcs.core.commands.EditorActionContext
import com.scto.mcs.core.commands.EditorCommand
import com.scto.mcs.core.commands.KeyCombination
import com.scto.mcs.core.ui.icons.Icon
import com.scto.mcs.core.resources.drawables
import com.scto.mcs.core.resources.getString
import com.scto.mcs.core.resources.strings

class ReplaceCommand(commandContext: CommandContext) : EditorCommand(commandContext) {
    override val id: String = "editor.replace"

    override fun getLabel(): String = strings.replace.getString()

    override fun action(editorActionContext: EditorActionContext) {
        editorActionContext.editorTab.editorState.apply {
            editorActionContext.editor.getSelectedText()?.let { searchKeyword = it }
            isSearching = true
            isReplaceShown = true
        }
    }

    override fun getIcon(): Icon = Icon.DrawableRes(drawables.find_replace)

    override val defaultKeybinds: KeyCombination = KeyCombination(keyCode = KeyEvent.KEYCODE_H, ctrl = true)
}
