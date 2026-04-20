package com.scto.mcs.core.navigation

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable

// Hinweis: Diese Funktion setzt voraus, dass du eine Composable-Funktion 
// namens "SettingsScreen" im Modul :feature:settings hast.
// Importiere diese, sobald die Dateien verschoben wurden.
// import com.scto.mcs.feature.settings.SettingsScreen 

fun NavGraphBuilder.settingsScreen(onNavigateBack: () -> Unit) {
    composable(Routes.SETTINGS) {
        // hiltViewModel() injiziert das ViewModel automatisch
        // val viewModel: SettingsViewModel = hiltViewModel()
        
        // SettingsScreen(
        //     viewModel = viewModel,
        //     onNavigateBack = onNavigateBack
        // )
    }
}
