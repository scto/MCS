package com.scto.mcs.core.buildtools.build

import android.util.Log
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.IOException
import java.nio.ByteBuffer
import java.nio.ByteOrder

/**
 * Hilfsklasse zum Ersetzen von Provider-Authorities im AndroidManifest.
 */
object ProviderAuthReplacer {
    private const val TAG = "ProviderAuthReplacer"
    private const val CHUNK_STRING_POOL = 0x001C0001

    /**
     * Ersetzt die Provider-Autorisierung im Manifest.
     * @param manifestFile Manifest-Datei
     * @param oldPackageName Alter Paketname
     * @param newPackageName Neuer Paketname
     */
    @Throws(Exception::class)
    fun replaceProviderAuthorities(manifestFile: File, oldPackageName: String?, newPackageName: String?) {
        if (oldPackageName == null || newPackageName == null || oldPackageName == newPackageName) {
            Log.d(TAG, "Paketname ist identisch, keine Ersetzung notwendig.")
            return
        }

        val authMapping = HashMap<String, String>()

        // 1. Grundlegende Provider-Autorisierungen ersetzen
        val baseProviders = arrayOf(
            ".provider",
            ".fileprovider",
            ".androidx-startup",
            ".appsflyer-provider",
            ".firebase-provider",
            ".download-provider",
            ".cache-provider",
            ".security-provider"
        )

        for (suffix in baseProviders) {
            authMapping[oldPackageName + suffix] = newPackageName + suffix
        }

        // 2. Gängige Drittanbieter-Bibliotheken
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
            Log.i(TAG, "Starte Ersetzung der Provider-Berechtigungen (${authMapping.size} Mappings).")
            batchReplaceStringInAXML(manifestFile, authMapping)
        }
    }

    /**
     * Scannt das Manifest nach allen Provider-Berechtigungen.
     */
    @Throws(Exception::class)
    fun scanProviderAuthorities(manifestFile: File): List<String> {
        val authorities = mutableListOf<String>()
        val data = readFileToBytes(manifestFile)

        val buffer = ByteBuffer.wrap(data).order(ByteOrder.LITTLE_ENDIAN)

        // Datei-Header überspringen
        if (buffer.int != 0x00080003) {
            throw IllegalArgumentException("Ungültige AXML-Datei")
        }
        buffer.position(8)

        val chunkType = buffer.int
        if (chunkType != CHUNK_STRING_POOL) return authorities

        buffer.int // chunkSize
        val stringCount = buffer.int
        buffer.int // styleCount
        val flags = buffer.int
        val stringsOffset = buffer.int
        buffer.int // stylesOffset

        val isUTF8 = (flags and 0x0100) != 0
        val stringPoolStart = buffer.position() - 28

        val offsets = IntArray(stringCount)
        for (i in 0 until stringCount) {
            offsets[i] = buffer.int
        }

        val dataStart = stringPoolStart + stringsOffset

        for (i in 0 until stringCount) {
            val strPos = dataStart + offsets[i]
            buffer.position(strPos)

            val str = readString(buffer, isUTF8)

            // Prüfen, ob es eine gängige Provider-Berechtigung ist
            if (str.contains(".provider") || str.contains(".fileprovider") ||
                str.contains("content://") || str.contains(".startup")) {
                authorities.add(str)
            }
        }

        return authorities
    }

    @Throws(Exception::class)
    fun batchReplaceStringInAXML(axmlFile: File, replacementMap: Map<String, String>) {
        if (replacementMap.isEmpty()) return

        val data = readFileToBytes(axmlFile)
        val buffer = ByteBuffer.wrap(data).order(ByteOrder.LITTLE_ENDIAN)

        if (buffer.int != 0x00080003) throw IllegalArgumentException("Ungültige AXML-Datei")
        buffer.position(8)

        val chunkType = buffer.int
        if (chunkType != CHUNK_STRING_POOL) {
            Log.w(TAG, "Kein String-Pool gefunden.")
            return
        }

        val chunkSize = buffer.int
        val stringCount = buffer.int
        val styleCount = buffer.int
        val flags = buffer.int
        val stringsOffset = buffer.int
        val stylesOffset = buffer.int

        val isUTF8 = (flags and 0x0100) != 0
        val stringPoolStart = buffer.position() - 28

        val offsets = IntArray(stringCount)
        for (i in 0 until stringCount) {
            offsets[i] = buffer.int
        }

        val strings = mutableListOf<String>()
        val dataStart = stringPoolStart + stringsOffset

        for (i in 0 until stringCount) {
            val strPos = dataStart + offsets[i]
            buffer.position(strPos)
            strings.add(readString(buffer, isUTF8))
        }

        var modified = false
        for (i in strings.indices) {
            val current = strings[i]
            for ((key, value) in replacementMap) {
                if (current == key) {
                    Log.d(TAG, "Ersetze: $key -> $value")
                    strings[i] = value
                    modified = true
                    break
                }
            }
        }

        if (!modified) {
            Log.w(TAG, "Keine passenden Strings zur Ersetzung gefunden.")
            return
        }

        val newStringData = buildStringPool(strings, isUTF8)
        val output = ByteArrayOutputStream()

        output.write(data, 0, 8) // Datei-Kopf

        val header = ByteBuffer.allocate(28).order(ByteOrder.LITTLE_ENDIAN)
        header.putInt(CHUNK_STRING_POOL)
        header.putInt(28 + (stringCount * 4) + newStringData.size)
        header.putInt(stringCount)
        header.putInt(styleCount)
        header.putInt(flags)
        header.putInt(28 + (stringCount * 4))
        header.putInt(0)
        output.write(header.array())

        val offsetsBuffer = ByteBuffer.allocate(stringCount * 4).order(ByteOrder.LITTLE_ENDIAN)
        var currentOffset = 0
        val tempStrings = ByteArrayOutputStream()

        for (str in strings) {
            offsetsBuffer.putInt(currentOffset)
            val strBytes = if (isUTF8) str.toByteArray(Charsets.UTF_8) else str.toByteArray(Charsets.UTF_16LE)

            if (isUTF8) {
                tempStrings.write(str.length and 0xFF)
                tempStrings.write(strBytes.size and 0xFF)
                tempStrings.write(strBytes)
                tempStrings.write(0)
                currentOffset += 2 + strBytes.size + 1
            } else {
                tempStrings.write(str.length and 0xFF)
                tempStrings.write((str.length shr 8) and 0xFF)
                tempStrings.write(strBytes)
                tempStrings.write(0)
                tempStrings.write(0)
                currentOffset += 2 + strBytes.size + 2
            }
        }

        while (currentOffset % 4 != 0) {
            tempStrings.write(0)
            currentOffset++
        }

        output.write(offsetsBuffer.array())
        output.write(tempStrings.toByteArray())

        val remainingPos = stringPoolStart + chunkSize
        output.write(data, remainingPos, data.size - remainingPos)

        val finalData = output.toByteArray()
        val finalBuffer = ByteBuffer.wrap(finalData).order(ByteOrder.LITTLE_ENDIAN)
        finalBuffer.putInt(4, finalData.size)

        FileOutputStream(axmlFile).use { it.write(finalData) }
        Log.i(TAG, "Ersetzung der Provider-Autorisierungen abgeschlossen.")
    }

    private fun readString(buffer: ByteBuffer, isUTF8: Boolean): String {
        return try {
            if (isUTF8) {
                val len1 = buffer.get().toInt() and 0xFF
                var len = len1
                if ((len1 and 0x80) != 0) {
                    len = ((len1 and 0x7F) shl 8) or (buffer.get().toInt() and 0xFF)
                }

                val len2 = buffer.get().toInt() and 0xFF
                var encodedLen = len2
                if ((len2 and 0x80) != 0) {
                    encodedLen = ((len2 and 0x7F) shl 8) or (buffer.get().toInt() and 0xFF)
                }

                val strBytes = ByteArray(encodedLen)
                buffer.get(strBytes)
                String(strBytes, Charsets.UTF_8)
            } else {
                val len = buffer.short.toInt() and 0xFFFF
                val strBytes = ByteArray(len * 2)
                buffer.get(strBytes)
                String(strBytes, Charsets.UTF_16LE)
            }
        } catch (e: Exception) {
            Log.e(TAG, "Fehler beim Lesen des Strings", e)
            ""
        }
    }

    @Throws(Exception::class)
    private fun buildStringPool(strings: List<String>, isUTF8: Boolean): ByteArray {
        val poolData = ByteArrayOutputStream()

        for (str in strings) {
            val strBytes = if (isUTF8) str.toByteArray(Charsets.UTF_8) else str.toByteArray(Charsets.UTF_16LE)

            if (isUTF8) {
                poolData.write(str.length and 0xFF)
                poolData.write(strBytes.size and 0xFF)
                poolData.write(strBytes)
                poolData.write(0)
            } else {
                poolData.write(str.length and 0xFF)
                poolData.write((str.length shr 8) and 0xFF)
                poolData.write(strBytes)
                poolData.write(0)
                poolData.write(0)
            }
        }

        while (poolData.size() % 4 != 0) {
            poolData.write(0)
        }

        return poolData.toByteArray()
    }

    @Throws(IOException::class)
    private fun readFileToBytes(file: File): ByteArray {
        FileInputStream(file).use { fis ->
            ByteArrayOutputStream().use { bos ->
                val buffer = ByteArray(8192)
                var len: Int
                while (fis.read(buffer).also { len = it } > 0) {
                    bos.write(buffer, 0, len)
                }
                return bos.toByteArray()
            }
        }
    }

    /**
     * Überprüft und repariert Konflikte von Provider-Berechtigungen schnell.
     */
    fun fixProviderConflicts(manifestFile: File, newPackageName: String) {
        try {
            Log.i(TAG, "Beginne Überprüfung auf Provider-Konflikte...")
            val authorities = scanProviderAuthorities(manifestFile)
            Log.i(TAG, "${authorities.size} Provider-Autorisierungen gefunden.")

            val replacements = HashMap<String, String>()
            for (auth in authorities) {
                if (auth.contains("com.web.webapp")) {
                    val newAuth = auth.replace("com.web.webapp", newPackageName)
                    replacements[auth] = newAuth
                    Log.d(TAG, "Muss ersetzt werden: $auth -> $newAuth")
                }
            }

            if (replacements.isNotEmpty()) {
                batchReplaceStringInAXML(manifestFile, replacements)
                Log.i(TAG, "Provider-Konflikte erfolgreich repariert.")
            } else {
                Log.i(TAG, "Keine zu ersetzenden Provider-Konflikte gefunden.")
            }
        } catch (e: Exception) {
            Log.e(TAG, "Fehler beim Beheben der Provider-Konflikte.", e)
        }
    }
}