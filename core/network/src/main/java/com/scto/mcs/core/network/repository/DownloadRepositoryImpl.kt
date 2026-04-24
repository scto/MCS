package com.scto.mcs.core.network.repository

import com.scto.mcs.core.domain.repository.DownloadStatus
import com.scto.mcs.core.domain.repository.DownloadRepository

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flowOn

import okhttp3.OkHttpClient
import okhttp3.Request

import java.io.File
import java.io.FileOutputStream

import javax.inject.Inject

/**
 * OkHttp-basierte Implementierung des DownloadRepository.
 */
class DownloadRepositoryImpl @Inject constructor(
    private val client: OkHttpClient
) : DownloadRepository {

    override fun downloadFile(url: String, targetFile: File): Flow<DownloadStatus> = callbackFlow {
        val request = Request.Builder().url(url).build()
        
        try {
            client.newCall(request).execute().use { response ->
                if (!response.isSuccessful) {
                    trySend(DownloadStatus.Error("Serverfehler: ${response.code}"))
                    close()
                    return@callbackFlow
                }

                val body = response.body ?: throw Exception("Leerer Response Body")
                val contentLength = body.contentLength()
                val inputStream = body.byteStream()
                val outputStream = FileOutputStream(targetFile)
                
                val buffer = ByteArray(8192)
                var bytesRead: Long = 0
                var read: Int

                while (inputStream.read(buffer).also { read = it } != -1) {
                    outputStream.write(buffer, 0, read)
                    bytesRead += read
                    trySend(DownloadStatus.Progress(bytesRead, contentLength))
                }

                outputStream.flush()
                outputStream.close()
                inputStream.close()

                trySend(DownloadStatus.Success(targetFile))
                close()
            }
        } catch (e: Exception) {
            trySend(DownloadStatus.Error(e.message ?: "Unbekannter Netzwerkfehler"))
            close(e)
        }
    }.flowOn(Dispatchers.IO)
}