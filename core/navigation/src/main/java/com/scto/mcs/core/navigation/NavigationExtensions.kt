package com.scto.mcs.core.navigation

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.scto.mcs.feature.settings.SettingsScreen
import com.scto.mcs.feature.settings.SettingsViewModel

fun NavGraphBuilder.settingsScreen(navController: NavController) {
    composable(Routes.SETTINGS) {
        val viewModel: SettingsViewModel = hiltViewModel()
        
        SettingsScreen(
            navController = navController,
            currentThemeState = viewModel.themeState,
            logConfigState = viewModel.logConfigState,
            onThemeChange = viewModel::updateTheme,
            onLogConfigChange = viewModel::updateLogConfig,
            editorViewModel = null
        )
    }
}
