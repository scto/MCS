package com.scto.mcs.core.data.repository

import com.scto.mcs.core.domain.repository.ProjectRepository
import com.scto.mcs.core.utils.FileSystemUtils
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ProjectRepositoryImpl @Inject constructor(
    private val fileSystemUtils: FileSystemUtils
) : ProjectRepository {

    override fun getProjects(): List<File> {
        return fileSystemUtils.listFiles("projects")
    }

    override fun createProject(name: String): File {
        return fileSystemUtils.ensureDirectoryExists("projects/$name")
    }
}
