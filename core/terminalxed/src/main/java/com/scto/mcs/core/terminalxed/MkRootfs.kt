package com.scto.mcs.core.terminal

import android.content.Context

import com.scto.mcs.core.files.child
import com.scto.mcs.core.files.sandboxDir
import com.scto.mcs.core.files.sandboxHomeDir
import com.scto.mcs.core.utils.getTempDir
import com.scto.mcs.core.utils.isMainThread

import java.io.File

import kotlinx.coroutines.CoroutineScope

enum class NEXT_STAGE {
    NONE,
    EXTRACTION,
}

/**
 * Ermittelt die nächste Phase der Terminal-Installation.
 * Verhindert IO-Operationen auf dem Main-Thread.
 */
suspend fun CoroutineScope.getNextStage(context: Context): NEXT_STAGE {
    if (isMainThread()) {
        throw RuntimeException("IO-Operation auf dem Main-Thread erkannt!")
    }

    val sandboxFile = File(getTempDir(), "sandbox.tar.gz")
    val rootfsFiles =
        sandboxDir().listFiles()?.filter {
            it.absolutePath != sandboxHomeDir().absolutePath &&
                it.absolutePath != sandboxDir().child("tmp").absolutePath
        } ?: emptyList()

    return if (sandboxFile.exists().not() || rootfsFiles.isEmpty().not()) {
        NEXT_STAGE.NONE
    } else {
        NEXT_STAGE.EXTRACTION
    }
}