package com.scto.mcs.core.editor.lsp.servers

import android.content.Context
import com.scto.mcs.core.exec.PipxUtils
import com.scto.mcs.core.exec.isTerminalInstalled
import com.scto.mcs.core.file.BuiltinFileType
import com.scto.mcs.core.file.child
import com.scto.mcs.core.file.localBinDir
import com.scto.mcs.core.file.sandboxHomeDir
import com.scto.mcs.core.editor.lsp.LspConnectionConfig
import com.scto.mcs.core.editor.lsp.LspConnector
import com.scto.mcs.core.editor.lsp.ScriptedLspServer
import com.scto.mcs.core.resources.getString
import com.scto.mcs.core.resources.strings
import org.eclipse.lsp4j.DidChangeConfigurationParams

object Python : ScriptedLspServer() {
    override val id: String = "python"
    override val languageName: String = strings.core_editor_lsp_server_python_language_name.getString()
    override val serverName = "python-lsp-server"
    override val supportedExtensions = BuiltinFileType.PYTHON.extensions
    override val icon = BuiltinFileType.PYTHON.icon

    override val installScript = localBinDir().child("lsp/python")
    override val installId = strings.core_editor_lsp_server_python_install_id.getString()

    override suspend fun isInstalled(context: Context): Boolean {
        if (!isTerminalInstalled()) {
            return false
        }

        return sandboxHomeDir().child(".local/share/pipx/venvs/python-lsp-server/bin/pylsp").exists()
    }

    override suspend fun isUpdatable(context: Context): Boolean {
        return PipxUtils.hasUpdate(serverName)
    }

    override fun getConnectionConfig(): LspConnectionConfig {
        return LspConnectionConfig.Process(arrayOf("/home/.local/share/pipx/venvs/python-lsp-server/bin/pylsp"))
    }

    override suspend fun onInitialize(lspConnector: LspConnector) {
        val requestManager = lspConnector.lspEditor!!.requestManager

        val params =
            DidChangeConfigurationParams(
                mapOf(
                    "pylsp" to
                        mapOf(
                            "plugins" to
                                mapOf(
                                    "pycodestyle" to
                                        mapOf(
                                            "enabled" to true,
                                            "ignore" to listOf("E501", "W291", "W293"),
                                            "maxLineLength" to 999,
                                        )
                                )
                        )
                )
            )

        requestManager.didChangeConfiguration(params)
    }
}
