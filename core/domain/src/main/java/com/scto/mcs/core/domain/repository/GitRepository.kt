package com.scto.mcs.core.domain.repository

import java.io.File

interface GitRepository {
    suspend fun cloneRepository(url: String, destination: File): Result<Unit>
}
