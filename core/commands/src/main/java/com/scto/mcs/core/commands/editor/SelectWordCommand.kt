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

class SelectWordCommand(commandContext: CommandContext) : EditorCommand(commandContext) {
    override val id: String = "editor.select_word"

    override fun getLabel(): String = strings.select_word.getString()

    override fun action(editorActionContext: EditorActionContext) {
        editorActionContext.editor.selectCurrentWord()
    }

    override fun getIcon(): Icon = Icon.DrawableRes(drawables.select)

    override val defaultKeybinds: KeyCombination = KeyCombination(keyCode = KeyEvent.KEYCODE_W, ctrl = true)
}
