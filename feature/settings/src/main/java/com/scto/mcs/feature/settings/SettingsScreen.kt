package com.scto.mcs.feature.settings

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

import com.scto.mcs.core.resources.drawables
import com.scto.mcs.core.resources.getFilledString
import com.scto.mcs.core.resources.getString
import com.scto.mcs.core.resources.strings
import com.scto.mcs.core.ui.components.compose.preferences.base.PreferenceLayout
import com.scto.mcs.core.ui.components.compose.preferences.base.PreferenceTemplate
import com.scto.mcs.core.ui.components.compose.preferences.category.PreferenceCategory
import com.scto.mcs.feature.settings.app.InbuiltFeatures

// Enums für Zustände (hier beibehalten für Kompatibilität)
enum class ThemeMode { SYSTEM, LIGHT, DARK }
enum class FileSortBy { NAME, TYPE, SIZE }
enum class FileSortOrder { ASCENDING, DESCENDING }

/**
 * Hauptbildschirm für die Einstellungen, nutzt die Struktur von SettingsScreenXed.
 */
@Composable
fun SettingsScreen(navController: NavController, viewModel: SettingsViewModel) {
    PreferenceLayout(
        label = stringResource(id = strings.settings), 
        backArrowVisible = true
    ) {
        Categories(navController)
    }
}

@Composable
private fun Categories(navController: NavController) {
    // App Einstellungen
    PreferenceCategory(
        label = stringResource(id = strings.app),
        description = stringResource(id = strings.app_desc),
        iconResource = drawables.android,
        onNavigate = { navController.navigate(SettingsRoutes.AppSettings.route) },
    )

    // Themes
    PreferenceCategory(
        label = stringResource(strings.themes),
        description = stringResource(strings.theme_settings),
        iconResource = drawables.palette,
        onNavigate = { navController.navigate(SettingsRoutes.Themes.route) },
    )

    // Editor
    PreferenceCategory(
        label = stringResource(id = strings.editor),
        description = stringResource(id = strings.editor_desc),
        iconResource = drawables.edit_note,
        onNavigate = { navController.navigate(SettingsRoutes.EditorSettings.route) },
    )

    // Keybindings
    PreferenceCategory(
        label = stringResource(strings.keybindings),
        description = stringResource(strings.keybindings_desc),
        iconResource = drawables.keyboard,
        onNavigate = { navController.navigate(SettingsRoutes.Keybindings.route) },
    )

    // Git (Bedingt)
    if (InbuiltFeatures.git.state.value) {
        PreferenceCategory(
            label = stringResource(strings.git),
            description = stringResource(strings.git_desc),
            iconResource = drawables.git,
            onNavigate = { navController.navigate(SettingsRoutes.Git.route) },
        )
    }

    // Terminal & Runners (Bedingt)
    if (InbuiltFeatures.terminal.state.value) {
        PreferenceCategory(
            label = stringResource(id = strings.terminal),
            description = stringResource(id = strings.terminal_desc),
            iconResource = drawables.terminal,
            onNavigate = { navController.navigate(SettingsRoutes.TerminalSettings.route) },
        )

        PreferenceCategory(
            label = stringResource(id = strings.runners),
            description = stringResource(id = strings.runners_desc),
            iconResource = drawables.run,
            onNavigate = { navController.navigate(SettingsRoutes.Runners.route) },
        )
    }

    // Extensions (Bedingt)
    if (InbuiltFeatures.extensions.state.value) {
        PreferenceCategory(
            label = stringResource(strings.ext),
            description = stringResource(strings.ext_desc),
            iconResource = drawables.extension,
            onNavigate = { navController.navigate(SettingsRoutes.Extensions.route) },
        )
    }

    // Debug Optionen
    if (InbuiltFeatures.debugMode.state.value) {
        PreferenceCategory(
            label = stringResource(strings.debug_options),
            description = strings.debug_options_desc.getFilledString(strings.app_name.getString()),
            iconResource = drawables.build,
            onNavigate = { navController.navigate(SettingsRoutes.DeveloperOptions.route) },
        )
    }

    // Über / About
    PreferenceTemplate(
        modifier = Modifier
            .padding(horizontal = 16.dp)
            .clip(MaterialTheme.shapes.large)
            .clickable { navController.navigate(SettingsRoutes.About.route) }
            .background(Color.Transparent),
        verticalPadding = 14.dp,
        title = { Text(stringResource(id = strings.about)) },
        description = { Text(stringResource(id = strings.about_desc)) },
        startWidget = {
            Box(contentAlignment = Alignment.Center, modifier = Modifier.size(32.dp)) {
                Icon(
                    imageVector = Icons.Outlined.Info,
                    contentDescription = null,
                    modifier = Modifier.size(24.dp),
                    tint = MaterialTheme.colorScheme.primary,
                )
            }
        },
    )

    // Support mit Heartbeat
    PreferenceTemplate(
        modifier = Modifier
            .padding(horizontal = 16.dp)
            .clip(MaterialTheme.shapes.large)
            .clickable { navController.navigate(SettingsRoutes.Support.route) }
            .background(Color.Transparent),
        verticalPadding = 14.dp,
        title = { Text(stringResource(strings.support)) },
        description = { Text(stringResource(id = strings.support_desc)) },
        startWidget = { HeartbeatIcon() },
    )
}

@Composable
fun HeartbeatIcon() {
    val infiniteTransition = rememberInfiniteTransition(label = "heartbeat")
    val scale = infiniteTransition.animateFloat(
        initialValue = 0.9f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 500, easing = LinearOutSlowInEasing),
            repeatMode = RepeatMode.Reverse,
        ),
        label = "scale",
    )

    Box(contentAlignment = Alignment.Center, modifier = Modifier.size(32.dp).scale(scale.value)) {
        Icon(
            imageVector = if (Settings.donated) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
            contentDescription = null,
            modifier = Modifier.size(24.dp),
            tint = MaterialTheme.colorScheme.primary,
        )
    }
}