package com.scto.mcs.core.di

import com.scto.mcs.core.terminal.TerminalService
import com.scto.mcs.core.terminal.TerminalSessionFactory
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
    fun provideTerminalService(service: TerminalService): TerminalService = service

    @Provides
    @Singleton
    fun provideTerminalSessionFactory(): TerminalSessionFactory = TerminalSessionFactory()

    @Provides
    @Singleton
    fun provideTerminalSessionManager(
        tabManager: TabManager,
        sessionFactory: TerminalSessionFactory
    ): TerminalSessionManager = TerminalSessionManager(tabManager, sessionFactory)
}
