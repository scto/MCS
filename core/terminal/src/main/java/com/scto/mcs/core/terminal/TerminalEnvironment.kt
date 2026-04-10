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
    // Erstellt Ordnerstruktur (home, usr/bin, tmp) im internen App-Speicher
    
    fun initializeEnvironment() {
        val mcsDir = File(context.filesDir, "mcs")
        val subDirs = listOf("home", "usr/bin", "tmp")
        
        subDirs.forEach { dir ->
            File(mcsDir, dir).mkdirs()
        }
    }
    
    fun getMcsPath(): String = File(context.filesDir, "mcs").absolutePath
}
