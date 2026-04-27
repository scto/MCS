package com.scto.mcs.core.commands.lsp

import com.scto.mcs.app.scope.DefaultScope
import com.scto.mcs.core.commands.CommandContext
import com.scto.mcs.core.commands.LspActionContext
import com.scto.mcs.core.commands.LspCommand
import com.scto.mcs.core.commands.LspNonActionContext
import com.scto.mcs.core.ui.icons.Icon
import com.scto.mcs.core.editor.lsp.formatDocumentRange
import com.scto.mcs.core.resources.drawables
import com.scto.mcs.core.resources.getString
import com.scto.mcs.core.resources.strings

class FormatSelectionCommand(commandContext: CommandContext) : LspCommand(commandContext) {
    override val id: String = "lsp.format_selection"

    override fun getLabel(): String = strings.format_selection.getString()

    override fun action(lspActionContext: LspActionContext) {
        formatDocumentRange(DefaultScope, lspActionContext.editorTab)
    }

    override fun isSupported(lspNonActionContext: LspNonActionContext): Boolean {
        return lspNonActionContext.lspConnector.isRangeFormattingSupported()
    }

    override fun getIcon(): Icon = Icon.DrawableRes(drawables.auto_fix)
}
