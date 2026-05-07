package com.scto.mcs.feature.settings.debugOptions

import androidx.compose.runtime.mutableStateListOf
import com.scto.mcs.core.resources.R

enum class LogLevel(val labelResId: Int) {
    DEBUG(R.string.debug),
    INFO(R.string.info),
    WARN(R.string.warning),
    ERROR(R.string.error),
}

data class LogEntry(val level: LogLevel, val message: String, val timestamp: Long = System.currentTimeMillis())

object LogCollector {
    val logs = mutableStateListOf<LogEntry>()

    fun reportDebug(message: String) {
        logs.add(LogEntry(LogLevel.DEBUG, message))
    }

    fun reportInfo(message: String) {
        logs.add(LogEntry(LogLevel.INFO, message))
    }

    fun reportWarn(message: String) {
        logs.add(LogEntry(LogLevel.WARN, message))
    }

    fun reportError(message: String) {
        logs.add(LogEntry(LogLevel.ERROR, message))
    }

    fun clearLogs() {
        logs.clear()
    }
}
