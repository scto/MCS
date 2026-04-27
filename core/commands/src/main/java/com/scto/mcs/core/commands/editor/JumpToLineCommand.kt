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

class JumpToLineCommand(commandContext: CommandContext) : EditorCommand(commandContext) {
    override val id: String = "editor.jump_to_line"

    override fun getLabel(): String = strings.jump_to_line.getString()

    override fun action(editorActionContext: EditorActionContext) {
        editorActionContext.editorTab.editorState.showJumpToLineDialog = true
    }

    override fun getIcon(): Icon = Icon.DrawableRes(drawables.arrow_outward)

    override val defaultKeybinds: KeyCombination = KeyCombination(keyCode = KeyEvent.KEYCODE_G, ctrl = true)
}
