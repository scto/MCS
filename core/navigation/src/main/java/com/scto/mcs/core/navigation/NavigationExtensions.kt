package com.scto.mcs.core.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.scto.mcs.feature.settings.SettingsScreen
import com.scto.mcs.feature.settings.SettingsViewModel

fun NavGraphBuilder.settingsScreen(navController: NavController) {
    composable(Routes.SETTINGS) {
        val viewModel: SettingsViewModel = hiltViewModel()
        
        // Wir sammeln den State hier, um ihn an den Screen zu übergeben
        // In einer echten App würde man den State direkt im ViewModel halten
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
