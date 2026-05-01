package com.scto.mcs.core.templates.data

import kotlinx.coroutines.flow.Flow

interface TemplateVersionRepository {
    /**
     * Returns a flow of the current Gradle version.
     */
    fun getGradleVersion(): Flow<String>

    /**
     * Returns a flow of the version for a specific library.
     */
    fun getLibraryVersion(libName: String): Flow<String?>

    /**
     * Returns a flow of all managed versions.
     */
    fun getAllVersions(): Flow<Map<String, String>>

    /**
     * Updates or adds a version for a specific key (gradle or library name).
     */
    suspend fun updateVersion(key: String, version: String)

    /**
     * Resets the versions to the default values.
     */
    suspend fun resetToDefaults()
}
