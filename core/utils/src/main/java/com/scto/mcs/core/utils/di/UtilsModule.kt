package com.scto.mcs.core.utils.di

import com.scto.mcs.core.utils.FileSystemUtils
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object UtilsModule {

    @Provides
    @Singleton
    fun provideFileSystemUtils(): FileSystemUtils {
        return FileSystemUtils()
    }
}
