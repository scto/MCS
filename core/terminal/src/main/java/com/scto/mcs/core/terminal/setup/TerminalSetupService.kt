package com.scto.mcs.core.terminal.setup

import android.content.Context
import com.scto.mcs.core.domain.repository.DownloadRepository
import com.scto.mcs.core.domain.repository.DownloadStatus
import com.scto.mcs.core.terminal.config.TerminalConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import org.apache.commons.compress.archivers.tar.TarArchiveInputStream
import org.apache.commons.compress.compressors.xz.XZCompressorInputStream
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.math.pow

/**
 * Orchestriert den Setup-Prozess mit robuster Fehlerbehandlung und Retries.
 */
@Singleton
class TerminalSetupService @Inject constructor(
    private val downloadRepository: DownloadRepository
) {

    /**
     * Führt das vollständige Setup aus. Nutzt Exponential Backoff für Downloads.
     */
    fun runFullSetup(context: Context): Flow<SetupState> = flow {
        val arch = TerminalConfig.getSystemArch()
        val config = TerminalConfig.ARCH_CONFIGS[arch] ?: TerminalConfig.ARCH_CONFIGS["aarch64"]!!
        
        val baseDir = File(context.filesDir, TerminalConfig.TERMINAL_ROOT_DIR).apply { mkdirs() }
        val binDir = File(baseDir, TerminalConfig.BIN_DIR).apply { mkdirs() }
        val rootFsDir = File(baseDir, TerminalConfig.ROOTFS_DIR).apply { mkdirs() }
        
        // 1. Download PRoot mit Retry-Logik
        val prootFile = File(binDir, "proot")
        downloadWithRetries("PRoot", config.prootUrl, prootFile)
            .collect { state -> 
                if (state is SetupState.Downloading && state.progress == 1f) prootFile.setExecutable(true)
                emit(state) 
            }

        // 2. Download RootFS mit Retry-Logik
        val archiveFile = File(baseDir, "rootfs.tar.xz")
        downloadWithRetries("RootFS", config.rootfsUrl, archiveFile)
            .collect { state ->
                if (state is SetupState.Downloading && state.progress == 1f) {
                    emit(SetupState.Extracting("Entpacke Linux-Dateisystem..."))
                    extractTarXz(archiveFile, rootFsDir)
                    archiveFile.delete()
                } else {
                    emit(state)
                }
            }

        // 3. Initialisierung der Shell
        emit(SetupState.Initializing("Konfiguriere Umgebung..."))
        initializeShellEnv(rootFsDir)

        emit(SetupState.Completed)
    }.catch { e ->
        emit(SetupState.Failed(e.message ?: "Ein unerwarteter Fehler ist aufgetreten."))
    }.flowOn(Dispatchers.IO)

    /**
     * Hilfsfunktion für Downloads mit automatischen Wiederholungen bei Fehlern.
     */
    private fun downloadWithRetries(name: String, url: String, target: File): Flow<SetupState> = 
        downloadRepository.downloadFile(url, target)
            .map { status ->
                when (status) {
                    is DownloadStatus.Progress -> SetupState.Downloading(name, status.percentage)
                    is DownloadStatus.Success -> SetupState.Downloading(name, 1f)
                    is DownloadStatus.Error -> throw IOException(status.message)
                }
            }
            .retryWhen { cause, attempt ->
                if (cause is IOException && attempt < 3) {
                    val waitTime = 2.0.pow(attempt.toDouble()).toLong() * 1000
                    // Informiere die UI über den Retry-Versuch
                    emit(SetupState.Retrying(name, attempt.toInt() + 1, waitTime))
                    delay(waitTime)
                    true
                } else {
                    false
                }
            }

    private fun initializeShellEnv(rootFsDir: File) {
        val rootHome = File(rootFsDir, "root").apply { mkdirs() }
        File(rootHome, ".bashrc").writeText("""
            export TERM=xterm-256color
            export PS1='\[\e[32m\]\u@ide\[\e[m\]:\[\e[34m\]\w\[\e[m\]\$ '
            alias ls='ls --color=auto'
            export PATH=${TerminalConfig.DEFAULT_ENV["PATH"]}
        """.trimIndent())
        
        File(rootHome, ".profile").writeText("if [ -f ~/.bashrc ]; then . ~/.bashrc; fi")
    }

    private fun extractTarXz(archive: File, targetDir: File) {
        FileInputStream(archive).use { fis ->
            XZCompressorInputStream(fis).use { xzis ->
                TarArchiveInputStream(xzis).use { tais ->
                    var entry = tais.nextTarEntry
                    while (entry != null) {
                        val outputFile = File(targetDir, entry.name)
                        if (entry.isDirectory) {
                            outputFile.mkdirs()
                        } else {
                            outputFile.parentFile?.mkdirs()
                            FileOutputStream(outputFile).use { tais.copyTo(it) }
                            if (entry.mode and 0x40 != 0) outputFile.setExecutable(true)
                        }
                        entry = tais.nextTarEntry
                    }
                }
            }
        }
    }

    sealed class SetupState {
        data class Downloading(val item: String, val progress: Float) : SetupState()
        data class Retrying(val item: String, val attempt: Int, val waitTimeMs: Long) : SetupState()
        data class Extracting(val message: String) : SetupState()
        data class Initializing(val message: String) : SetupState()
        object Completed : SetupState()
        data class Failed(val error: String) : SetupState()
    }
}