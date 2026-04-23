package com.srvhive.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.google.android.material.color.DynamicColors

import com.srvhive.app.ui.MainScreen
import com.srvhive.app.ui.theme.MCSTheme
import com.srvhive.app.ui.screens.SettingsViewModel

class MainActivity : ComponentActivity() {
    
    // ViewModel wird auf Activity-Ebene verwaltet
    private val settingsViewModel: SettingsViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        // Startbildschirm installieren
        installSplashScreen()
        
        // Edge-to-Edge global aktivieren
        enableEdgeToEdge()
        
        super.onCreate(savedInstanceState)

        // Material You Unterstützung
        DynamicColors.applyToActivitiesIfAvailable(this.application)

        setContent {
            // Reaktiviert das Theme bei Zustandsänderungen
            val customScheme = settingsViewModel.getActiveCustomColorScheme()

            MCSTheme(
                themeMode = settingsViewModel.themeMode,
                dynamicColor = settingsViewModel.isDynamicColorEnabled,
                amoled = settingsViewModel.isAmoledEnabled,
                customColorScheme = customScheme
            ) {
                MainScreen(settingsViewModel)
            }
        }
    }
}