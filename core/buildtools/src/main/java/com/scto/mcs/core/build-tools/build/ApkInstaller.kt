package com.scto.mcs.core.buildtools.build

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.Settings
import android.widget.Toast
import androidx.core.content.FileProvider
import java.io.File

/**
 * Hilfsklasse zum Aufrufen des systemeigenen APK-Installationsprogramms.
 */
object ApkInstaller {

    /**
     * Ruft den Installer auf.
     * @param context Kontext
     * @param apkFile Das zu installierende APK-Dateiobjekt
     */
    fun install(context: Context, apkFile: File) {
        if (!apkFile.exists()) {
            Toast.makeText(context, "Installationsdatei existiert nicht", Toast.LENGTH_SHORT).show()
            return
        }

        // 1. Android 8.0+ erfordert die Berechtigung "Unbekannte Apps installieren"
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            if (!context.packageManager.canRequestPackageInstalls()) {
                // Wenn keine Berechtigung vorhanden ist, Nutzer zu den Einstellungen leiten
                val intent = Intent(Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES).apply {
                    data = Uri.parse("package:${context.packageName}")
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                }
                Toast.makeText(context, "Bitte gewähren Sie zuerst die Berechtigung zur Installation unbekannter Apps", Toast.LENGTH_LONG).show()
                context.startActivity(intent)
                return
            }
        }

        // 2. Kern-Installationslogik
        try {
            val intent = Intent(Intent.ACTION_VIEW)
            val uri: Uri

            // Version prüfen: Android 7.0 (N) und höher erzwingen FileProvider
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                // Wichtig: Die Authority muss mit der im AndroidManifest.xml übereinstimmen
                val authority = "${context.packageName}.fileprovider"
                uri = FileProvider.getUriForFile(context, authority, apkFile)

                // Wichtiger Punkt: Temporäre Leseberechtigungen gewähren
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            } else {
                // Sehr alte Geräte (aus Kompatibilitätsgründen beibehalten)
                uri = Uri.fromFile(apkFile)
            }

            intent.setDataAndType(uri, "application/vnd.android.package-archive")
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK) // Zwingend erforderlich außerhalb von Activities

            context.startActivity(intent)

        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(context, "Fehler beim Aufruf des Installers: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }
}