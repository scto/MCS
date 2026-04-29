package com.scto.mcs.core.editor.lsp.servers

import android.content.Context
import com.scto.mcs.core.exec.NpmUtils
import com.scto.mcs.core.exec.isTerminalInstalled
import com.scto.mcs.core.file.BuiltinFileType
import com.scto.mcs.core.file.child
import com.scto.mcs.core.file.localBinDir
import com.scto.mcs.core.file.sandboxDir
import com.scto.mcs.core.editor.lsp.LspConnectionConfig
import com.scto.mcs.core.editor.lsp.ScriptedLspServer
import com.scto.mcs.core.resources.getString
import com.scto.mcs.core.resources.strings

object HTML : ScriptedLspServer() {
    override val id: String = "html"
    override val languageName: String = strings.core_editor_lsp_server_html_language_name.getString()
    override val languageName: String = strings.core_editor_lsp_server_html_language_name.getString()
    override val serverName = "vscode-html-language-server"
    override val supportedExtensions = BuiltinFileType.HTML.extensions + BuiltinFileType.HTMX.extensions
    override val icon = BuiltinFileType.HTML.icon

    override val installScript = localBinDir().child("lsp/html")
    override val installId = strings.core_editor_lsp_server_html_install_id.getString()

    override suspend fun isInstalled(context: Context): Boolean {
        if (!isTerminalInstalled()) {
            return false
        }

        return sandboxDir().child("/usr/bin/$serverName").exists()
    }

    override suspend fun isUpdatable(context: Context): Boolean {
        return NpmUtils.hasUpdate("vscode-langservers-extracted")
    }

    override fun getConnectionConfig(): LspConnectionConfig {
        return LspConnectionConfig.Process(arrayOf("/usr/bin/node", "/usr/bin/$serverName", "--stdio"))
    }
}
