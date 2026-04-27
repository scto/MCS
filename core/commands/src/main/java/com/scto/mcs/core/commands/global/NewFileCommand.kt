package com.scto.mcs.core.commands.global

import android.view.KeyEvent

import com.scto.mcs.core.commands.ActionContext
import com.scto.mcs.core.commands.CommandContext
import com.scto.mcs.core.commands.GlobalCommand
import com.scto.mcs.core.commands.KeyCombination
import com.scto.mcs.core.ui.components.addDialog
import com.scto.mcs.core.ui.icons.Icon
import com.scto.mcs.core.resources.drawables
import com.scto.mcs.core.resources.getString
import com.scto.mcs.core.resources.strings

class NewFileCommand(commandContext: CommandContext) : GlobalCommand(commandContext) {
    override val id: String = "global.new_file"

    override fun getLabel(): String = strings.new_file.getString()

    override fun action(actionContext: ActionContext) {
        addDialog = true
    }

    override fun getIcon(): Icon = Icon.DrawableRes(drawables.add)

    override val defaultKeybinds: KeyCombination = KeyCombination(keyCode = KeyEvent.KEYCODE_N, ctrl = true)
}
