package com.scto.mcs.core.di

import com.scto.mcs.core.terminal.TerminalService
import com.scto.mcs.core.terminal.session.TerminalSessionManager
import com.scto.mcs.core.terminal.session.TabManager
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
    fun provideTerminalService(): TerminalService = TerminalService()

    @Provides
    @Singleton
    fun provideTerminalSessionManager(tabManager: TabManager): TerminalSessionManager = 
        TerminalSessionManager(tabManager)
}
