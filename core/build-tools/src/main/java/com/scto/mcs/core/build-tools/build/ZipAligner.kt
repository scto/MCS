package com.scto.mcs.core.build-tools.build

import android.util.Log
import java.io.*
import java.nio.charset.StandardCharsets
import java.util.zip.CRC32
import java.util.zip.ZipEntry
import java.util.zip.ZipFile
import java.util.zip.ZipOutputStream

/**
 * Optimiert das APK-Layout (ZipAlign) für eine verbesserte Performance.
 */
object ZipAligner {
    private const val TAG = "ZipAligner"
    private const val ALIGNMENT = 4

    @Throws(IOException::class)
    fun align(inputFile: File, outputFile: File) {
        ZipFile(inputFile).use { zipFile ->
            // 1. Direktes Wrapping des FileOutputStream, ohne BufferedOutputStream, um Zählabweichungen zu vermeiden
            ByteCountingOutputStream(FileOutputStream(outputFile)).use { counter ->
                ZipOutputStream(counter).use { zos ->
                    zos.setLevel(9)

                    zipFile.entries().asSequence().forEach { entry ->
                        val name = entry.name

                        // Daten lesen
                        val data = readEntryData(zipFile, entry)

                        // 2. Erstelle einen komplett neuen Entry
                        val newEntry = ZipEntry(name)
                        val isArsc = name == "resources.arsc"

                        if (isArsc || entry.method == ZipEntry.STORED) {
                            newEntry.method = ZipEntry.STORED
                            newEntry.size = data.size.toLong()
                            newEntry.compressedSize = data.size.toLong()
                            val crc = CRC32()
                            crc.update(data)
                            newEntry.crc = crc.value
                        } else {
                            newEntry.method = ZipEntry.DEFLATED
                        }

                        // 3. Kern-Ausrichtungsberechnung
                        if (newEntry.method == ZipEntry.STORED) {
                            // Holen der aktuellen Schreibposition (Startposition LFH)
                            val currentPos = counter.bytesWritten

                            val nameBytes = name.toByteArray(StandardCharsets.UTF_8)
                            val nameLen = nameBytes.size

                            // Berechnung der Standard-Header-Länge:
                            // LFH fester Header (30) + Dateinamenslänge + Extra-Feld-Header (4, d.h. ID+Größe)
                            val headerLenWithoutPadding = 30L + nameLen + 4L

                            // Vorhersage des Datenstarts
                            val predictedDataStart = currentPos + headerLenWithoutPadding

                            // Berechne die benötigten Füllbytes (Padding)
                            var padding = ((ALIGNMENT - (predictedDataStart % ALIGNMENT)) % ALIGNMENT).toInt()

                            // Wichtiger Fix: Wenn Padding 0 ist, könnte die Signaturbibliothek diesen
                            // leeren Extra-Block entfernen und die Header-Länge verändern.
                            // Wir erzwingen eine 4-Byte-Auffüllung.
                            if (padding == 0) {
                                padding = 4
                            }

                            if (isArsc) {
                                Log.i(TAG, "Richte resources.arsc aus | StartPos: $currentPos | Padding: $padding")
                            }

                            // Extra Field erstellen (zipalign ID: 0xD935)
                            val extra = ByteArray(4 + padding)
                            extra[0] = 0x35.toByte()
                            extra[1] = 0xD9.toByte()
                            extra[2] = padding.toByte()
                            extra[3] = 0.toByte()
                            // Padding-Daten sind standardmäßig 0

                            newEntry.extra = extra
                        }

                        zos.putNextEntry(newEntry)
                        zos.write(data)
                        zos.closeEntry()
                    }
                    zos.finish()
                }
            }
        }
    }

    @Throws(IOException::class)
    private fun readEntryData(zf: ZipFile, entry: ZipEntry): ByteArray {
        val bos = ByteArrayOutputStream()
        zf.getInputStream(entry).use { inputStream ->
            val buf = ByteArray(8192)
            var len: Int
            while (inputStream.read(buf).also { len = it } > 0) {
                bos.write(buf, 0, len)
            }
        }
        return bos.toByteArray()
    }

    // Zähl-Stream (direkt mit FileOutputStream verbunden)
    private class ByteCountingOutputStream(out: OutputStream) : FilterOutputStream(out) {
        var bytesWritten: Long = 0
            private set

        @Throws(IOException::class)
        override fun write(b: Int) {
            super.write(b)
            bytesWritten++
        }

        @Throws(IOException::class)
        override fun write(b: ByteArray) {
            super.write(b)
            bytesWritten += b.size.toLong()
        }

        @Throws(IOException::class)
        override fun write(b: ByteArray, off: Int, len: Int) {
            super.write(b, off, len)
            bytesWritten += len.toLong()
        }
    }
}