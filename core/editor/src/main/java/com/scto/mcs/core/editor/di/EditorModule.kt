package com.scto.mcs.core.editor.di

import com.scto.mcs.core.editor.EditorConfigManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object EditorModule {

    @Provides
    @Singleton
    fun provideEditorConfigManager(): EditorConfigManager {
        return EditorConfigManager()
    }
}
