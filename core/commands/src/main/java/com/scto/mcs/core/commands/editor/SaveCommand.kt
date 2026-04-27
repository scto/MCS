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

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class SaveCommand(commandContext: CommandContext) : EditorCommand(commandContext) {
    override val id: String = "editor.save"

    override fun getLabel(): String = strings.save.getString()

    override fun action(editorActionContext: EditorActionContext) {
        GlobalScope.launch(Dispatchers.IO) { editorActionContext.editorTab.save() }
    }

    override fun isEnabled(editorNonActionContext: EditorNonActionContext): Boolean {
        return editorNonActionContext.editorTab.file.canWrite()
    }

    override fun getIcon(): Icon = Icon.DrawableRes(drawables.save)

    override val defaultKeybinds: KeyCombination = KeyCombination(keyCode = KeyEvent.KEYCODE_S, ctrl = true)
}
