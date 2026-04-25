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

object JSON : ScriptedLspServer() {
    override val id: String = "json"
    override val languageName: String = "JSON"
    override val serverName = "vscode-json-language-server"
    override val supportedExtensions = BuiltinFileType.JSON.extensions
    override val icon = BuiltinFileType.JSON.icon

    override val installScript = localBinDir().child("lsp/json")
    override val installId = "JSON language server"

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
