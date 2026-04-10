package com.scto.mcs.core.terminal

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TerminalEnvironment @Inject constructor(
    @ApplicationContext private val context: Context
) {
    // Verwaltet PATH, JAVA_HOME, ANDROID_HOME
    private val envVars = mutableMapOf<String, String>()

    init {
        // Initialisiere Standard-Pfade
        val mcsDir = File(context.filesDir, "mcs")
        envVars["HOME"] = File(mcsDir, "home").absolutePath
        envVars["PATH"] = "${File(mcsDir, "usr/bin").absolutePath}:/usr/bin:/bin"
        envVars["TMPDIR"] = File(mcsDir, "tmp").absolutePath
    }

    fun initializeEnvironment() {
        val mcsDir = File(context.filesDir, "mcs")
        val subDirs = listOf("home", "usr/bin", "tmp")
        
        subDirs.forEach { dir ->
            File(mcsDir, dir).mkdirs()
        }
    }

    fun setEnv(key: String, value: String) {
        envVars[key] = value
    }

    fun getEnv(key: String): String? = envVars[key]
    
    fun getMcsPath(): String = File(context.filesDir, "mcs").absolutePath
}
