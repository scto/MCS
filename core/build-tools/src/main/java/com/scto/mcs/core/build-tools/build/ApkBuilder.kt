package com.scto.mcs.core.build-tools.build

import android.content.Context
import android.util.Log
import com.Day.Studio.Function.ApkXmlEditor
import com.Day.Studio.Function.axmleditor.decode.AXMLDoc
import com.Day.Studio.Function.axmleditor.editor.PermissionEditor
import com.mcal.apksigner.ApkSigner
import java.io.*
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.charset.StandardCharsets
import java.util.zip.CRC32
import java.util.zip.ZipEntry
import java.util.zip.ZipFile
import java.util.zip.ZipOutputStream

/**
 * Kernkomponente für das Zusammenbauen und Kompilieren des APKs.
 */
object ApkBuilder {
    private const val TAG = "ApkBuilder"
    private const val OLD_PACKAGE_NAME = "com.web.webapp"
    private const val ICON_RES_1 = "res/MO.webp"
    private const val ICON_RES_2 = "res/fq.webp"

    private data class AppConfig(
        var appName: String = "WebApp",
        var appPackage: String = "com.example.webapp",
        var versionName: String = "1.0.0",
        var versionCode: String = "1",
        var iconPath: String? = null,
        val permissions: MutableList<String> = mutableListOf()
    )

    fun bin(
        context: Context,
        rootDir: String,
        projectPath: String,
        appName: String,
        pkg: String,
        ver: String,
        code: String,
        iconPath: String?,
        permissions: Array<String>?,
        isDebug: Boolean,
        enableEncryption: Boolean,
        customKeyPath: String?,
        customStorePass: String?,
        customAlias: String?,
        customKeyPass: String?
    ): String {
        val bf = File(projectPath, "build")
        if (!bf.exists()) bf.mkdirs()

        val templateApk = File(context.cacheDir, "webapp_template.apk")
        val rawZipFile = File(bf, "temp_raw.zip")
        val alignedZipFile = File(bf, "temp_aligned.apk")
        val finalApkFile = File(bf, "${appName}_release.apk")

        Log.i(TAG, "========== Starte WebApp-Build (Debug: $isDebug) ==========")
        Log.d(TAG, "Projekt: $projectPath | Paket: $pkg | Version: $ver")

        try {
            // 0. Alte Dateien aufräumen
            Log.d(TAG, "Räume temporäre Dateien auf...")
            if (rawZipFile.exists()) rawZipFile.delete()
            if (alignedZipFile.exists()) alignedZipFile.delete()
            if (finalApkFile.exists()) finalApkFile.delete()

            // 1. Konfiguration vorbereiten
            val config = AppConfig(
                appName = appName,
                appPackage = pkg,
                versionName = ver,
                versionCode = code
            )

            if (!iconPath.isNullOrEmpty() && File(iconPath).exists()) {
                config.iconPath = iconPath
                Log.d(TAG, "Benutzerdefiniertes Icon erkannt: $iconPath")
            }

            permissions?.let { config.permissions.addAll(it) }

            // 2. Template APK extrahieren
            Log.i(TAG, ">> Extrahiere Build-Template...")
            if (!copyAssetFile(context, "webapp_1.0.apk", templateApk)) {
                return "error: Build-Template (assets/webapp_1.0.apk) nicht gefunden."
            }

            // 3. Zusammenführen & Icon austauschen
            mergeApk(context, templateApk, rawZipFile, projectPath, config, isDebug, enableEncryption)

            if (rawZipFile.length() < 1000) {
                return "error: Build fehlgeschlagen. Generiertes Paket ist zu klein."
            }

            // 4. ZipAlign
            Log.i(TAG, ">> ZipAlign ausführen...")
            try {
                ZipAligner.align(rawZipFile, alignedZipFile)
            } catch (e: Exception) {
                Log.e(TAG, "ZipAlign fehlgeschlagen", e)
                return "error: ZipAlign fehlgeschlagen - ${e.message}"
            }

            // 5. Signierung
            Log.i(TAG, ">> APK wird signiert...")
            val finalKeyPath: String
            val finalStorePass: String
            val finalAlias: String
            val finalKeyPass: String

            if (!customKeyPath.isNullOrEmpty() && File(customKeyPath).exists()) {
                finalKeyPath = customKeyPath
                finalStorePass = customStorePass ?: ""
                finalAlias = customAlias ?: ""
                finalKeyPass = customKeyPass ?: ""
            } else {
                var signaturePath = File(rootDir, "WebIDE.jks").absolutePath
                val keyFile = File(signaturePath)
                if (!keyFile.exists()) {
                    val internalKey = File(context.filesDir, "WebIDE.jks")
                    if (!internalKey.exists()) copyAssetFile(context, "WebIDE.jks", internalKey)
                    signaturePath = internalKey.absolutePath
                }

                finalKeyPath = signaturePath
                finalStorePass = "WebIDE"
                finalAlias = "WebIDE"
                finalKeyPass = "WebIDE"
            }

            val signResult = signerApk(
                finalKeyPath, finalStorePass, finalAlias, finalKeyPass,
                alignedZipFile.absolutePath, finalApkFile.absolutePath
            )

            // Temporäre Dateien löschen
            rawZipFile.delete()
            alignedZipFile.delete()

            return if (signResult && finalApkFile.length() > 0) {
                Log.i(TAG, "Erfolgreich kompiliert: ${finalApkFile.absolutePath}")
                finalApkFile.absolutePath
            } else {
                "error: Signierung fehlgeschlagen. Bitte überprüfen Sie die Passwort/Alias-Konfiguration."
            }

        } catch (e: Exception) {
            Log.e(TAG, "Kritischer Fehler beim Kompilieren", e)
            return "error: ${e.message}"
        }
    }

