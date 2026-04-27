package com.scto.mcs.core.commands.lsp

import com.scto.mcs.app.scope.DefaultScope
import com.scto.mcs.core.commands.CommandContext
import com.scto.mcs.core.commands.LspActionContext
import com.scto.mcs.core.commands.LspCommand
import com.scto.mcs.core.commands.LspNonActionContext
import com.scto.mcs.core.ui.icons.Icon
import com.scto.mcs.core.editor.lsp.goToReferences
import com.scto.mcs.core.resources.drawables
import com.scto.mcs.core.resources.getString
import com.scto.mcs.core.resources.strings

class GoToReferencesCommand(commandContext: CommandContext) : LspCommand(commandContext) {
    override val id: String = "lsp.go_to_references"

    override fun getLabel(): String = strings.go_to_references.getString()

    override fun action(lspActionContext: LspActionContext) {
        goToReferences(
            scope = DefaultScope,
            context = lspActionContext.currentActivity,
            viewModel = commandContext.mainViewModel,
            editorTab = lspActionContext.editorTab,
        )
    }

    override fun isSupported(lspNonActionContext: LspNonActionContext): Boolean {
        return lspNonActionContext.lspConnector.isGoToReferencesSupported()
    }

    override fun getIcon(): Icon = Icon.DrawableRes(drawables.manage_search)
}
