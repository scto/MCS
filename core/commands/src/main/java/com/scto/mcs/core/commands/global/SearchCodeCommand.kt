package com.scto.mcs.core.commands.global

import android.view.KeyEvent

import com.scto.mcs.core.commands.ActionContext
import com.scto.mcs.core.commands.CommandContext
import com.scto.mcs.core.commands.GlobalCommand
import com.scto.mcs.core.commands.KeyCombination
import com.scto.mcs.core.ui.components.codeSearchDialog
import com.scto.mcs.core.filetree.currentDrawerTab
import com.scto.mcs.core.ui.icons.Icon
import com.scto.mcs.core.resources.drawables
import com.scto.mcs.core.resources.getString
import com.scto.mcs.core.resources.strings

class SearchCodeCommand(commandContext: CommandContext) : GlobalCommand(commandContext) {
    override val id: String = "global.search_code"

    override fun getLabel(): String = strings.search_code.getString()

    override fun action(actionContext: ActionContext) {
        codeSearchDialog = true
    }

    override fun isEnabled(): Boolean {
        return currentDrawerTab != null
    }

    override fun getIcon(): Icon = Icon.DrawableRes(drawables.search)

    override val defaultKeybinds: KeyCombination =
        KeyCombination(keyCode = KeyEvent.KEYCODE_F, ctrl = true, shift = true)
}
