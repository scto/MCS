package com.scto.mcs.core.utils

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Environment
import android.provider.Settings

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import androidx.core.net.toUri

object PermissionManager {

    fun hasAllFilesAccess(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            Environment.isExternalStorageManager()
        } else {
            true
        }
    }

    // Kompatibel mit Legacy-Codeaufrufen
    fun hasRequiredPermissions(context: Context): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            hasAllFilesAccess()
        } else {
            hasBasicStoragePermission(context)
        }
    }

    /**
      * ✅ Intelligente Erkennung: Gibt an, ob für den angegebenen Pfad Systemberechtigungen erforderlich sind.
      * Privates Verzeichnis (Android/data/...) -> Nicht erforderlich -> Gibt false zurück.
      * Öffentliches Verzeichnis (SDCard/...) -> Erforderlich -> Gibt true zurück.
    */
    fun isSystemPermissionRequiredForPath(context: Context, path: String): Boolean {
        // Den Stammverzeichnispfad des privaten Verzeichnisses abrufen .../Android/data/Paketname
        val appExternalDir = context.getExternalFilesDir(null)?.parentFile?.parentFile?.absolutePath

        // Falls der Abruf fehlschlägt, sind aus Sicherheitsgründen standardmäßig Berechtigungen erforderlich.
        if (appExternalDir == null) return true

        // Wenn es sich bei dem Pfad um ein Unterverzeichnis eines privaten Verzeichnisses handelt, ist dies eine Ausnahme.
        if (path.startsWith(appExternalDir)) {
            return false
        }

        // Andere Verzeichnisse werden anhand der Version bestimmt.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            return !Environment.isExternalStorageManager()
        } else {
            return !hasBasicStoragePermission(context)
        }
    }

    fun hasBasicStoragePermission(context: Context): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            true
        } else {
            val read = ContextCompat.checkSelfPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE)
            val write = ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE)
            read == PackageManager.PERMISSION_GRANTED && write == PackageManager.PERMISSION_GRANTED
        }
    }

    @Composable
    fun rememberPermissionRequest(
        onPermissionGranted: () -> Unit = {},
        onPermissionDenied: () -> Unit = {}
    ): PermissionRequestState {
        val context = LocalContext.current
        var showRationale by remember { mutableStateOf(false) }

        val allFilesLauncher = rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (hasAllFilesAccess()) onPermissionGranted() else onPermissionDenied()
        }

        val basicLauncher = rememberLauncherForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { perms ->
            if (perms.values.all { it }) onPermissionGranted() else {
                onPermissionDenied()
                showRationale = true
            }
        }

        return remember(context) {
            PermissionRequestState(
                requestPermissions = {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                        if (hasAllFilesAccess()) {
                            onPermissionGranted()
                        } else {
                            try {
                                val intent = Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION).apply {
                                    data = "package:${context.packageName}".toUri()
                                }
                                allFilesLauncher.launch(intent)
                            } catch (_: Exception) {
                                val intent = Intent(Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION)
                                allFilesLauncher.launch(intent)
                            }
                        }
                    } else {
                        if (hasBasicStoragePermission(context)) {
                            onPermissionGranted()
                        } else {
                            basicLauncher.launch(arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE))
                        }
                    }
                },
                showRationale = showRationale,
                hasPermissions = { hasRequiredPermissions(context) }
            )
        }
    }

    data class PermissionRequestState(
        val requestPermissions: () -> Unit,
        val showRationale: Boolean,
        val hasPermissions: () -> Boolean
    )
}