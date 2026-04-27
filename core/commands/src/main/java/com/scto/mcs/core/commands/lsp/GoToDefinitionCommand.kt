package com.scto.mcs.core.commands.lsp

import com.scto.mcs.app.scope.DefaultScope
import com.scto.mcs.core.commands.CommandContext
import com.scto.mcs.core.commands.LspActionContext
import com.scto.mcs.core.commands.LspCommand
import com.scto.mcs.core.commands.LspNonActionContext
import com.scto.mcs.core.ui.icons.Icon
import com.scto.mcs.core.editor.lsp.goToDefinition
import com.scto.mcs.core.resources.drawables
import com.scto.mcs.core.resources.getString
import com.scto.mcs.core.resources.strings

class GoToDefinitionCommand(commandContext: CommandContext) : LspCommand(commandContext) {
    override val id: String = "lsp.go_to_definition"

    override fun getLabel(): String = strings.go_to_definition.getString()

    override fun action(lspActionContext: LspActionContext) {
        goToDefinition(
            scope = DefaultScope,
            context = lspActionContext.currentActivity,
            viewModel = commandContext.mainViewModel,
            editorTab = lspActionContext.editorTab,
        )
    }

    override fun isSupported(lspNonActionContext: LspNonActionContext): Boolean {
        return lspNonActionContext.lspConnector.isGoToDefinitionSupported()
    }

    override fun getIcon(): Icon = Icon.DrawableRes(drawables.jump_to_element)
}
