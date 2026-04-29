package com.scto.mcs.app.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.*

import com.scto.mcs.core.navigation.NavRoutes
import com.scto.mcs.app.ui.screens.HomeScreen
import com.scto.mcs.feature.editor.ui.EditorScreen
import com.scto.mcs.feature.editor.EditorViewModel

import com.scto.mcs.feature.settings.SettingsViewModel
import com.scto.mcs.core.ui.components.sidepanel.SidePanel
import com.scto.mcs.core.ui.components.sidepanel.SidePanelViewModel

import kotlinx.coroutines.launch

/**
 * Zentraler Screen der Anwendung.
 * Verwaltet das SidePanel im Drawer und den Navigations-Host.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    settingsViewModel: SettingsViewModel,
    sidePanelViewModel: SidePanelViewModel = hiltViewModel()
) {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    val editorViewModel: EditorViewModel = hiltViewModel()

    ModalNavigationDrawer(
        drawerState = drawerState,
        gesturesEnabled = true,
        drawerContent = {
            ModalDrawerSheet(modifier = Modifier.width(320.dp)) {
                SidePanel(
                    viewModel = sidePanelViewModel,
                    currentRoute = currentRoute,
                    onNavigate = { route ->
                        navController.navigate(route) {
                            popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                            launchSingleTop = true
                            restoreState = true
                        }
                        scope.launch { drawerState.close() }
                    }
                )
            }
        }
    ) {
        Scaffold(
            topBar = {
                CenterAlignedTopAppBar(
                    title = { Text("KW IDE") },
                    navigationIcon = {
                        IconButton(onClick = { scope.launch { drawerState.open() } }) {
                            Icon(Icons.Default.Menu, "Menü")
                        }
                    }
                )
            }
        ) { padding ->
            Box(modifier = Modifier.padding(padding).fillMaxSize()) {
                NavHost(
                    navController = navController,
                    startDestination = NavRoutes.EDITOR,
                    modifier = Modifier.fillMaxSize()
                ) {
                    composable(NavRoutes.EDITOR) { 
                        EditorScreen(settingsViewModel, editorViewModel) 
                    }
                    composable(NavRoutes.SETTINGS) { 
                        // SettingsScreen(settingsViewModel) 
                    }
                }
            }
        }
    }
}