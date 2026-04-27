package com.scto.mcs.core.runner.runners

import android.annotation.SuppressLint
import android.content.Context
import android.os.Environment

import com.scto.mcs.app.scope.DefaultScope
import com.scto.mcs.core.exec.TerminalCommand
import com.scto.mcs.core.exec.launchTerminal
import com.scto.mcs.core.files.FileObject
import com.scto.mcs.core.files.FileWrapper
import com.scto.mcs.core.files.child
import com.scto.mcs.core.files.localBinDir
import com.scto.mcs.core.ui.icons.Icon
import com.scto.mcs.core.resources.drawables
import com.scto.mcs.core.resources.getString
import com.scto.mcs.core.resources.strings
import com.scto.mcs.core.runner.RunnerImpl
import com.scto.mcs.core.terminal.setupAssetFile
import com.scto.mcs.core.utils.dialog

import kotlinx.coroutines.launch

class UniversalRunner : RunnerImpl() {
    @SuppressLint("SdCardPath")
    override suspend fun run(context: Context, fileObject: FileObject) {
        setupAssetFile("universal_runner")

        if (fileObject !is FileWrapper) {
            dialog(title = strings.attention.getString(), msg = strings.non_native_filetype.getString(), onOk = {})
            return
        }

        val path = fileObject.getAbsolutePath()
        if (
            path.startsWith("/sdcard") ||
                path.startsWith("/storage/") ||
                path.startsWith(Environment.getExternalStorageDirectory().absolutePath)
        ) {
            dialog(
                title = strings.attention.getString(),
                msg = strings.sdcard_filetype.getString(),
                okString = strings.continue_action,
                onCancel = {},
                onOk = { DefaultScope.launch { launchUniversalRunner(context, fileObject) } },
            )
            return
        }

        launchUniversalRunner(context, fileObject)
    }

    suspend fun launchUniversalRunner(context: Context, fileObject: FileObject) {
        launchTerminal(
            context,
            terminalCommand =
                TerminalCommand(
                    sandbox = true,
                    exe = "/bin/bash",
                    args = arrayOf(localBinDir().child("universal_runner").absolutePath, fileObject.getAbsolutePath()),
                    id = "universal_runner",
                    terminatePreviousSession = true,
                    workingDir = fileObject.getParentFile()?.getAbsolutePath() ?: "/",
                ),
        )
    }

    override fun getName(): String {
        return strings.universal_runner.getString()
    }

    override fun getIcon(context: Context): Icon {
        return Icon.DrawableRes(drawables.run)
    }

    override suspend fun isRunning(): Boolean {
        return false
    }

    override suspend fun stop() {}
}
