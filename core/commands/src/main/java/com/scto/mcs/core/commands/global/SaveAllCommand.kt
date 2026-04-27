package com.scto.mcs.core.commands.global

import android.view.KeyEvent

import com.scto.mcs.core.commands.ActionContext
import com.scto.mcs.core.commands.CommandContext
import com.scto.mcs.core.commands.GlobalCommand
import com.scto.mcs.core.commands.KeyCombination
import com.scto.mcs.core.ui.icons.Icon
import com.scto.mcs.core.resources.drawables
import com.scto.mcs.core.resources.getString
import com.scto.mcs.core.resources.strings
import com.scto.mcs.core.editor.tabs.editor.EditorTab

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class SaveAllCommand(commandContext: CommandContext) : GlobalCommand(commandContext) {
    override val id: String = "global.save_all"

    override fun getLabel(): String = strings.save_all.getString()

    override fun action(actionContext: ActionContext) {
        commandContext.mainViewModel.tabs.filterIsInstance<EditorTab>().forEach {
            GlobalScope.launch(Dispatchers.IO) { it.save() }
        }
    }

    override fun isEnabled(): Boolean {
        return commandContext.mainViewModel.tabs.isNotEmpty()
    }

    override fun getIcon(): Icon = Icon.DrawableRes(drawables.save)

    override val defaultKeybinds: KeyCombination =
        KeyCombination(keyCode = KeyEvent.KEYCODE_S, ctrl = true, shift = true)
}
