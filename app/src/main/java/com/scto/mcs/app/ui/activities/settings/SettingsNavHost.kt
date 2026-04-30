package com.scto.mcs.app.ui.activities.settings

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument

import com.scto.mcs.app.App
import com.scto.mcs.core.ui.animations.NavigationAnimationTransitions
import com.scto.mcs.core.editor.lsp.LspRegistry
import com.scto.mcs.feature.settings.SettingsScreen
import com.scto.mcs.feature.settings.about.AboutScreen
import com.scto.mcs.feature.settings.app.SettingsAppScreen
import com.scto.mcs.feature.settings.debugOptions.AppLogs
import com.scto.mcs.feature.settings.debugOptions.DeveloperOptions
import com.scto.mcs.feature.settings.editor.AppFontScreen
import com.scto.mcs.feature.settings.editor.DefaultEncoding
import com.scto.mcs.feature.settings.editor.DefaultLineEnding
import com.scto.mcs.feature.settings.editor.EditExtraKeys
import com.scto.mcs.feature.settings.editor.EditToolbarActions
import com.scto.mcs.feature.settings.editor.EditorFontScreen
import com.scto.mcs.feature.settings.editor.ExcludeFiles
import com.scto.mcs.feature.settings.editor.SettingsEditorScreen
import com.scto.mcs.feature.settings.editor.TerminalFontScreen
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
fun SettingsNavHost(navController: NavHostController, activity: SettingsActivity) {
    NavHost(
        navController = navController,
        startDestination = SettingsRoutes.Settings.route,
        enterTransition = { NavigationAnimationTransitions.enterTransition },
        exitTransition = { NavigationAnimationTransitions.exitTransition },
        popEnterTransition = { NavigationAnimationTransitions.popEnterTransition },
        popExitTransition = { NavigationAnimationTransitions.popExitTransition },
    ) {
        composable(SettingsRoutes.Settings.route) { SettingsScreen(navController) }
        composable(SettingsRoutes.AppSettings.route) { SettingsAppScreen(activity, navController) }
        composable(SettingsRoutes.EditorSettings.route) { SettingsEditorScreen(navController) }
        composable(SettingsRoutes.Keybindings.route) { KeybindingsScreen() }
        composable(SettingsRoutes.TerminalSettings.route) { SettingsTerminalScreen() }
        composable(SettingsRoutes.TerminalExtraKeys.route) { TerminalExtraKeys() }
        composable(SettingsRoutes.About.route) { AboutScreen() }
        composable(SettingsRoutes.EditorFontScreen.route) { EditorFontScreen() }
        composable(SettingsRoutes.AppFontScreen.route) { AppFontScreen() }
        composable(SettingsRoutes.TerminalFontScreen.route) { TerminalFontScreen() }
        composable(SettingsRoutes.DefaultEncoding.route) { DefaultEncoding() }
        composable(SettingsRoutes.DefaultLineEnding.route) { DefaultLineEnding() }
        composable(SettingsRoutes.ToolbarActions.route) { EditToolbarActions() }
        composable(SettingsRoutes.ExtraKeys.route) { EditExtraKeys() }
        composable(
            "${SettingsRoutes.ExcludeFiles.route}/{isDrawer}",
            arguments = listOf(navArgument("isDrawer", builder = { type = NavType.BoolType })),
        ) {
            val isDrawer = it.arguments?.getBoolean("isDrawer")!!
            ExcludeFiles(isDrawer)
        }
        composable(SettingsRoutes.DeveloperOptions.route) { DeveloperOptions(navController = navController) }
        composable(SettingsRoutes.AppLogs.route) { AppLogs() }
        composable(SettingsRoutes.Support.route) { Support() }
        composable(SettingsRoutes.LanguageScreen.route) { LanguageScreen() }
        composable(SettingsRoutes.Runners.route) { RunnerSettings(navController = navController) }
        composable(SettingsRoutes.HtmlRunner.route) { HtmlRunnerSettings() }
        composable(SettingsRoutes.LspSettings.route) { LspSettings(navController = navController) }
        composable(
            "${SettingsRoutes.LspServerDetail.route}/{serverId}",
            arguments = listOf(navArgument("serverId", builder = { type = NavType.StringType })),
        ) { backStackEntry ->
            val serverId = backStackEntry.arguments?.getString("serverId")!!
            val server = LspRegistry.getForId(serverId)!!
            LspServerDetail(navController, server)
        }
        composable(
            "${SettingsRoutes.LspServerLogs.route}/{serverId}/{instanceId}",
            arguments =
                listOf(
                    navArgument("serverId", builder = { type = NavType.StringType }),
                    navArgument("instanceId", builder = { type = NavType.StringType }),
                ),
        ) { backStackEntry ->
            val serverId = backStackEntry.arguments?.getString("serverId")!!
            val server = LspRegistry.getForId(serverId)!!
            val instanceId = backStackEntry.arguments?.getString("instanceId")!!
            LspServerLogs(server, instanceId)
        }
        composable(SettingsRoutes.Themes.route) { ThemeScreen() }
        composable(SettingsRoutes.Extensions.route) { ExtensionScreen(navController = navController) }
        composable(
            "${SettingsRoutes.ExtensionDetail.route}/{extensionId}",
            arguments = listOf(navArgument("extensionId", builder = { type = NavType.StringType })),
        ) {
            val extensionId = it.arguments?.getString("extensionId")
            val extension = extensionId?.let { App.extensionManager.getExtension(it) }
            ExtensionDetail(extension)
        }
        composable(SettingsRoutes.Git.route) { GitSettings() }
    }
}
