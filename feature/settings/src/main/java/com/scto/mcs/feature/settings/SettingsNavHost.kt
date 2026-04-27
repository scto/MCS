package com.scto.mcs.feature.settings

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument

import com.scto.mcs.app.App
import com.scto.mcs.core.ui.animations.NavigationAnimationTransitions
import com.scto.mcs.core.network.lsp.LspRegistry
import com.scto.mcs.feature.settings.about.AboutScreen
import com.scto.mcs.feature.settings.app.SettingsAppScreen
import com.scto.mcs.feature.settings.debug.AppLogs
import com.scto.mcs.feature.settings.debug.DeveloperOptions
import com.scto.mcs.feature.settings.editor.*
import com.scto.mcs.feature.settings.extension.ExtensionDetail
import com.scto.mcs.feature.settings.extension.ExtensionScreen
import com.scto.mcs.feature.settings.git.GitSettings
import com.scto.mcs.feature.settings.keybinds.KeybindingsScreen
import com.scto.mcs.feature.settings.language.LanguageScreen
import com.scto.mcs.feature.settings.lsp.LspServerDetail
import com.scto.mcs.feature.settings.lsp.LspServerLogs
import com.scto.mcs.feature.settings.lsp.LspSettings
import com.scto.mcs.feature.settings.runners.HtmlRunnerSettings
import com.scto.mcs.feature.settings.runners.RunnerSettings
import com.scto.mcs.feature.settings.support.Support
import com.scto.mcs.feature.settings.terminal.SettingsTerminalScreen
import com.scto.mcs.feature.settings.terminal.TerminalExtraKeys
import com.scto.mcs.feature.settings.theme.ThemeScreen

@Composable
fun SettingsNavHost(
    navController: NavHostController, 
    activity: SettingsActivity,
    viewModel: SettingsViewModel
) {
    NavHost(
        navController = navController,
        startDestination = SettingsRoutes.Settings.route,
        enterTransition = { NavigationAnimationTransitions.enterTransition },
        exitTransition = { NavigationAnimationTransitions.exitTransition },
    ) {
        // Hauptbildschirm (Refaktoriertes SettingsScreen aus vorherigem Schritt)
        composable(SettingsRoutes.Settings.route) { 
            SettingsScreen(navController, viewModel) 
        }

        // App & System
        composable(SettingsRoutes.AppSettings.route) { SettingsAppScreen(activity, navController) }
        composable(SettingsRoutes.About.route) { AboutScreen() }
        composable(SettingsRoutes.Support.route) { Support() }
        composable(SettingsRoutes.LanguageScreen.route) { LanguageScreen() }

        // Editor Konfiguration
        composable(SettingsRoutes.EditorSettings.route) { SettingsEditorScreen(navController) }
        composable(SettingsRoutes.EditorFontScreen.route) { EditorFontScreen() }
        composable(SettingsRoutes.ToolbarActions.route) { EditToolbarActions() }
        composable(SettingsRoutes.DefaultEncoding.route) { DefaultEncoding() }
        composable(SettingsRoutes.DefaultLineEnding.route) { DefaultLineEnding() }
        
        // Terminal & Shell
        composable(SettingsRoutes.TerminalSettings.route) { SettingsTerminalScreen() }
        composable(SettingsRoutes.TerminalFontScreen.route) { TerminalFontScreen() }
        composable(SettingsRoutes.TerminalExtraKeys.route) { TerminalExtraKeys() }

        // Features (Git, LSP, Extensions)
        composable(SettingsRoutes.Git.route) { GitSettings() }
        composable(SettingsRoutes.Themes.route) { ThemeScreen() }
        composable(SettingsRoutes.Extensions.route) { ExtensionScreen(navController) }
        
        composable(
            "${SettingsRoutes.ExtensionDetail.route}/{extensionId}",
            arguments = listOf(navArgument("extensionId") { type = NavType.StringType }),
        ) { backStackEntry ->
            val id = backStackEntry.arguments?.getString("extensionId")
            val extension = id?.let { App.extensionManager.getExtension(it) }
            ExtensionDetail(extension)
        }

        // Debugging
        composable(SettingsRoutes.DeveloperOptions.route) { DeveloperOptions(navController) }
        composable(SettingsRoutes.AppLogs.route) { AppLogs() }

        // Runners & LSP
        composable(SettingsRoutes.Runners.route) { RunnerSettings(navController) }
        composable(SettingsRoutes.LspSettings.route) { LspSettings(navController) }
    }
}