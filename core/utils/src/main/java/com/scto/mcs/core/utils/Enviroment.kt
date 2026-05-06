package com.mcs.core.utils

import android.annotation.SuppressLint
import android.content.Context
import java.io.File
import java.util.UUID

/**
 * Zentrale Pfadverwaltung für MobileCodeSpace.
 * Beinhaltet nun auch Pfade für die Language Server (LSP).
 */
@SuppressLint("SdCardPath")
object Environment {
    private lateinit var filesDir: String
    private lateinit var packageName: String

    // Basis-Verzeichnisse
    val PRE_ROOT by lazy { filesDir.substringBeforeLast("/files") }
    val FILES by lazy { filesDir }
    val HOME by lazy { "$filesDir/home" }
    val USR by lazy { "$filesDir/usr" }
    val DISTROS by lazy { "$filesDir/distros" }
    val ROOTFS by lazy { "$DISTROS/rootfs" }
    val SCRIPTS by lazy { "$filesDir/scripts" }
    val TMP_DIR by lazy { "$filesDir/tmp" }
    val BIN_DIR by lazy { "$USR/bin" }
    val LIB_DIR by lazy { "$USR/lib" }

    // SDK & Tooling Pfade
    val ANDROID_HOME by lazy { "$HOME/android-sdk" }
    val ANDROID_CMDLINE_TOOLS by lazy { "$ANDROID_HOME/cmdline-tools" }
    val JAVA_HOME by lazy { "$ROOTFS/lib/jvm_home/openjdk" }
    val PRE_JAVA_HOME by lazy { "$USR/jvm" }
    val GRADLE_USER_HOME by lazy { "$HOME/.gradle" }
    val ANDROID_USER_HOME by lazy { "$HOME/.android" }
    val MOBILECODESPACE_HOME by lazy { "$HOME/.mobilecodespace" }
    val AAPT2 by lazy { "$MOBILECODESPACE_HOME/aapt2" }

    // LSP Server Pfade (MobileCodeSpace spezifisch)
    val LSP_DIR by lazy { "$USR/lsp" }
    val KOTLIN_LSP by lazy { "$LSP_DIR/kotlin/bin/kotlin-language-server" }
    val JAVA_LSP by lazy { "$LSP_DIR/java/bin/jdtls" }
    
    // Externer Speicher
    val PROJECTS_DIR = "/sdcard/MobileCodeSpaceProjects"
    
    var archApp: String = "64"
    val envVars = mutableMapOf<String, String>()

    fun init(context: Context) {
        filesDir = context.filesDir.absolutePath
        packageName = context.packageName
        archApp = if (FileUtils.isAarch64()) "64" else "32"
        
        val requiredDirs = listOf(
            HOME, "$HOME/@Projects", "$HOME/@Icons",
            "$TMP_DIR/build", "$TMP_DIR/proot", "$TMP_DIR/bin", "$TMP_DIR/download", "$TMP_DIR/rootfs",
            PRE_JAVA_HOME, MOBILECODESPACE_HOME, BIN_DIR, LIB_DIR, SCRIPTS, DISTROS, LSP_DIR
        )
        
        requiredDirs.forEach { FileUtils.createDirectory(it) }
        
        // Ausführungsrechte setzen
        FileUtils.setFileExecutable("$TMP_DIR/proot")
        FileUtils.setFileExecutable("$TMP_DIR/bin")
        
        updateSystemProperties()
    }

    fun getEnvironment(projectPath: String = "", isCompile: Boolean = false): Map<String, String> {
        envVars.clear()
        
        System.getenv("PATH")?.let { envVars["PATH"] = "$BIN_DIR:$it" }
        System.getenv("BOOTCLASSPATH")?.let { envVars["BOOTCLASSPATH"] = it }
        
        envVars["FILES"] = FILES
        envVars["PREFIX"] = USR
        envVars["HOME"] = HOME
        envVars["DISTROS"] = DISTROS
        envVars["ROOTFS"] = ROOTFS
        envVars["SCRIPTS"] = SCRIPTS
        envVars["JAVA_HOME"] = JAVA_HOME
        envVars["ANDROID_HOME"] = ANDROID_HOME
        envVars["GRADLE_USER_HOME"] = GRADLE_USER_HOME
        envVars["TMPDIR"] = TMP_DIR
        envVars["ARCH_APP"] = archApp
        envVars["LD_LIBRARY_PATH"] = LIB_DIR
        
        val utf8 = "C.UTF-8"
        envVars["LANG"] = utf8
        envVars["LC_ALL"] = utf8
        
        if (projectPath.isNotEmpty()) envVars["pjct"] = projectPath
        return envVars
    }

    private fun updateSystemProperties() {
        getEnvironment().forEach { (k, v) -> System.setProperty(k, v) }
    }
}