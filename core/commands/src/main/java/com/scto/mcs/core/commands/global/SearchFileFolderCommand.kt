package com.scto.mcs.core.commands.global

import android.view.KeyEvent

import com.scto.mcs.core.commands.ActionContext
import com.scto.mcs.core.commands.CommandContext
import com.scto.mcs.core.commands.GlobalCommand
import com.scto.mcs.core.commands.KeyCombination
import com.scto.mcs.core.ui.components.fileSearchDialog
import com.scto.mcs.core.filetree.FileTreeTab
import com.scto.mcs.core.filetree.currentDrawerTab
import com.scto.mcs.core.ui.icons.Icon
import com.scto.mcs.core.resources.drawables
import com.scto.mcs.core.resources.getString
import com.scto.mcs.core.resources.strings

class SearchFileFolderCommand(commandContext: CommandContext) : GlobalCommand(commandContext) {
    override val id: String = "global.search_file_folder"

    override fun getLabel(): String = strings.search_file_folder.getString()

    override fun action(actionContext: ActionContext) {
        fileSearchDialog = true
    }

    override fun isEnabled(): Boolean {
        return currentDrawerTab is FileTreeTab
    }

    override fun getIcon(): Icon = Icon.DrawableRes(drawables.search)

    override val defaultKeybinds: KeyCombination = KeyCombination(keyCode = KeyEvent.KEYCODE_P, ctrl = true)
}
