package com.scto.mcs.core.build

import com.web.webide.core.utils.LogCatcher
import java.io.*
import java.util.zip.CRC32
import java.util.zip.ZipEntry
import java.util.zip.ZipFile
import java.util.zip.ZipOutputStream

/**
 * Android ZipAlign Implementierung zur Optimierung des APK-Zugriffs.
 */
object ZipAligner {
    private const val ALIGNMENT = 4
    private const val TAG = "ZipAligner"

    fun align(inputFile: File, outputFile: File) {
        ZipFile(inputFile).use { zipFile ->
            ByteCountingOutputStream(FileOutputStream(outputFile)).use { counter ->
                ZipOutputStream(counter).use { zos ->
                    zos.setLevel(9)
                    val entries = zipFile.entries()
                    while (entries.hasMoreElements()) {
                        val entry = entries.nextElement()
                        val data = zipFile.getInputStream(entry).readBytes()
                        val newEntry = ZipEntry(entry.name)

                        val isArsc = entry.name == "resources.arsc"
                        if (isArsc || entry.method == ZipEntry.STORED) {
                            newEntry.method = ZipEntry.STORED
                            newEntry.size = data.size.toLong()
                            newEntry.compressedSize = data.size.toLong()
                            newEntry.crc = CRC32().apply { update(data) }.value
                        } else {
                            newEntry.method = ZipEntry.DEFLATED
                        }

                        if (newEntry.method == ZipEntry.STORED) {
                            val currentPos = counter.bytesWritten
                            val nameLen = entry.name.toByteArray(Charsets.UTF_8).size
                            // LFH(30) + Name + ExtraHeader(4)
                            val headerLenWithoutPadding = 30 + nameLen + 4
                            val predictedDataStart = currentPos + headerLenWithoutPadding
                            var padding = ((ALIGNMENT - (predictedDataStart % ALIGNMENT)) % ALIGNMENT).toInt()

                            // Padding erzwingen (Kompatibilität mit manchen Signern)
                            if (padding == 0) padding = 4

                            if (isArsc) LogCatcher.i(TAG, "Aligning resources.arsc | Padding: $padding")

                            val extra = ByteArray(4 + padding).apply {
                                this[0] = 0x35.toByte()
                                this[1] = 0xD9.toByte()
                                this[2] = padding.toByte()
                                this[3] = 0
                            }
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

    private class ByteCountingOutputStream(out: OutputStream) : FilterOutputStream(out) {
        var bytesWritten: Long = 0
            private set

        override fun write(b: Int) {
            super.write(b)
            bytesWritten++
        }

        override fun write(b: ByteArray, off: Int, len: Int) {
            out.write(b, off, len)
            bytesWritten += len.toLong()
        }
    }
}