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

class ToggleWordWrapCommand(commandContext: CommandContext) : EditorCommand(commandContext) {
    override val id: String = "editor.toggle_word_wrap"

    override fun getLabel(): String = strings.toggle_word_wrap.getString()

    override fun action(editorActionContext: EditorActionContext) {
        val editor = editorActionContext.editor
        editor.setWordwrap(!editor.isWordwrap, true, true)
    }

    override fun getIcon(): Icon = Icon.DrawableRes(drawables.edit_note)

    override val defaultKeybinds: KeyCombination = KeyCombination(keyCode = KeyEvent.KEYCODE_Z, alt = true)
}
