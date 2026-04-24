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

object Yaml : ScriptedLspServer() {
    override val id: String = "yaml"
    override val languageName: String = "Yaml"
    override val serverName = "vscode-language-server"
    override val supportedExtensions = BuiltinFileType.YAML.extensions
    override val icon = BuiltinFileType.YAML.icon

    override val installScript = localBinDir().child("lsp/yaml")
    override val installId = "Yaml language server"

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