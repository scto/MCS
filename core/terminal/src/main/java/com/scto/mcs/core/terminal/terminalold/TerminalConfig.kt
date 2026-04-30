package com.scto.mcs.core.terminal.terminalold

data class TerminalConfig(
    val initialCommand: String = "bash",
    val workingDirectory: String = "/",
    val environmentVariables: Map<String, String> = emptyMap(),
    val useProot: Boolean = true
)
