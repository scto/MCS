package com.scto.mcs.core.di

import com.scto.mcs.core.terminal.TerminalService

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
}