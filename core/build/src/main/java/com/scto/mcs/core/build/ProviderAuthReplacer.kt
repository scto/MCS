package com.scto.mcs.core.build

import com.web.webide.core.utils.LogCatcher
import java.io.*
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.util.ArrayList

/**
 * Ersetzt Provider-Authorities in einer binären AndroidManifest.xml (AXML).
 */
object ProviderAuthReplacer {

    private const val CHUNK_STRING_POOL = 0x001C0001
    private const val TAG = "ProviderAuthReplacer"

    fun replaceProviderAuthorities(manifestFile: File, oldPackageName: String?, newPackageName: String?) {
        if (oldPackageName == null || newPackageName == null || oldPackageName == newPackageName) {
            LogCatcher.d(TAG, "Package-Name identisch oder null, kein Austausch nötig.")
            return
        }

        val authMapping = mutableMapOf<String, String>()
        val baseProviders = arrayOf(
            ".provider", ".fileprovider", ".androidx-startup", ".appsflyer-provider",
            ".firebase-provider", ".download-provider", ".cache-provider", ".security-provider"
        )

        for (suffix in baseProviders) {
            authMapping[oldPackageName + suffix] = newPackageName + suffix
        }

        val commonProviders = arrayOf(
            "com.web.webapp.provider",
            "com.web.webapp.fileprovider",
            "com.web.webapp.androidx-startup",
            "$oldPackageName.provider.Provider",
            "$oldPackageName.provider.FileProvider",
            "$oldPackageName.provider.DownloadProvider"
        )

        for (oldAuth in commonProviders) {
            val newAuth = when {
                oldAuth.startsWith("com.web.webapp.") -> oldAuth.replace("com.web.webapp.", "$newPackageName.")
                oldAuth.startsWith("$oldPackageName.") -> oldAuth.replace("$oldPackageName.", "$newPackageName.")
                else -> newPackageName + oldAuth.substring(oldAuth.lastIndexOf('.'))
            }
            authMapping[oldAuth] = newAuth
        }

        if (authMapping.isNotEmpty()) {
            LogCatcher.i(TAG, "Starte Provider-Austausch (${authMapping.size} Mappings)")
            batchReplaceStringInAXML(manifestFile, authMapping)
        }
    }

    fun scanProviderAuthorities(manifestFile: File): List<String> {
        val authorities = mutableListOf<String>()
        val data = manifestFile.readBytes()
        val buffer = ByteBuffer.wrap(data).order(ByteOrder.LITTLE_ENDIAN)

        if (buffer.getInt() != 0x00080003) throw IllegalArgumentException("Ungültige AXML-Datei")
        buffer.position(8)

        if (buffer.getInt() != CHUNK_STRING_POOL) return authorities

        buffer.getInt() // chunkSize
        val stringCount = buffer.getInt()
        buffer.getInt() // styleCount
        val flags = buffer.getInt()
        val stringsOffset = buffer.getInt()
        buffer.getInt() // stylesOffset

        val isUTF8 = (flags and 0x0100) != 0
        val stringPoolStart = buffer.position() - 28
        val dataStart = stringPoolStart + stringsOffset

        val offsets = IntArray(stringCount) { buffer.getInt() }

        for (i in 0 until stringCount) {
            buffer.position(dataStart + offsets[i])
            val str = readString(buffer, isUTF8)
            if (str.contains(".provider") || str.contains(".fileprovider") ||
                str.contains("content://") || str.contains(".startup")
            ) {
                authorities.add(str)
            }
        }
        return authorities
    }

