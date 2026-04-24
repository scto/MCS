package com.srvhive.app.utils

import android.content.Context
import android.util.Log
import java.io.File
import java.util.*

/**
 * Hilfsklasse zum Starten von Prozessen innerhalb einer PRoot-Umgebung.
 * Erlaubt das "Mounten" von Verzeichnissen und das Setzen von Umgebungsvariablen.
 */
class ProotProcessWrapper private constructor(
    private val context: Context,
    private val workingDirectory: File?,
    private val bindMounts: List<BindMount>,
    private val environment: Map<String, String>,
    private val includeStandardBinds: Boolean,
    private val redirectErrorStream: Boolean
) {

    data class BindMount(val source: String, val target: String? = null) {
        fun toProotArg(): String {
            return if (target != null) "--bind=$source:$target" else "--bind=$source"
        }
    }

    companion object {
        private const val TAG = "ProotWrapper"
        private val STANDARD_BINDS = listOf("/dev", "/proc", "/sys")

        /**
         * Singleton-ähnlicher Zugriff auf Pfade der IDE-Umgebung.
         */
        object IDEEnvironment {
            fun getBinDir(context: Context) = File(context.filesDir, "bin")
            fun getRootfsDir(context: Context) = File(context.filesDir, "rootfs")
            fun getHomeDir(context: Context) = File(context.filesDir, "home")
            fun getAndroidSdkDir(context: Context) = File(context.filesDir, "sdk")
        }
    }

    /**
     * Startet den Prozess mit den konfigurierten Parametern.
     */
    fun start(vararg command: String): Process {
        if (command.isEmpty()) throw IllegalArgumentException("Kommando darf nicht leer sein")

        val prootExe = File(IDEEnvironment.getBinDir(context), "proot")
        val rootfsDir = IDEEnvironment.getRootfsDir(context)

        if (!prootExe.exists()) throw IllegalStateException("PRoot Binärdatei nicht gefunden unter: ${prootExe.absolutePath}")
        if (!rootfsDir.exists()) rootfsDir.mkdirs()

        val fullCommand = buildCommand(prootExe, rootfsDir, command.toList())
        
        Log.d(TAG, "Starte PRoot: ${fullCommand.take(5).joinToString(" ")} ...")

        return ProcessBuilder(fullCommand)
            .directory(workingDirectory ?: context.filesDir)
            .redirectErrorStream(redirectErrorStream)
            .apply {
                // Umgebungsvariablen setzen
                environment().putAll(environment)
                environment()["HOME"] = "/root"
                environment()["PATH"] = "/usr/bin:/usr/sbin:/bin:/sbin"
            }
            .start()
    }

    private fun buildCommand(proot: File, rootfs: File, userCommand: List<String>): List<String> {
        val args = mutableListOf<String>()
        
        args.add(proot.absolutePath)
        args.add("--rootfs=${rootfs.absolutePath}")
        
        workingDirectory?.let {
            args.add("--cwd=${it.absolutePath}")
        }

        if (includeStandardBinds) {
            STANDARD_BINDS.forEach { args.add("--bind=$it") }
        }

        bindMounts.forEach { args.add(it.toProotArg()) }
        
        args.addAll(userCommand)
        return args
    }

    /**
     * Builder-Klasse für eine komfortable Konfiguration.
     */
    class Builder(private val context: Context) {
        private var workingDirectory: File? = null
        private val bindMounts = mutableListOf<BindMount>()
        private val environment = mutableMapOf<String, String>()
        private var includeStandardBinds = true
        private var redirectErrorStream = false

        fun setWorkingDirectory(dir: File): Builder {
            this.workingDirectory = dir
            return this
        }

        fun addBindMount(source: String, target: String? = null): Builder {
            this.bindMounts.add(BindMount(source, target))
            return this
        }

        fun addEnvironment(key: String, value: String): Builder {
            this.environment[key] = value
            return this
        }

        /**
         * Mountet wichtige Verzeichnisse für die Android-Entwicklung (SDK, Gradle).
         */
        fun bindAndroidDevelopmentDirs(): Builder {
            val sdkDir = IDEEnvironment.getAndroidSdkDir(context)
            if (sdkDir.exists()) {
                addBindMount(sdkDir.absolutePath, "/opt/android-sdk")
            }
            
            val home = IDEEnvironment.getHomeDir(context)
            if (!home.exists()) home.mkdirs()
            addBindMount(home.absolutePath, "/root")
            
            // Zugriff auf den öffentlichen Speicher (Downloads/Projekte)
            val storage = File("/storage/emulated/0")
            if (storage.exists()) {
                addBindMount(storage.absolutePath, "/sdcard")
            }
            
            return this
        }

        fun setRedirectErrorStream(redirect: Boolean): Builder {
            this.redirectErrorStream = redirect
            return this
        }

        fun build(): ProotProcessWrapper {
            return ProotProcessWrapper(
                context, 
                workingDirectory, 
                bindMounts, 
                environment, 
                includeStandardBinds, 
                redirectErrorStream
            )
        }
    }
}