package com.scto.mcs.core.terminal.config

import android.content.Context
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Zentrale Konfiguration für die Terminal-Umgebung.
 */
@Singleton
class TerminalConfig @Inject constructor() {
    
    companion object {
        const val TERMINAL_ROOT_DIR = "terminal"
        const val ROOTFS_DIR = "rootfs"
        const val BIN_DIR = "bin"
        
        var debugLevel = 1
        var isDebugEnabled = true

        val DEFAULT_ENV = mapOf(
            "PATH" to "/bin:/usr/bin",
            "HOME" to "/home"
        )
        
        val ARCH_CONFIGS = mapOf(
            "arm64-v8a" to "aarch64",
            "armeabi-v7a" to "arm"
        )

        fun getSystemArch(): String = System.getProperty("os.arch") ?: "aarch64"
        
        fun getRootFsDir(context: Context): File = 
            File(context.filesDir, "$TERMINAL_ROOT_DIR/$ROOTFS_DIR")
    }
}
