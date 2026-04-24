package com.scto.mcs.core.domain.repository

import kotlinx.coroutines.flow.Flow

import java.io.File

/**
 * Status eines laufenden Downloads.
 */
sealed class DownloadStatus {
    data class Progress(val bytesRead: Long, val contentLength: Long) : DownloadStatus() {
        val percentage: Float get() = if (contentLength > 0) bytesRead.toFloat() / contentLength else 0f
    }
    data class Success(val file: File) : DownloadStatus()
    data class Error(val message: String) : DownloadStatus()
}

/**
 * Schnittstelle für den Download von Ressourcen.
 */
interface DownloadRepository {
    /**
     * Lädt eine Datei von [url] herunter und speichert sie in [targetFile].
     */
    fun downloadFile(url: String, targetFile: File): Flow<DownloadStatus>
}