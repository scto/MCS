package com.scto.mcs.core.build

import android.content.Context
import com.Day.Studio.Function.ApkXmlEditor
import com.Day.Studio.Function.axmleditor.decode.AXMLDoc
import com.Day.Studio.Function.axmleditor.editor.PermissionEditor
import com.scto.mcs.core.utils.LogCatcher
import java.io.*
import java.util.zip.ZipEntry
import java.util.zip.ZipFile
import java.util.zip.ZipOutputStream

/**
 * Hauptklasse zum Erstellen der APK aus dem Web-Projekt.
 */
object ApkBuilder {

    private const val OLD_PACKAGE_NAME = "com.scto.mcs"
    private const val ICON_RES_1 = "res/MO.mcs"
    private const val ICON_RES_2 = "res/fq.mcs"
    private const val TAG = "ApkBuilder"

    data class AppConfig(
        var appName: String = "MCS",
        var appPackage: String = "com.example.mcs",
        var versionName: String = "1.0.0",
        var versionCode: String = "1",
        var iconPath: String? = null,
        val permissions: MutableList<String> = mutableListOf()
    )

    fun build(
        context: Context,
        mRootDir: String,
        projectPath: String,
        appName: String,
        packageName: String,
        versionName: String,
        versionCode: String,
        iconPath: String?,
        permissions: Array<String>?,
        isDebug: Boolean,
        enableEncryption: Boolean,
        customKeyPath: String?,
        customStorePass: String?,
        customAlias: String?,
        customKeyPass: String?
    ): String {
        val buildDir = File(projectPath, "build").apply { if (!exists()) mkdirs() }
        val templateApk = File(context.cacheDir, "mcs_template.apk")
        val rawZip = File(buildDir, "temp_raw.zip")
        val alignedApk = File(buildDir, "temp_aligned.apk")
        val finalApk = File(buildDir, "${appName}_release.apk")

        LogCatcher.clearBuildLogs()
        LogCatcher.i(TAG, "========== Starte Build (Debug: $isDebug) ==========")

        try {
            listOf(rawZip, alignedApk, finalApk).forEach { if (it.exists()) it.delete() }

            val config = AppConfig(appName, packageName, versionName, versionCode).apply {
                if (iconPath != null && File(iconPath).exists()) this.iconPath = iconPath
                permissions?.forEach { this.permissions.add(it) }
            }

            // 1. Template extrahieren
            if (!copyAssetFile(context, "mcs_1.0.apk", templateApk)) {
                return "error: Template nicht gefunden."
            }

            // 2. APK zusammenführen
            mergeApk(context, templateApk, rawZip, projectPath, config, isDebug, enableEncryption)

            // 3. ZipAlign
            LogCatcher.i(TAG, ">> ZipAlign wird ausgeführt...")
            ZipAligner.align(rawZip, alignedApk)

            // 4. Signierung
            val keyPath: String
            val storePass: String
            val alias: String
            val keyPass: String

            if (customKeyPath != null && File(customKeyPath).exists()) {
                keyPath = customKeyPath
                storePass = customStorePass ?: ""
                alias = customAlias ?: ""
                keyPass = customKeyPass ?: ""
            } else {
                val internalKey = File(context.filesDir, "MCS.jks")
                if (!internalKey.exists()) copyAssetFile(context, "Mcs.jks", internalKey)
                keyPath = internalKey.absolutePath
                storePass = "MCS"; alias = "MCS"; keyPass = "MCS"
            }

            LogCatcher.i(TAG, ">> Signierung...")
            val signResult = signApk(keyPath, storePass, alias, keyPass, alignedApk.absolutePath, finalApk.absolutePath)

            rawZip.delete()
            alignedApk.delete()

            return if (signResult && finalApk.length() > 0) {
                LogCatcher.i(TAG, "Build erfolgreich: ${finalApk.absolutePath}")
                finalApk.absolutePath
            } else {
                "error: Signierung fehlgeschlagen."
            }

        } catch (e: Exception) {
            LogCatcher.e(TAG, "Build abgebrochen", e)
            return "error: ${e.message}"
        }
    }

    private fun mergeApk(context: Context, template: File, output: File, projectPath: String, config: AppConfig, isDebug: Boolean, encrypt: Boolean) {
        ZipFile(template).use { zip ->
            ZipOutputStream(FileOutputStream(output)).use { zos ->
                zos.setLevel(5)

                // resources.arsc zuerst (STORED)
                zip.getEntry("resources.arsc")?.let { copyAsStored(zip, it, zos) }

                val entries = zip.entries()
                while (entries.hasMoreElements()) {
                    val entry = entries.nextElement()
                    val name = entry.name

                    if (name == "resources.arsc" || name.startsWith("META-INF/") || name.startsWith("assets/")) continue

                    when {
                        name == "AndroidManifest.xml" -> processManifest(zip, entry, zos, config)
                        config.iconPath != null && (name == ICON_RES_1 || name == ICON_RES_2) -> {
                            zos.putNextEntry(ZipEntry(name))
                            File(config.iconPath!!).inputStream().use { it.copyTo(zos) }
                            zos.closeEntry()
                        }
                        else -> {
                            zos.putNextEntry(ZipEntry(name))
                            zip.getInputStream(entry).use { it.copyTo(zos) }
                            zos.closeEntry()
                        }
                    }
                }

                if (isDebug) {
                    context.assets.open("eruda.min.js").use { input ->
                        zos.putNextEntry(ZipEntry("assets/eruda.min.js"))
                        input.copyTo(zos)
                        zos.closeEntry()
                    }
                }

                // Projekt Assets hinzufügen
                File(projectPath, "src/main/assets").let { if (it.exists()) addProjectFiles(zos, it, "assets", isDebug, encrypt) }
                File(projectPath, "webapp.json").let { if (it.exists()) {
                    zos.putNextEntry(ZipEntry("assets/webapp.json"))
                    it.inputStream().use { input -> input.copyTo(zos) }
                    zos.closeEntry()
                }}
            }
        }
    }

