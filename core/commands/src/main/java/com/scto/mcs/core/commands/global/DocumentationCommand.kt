package com.scto.mcs.core.commands.global

import android.content.Intent
import android.view.KeyEvent

import androidx.core.net.toUri

import com.scto.mcs.core.commands.ActionContext
import com.scto.mcs.core.commands.CommandContext
import com.scto.mcs.core.commands.GlobalCommand
import com.scto.mcs.core.commands.KeyCombination
import com.scto.mcs.core.ui.icons.Icon
import com.scto.mcs.core.ui.icons.Menu_book
import com.scto.mcs.core.ui.icons.McsIcons
import com.scto.mcs.core.resources.getString
import com.scto.mcs.core.resources.strings

class DocumentationCommand(commandContext: CommandContext) : GlobalCommand(commandContext) {
    override val id: String = "global.documentation"

    override fun getLabel(): String = strings.docs.getString()

    override fun action(actionContext: ActionContext) {
        val url = "https://xed-editor.github.io/Xed-Docs/"
        val intent = Intent(Intent.ACTION_VIEW, url.toUri())
        actionContext.currentActivity.startActivity(intent)
    }

    override fun getIcon(): Icon = Icon.VectorIcon(XedIcons.Menu_book)

    override val defaultKeybinds: KeyCombination = KeyCombination(keyCode = KeyEvent.KEYCODE_F1)
}
