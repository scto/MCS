package com.scto.mcs.core.commands.global

import android.content.Intent
import android.view.KeyEvent

import com.scto.mcs.app.activities.SettingsActivity
import com.scto.mcs.core.commands.ActionContext
import com.scto.mcs.core.commands.CommandContext
import com.scto.mcs.core.commands.GlobalCommand
import com.scto.mcs.core.commands.KeyCombination
import com.scto.mcs.core.ui.icons.Icon
import com.scto.mcs.core.resources.drawables
import com.scto.mcs.core.resources.getString
import com.scto.mcs.core.resources.strings

class SettingsCommand(commandContext: CommandContext) : GlobalCommand(commandContext) {
    override val id: String = "global.settings"

    override fun getLabel(): String = strings.settings.getString()

    override fun action(actionContext: ActionContext) {
        val activity = actionContext.currentActivity
        activity.startActivity(Intent(activity, SettingsActivity::class.java))
    }

    override fun getIcon(): Icon = Icon.DrawableRes(drawables.settings)

    override val defaultKeybinds: KeyCombination = KeyCombination(keyCode = KeyEvent.KEYCODE_COMMA, ctrl = true)
}
