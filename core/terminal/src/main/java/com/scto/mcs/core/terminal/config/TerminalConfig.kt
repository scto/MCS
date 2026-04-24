package com.scto.mcs.core.terminal.config

import java.io.File

/**
 * Zentrale Konfiguration für das Terminal-System.
 */
object TerminalConfig {
    
    // Verzeichnisnamen
    const val TERMINAL_ROOT_DIR = "terminal"
    const val ROOTFS_DIR = "rootfs"
    const val BIN_DIR = "bin"
    
    // Debugging
    var debugLevel = DebugLevel.INFO
    var isDebugEnabled = true

    enum class DebugLevel(val priority: Int) {
        NONE(0), ERROR(1), WARN(2), INFO(3), DEBUG(4)
    }

    // Standard-Umgebungsvariablen für die Linux-Umgebung
    val DEFAULT_ENV = mapOf(
        "TERM" to "xterm-256color",
        "PATH" to "/usr/local/bin:/usr/bin:/bin:/usr/local/sbin:/usr/sbin:/sbin",
        "HOME" to "/root",
        "LANG" to "en_US.UTF-8",
        "SHELL" to "/bin/bash"
    )

    // Architektur-spezifische Konfigurationen
    data class ArchUrls(
        val prootUrl: String,
        val rootfsUrl: String,
        val liballocUrl: String
    )

    val ARCH_CONFIGS = mapOf(
        "aarch64" to ArchUrls(
            prootUrl = "https://example.com/proot-aarch64",
            rootfsUrl = "https://example.com/rootfs-aarch64.tar.xz",
            liballocUrl = "https://example.com/liballoc-aarch64.so"
        ),
        "arm" to ArchUrls(
            prootUrl = "https://example.com/proot-arm",
            rootfsUrl = "https://example.com/rootfs-arm.tar.xz",
            liballocUrl = "https://example.com/liballoc-arm.so"
        )
    )

    fun getSystemArch(): String = System.getProperty("os.arch") ?: "aarch64"
    
    // Hilfspfad-Ermittlung
    fun getRootFsDir(context: android.content.Context): File = 
        File(File(context.filesDir, TERMINAL_ROOT_DIR), ROOTFS_DIR)
}