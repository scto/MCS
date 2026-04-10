package com.scto.mcs.core.terminal.di

import com.scto.mcs.core.terminal.TerminalEnvironment
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object TerminalModule {

    @Provides
    @Singleton
    fun provideTerminalEnvironment(): TerminalEnvironment {
        return TerminalEnvironment()
    }
}
