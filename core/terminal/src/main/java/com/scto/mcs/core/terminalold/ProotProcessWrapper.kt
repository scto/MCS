package com.scto.mcs.core.terminalold

import android.content.Context
import com.scto.mcs.core.terminal.config.TerminalConfig
import java.io.File

/**
 * Implementierung des ProotProcessWrapper unter Verwendung der modernen TerminalConfig.
 */
class ProotProcessWrapper private constructor(
    private val context: Context,
    private val workingDirectory: File?,
    private val bindMounts: List<BindMount>,
    private val environment: Map<String, String>,
    private val includeStandardBinds: Boolean,
    private val redirectErrorStream: Boolean
) {

    data class BindMount(val source: String, val target: String? = null) {
        fun toProotArg(): String {
            return if (target != null) "--bind=$source:$target" else "--bind=$source"
        }
    }

    companion object {
        private val STANDARD_BINDS = listOf("/dev", "/proc", "/sys")
    }

    fun start(vararg command: String): Process {
        if (command.isEmpty()) throw IllegalArgumentException("Kommando darf nicht leer sein")

        val baseDir = File(context.filesDir, TerminalConfig.TERMINAL_ROOT_DIR)
        val prootExe = File(File(baseDir, TerminalConfig.BIN_DIR), "proot")
        val rootfsDir = File(baseDir, TerminalConfig.ROOTFS_DIR)

        if (!prootExe.exists()) throw IllegalStateException("PRoot Binärdatei nicht gefunden")
        if (!rootfsDir.exists()) rootfsDir.mkdirs()

        val args = mutableListOf<String>()
        args.add(prootExe.absolutePath)
        args.add("--rootfs=${rootfsDir.absolutePath}")
        
        workingDirectory?.let { args.add("--cwd=${it.absolutePath}") }
        if (includeStandardBinds) STANDARD_BINDS.forEach { args.add("--bind=$it") }
        bindMounts.forEach { args.add(it.toProotArg()) }
        args.addAll(command)

        return ProcessBuilder(args)
            .directory(workingDirectory ?: context.filesDir)
            .redirectErrorStream(redirectErrorStream)
            .apply { environment().putAll(environment) }
            .start()
    }

    class Builder(private val context: Context) {
        private var workingDirectory: File? = null
        private val bindMounts = mutableListOf<BindMount>()
        private val environment = mutableMapOf<String, String>()
        private var includeStandardBinds = true
        private var redirectErrorStream = false

        fun setWorkingDirectory(dir: File) = apply { this.workingDirectory = dir }
        fun addBindMount(source: String, target: String? = null) = apply { this.bindMounts.add(BindMount(source, target)) }
        fun addEnvironment(key: String, value: String) = apply { this.environment[key] = value }
        fun setRedirectErrorStream(redirect: Boolean) = apply { this.redirectErrorStream = redirect }

        fun build(): ProotProcessWrapper {
            return ProotProcessWrapper(context, workingDirectory, bindMounts, environment, includeStandardBinds, redirectErrorStream)
        }
    }
}