    fun batchReplaceStringInAXML(axmlFile: File, replacementMap: Map<String, String>) {
        if (replacementMap.isEmpty()) return

        val data = axmlFile.readBytes()
        val buffer = ByteBuffer.wrap(data).order(ByteOrder.LITTLE_ENDIAN)

        if (buffer.getInt() != 0x00080003) throw IllegalArgumentException("Ungültige AXML-Datei")
        buffer.position(8)

        if (buffer.getInt() != CHUNK_STRING_POOL) return

        val chunkSize = buffer.getInt()
        val stringCount = buffer.getInt()
        val styleCount = buffer.getInt()
        val flags = buffer.getInt()
        val stringsOffset = buffer.getInt()
        val stylesOffset = buffer.getInt()

        val isUTF8 = (flags and 0x0100) != 0
        val stringPoolStart = buffer.position() - 28
        val dataStart = stringPoolStart + stringsOffset

        val offsets = IntArray(stringCount) { buffer.getInt() }
        val strings = MutableList(stringCount) { i ->
            buffer.position(dataStart + offsets[i])
            readString(buffer, isUTF8)
        }

        var modified = false
        for (i in strings.indices) {
            replacementMap[strings[i]]?.let { newVal ->
                LogCatcher.d(TAG, "Ersetze: ${strings[i]} -> $newVal")
                strings[i] = newVal
                modified = true
            }
        }

        if (!modified) return

        val poolBos = ByteArrayOutputStream()
        val newOffsets = IntArray(stringCount)

        for (i in strings.indices) {
            newOffsets[i] = poolBos.size()
            val s = strings[i]
            if (isUTF8) {
                val strBytes = s.toByteArray(Charsets.UTF_8)
                poolBos.write(s.length and 0xFF)
                poolBos.write(strBytes.size and 0xFF)
                poolBos.write(strBytes)
                poolBos.write(0)
            } else {
                val strBytes = s.toByteArray(Charsets.UTF_16LE)
                poolBos.write(s.length and 0xFF)
                poolBos.write((s.length ushr 8) and 0xFF)
                poolBos.write(strBytes)
                poolBos.write(0); poolBos.write(0)
            }
        }

        while (poolBos.size() % 4 != 0) poolBos.write(0)
        val newStringData = poolBos.toByteArray()

        val output = ByteArrayOutputStream()
        output.write(data, 0, 8)

        val header = ByteBuffer.allocate(28).order(ByteOrder.LITTLE_ENDIAN).apply {
            putInt(CHUNK_STRING_POOL)
            putInt(28 + (stringCount * 4) + newStringData.size)
            putInt(stringCount)
            putInt(styleCount)
            putInt(flags)
            putInt(28 + (stringCount * 4))
            putInt(0)
        }
        output.write(header.array())

        val offsetBuf = ByteBuffer.allocate(stringCount * 4).order(ByteOrder.LITTLE_ENDIAN)
        newOffsets.forEach { offsetBuf.putInt(it) }
        output.write(offsetBuf.array())
        output.write(newStringData)

        val remainingPos = stringPoolStart + chunkSize
        output.write(data, remainingPos, data.size - remainingPos)

        val finalData = output.toByteArray()
        ByteBuffer.wrap(finalData).order(ByteOrder.LITTLE_ENDIAN).putInt(4, finalData.size)

        axmlFile.writeBytes(finalData)
        LogCatcher.i(TAG, "AXML erfolgreich aktualisiert.")
    }

    private fun readString(buffer: ByteBuffer, isUTF8: Boolean): String {
        return try {
            if (isUTF8) {
                var len = buffer.get().toInt() and 0xFF
                if ((len and 0x80) != 0) len = ((len and 0x7F) shl 8) or (buffer.get().toInt() and 0xFF)
                var encodedLen = buffer.get().toInt() and 0xFF
                if ((encodedLen and 0x80) != 0) encodedLen = ((encodedLen and 0x7F) shl 8) or (buffer.get().toInt() and 0xFF)
                val bytes = ByteArray(encodedLen)
                buffer.get(bytes)
                String(bytes, Charsets.UTF_8)
            } else {
                val len = buffer.short.toInt() and 0xFFFF
                val bytes = ByteArray(len * 2)
                buffer.get(bytes)
                String(bytes, Charsets.UTF_16LE)
            }
        } catch (e: Exception) { "" }
    }

    fun fixProviderConflicts(manifestFile: File, newPackageName: String) {
        try {
            val authorities = scanProviderAuthorities(manifestFile)
            val replacements = authorities.filter { it.contains("com.web.webapp") }
                .associateWith { it.replace("com.web.webapp", newPackageName) }

            if (replacements.isNotEmpty()) {
                batchReplaceStringInAXML(manifestFile, replacements)
            }
        } catch (e: Exception) {
            LogCatcher.e(TAG, "Fehler beim Beheben von Provider-Konflikten", e)
        }
    }
}