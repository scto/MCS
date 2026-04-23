package com.srvhive.app.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.*

import com.srvhive.app.navigation.NavRoutes
import com.srvhive.app.ui.screens.*

/**
 * Der MainScreen dient als Container für die NavigationRail und den NavHost.
 * Er reicht das SettingsViewModel an alle Unterbildschirme weiter.
 */
@Composable
fun MainScreen(settingsViewModel: SettingsViewModel) {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    // Wir stellen sicher, dass das EditorViewModel App-weit stabil bleibt,
    // damit geöffnete Tabs beim Navigieren nicht verloren gehen.
    val editorViewModel: EditorViewModel = viewModel()

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .safeDrawingPadding()
        ) {
            // Seiten-Navigation (Rail) für Tablets und Querformat-Handys optimiert
            NavigationRail(
                containerColor = MaterialTheme.colorScheme.surface
            ) {
                NavigationRailItem(
                    selected = currentDestination?.hierarchy?.any { it.route == NavRoutes.HOME } == true,
                    onClick = {
                        navController.navigate(NavRoutes.HOME) {
                            popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                            launchSingleTop = true
                            restoreState = true
                        }
                    },
                    icon = { Icon(Icons.Default.Home, "Start") },
                    label = { Text("Start") }
                )

                NavigationRailItem(
                    selected = currentDestination?.hierarchy?.any { it.route == NavRoutes.EDITOR } == true,
                    onClick = {
                        navController.navigate(NavRoutes.EDITOR) {
                            popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                            launchSingleTop = true
                            restoreState = true
                        }
                    },
                    icon = { Icon(Icons.Default.Edit, "Editor") },
                    label = { Text("Editor") }
                )

                NavigationRailItem(
                    selected = currentDestination?.hierarchy?.any { it.route == NavRoutes.SETTINGS } == true,
                    onClick = {
                        navController.navigate(NavRoutes.SETTINGS) {
                            popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                            launchSingleTop = true
                            restoreState = true
                        }
                    },
                    icon = { Icon(Icons.Default.Settings, "Optionen") },
                    label = { Text("Optionen") }
                )
            }

            // Inhaltsbereich
            NavHost(
                navController = navController,
                startDestination = NavRoutes.HOME,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(8.dp)
            ) {
                composable(NavRoutes.HOME) { HomeScreen() }
                composable(NavRoutes.EDITOR) { 
                    EditorScreen(settingsViewModel, editorViewModel) 
                }
                composable(NavRoutes.SETTINGS) { 
                    SettingsScreen(settingsViewModel) 
                }
            }
        }
    }
}