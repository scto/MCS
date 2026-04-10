package com.scto.mcs

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.scto.mcs.core.navigation.NavigationManager
import com.scto.mcs.core.navigation.Routes
import com.scto.mcs.core.ui.theme.MCSTheme
import com.scto.mcs.feature.dashboard.DashboardScreen
import com.scto.mcs.feature.editor.EditorScreen
import com.scto.mcs.feature.onboarding.OnboardingScreen
import com.scto.mcs.feature.setup.SetupScreen
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var navigationManager: NavigationManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MCSTheme {
                MCSNavHost()
            }
        }
    }

    @Composable
    fun MCSNavHost() {
        val navController = rememberNavController()
        navigationManager.setNavController(navController)

        NavHost(navController = navController, startDestination = Routes.ONBOARDING) {
            composable(Routes.ONBOARDING) {
                OnboardingScreen(onPermissionGranted = {
                    navigationManager.navigateTo(Routes.SETUP)
                })
            }
            composable(Routes.SETUP) {
                SetupScreen()
            }
            composable(Routes.DASHBOARD) {
                DashboardScreen(onNavigateToEditor = { path ->
                    navigationManager.navigateToEditor(path)
                })
            }
            composable(Routes.EDITOR) { backStackEntry ->
                val path = backStackEntry.arguments?.getString("projectPath")?.replace("|", "/") ?: ""
                EditorScreen(projectPath = path)
            }
        }
    }
}
