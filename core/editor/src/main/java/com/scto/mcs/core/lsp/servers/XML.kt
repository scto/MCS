package com.scto.mcs.core.editor.lsp.servers

import android.content.Context
import com.scto.mcs.core.exec.isTerminalInstalled
import com.scto.mcs.core.file.BuiltinFileType
import com.scto.mcs.core.file.child
import com.scto.mcs.core.file.localBinDir
import com.scto.mcs.core.file.sandboxHomeDir
import com.scto.mcs.core.editor.lsp.LspConnectionConfig
import com.scto.mcs.core.editor.lsp.ScriptedLspServer
import com.scto.mcs.core.resources.getString
import com.scto.mcs.core.resources.strings

object XML : ScriptedLspServer() {
    override val id: String = "xml"
    override val languageName: String = strings.core_editor_lsp_server_xml_language_name.getString()
    override val serverName = "lemminx"
    override val supportedExtensions = BuiltinFileType.XML.extensions
    override val icon = BuiltinFileType.XML.icon

    override val installScript = localBinDir().child("lsp/xml")
    override val installId = strings.core_editor_lsp_server_xml_install_id.getString()

    // Has to be manually updated when a new version is released (Don't forgot to also update xml.sh)
    const val LATEST_VERSION = "0.31.0"

    override suspend fun isInstalled(context: Context): Boolean {
        if (!isTerminalInstalled()) {
            return false
        }

        return sandboxHomeDir().child(".lsp/lemminx/server.jar").exists()
    }

    override suspend fun isUpdatable(context: Context): Boolean {
        val versionFile = sandboxHomeDir().child(".lsp/lemminx/version.txt")
        val currentVersion = runCatching { versionFile.readText().trim() }.getOrNull()
        return currentVersion != LATEST_VERSION
    }

    override fun getConnectionConfig(): LspConnectionConfig {
        return LspConnectionConfig.Process(arrayOf("java", "-jar", "/home/.lsp/lemminx/server.jar"))
    }
}
