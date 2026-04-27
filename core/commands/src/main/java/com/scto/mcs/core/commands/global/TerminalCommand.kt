package com.scto.mcs.core.commands.global

import android.content.Intent
import android.view.KeyEvent

import com.scto.mcs.app.activities.Terminal
import com.scto.mcs.core.commands.ActionContext
import com.scto.mcs.core.commands.CommandContext
import com.scto.mcs.core.commands.GlobalCommand
import com.scto.mcs.core.commands.KeyCombination
import com.scto.mcs.core.ui.icons.Icon
import com.scto.mcs.core.resources.drawables
import com.scto.mcs.core.resources.getString
import com.scto.mcs.core.resources.strings
import com.scto.mcs.feature.settings.app.InbuiltFeatures
import com.scto.mcs.core.utils.showTerminalNotice

class TerminalCommand(commandContext: CommandContext) : GlobalCommand(commandContext) {
    override val id: String = "global.terminal"

    override fun getLabel(): String = strings.terminal.getString()

    override fun action(actionContext: ActionContext) {
        val activity = actionContext.currentActivity
        showTerminalNotice(activity) {
            val intent =
                Intent(activity, Terminal::class.java).apply {
                    commandContext.mainViewModel.currentTab?.file?.let { currentFile ->
                        //                                //                                val currentFile =
                        // viewModel.currentTab?.file ?:
                        //                                // return@apply
                        //                                //                                val currentPath =
                        // currentFile.getAbsolutePath()
                        //                                //                                val project =
                        //                                //                                    tabs
                        //                                //                                        .filter {
                        //                                // currentPath.startsWith(it.fileObject.getAbsolutePath()) }
                        //                                //                                        .maxByOrNull {
                        //                                // it.fileObject.getAbsolutePath().length } ?: return@apply
                        //                                //                                putExtra("cwd",

                        // TODO: Fix this
                    }
                }
            activity.startActivity(intent)
        }
    }

    override fun isSupported(): Boolean = InbuiltFeatures.terminal.state.value

    override fun getIcon(): Icon = Icon.DrawableRes(drawables.terminal)

    override val defaultKeybinds: KeyCombination = KeyCombination(keyCode = KeyEvent.KEYCODE_J, ctrl = true)
}
