package com.scto.mcs.core.utils.di

import android.content.Context
import com.scto.mcs.core.utils.FileSystemUtils
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object UtilsModule {

    @Provides
    @Singleton
    fun provideFileSystemUtils(@ApplicationContext context: Context): FileSystemUtils {
        return FileSystemUtils(context)
    }
}
