package com.rk.lsp.servers

import android.content.Context
import com.rk.exec.NpmUtils
import com.rk.exec.isTerminalInstalled
import com.rk.file.BuiltinFileType
import com.rk.file.child
import com.rk.file.localBinDir
import com.rk.file.sandboxDir
import com.rk.lsp.LspConnectionConfig
import com.rk.lsp.ScriptedLspServer

object HTML : ScriptedLspServer() {
    override val id: String = "html"
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
