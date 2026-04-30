package com.scto.mcs.core.terminal

import com.scto.mcs.core.files.child
import com.scto.mcs.core.files.createFileIfNot
import com.scto.mcs.core.files.localBinDir
import com.scto.mcs.core.files.localDir
import com.scto.mcs.core.files.sandboxDir
import com.scto.mcs.core.utils.application

/**
 * Richtet die notwendigen Systemdateien und Skripte für die Terminal-Umgebung ein.
 */
fun setupTerminalFiles() {
    if (sandboxDir().exists().not() || localBinDir().exists().not()) return

    // Fake CPU & Memory Stats
    setupStatFile("stat", stat)
    setupStatFile("vmstat", vmstat)

    // X11 Support
    with(localBinDir().child("termux-x11")) {
        if (!exists()) {
            createFileIfNot()
            writeText(application!!.assets.open("terminal/termux-x11.sh").bufferedReader().use { it.readText() })
        }
    }

    // Basisskripte
    listOf("init", "sandbox", "setup", "utils").forEach { setupAssetFile(it) }

    // LSP Server Skripte
    application!!.assets.list("terminal/lsp")?.forEach { setupLspFile(it.removeSuffix(".sh")) }
}

private fun setupStatFile(name: String, content: String) {
    with(localDir().child(name)) {
        if (!exists()) {
            createFileIfNot()
            writeText(content)
        }
    }
}

fun setupLspFile(fileName: String) = setupAssetFile("lsp/$fileName")

fun setupAssetFile(fileName: String) {
    with(localBinDir().child(fileName)) {
        parentFile?.mkdirs()
        if (!exists()) {
            createFileIfNot()
            writeText(application!!.assets.open("terminal/$fileName.sh").bufferedReader().use { it.readText() })
        }
    }
}