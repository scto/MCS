package com.scto.mcs.core.templates.data

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

class TemplateVersionRepositoryImpl(
    private val ioDispatcher: CoroutineDispatcher
) : TemplateVersionRepository {

    private val defaultVersions = mapOf(
        "gradle" to "8.4",
        "kotlin" to "1.9.20",
        "compose" to "1.5.4",
        "coroutines" to "1.7.3"
    )

    private val _versions = MutableStateFlow<Map<String, String>>(defaultVersions)

    override fun getGradleVersion(): Flow<String> = _versions.map { it["gradle"] ?: "8.4" }

    override fun getLibraryVersion(libName: String): Flow<String?> = _versions.map { it[libName] }

    override fun getAllVersions(): Flow<Map<String, String>> = _versions.asStateFlow()

    override suspend fun updateVersion(key: String, version: String) = withContext(ioDispatcher) {
        _versions.value = _versions.value.toMutableMap().apply {
            put(key, version)
        }
    }

    override suspend fun resetToDefaults() = withContext(ioDispatcher) {
        _versions.value = defaultVersions
    }
}
