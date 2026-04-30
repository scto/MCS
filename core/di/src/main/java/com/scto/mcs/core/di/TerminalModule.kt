package com.scto.mcs.core.di

import com.scto.mcs.core.terminal.terminalold.TerminalService
import com.scto.mcs.core.terminal.terminalold.TerminalSessionManager
import com.scto.mcs.core.terminal.terminalold.TerminalSetupService
import com.scto.mcs.core.domain.repository.FileRepository

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
    fun provideTerminalSessionManager(): TerminalSessionManager = TerminalSessionManager()

    @Provides
    @Singleton
    fun provideTerminalSetupService(
        fileRepository: FileRepository
    ): TerminalSetupService = TerminalSetupService(fileRepository)

    @Provides
    @Singleton
    fun provideTerminalService(
        sessionManager: TerminalSessionManager,
        setupService: TerminalSetupService
    ): TerminalService = TerminalService(sessionManager, setupService)
}
