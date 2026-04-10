package com.scto.mcs.core.terminal.di

import android.content.Context
import com.scto.mcs.core.terminal.TerminalEnvironment
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object TerminalModule {

    @Provides
    @Singleton
    fun provideTerminalEnvironment(@ApplicationContext context: Context): TerminalEnvironment {
        return TerminalEnvironment(context)
    }
}
