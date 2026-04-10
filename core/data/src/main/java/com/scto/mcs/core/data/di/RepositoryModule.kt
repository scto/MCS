package com.scto.mcs.core.data.di

import com.scto.mcs.core.data.repository.EditorRepositoryImpl
import com.scto.mcs.core.data.repository.GitRepositoryImpl
import com.scto.mcs.core.data.repository.ProjectRepositoryImpl
import com.scto.mcs.core.domain.repository.EditorRepository
import com.scto.mcs.core.domain.repository.GitRepository
import com.scto.mcs.core.domain.repository.ProjectRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindProjectRepository(
        projectRepositoryImpl: ProjectRepositoryImpl
    ): ProjectRepository

    @Binds
    @Singleton
    abstract fun bindGitRepository(
        gitRepositoryImpl: GitRepositoryImpl
    ): GitRepository

    @Binds
    @Singleton
    abstract fun bindEditorRepository(
        editorRepositoryImpl: EditorRepositoryImpl
    ): EditorRepository
}
