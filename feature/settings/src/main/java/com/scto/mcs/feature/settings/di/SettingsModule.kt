package com.scto.mcs.feature.settings.di

import com.scto.mcs.feature.settings.ui.SettingsViewModel
import com.scto.mcs.core.navigation.NavigationManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.scopes.ViewModelScoped

@Module
@InstallIn(ViewModelComponent::class)
object SettingsModule {

    @Provides
    @ViewModelScoped
    fun provideSettingsViewModel(navigationManager: NavigationManager): SettingsViewModel {
        return SettingsViewModel(navigationManager)
    }
}
