package com.scto.mcs.core.terminal

import android.os.Build
import com.scto.mcs.app.BuildConfig
import com.scto.mcs.app.ui.activities.main.MainActivity
import com.scto.mcs.app.ui.activities.terminal.TerminalActivity
import com.scto.mcs.core.editor.tabs.editor.EditorTab
import com.scto.mcs.core.exec.pendingCommand
import com.scto.mcs.core.files.*
import com.scto.mcs.core.utils.getSourceDirOfPackage
import com.scto.mcs.core.utils.getTempDir
import com.scto.mcs.core.utils.isFDroid
import com.scto.mcs.feature.settings.Settings
import com.scto.mcs.feature.settings.SettingsViewModel

import com.termux.terminal.TerminalSession
import com.termux.terminal.TerminalSessionClient

import java.io.File
import kotlinx.coroutines.runBlocking

object MkSession {
    /**
     * Erstellt eine neue Termux TerminalSession mit MCS-spezifischen Umgebungsvariablen.
     */
    fun createSession(
        activity: TerminalActivity,
        sessionClient: TerminalSessionClient,
        sessionId: String,
    ): Pair<TerminalSession, SessionPwd> {
        with(activity) {
            val envVariables = mapOf(
                "ANDROID_ART_ROOT" to System.getenv("ANDROID_ART_ROOT"),
                "ANDROID_DATA" to System.getenv("ANDROID_DATA"),
                "ANDROID_I18N_ROOT" to System.getenv("ANDROID_I18N_ROOT"),
                "ANDROID_ROOT" to System.getenv("ANDROID_ROOT"),
                "ANDROID_RUNTIME_ROOT" to System.getenv("ANDROID_RUNTIME_ROOT"),
                "ANDROID_TZDATA_ROOT" to System.getenv("ANDROID_TZDATA_ROOT"),
                "BOOTCLASSPATH" to System.getenv("BOOTCLASSPATH"),
                "DEX2OATBOOTCLASSPATH" to System.getenv("DEX2OATBOOTCLASSPATH"),
                "EXTERNAL_STORAGE" to System.getenv("EXTERNAL_STORAGE"),
                "PATH" to "${System.getenv("PATH")}:${localBinDir().absolutePath}",
            )

            val workingDir = runBlocking { getPwd(activity) }
            val tmpDir = File(getTempDir(), "terminal/$sessionId").apply { if (exists()) deleteRecursively(); mkdirs() }

            val env = mutableListOf(
                "PROOT_TMP_DIR=${tmpDir.absolutePath}",
                "WKDIR=$workingDir",
                "PUBLIC_HOME=${getExternalFilesDir(null)?.absolutePath}",
                "COLORTERM=truecolor",
                "TERM=xterm-256color",
                "LANG=C.UTF-8",
                "DEBUG=${BuildConfig.DEBUG}",
                "LOCAL=${localDir().absolutePath}",
                "PRIVATE_DIR=${filesDir.parentFile!!.absolutePath}",
                "LD_LIBRARY_PATH=${localLibDir().absolutePath}",
                "EXT_HOME=${sandboxHomeDir()}",
                "HOME=${if (Settings.sandbox) "/home" else sandboxHomeDir().absolutePath}",
                "PROMPT_DIRTRIM=2",
                "LINKER=${if(File("/system/bin/linker64").exists()) "/system/bin/linker64" else "/system/bin/linker"}",
                "NATIVE_LIB_DIR=${applicationInfo.nativeLibraryDir}",
                "FDROID=$isFDroid",
                "SANDBOX=${Settings.sandbox}",
                "TMP_DIR=${getTempDir()}",
                "TMPDIR=${getTempDir()}",
                "TZ=UTC",
                "DOTNET_GCHeapHardLimit=1C0000000",
                "SOURCE_DIR=${applicationInfo.sourceDir}",
                "TERMUX_X11_SOURCE_DIR=${getSourceDirOfPackage(applicationContext, "com.termux.x11")}",
                "DISPLAY=:0",
            )

            if (!isFDroid) {
                env.add("PROOT_LOADER=${applicationInfo.nativeLibraryDir}/libproot-loader.so")
                if (Build.SUPPORTED_32_BIT_ABIS.isNotEmpty() && File(applicationInfo.nativeLibraryDir).child("libproot-loader32.so").exists()) {
                    env.add("PROOT_LOADER32=${applicationInfo.nativeLibraryDir}/libproot-loader32.so")
                }
            }

            if (Settings.seccomp) env.add("SECCOMP=1")
            env.addAll(envVariables.map { "${it.key}=${it.value}" })
            pendingCommand?.env?.let { env.addAll(it) }

            setupTerminalFiles()

            val sandboxSH = localBinDir().child("sandbox")
            val setupSH = localBinDir().child("setup")

            val (shell, args) = when {
                pendingCommand == null -> {
                    val a = if (Settings.sandbox) arrayOf(sandboxSH.absolutePath) else arrayOf()
                    "/system/bin/sh" to a
                }
                pendingCommand!!.sandbox.not() -> pendingCommand!!.exe to pendingCommand!!.args
                else -> {
                    val a = mutableListOf(sandboxSH.absolutePath, pendingCommand!!.exe, *pendingCommand!!.args).toTypedArray()
                    "/system/bin/sh" to a
                }
            }

            // Installations-Check (Stage-Abhängig)
            // Hier sollte installNextStage über ein Repository oder State-Flow geladen werden
            val actualShell = shell
            val actualArgs = arrayOf("-c", *args)

            pendingCommand = null

            return TerminalSession(
                actualShell,
                localDir().absolutePath,
                actualArgs,
                env.toTypedArray(),
                TerminalEmulator.DEFAULT_TERMINAL_TRANSCRIPT_ROWS,
                sessionClient,
            ) to workingDir
        }
    }

    private suspend fun getPwd(activity: TerminalActivity): String {
        pendingCommand?.workingDir?.let { return it }
        if (activity.intent.hasExtra("cwd")) return activity.intent.getStringExtra("cwd")!!

        val currentTab = MainActivity.instance?.viewModel?.tabManager?.currentTab
        if (Settings.project_as_pwd) {
            currentTab?.let {
                if (it is EditorTab && it.file is FileWrapper) {
                    val parent = it.file.getParentFile()
                    if (parent is FileWrapper) {
                        return if (Settings.sandbox) {
                            parent.getAbsolutePath().removePrefix(localDir().absolutePath)
                        } else {
                            parent.getAbsolutePath()
                        }
                    }
                }
            }
        }
        return if (Settings.sandbox) "/home" else sandboxHomeDir().absolutePath
    }
}