    private fun processManifest(zip: ZipFile, entry: ZipEntry, zos: ZipOutputStream, config: AppConfig) {
        val temp = File.createTempFile("manifest", ".xml").apply {
            writeBytes(zip.getInputStream(entry).readBytes())
        }
        try {
            ApkXmlEditor.setXmlPaht(temp.absolutePath)
            ApkXmlEditor.setAppName(config.appName)
            ApkXmlEditor.setAppPack(config.appPackage)
            ApkXmlEditor.setAppbcode(config.versionCode.toIntOrNull() ?: 1)
            ApkXmlEditor.setAppbname(config.versionName)
            ApkXmlEditor.operation()

            config.permissions.forEach { setPermission(temp.absolutePath, it, false) }

            if (config.appPackage != OLD_PACKAGE_NAME) {
                val reps = mapOf(
                    "$OLD_PACKAGE_NAME.androidx-startup" to "${config.appPackage}.androidx-startup",
                    "$OLD_PACKAGE_NAME.fileprovider" to "${config.appPackage}.fileprovider",
                    "$OLD_PACKAGE_NAME.DYNAMIC_RECEIVER_NOT_EXPORTED_PERMISSION" to "${config.appPackage}.DYNAMIC_RECEIVER_NOT_EXPORTED_PERMISSION"
                )
                // Hier müsste ManifestStringReplacer Logik implementiert sein (siehe Java-Code)
                ProviderAuthReplacer.replaceProviderAuthorities(temp, OLD_PACKAGE_NAME, config.appPackage)
                ProviderAuthReplacer.fixProviderConflicts(temp, config.appPackage)
            }

            zos.putNextEntry(ZipEntry("AndroidManifest.xml"))
            temp.inputStream().use { it.copyTo(zos) }
            zos.closeEntry()
        } finally {
            temp.delete()
        }
    }

    private fun addProjectFiles(zos: ZipOutputStream, file: File, zipPath: String, isDebug: Boolean, encrypt: Boolean) {
        if (file.isDirectory) {
            file.listFiles()?.forEach { addProjectFiles(zos, it, "$zipPath/${it.name}", isDebug, encrypt) }
        } else {
            val shouldEncrypt = !isDebug && encrypt && isEncryptable(file.name)
            if (shouldEncrypt) {
                zos.putNextEntry(ZipEntry("$zipPath.bin"))
                zos.write(Encryptor.encrypt(file.readBytes()))
            } else if (isDebug && (file.name.endsWith(".html") || file.name.endsWith(".htm"))) {
                zos.putNextEntry(ZipEntry(zipPath))
                injectEruda(file, zos)
            } else {
                zos.putNextEntry(ZipEntry(zipPath))
                file.inputStream().use { it.copyTo(zos) }
            }
            zos.closeEntry()
        }
    }

    private fun injectEruda(file: File, zos: ZipOutputStream) {
        var html = file.readText(Charsets.UTF_8)
        val script = "<script src=\"eruda.min.js\"></script><script>eruda.init();</script>"
        html = if (html.contains("</body>", true)) html.replace("</body>", "$script\n</body>", true) else html + script
        zos.write(html.toByteArray(Charsets.UTF_8))
    }

    private fun isEncryptable(name: String) = name.lowercase().let { it.endsWith(".html") || it.endsWith(".js") || it.endsWith(".css") }

    private fun copyAsStored(zip: ZipFile, entry: ZipEntry, zos: ZipOutputStream) {
        val data = zip.getInputStream(entry).readBytes()
        zos.putNextEntry(ZipEntry(entry.name).apply {
            method = ZipEntry.STORED
            size = data.size.toLong()
            compressedSize = data.size.toLong()
            crc = CRC32().apply { update(data) }.value
        })
        zos.write(data)
        zos.closeEntry()
    }

    private fun copyAssetFile(ctx: Context, name: String, dest: File) = try {
        ctx.assets.open(name).use { input -> dest.outputStream().use { output -> input.copyTo(output) } }
        true
    } catch (e: Exception) { false }

    private fun signApk(key: String, pass: String, alias: String, kPass: String, inP: String, outP: String): Boolean {
        return try {
            com.mcal.apksigner.ApkSigner(File(inP), File(outP)).apply {
                setV1SigningEnabled(true)
                setV2SigningEnabled(true)
                signRelease(File(key), pass, alias, kPass)
            }
            true
        } catch (t: Throwable) { t.printStackTrace(); false }
    }

    private fun setPermission(path: String, permission: String, remove: Boolean) {
        try {
            val doc = AXMLDoc().apply { parse(FileInputStream(path)) }
            val pe = PermissionEditor(doc)
            val info = PermissionEditor.EditorInfo().apply {
                with(if (remove) PermissionEditor.PermissionOpera(permission).remove() else PermissionEditor.PermissionOpera(permission).add())
            }
            pe.setEditorInfo(info)
            pe.commit()
            doc.build(FileOutputStream(path))
            doc.release()
        } catch (e: Exception) { }
    }
}