package com.scto.mcs.feature.settings

import android.os.Bundle

import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels

import androidx.appcompat.app.AppCompatActivity
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember

import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController

import com.scto.mcs.core.files.FileManager
import com.scto.mcs.core.resources.strings
import com.scto.mcs.core.ui.theme.McsTheme
import com.scto.mcs.core.utils.toast

import dagger.hilt.android.AndroidEntryPoint

import java.lang.ref.WeakReference

// Globale Referenzen für den Zugriff von außerhalb der Compose-Hierarchie
var settingsNavController = WeakReference<NavController?>(null)
var snackbarHostStateRef: WeakReference<SnackbarHostState?> = WeakReference(null)

/**
 * Die Haupt-Activity für alle App-Einstellungen.
 * Nutzt [AndroidEntryPoint] für Hilt-Integration.
 */
@AndroidEntryPoint
class SettingsActivity : AppCompatActivity() {
    // Initialisierung des ViewModels über den Hilt-Delegate
    private val viewModel: SettingsViewModel by viewModels()
    
    // FileManager Instanz
    val fileManager by lazy { FileManager(this) }

    companion object {
        private var activityRef = WeakReference<SettingsActivity?>(null)
        var instance: SettingsActivity?
            get() = activityRef.get()
            private set(value) {
                activityRef = WeakReference(value)
            }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        instance = this
        enableEdgeToEdge()

        setContent {
            McsTheme {
                Surface {
                    val navController = rememberNavController()
                    settingsNavController = WeakReference(navController)

                    // Aufruf des NavHosts mit Übergabe des Activities und des ViewModels
                    SettingsNavHost(
                        navController = navController,
                        activity = this@SettingsActivity,
                        viewModel = viewModel
                    )

                    // Snackbar-Management
                    val snackbarHostState = remember { SnackbarHostState() }
                    LaunchedEffect(snackbarHostState) {
                        snackbarHostStateRef = WeakReference(snackbarHostState)
                    }

                    // Deep-Linking / Routen-Handling über Intents
                    LaunchedEffect(intent) {
                        if (intent.hasExtra("route")) {
                            val route = intent.getStringExtra("route")
                            if (route != null) {
                                navController.navigate(route)
                            } else {
                                toast(strings.unknown_err)
                            }
                        }
                    }
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        if (instance == this) {
            instance = null
        }
    }
}