    @Throws(Exception::class)
    private fun mergeApk(
        context: Context,
        templateFile: File,
        outputFile: File,
        projectPath: String,
        config: AppConfig,
        isDebug: Boolean,
        enableEncryption: Boolean
    ) {
        ZipFile(templateFile).use { zipFile ->
            ZipOutputStream(FileOutputStream(outputFile)).use { zos ->
                zos.setLevel(5)

                // A. resources.arsc zuerst (STORED)
                zipFile.getEntry("resources.arsc")?.let { arscEntry ->
                    copyAsStored(zipFile, arscEntry, zos)
                }

                zipFile.entries().asSequence().forEach { entry ->
                    val name = entry.name
                    if (name == "resources.arsc" || name.startsWith("META-INF/") || name.startsWith("assets/")) return@forEach

                    // 1. AndroidManifest
                    if (name == "AndroidManifest.xml") {
                        processManifest(zipFile, entry, zos, config)
                        return@forEach
                    }

                    // 2. Icons austauschen
                    if (config.iconPath != null && (name == ICON_RES_1 || name == ICON_RES_2)) {
                        zos.putNextEntry(ZipEntry(name))
                        FileInputStream(File(config.iconPath!!)).use { copyStream(it, zos) }
                        zos.closeEntry()
                        return@forEach
                    }

                    // 3. Andere Dateien
                    zos.putNextEntry(ZipEntry(name))
                    zipFile.getInputStream(entry).use { copyStream(it, zos) }
                    zos.closeEntry()
                }

                // Eruda injizieren bei Debug
                if (isDebug) {
                    try {
                        context.assets.open("eruda.min.js").use { erudaIn ->
                            zos.putNextEntry(ZipEntry("assets/eruda.min.js"))
                            copyStream(erudaIn, zos)
                            zos.closeEntry()
                            Log.d(TAG, "eruda.min.js erfolgreich injiziert.")
                        }
                    } catch (e: Exception) {
                        Log.w(TAG, "Eruda Injektion fehlgeschlagen: ${e.message}")
                    }
                }

                // Benutzer-Assets einfügen
                val userAssetsDir = File(projectPath, "src/main/assets")
                if (userAssetsDir.exists() && userAssetsDir.isDirectory) {
                    addProjectFilesRecursively(zos, userAssetsDir, "assets", isDebug, enableEncryption)
                }

                // Config-File einfügen
                val configFile = File(projectPath, "webapp.json")
                if (configFile.exists()) {
                    zos.putNextEntry(ZipEntry("assets/webapp.json"))
                    FileInputStream(configFile).use { copyStream(it, zos) }
                    zos.closeEntry()
                }
            }
        }
    }

