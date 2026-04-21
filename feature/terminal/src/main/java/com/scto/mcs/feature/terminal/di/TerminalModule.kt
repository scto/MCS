package com.scto.mcs.feature.terminal.di

import com.scto.mcs.feature.terminal.TerminalViewModel
import com.scto.mcs.core.navigation.NavigationManager

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.scopes.ViewModelScoped

@Module
@InstallIn(ViewModelComponent::class)
object TerminalModule {

    @Provides
    @ViewModelScoped
    fun provideTerminalViewModel(navigationManager: NavigationManager): TerminalModuleViewModel {
        return TerminalModuleViewModelViewModel(navigationManager)
    }
}
