package com.scto.mcs.core.commands.lsp

import com.scto.mcs.app.scope.DefaultScope
import com.scto.mcs.core.commands.CommandContext
import com.scto.mcs.core.commands.LspActionContext
import com.scto.mcs.core.commands.LspCommand
import com.scto.mcs.core.commands.LspNonActionContext
import com.scto.mcs.core.ui.icons.Icon
import com.scto.mcs.core.editor.lsp.formatDocument
import com.scto.mcs.core.resources.drawables
import com.scto.mcs.core.resources.getString
import com.scto.mcs.core.resources.strings

class FormatDocumentCommand(commandContext: CommandContext) : LspCommand(commandContext) {
    override val id: String = "lsp.format_document"

    override fun getLabel(): String = strings.format_document.getString()

    override fun action(lspActionContext: LspActionContext) {
        formatDocument(DefaultScope, lspActionContext.editorTab)
    }

    override fun isSupported(lspNonActionContext: LspNonActionContext): Boolean {
        return lspNonActionContext.lspConnector.isFormattingSupported()
    }

    override fun getIcon(): Icon = Icon.DrawableRes(drawables.auto_fix)
}