    @Throws(Exception::class)
    private fun processManifest(zipFile: ZipFile, entry: ZipEntry, zos: ZipOutputStream, config: AppConfig) {
        val bos = ByteArrayOutputStream()
        zipFile.getInputStream(entry).use { copyStream(it, bos) }
        
        val tempManifest = File.createTempFile("TempManifest", ".xml")
        FileOutputStream(tempManifest).use { it.write(bos.toByteArray()) }

        try {
            ApkXmlEditor.setXmlPaht(tempManifest.absolutePath)
            ApkXmlEditor.setAppName(config.appName)
            ApkXmlEditor.setAppPack(config.appPackage)
            try {
                ApkXmlEditor.setAppbcode(config.versionCode.toInt())
            } catch (e: NumberFormatException) {
                ApkXmlEditor.setAppbcode(1)
            }
            ApkXmlEditor.setAppbname(config.versionName)
            ApkXmlEditor.operation()

            config.permissions.forEach { perm ->
                setPermission(tempManifest.absolutePath, perm, false)
            }

            if (config.appPackage != OLD_PACKAGE_NAME) {
                val replacements = mapOf(
                    "$OLD_PACKAGE_NAME.androidx-startup" to "${config.appPackage}.androidx-startup",
                    "$OLD_PACKAGE_NAME.fileprovider" to "${config.appPackage}.fileprovider",
                    ".MainActivity" to "$OLD_PACKAGE_NAME.MainActivity",
                    "$OLD_PACKAGE_NAME.DYNAMIC_RECEIVER_NOT_EXPORTED_PERMISSION" to "${config.appPackage}.DYNAMIC_RECEIVER_NOT_EXPORTED_PERMISSION"
                )
                ManifestStringReplacer.batchReplaceStringInAXML(tempManifest, replacements)
            }

            ProviderAuthReplacer.replaceProviderAuthorities(tempManifest, OLD_PACKAGE_NAME, config.appPackage)
            ProviderAuthReplacer.fixProviderConflicts(tempManifest, config.appPackage)

            zos.putNextEntry(ZipEntry("AndroidManifest.xml"))
            FileInputStream(tempManifest).use { copyStream(it, zos) }
            zos.closeEntry()
        } finally {
            tempManifest.delete()
        }
    }

    private fun setPermission(path: String, permission: String, remove: Boolean) {
        try {
            val file = File(path)
            val doc = AXMLDoc()
            FileInputStream(file).use { doc.parse(it) }

            val pe = PermissionEditor(doc)
            val info = PermissionEditor.EditorInfo()
            val op = PermissionEditor.PermissionOpera(permission)

            info.with(if (remove) op.remove() else op.add())
            pe.setEditorInfo(info)
            pe.commit()

            FileOutputStream(file).use { doc.build(it) }
            doc.release()
        } catch (e: Exception) {
            Log.e(TAG, "Berechtigungsänderung fehlgeschlagen für: $permission", e)
        }
    }

    private fun addProjectFilesRecursively(zos: ZipOutputStream, file: File, zipPath: String, isDebug: Boolean, enableEncryption: Boolean) {
        if (file.isDirectory) {
            file.listFiles()?.forEach { child ->
                addProjectFilesRecursively(zos, child, "$zipPath/${child.name}", isDebug, enableEncryption)
            }
        } else {
            try {
                val shouldEncrypt = !isDebug && enableEncryption && isEncryptable(file.name)

                if (shouldEncrypt) {
                    zos.putNextEntry(ZipEntry("$zipPath.bin"))
                    val fileBytes = readFileToBytes(file)
                    val encrypted = Encryptor.encrypt(fileBytes)
                    zos.write(encrypted)
                    zos.closeEntry()
                } else {
                    zos.putNextEntry(ZipEntry(zipPath))
                    if (isDebug && (file.name.endsWith(".html") || file.name.endsWith(".htm"))) {
                        injectScriptToHtml(file, zos)
                    } else {
                        FileInputStream(file).use { copyStream(it, zos) }
                    }
                    zos.closeEntry()
                }
            } catch (e: IOException) {
                Log.e(TAG, "Fehler beim Packen der Datei: $zipPath", e)
            }
        }
    }

