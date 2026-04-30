package com.scto.mcs.core.terminal.session

import android.content.Context
import android.os.Build
import com.scto.mcs.app.BuildConfig
import com.scto.mcs.core.exec.PendingCommand
import com.scto.mcs.core.files.*
import com.scto.mcs.core.terminal.config.TerminalConfig
import com.scto.mcs.core.utils.getSourceDirOfPackage
import com.scto.mcs.core.utils.getTempDir
import com.scto.mcs.core.utils.isFDroid
import com.scto.mcs.feature.settings.Settings
import dagger.hilt.android.qualifiers.ApplicationContext
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Factory service to create environment configurations for terminal sessions.
 * Migrated logic from legacy MkSession.kt.
 */
@Singleton
class TerminalSessionFactory @Inject constructor(
    @ApplicationContext private val context: Context,
    private val terminalConfig: TerminalConfig
) {

    /**
     * Creates the environment variables map for a new terminal session.
     * Migrated from MkSession.kt.
     */
    fun createEnvironment(
        sessionId: String,
        workingDir: String,
        pendingEnv: Map<String, String>? = null
    ): Map<String, String> {
        val env = mutableMapOf<String, String>()

        // System environment variables
        env["ANDROID_ART_ROOT"] = System.getenv("ANDROID_ART_ROOT") ?: ""
        env["ANDROID_DATA"] = System.getenv("ANDROID_DATA") ?: ""
        env["ANDROID_I18N_ROOT"] = System.getenv("ANDROID_I18N_ROOT") ?: ""
        env["ANDROID_ROOT"] = System.getenv("ANDROID_ROOT") ?: ""
        env["ANDROID_RUNTIME_ROOT"] = System.getenv("ANDROID_RUNTIME_ROOT") ?: ""
        env["ANDROID_TZDATA_ROOT"] = System.getenv("ANDROID_TZDATA_ROOT") ?: ""
        env["BOOTCLASSPATH"] = System.getenv("BOOTCLASSPATH") ?: ""
        env["DEX2OATBOOTCLASSPATH"] = System.getenv("DEX2OATBOOTCLASSPATH") ?: ""
        env["EXTERNAL_STORAGE"] = System.getenv("EXTERNAL_STORAGE") ?: ""
        
        // Path configuration
        val localBinDir = terminalConfig.localBinDir()
        env["PATH"] = "${System.getenv("PATH")}:${localBinDir.absolutePath}"

        // MCS specific directories
        val tmpDir = File(getTempDir(), "terminal/$sessionId").apply { if (exists()) deleteRecursively(); mkdirs() }
        
        env["PROOT_TMP_DIR"] = tmpDir.absolutePath
        env["WKDIR"] = workingDir
        env["PUBLIC_HOME"] = context.getExternalFilesDir(null)?.absolutePath ?: ""
        env["COLORTERM"] = "truecolor"
        env["TERM"] = "xterm-256color"
        env["LANG"] = "C.UTF-8"
        env["DEBUG"] = BuildConfig.DEBUG.toString()
        env["LOCAL"] = terminalConfig.localDir().absolutePath
        env["PRIVATE_DIR"] = context.filesDir.parentFile?.absolutePath ?: ""
        env["LD_LIBRARY_PATH"] = terminalConfig.localLibDir().absolutePath
        env["EXT_HOME"] = terminalConfig.sandboxHomeDir().absolutePath
        env["HOME"] = if (Settings.sandbox) "/home" else terminalConfig.sandboxHomeDir().absolutePath
        env["PROMPT_DIRTRIM"] = "2"
        env["LINKER"] = if (File("/system/bin/linker64").exists()) "/system/bin/linker64" else "/system/bin/linker"
        env["NATIVE_LIB_DIR"] = context.applicationInfo.nativeLibraryDir
        env["FDROID"] = isFDroid.toString()
        env["SANDBOX"] = Settings.sandbox.toString()
        env["TMP_DIR"] = getTempDir().absolutePath
        env["TMPDIR"] = getTempDir().absolutePath
        env["TZ"] = "UTC"
        env["DOTNET_GCHeapHardLimit"] = "1C0000000"
        env["SOURCE_DIR"] = context.applicationInfo.sourceDir
        env["TERMUX_X11_SOURCE_DIR"] = getSourceDirOfPackage(context, "com.termux.x11") ?: ""
        env["DISPLAY"] = ":0"

        // Proot loader configuration
        if (!isFDroid) {
            val nativeLibDir = context.applicationInfo.nativeLibraryDir
            env["PROOT_LOADER"] = "$nativeLibDir/libproot-loader.so"
            if (Build.SUPPORTED_32_BIT_ABIS.isNotEmpty() && File(nativeLibDir).child("libproot-loader32.so").exists()) {
                env["PROOT_LOADER32"] = "$nativeLibDir/libproot-loader32.so"
            }
        }

        if (Settings.seccomp) {
            env["SECCOMP"] = "1"
        }

        // Merge pending environment variables
        pendingEnv?.let {
            env.putAll(it)
        }

        return env
    }

    /**
     * Determines the shell and arguments for a new terminal session.
     * Migrated from MkSession.kt.
     */
    fun getShellAndArgs(pendingCommand: PendingCommand?): Pair<String, Array<String>> {
        val sandboxSH = terminalConfig.localBinDir().child("sandbox")
        
        return when {
            pendingCommand == null -> {
                val a = if (Settings.sandbox) arrayOf(sandboxSH.absolutePath) else arrayOf()
                "/system/bin/sh" to a
            }
            pendingCommand.sandbox.not() -> pendingCommand.exe to pendingCommand.args
            else -> {
                val a = mutableListOf(sandboxSH.absolutePath, pendingCommand.exe, *pendingCommand.args).toTypedArray()
                "/system/bin/sh" to a
            }
        }
    }
}
