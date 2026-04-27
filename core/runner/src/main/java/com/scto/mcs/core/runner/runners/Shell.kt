package com.scto.mcs.core.runner.runners

import android.content.Context

import com.scto.mcs.core.exec.TerminalCommand
import com.scto.mcs.core.exec.launchTerminal
import com.scto.mcs.core.files.FileObject
import com.scto.mcs.core.files.FileWrapper
import com.scto.mcs.core.ui.icons.Icon
import com.scto.mcs.core.resources.drawables
import com.scto.mcs.core.resources.strings
import com.scto.mcs.core.runner.RunnerImpl
import com.scto.mcs.core.utils.errorDialog

class Shell : RunnerImpl() {
    override suspend fun run(context: Context, fileObject: FileObject) {
        if (fileObject !is FileWrapper) {
            errorDialog(msgRes = strings.native_runner)
            return
        }

        launchTerminal(
            context,
            terminalCommand =
                TerminalCommand(
                    sandbox = true,
                    exe = "/bin/bash",
                    args = arrayOf(fileObject.getAbsolutePath()),
                    id = "shell-runner",
                    terminatePreviousSession = true,
                    workingDir = fileObject.getParentFile()?.getAbsolutePath() ?: "/",
                ),
        )
    }

    override fun getName(): String {
        return "Shell Runner"
    }

    override fun getIcon(context: Context): Icon {
        return Icon.DrawableRes(drawables.bash)
    }

    override suspend fun isRunning(): Boolean {
        return false
    }

    override suspend fun stop() {}
}