    private fun isEncryptable(name: String): Boolean {
        val lower = name.lowercase()
        return lower.endsWith(".html") || lower.endsWith(".htm") || lower.endsWith(".js") || lower.endsWith(".css")
    }

    @Throws(IOException::class)
    private fun readFileToBytes(file: File): ByteArray {
        val bytes = ByteArray(file.length().toInt())
        FileInputStream(file).use { it.read(bytes) }
        return bytes
    }

    @Throws(IOException::class)
    private fun injectScriptToHtml(htmlFile: File, zos: ZipOutputStream) {
        val bytes = readFileToBytes(htmlFile)
        var html = String(bytes, StandardCharsets.UTF_8)
        val injection = "<script src=\"eruda.min.js\"></script><script>eruda.init();</script>"

        html = when {
            html.contains("</body>") -> html.replace("</body>", "$injection\n</body>")
            html.contains("</BODY>") -> html.replace("</BODY>", "$injection\n</BODY>")
            else -> html + injection
        }

        zos.write(html.toByteArray(StandardCharsets.UTF_8))
    }

    @Throws(IOException::class)
    private fun copyAsStored(zipFile: ZipFile, entry: ZipEntry, zos: ZipOutputStream) {
        val bos = ByteArrayOutputStream()
        zipFile.getInputStream(entry).use { copyStream(it, bos) }
        val data = bos.toByteArray()
        val crc = CRC32().apply { update(data) }
        
        val newEntry = ZipEntry("resources.arsc").apply {
            method = ZipEntry.STORED
            size = data.size.toLong()
            compressedSize = data.size.toLong()
            this.crc = crc.value
            extra = null
        }
        
        zos.putNextEntry(newEntry)
        zos.write(data)
        zos.closeEntry()
    }

    @Throws(IOException::class)
    private fun copyStream(inputStream: InputStream, outputStream: OutputStream) {
        val buf = ByteArray(8192)
        var len: Int
        while (inputStream.read(buf).also { len = it } > 0) {
            outputStream.write(buf, 0, len)
        }
    }

    private fun copyAssetFile(ctx: Context, name: String, dest: File): Boolean {
        return try {
            ctx.assets.open(name).use { input ->
                FileOutputStream(dest).use { output ->
                    copyStream(input, output)
                }
            }
            true
        } catch (e: IOException) {
            false
        }
    }

    private fun signerApk(keyPath: String, pass: String, alias: String, keyPass: String, inPath: String, outPath: String): Boolean {
        return try {
            val signer = ApkSigner(File(inPath), File(outPath))
            signer.isV1SigningEnabled = true
            signer.isV2SigningEnabled = true
            signer.signRelease(File(keyPath), pass, alias, keyPass)
            true
        } catch (e: Throwable) {
            e.printStackTrace()
            false
        }
    }

    private object ManifestStringReplacer {
        private const val CHUNK_STRING_POOL = 0x001C0001

