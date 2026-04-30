package com.scto.mcs.core.di

import com.scto.mcs.core.terminal.TerminalService
import com.scto.mcs.core.terminal.TerminalServiceImpl
import com.scto.mcs.core.terminal.session.TerminalSessionManager

import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class TerminalModule {

    @Binds
    @Singleton
    abstract fun bindTerminalService(impl: TerminalServiceImpl): TerminalService

    companion object {
        @Provides
        @Singleton
        fun provideTerminalSessionManager(): TerminalSessionManager = TerminalSessionManager()
    }
}