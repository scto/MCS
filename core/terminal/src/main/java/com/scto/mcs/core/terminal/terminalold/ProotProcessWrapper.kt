package com.scto.mcs.core.terminal.terminalold

import java.io.InputStream
import java.io.OutputStream

class ProotProcessWrapper(private val config: TerminalConfig) {

    private var process: Process? = null

    fun start(): Process {
        val command = if (config.useProot) {
            listOf("proot", "-r", config.workingDirectory, config.initialCommand)
        } else {
            listOf(config.initialCommand)
        }

        val processBuilder = ProcessBuilder(command)
        processBuilder.environment().putAll(config.environmentVariables)
        processBuilder.directory(java.io.File(config.workingDirectory))
        
        process = processBuilder.start()
        return process!!
    }

    fun stop() {
        process?.destroy()
    }
}