        @Throws(Exception::class)
        fun batchReplaceStringInAXML(axmlFile: File, replacementMap: Map<String, String>) {
            val data = ByteArray(axmlFile.length().toInt())
            FileInputStream(axmlFile).use { it.read(data) }

            val buffer = ByteBuffer.wrap(data).order(ByteOrder.LITTLE_ENDIAN)
            buffer.position(8)

            if (buffer.int != CHUNK_STRING_POOL) return

            val chunkSize = buffer.int
            val stringCount = buffer.int
            val styleCount = buffer.int
            val flags = buffer.int
            val stringsOffset = buffer.int
            buffer.int // stylesOffset

            val isUTF8 = (flags and 0x0100) != 0
            val stringPoolStart = buffer.position() - 28

            val offsets = IntArray(stringCount)
            for (i in 0 until stringCount) offsets[i] = buffer.int

            val strings = mutableListOf<String>()
            val dataStart = stringPoolStart + stringsOffset

            for (i in 0 until stringCount) {
                buffer.position(dataStart + offsets[i])
                if (isUTF8) {
                    val len1 = buffer.get().toInt() and 0xFF
                    var len = len1
                    if ((len1 and 0x80) != 0) len = ((len1 and 0x7F) shl 8) or (buffer.get().toInt() and 0xFF)

                    val len2 = buffer.get().toInt() and 0xFF
                    var encodedLen = len2
                    if ((len2 and 0x80) != 0) encodedLen = ((len2 and 0x7F) shl 8) or (buffer.get().toInt() and 0xFF)

                    val strBytes = ByteArray(encodedLen)
                    buffer.get(strBytes)
                    strings.add(String(strBytes, StandardCharsets.UTF_8))
                } else {
                    val len = buffer.short.toInt() and 0xFFFF
                    val strBytes = ByteArray(len * 2)
                    buffer.get(strBytes)
                    strings.add(String(strBytes, StandardCharsets.UTF_16LE))
                }
            }

            var modified = false
            for (i in strings.indices) {
                val currentStr = strings[i]
                for ((target, replacement) in replacementMap) {
                    if (currentStr == target) {
                        strings[i] = replacement
                        modified = true
                        break
                    }
                }
            }

            if (!modified) return

            val poolBos = ByteArrayOutputStream()
            val newOffsets = mutableListOf<Int>()
            var currentOffset = 0

            for (s in strings) {
                newOffsets.add(currentOffset)
                if (isUTF8) {
                    val rawBytes = s.toByteArray(StandardCharsets.UTF_8)
                    poolBos.write(s.length)
                    poolBos.write(rawBytes.size)
                    poolBos.write(rawBytes)
                    poolBos.write(0)
                    currentOffset += 2 + rawBytes.size + 1
                } else {
                    val rawBytes = s.toByteArray(StandardCharsets.UTF_16LE)
                    val charLen = s.length
                    poolBos.write(charLen and 0xFF)
                    poolBos.write((charLen shr 8) and 0xFF)
                    poolBos.write(rawBytes)
                    poolBos.write(0)
                    poolBos.write(0)
                    currentOffset += 2 + rawBytes.size + 2
                }
            }

            while (currentOffset % 4 != 0) {
                poolBos.write(0)
                currentOffset++
            }

            val newStringData = poolBos.toByteArray()
            val fileBos = ByteArrayOutputStream()
            fileBos.write(data, 0, 8)

            val newChunkSize = 28 + (stringCount * 4) + (styleCount * 4) + newStringData.size
            val headerBuf = ByteBuffer.allocate(28).order(ByteOrder.LITTLE_ENDIAN).apply {
                putInt(CHUNK_STRING_POOL)
                putInt(newChunkSize)
                putInt(stringCount)
                putInt(styleCount)
                putInt(flags)
                putInt(28 + (stringCount * 4) + (styleCount * 4))
                putInt(0)
            }

            fileBos.write(headerBuf.array())

            val offsetBuf = ByteBuffer.allocate(stringCount * 4).order(ByteOrder.LITTLE_ENDIAN)
            newOffsets.forEach { offsetBuf.putInt(it) }
            fileBos.write(offsetBuf.array())

            fileBos.write(newStringData)

            val oldChunkEnd = stringPoolStart + chunkSize
            fileBos.write(data, oldChunkEnd, data.size - oldChunkEnd)

            val finalData = fileBos.toByteArray()
            val finalBuf = ByteBuffer.wrap(finalData).order(ByteOrder.LITTLE_ENDIAN)
            finalBuf.putInt(4, finalData.size)

            FileOutputStream(axmlFile).use { it.write(finalData) }
        }
    }
}