package com.scto.mcs.core.domain.repository

import java.io.File

interface ProjectRepository {
    fun getProjects(): List<File>
    fun createProject(name: String): File
}
