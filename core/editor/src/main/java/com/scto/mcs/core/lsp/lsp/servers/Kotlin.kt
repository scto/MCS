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

object Kotlin : ScriptedLspServer() {
    override val id: String = "kotlin"
    override val languageName: String = "kotlin"
    override val serverName = "vscode-kotlin-language-server"
    override val supportedExtensions = BuiltinFileType.KOTLIN.extensions
    override val icon = BuiltinFileType.KOTLIN.icon

    override val installScript = localBinDir().child("lsp/kotlin")
    override val installId = "Kotlin language server"

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
        return NpmUtils.hasUpdate("vscode-langservers-extracted")
    }
}