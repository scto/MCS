package com.scto.mcs.core.terminal

import android.content.Context

import com.scto.mcs.core.terminal.config.TerminalConfig

import dagger.hilt.android.qualifiers.ApplicationContext

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch

import java.io.BufferedReader
import java.io.File
import java.io.InputStreamReader

import javax.inject.Inject
import javax.inject.Singleton

/**
 * Führt Befehle innerhalb der PRoot-Umgebung aus.
 */
@Singleton
class TerminalService @Inject constructor(
    @ApplicationContext private val context: Context
) {

    fun execute(command: String, workingDir: String? = null): Flow<String> = callbackFlow {
        val baseDir = File(context.filesDir, TerminalConfig.TERMINAL_ROOT_DIR)
        val prootBin = File(baseDir, "${TerminalConfig.BIN_DIR}/proot")
        val rootFs = File(baseDir, TerminalConfig.ROOTFS_DIR)

        // PRoot Kommando-Konstruktion
        // -r: RootFS Pfad
        // -0: Simuliere Root-Berechtigungen
        // -b: Mount-Punkte für Android-Systeme
        val prootCmd = mutableListOf(
            prootBin.absolutePath,
            "-r", rootFs.absolutePath,
            "-0",
            "-b", "/dev",
            "-b", "/proc",
            "-b", "/sys",
            "-w", "/root",
            "/bin/bash", "--login", "-c", command
        )

        val processBuilder = ProcessBuilder(prootCmd)
            .redirectErrorStream(true)
        
        // Umgebungsvariablen setzen
        TerminalConfig.DEFAULT_ENV.forEach { (k, v) -> 
            processBuilder.environment()[k] = v 
        }

        val process = processBuilder.start()
        val reader = BufferedReader(InputStreamReader(process.inputStream))

        val job = launch(Dispatchers.IO) {
            var line: String?
            while (reader.readLine().also { line = it } != null) {
                trySend(line ?: "")
            }
            process.waitFor()
            close()
        }

        awaitClose {
            process.destroy()
            job.cancel()
        }
    }.flowOn(Dispatchers.IO)
}