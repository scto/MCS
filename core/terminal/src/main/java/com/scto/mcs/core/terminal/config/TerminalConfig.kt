package com.scto.mcs.core.terminalold.config

import com.scto.mcs.core.terminal.config.TerminalConfig as ModernTerminalConfig
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Wrapper für die Terminal-Konfiguration, die auf die moderne Implementierung verweist.
 */
@Singleton
class TerminalConfig @Inject constructor() {
    val TERMINAL_ROOT_DIR = ModernTerminalConfig.TERMINAL_ROOT_DIR
    val ROOTFS_DIR = ModernTerminalConfig.ROOTFS_DIR
    val BIN_DIR = ModernTerminalConfig.BIN_DIR
    
    var debugLevel = ModernTerminalConfig.debugLevel
    var isDebugEnabled = ModernTerminalConfig.isDebugEnabled

    val DEFAULT_ENV = ModernTerminalConfig.DEFAULT_ENV
    val ARCH_CONFIGS = ModernTerminalConfig.ARCH_CONFIGS

    fun getSystemArch(): String = ModernTerminalConfig.getSystemArch()
    
    fun getRootFsDir(context: android.content.Context): File = 
        ModernTerminalConfig.getRootFsDir(context)
}
