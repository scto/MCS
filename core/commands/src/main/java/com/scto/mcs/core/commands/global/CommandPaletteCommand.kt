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

class CommandPaletteCommand(commandContext: CommandContext) : GlobalCommand(commandContext) {
    override val id: String = "global.command_palette"

    override fun getLabel(): String = strings.command_palette.getString()

    override fun action(actionContext: ActionContext) {
        commandContext.mainViewModel.showCommandPalette()
    }

    override fun getIcon(): Icon = Icon.DrawableRes(drawables.command_palette)

    override val defaultKeybinds: KeyCombination =
        KeyCombination(keyCode = KeyEvent.KEYCODE_P, ctrl = true, shift = true)
}
