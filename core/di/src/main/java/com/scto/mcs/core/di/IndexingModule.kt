package com.scto.mcs.core.di

import com.scto.mcs.core.build_tools.indexing.api.ProjectIndexer
import com.scto.mcs.core.build_tools.indexing.impl.KotlinProjectIndexer
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class IndexingModule {

    @Binds
    @Singleton
    abstract fun bindProjectIndexer(
        impl: KotlinProjectIndexer
    ): ProjectIndexer
}