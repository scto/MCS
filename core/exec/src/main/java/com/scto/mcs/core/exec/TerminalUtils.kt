package com.scto.mcs.core.exec

import android.content.Context
import android.content.Intent

import com.scto.mcs.app.activities.MainActivity
import com.scto.mcs.app.activities.TerminalActivity
import com.scto.mcs.core.files.child
import com.scto.mcs.core.files.localDir
import com.scto.mcs.core.files.sandboxDir
import com.scto.mcs.core.files.sandboxHomeDir
import com.scto.mcs.core.utils.showTerminalNotice

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

fun isTerminalInstalled(): Boolean {
    val rootfs =
        sandboxDir().listFiles()?.filter {
            it.absolutePath != sandboxHomeDir().absolutePath &&
                it.absolutePath != sandboxDir().child("tmp").absolutePath
        } ?: emptyList()

    return localDir().child(".terminal_setup_ok_DO_NOT_REMOVE").exists() && rootfs.isNotEmpty()
}

suspend fun isTerminalWorking(): Boolean =
    withContext(Dispatchers.IO) {
        val process = ubuntuProcess(command = arrayOf("true"))
        return@withContext process.waitFor() == 0
    }

fun launchTerminal(context: Context, terminalCommand: TerminalCommand) {
    showTerminalNotice(activity = MainActivity.instance!!) {
        pendingCommand = terminalCommand
        context.startActivity(Intent(context, Terminal::class.java))
    }
}
