package com.scto.mcs.core.data.repository

import com.scto.mcs.core.domain.repository.GitRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.eclipse.jgit.api.Git
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GitRepositoryImpl @Inject constructor() : GitRepository {

    override suspend fun cloneRepository(url: String, destination: File): Result<Unit> = withContext(Dispatchers.IO) {
        runCatching {
            Git.cloneRepository()
                .setURI(url)
                .setDirectory(destination)
                .call()
                .close()
        }
    }
}
