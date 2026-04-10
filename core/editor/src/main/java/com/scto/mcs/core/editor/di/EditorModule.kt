package com.scto.mcs.core.editor.di

import android.content.Context
import com.scto.mcs.core.editor.EditorConfigManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object EditorModule {

    @Provides
    @Singleton
    fun provideEditorConfigManager(@ApplicationContext context: Context): EditorConfigManager {
        return EditorConfigManager(context)
    }
}
