/*
 * Copyright (C) 2025 SC T O., Inc.
 */

package com.scto.mcs.feature.settings.ui

import androidx.compose.animation.*
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.scto.mcs.core.utils.LogConfigState
import com.scto.mcs.core.utils.ThemeState
import com.scto.mcs.feature.settings.R
import com.scto.mcs.feature.settings.viewModel.SettingsViewModel

// Routen für die interne Navigation innerhalb der Einstellungen
sealed class SettingsSubRoute {
    object Main : SettingsSubRoute()
    object General : SettingsSubRoute()
    object Editor : SettingsSubRoute()
    object FileExplorer : SettingsSubRoute()
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    navController: NavController,
    currentThemeState: ThemeState,
    logConfigState: LogConfigState,
    onThemeChange: (modeIndex: Int, themeIndex: Int, customColor: Color, isMonet: Boolean, isCustom: Boolean) -> Unit,
    onLogConfigChange: (enabled: Boolean, filePath: String) -> Unit,
    viewModel: SettingsViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    var currentSubScreen by remember { mutableStateOf<SettingsSubRoute>(SettingsSubRoute.Main) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(R.string.settings_title),
                        fontWeight = FontWeight.SemiBold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = {
                        if (currentSubScreen == SettingsSubRoute.Main) {
                            navController.popBackStack()
                        } else {
                            currentSubScreen = SettingsSubRoute.Main
                        }
                    }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = stringResource(R.string.settings_nav_back_content_description))
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            Crossfade(targetState = currentSubScreen, label = "SettingsTransition") { screen ->
                when (screen) {
                    SettingsSubRoute.Main -> MainSettingsList(
                        onNavigate = { currentSubScreen = it },
                        onAboutNavigate = { navController.navigate("about") }
                    )
                    SettingsSubRoute.General -> GeneralSettingsList(
                        settings = uiState.generalSettings,
                        viewModel = viewModel,
                        themeState = currentThemeState,
                        onThemeChange = onThemeChange
                    )
                    SettingsSubRoute.Editor -> EditorSettingsList(
                        settings = uiState.editorSettings,
                        viewModel = viewModel
                    )
                    SettingsSubRoute.FileExplorer -> FileExplorerSettingsList(
                        settings = uiState.fileExplorerSettings,
                        viewModel = viewModel,
                        logConfigState = logConfigState,
                        onLogConfigChange = onLogConfigChange
                    )
                }
            }
        }
    }
}

@Composable
private fun MainSettingsList(
    onNavigate: (SettingsSubRoute) -> Unit,
    onAboutNavigate: () -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item { SectionHeader("Konfiguration") }
        item {
            SimpleSettingsCard(
                icon = Icons.Outlined.Settings,
                title = stringResource(R.string.settings_general_title),
                subtitle = "Erscheinungsbild und Systemverhalten",
                onClick = { onNavigate(SettingsSubRoute.General) }
            )
        }
        item {
            SimpleSettingsCard(
                icon = Icons.Outlined.Code,
                title = stringResource(R.string.settings_editor_title),
                subtitle = "Schriftart, Einrückung und KI-Hilfe",
                onClick = { onNavigate(SettingsSubRoute.Editor) }
            )
        }
        item {
            SimpleSettingsCard(
                icon = Icons.Outlined.Folder,
                title = "Datei-Explorer",
                subtitle = "Versteckte Dateien und Log-Einstellungen",
                onClick = { onNavigate(SettingsSubRoute.FileExplorer) }
            )
        }

        item { Spacer(modifier = Modifier.height(16.dp)) }
        item { SectionHeader("Informationen") }
        item {
            SimpleSettingsCard(
                icon = Icons.Outlined.Info,
                title = stringResource(R.string.about_screen_title),
                subtitle = "Version, Team und Lizenzen",
                onClick = onAboutNavigate
            )
        }
    }
}

@Composable
private fun GeneralSettingsList(
    settings: com.scto.mcs.feature.settings.GeneralSettings,
    viewModel: SettingsViewModel,
    themeState: ThemeState,
    onThemeChange: (Int, Int, Color, Boolean, Boolean) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            ThemeSettingsItem(
                currentThemeState = themeState,
                onThemeChange = onThemeChange
            )
        }
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainer),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(8.dp)) {
                    CompactSwitchRow(
                        title = "Gesten im Drawer aktivieren",
                        checked = settings.enableGestureInDrawer,
                        onCheckedChange = viewModel::setEnableGestureInDrawer
                    )
                }
            }
        }
    }
}

@Composable
private fun EditorSettingsList(
    settings: com.scto.mcs.feature.settings.EditorSettings,
    viewModel: SettingsViewModel
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            EditorSettingsItem(
                settings = settings,
                viewModel = viewModel
            )
        }
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainer),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(8.dp)) {
                    CompactSwitchRow("Zeilennummern anzeigen", settings.showLineNumbers, viewModel::setShowLineNumbers)
                    CompactSwitchRow("Zeilenumbruch", settings.wordWrap, viewModel::setWordWrap)
                    CompactSwitchRow("Sticky Scroll", settings.stickyScroll, viewModel::setStickyScroll)
                    CompactSwitchRow("Font Ligatures", settings.fontLigatures, viewModel::setFontLigatures)
                }
            }
        }
    }
}

@Composable
private fun FileExplorerSettingsList(
    settings: com.scto.mcs.feature.settings.FileExplorerSettings,
    viewModel: SettingsViewModel,
    logConfigState: LogConfigState,
    onLogConfigChange: (Boolean, String) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainer),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(8.dp)) {
                    CompactSwitchRow("Versteckte Dateien anzeigen", settings.showHiddenFiles, viewModel::setShowHiddenFiles)
                }
            }
        }
        item {
            LogSettingsItem(
                logConfigState = logConfigState,
                onLogConfigChange = onLogConfigChange,
                onPathClick = { /* Selector Logik hier */ }
            )
        }
    }
}

@Composable
fun SectionHeader(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.labelLarge,
        color = MaterialTheme.colorScheme.primary,
        modifier = Modifier.padding(start = 4.dp, bottom = 4.dp)
    )
}

@Composable
fun CompactSwitchRow(title: String, checked: Boolean, onCheckedChange: (Boolean) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onCheckedChange(!checked) }
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(title, style = MaterialTheme.typography.bodyMedium)
        Switch(checked = checked, onCheckedChange = onCheckedChange)
    }
}

@Composable
fun SimpleSettingsCard(icon: androidx.compose.ui.graphics.vector.ImageVector, title: String, subtitle: String, onClick: () -> Unit) {
    Card(
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainer),
        modifier = Modifier.fillMaxWidth(),
        onClick = onClick
    ) {
        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Icon(icon, null, tint = MaterialTheme.colorScheme.primary)
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(title, style = MaterialTheme.typography.titleMedium)
                Text(subtitle, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant, maxLines = 1, overflow = TextOverflow.Ellipsis)
            }
            Icon(Icons.Default.ChevronRight, null, tint = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

@Composable
fun ThemeSettingsItem(
    currentThemeState: ThemeState,
    onThemeChange: (Int, Int, Color, Boolean, Boolean) -> Unit
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainer),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Outlined.Palette, null, tint = MaterialTheme.colorScheme.primary)
                Spacer(Modifier.width(16.dp))
                Text("Erscheinungsbild", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            }
            
            Spacer(Modifier.height(16.dp))
            
            Text("Modus", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.primary)
            Row(modifier = Modifier.padding(top = 8.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                val modes = listOf("System", "Hell", "Dunkel")
                modes.forEachIndexed { index, label ->
                    val isSelected = currentThemeState.selectedModeIndex == index
                    FilterChip(
                        selected = isSelected,
                        onClick = { onThemeChange(index, currentThemeState.selectedThemeIndex, currentThemeState.customColor, currentThemeState.isMonetEnabled, currentThemeState.isCustomTheme) },
                        label = { Text(label) },
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
    }
}

@Composable
fun EditorSettingsItem(
    settings: com.scto.mcs.feature.settings.EditorSettings,
    viewModel: SettingsViewModel
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainer),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("Editor-Optionen", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(16.dp))
            
            Text("Schriftgröße: ${settings.fontSize.toInt()} sp", style = MaterialTheme.typography.bodyMedium)
            Slider(
                value = settings.fontSize,
                onValueChange = viewModel::setFontSize,
                valueRange = 8f..30f,
                steps = 21
            )
            
            Spacer(Modifier.height(8.dp))
            Text("Tab-Breite", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.primary)
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                listOf(2, 4, 8).forEach { size ->
                    val isSelected = settings.indentSize == size
                    ElevatedAssistChip(
                        onClick = { viewModel.setIndentSize(size) },
                        label = { Text("$size Spaces") },
                        colors = if (isSelected) AssistChipDefaults.elevatedAssistChipColors(containerColor = MaterialTheme.colorScheme.primaryContainer) 
                                 else AssistChipDefaults.elevatedAssistChipColors()
                    )
                }
            }
        }
    }
}

@Composable
fun LogSettingsItem(
    logConfigState: LogConfigState,
    onLogConfigChange: (Boolean, String) -> Unit,
    onPathClick: () -> Unit
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainer),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Outlined.BugReport, null, tint = MaterialTheme.colorScheme.primary)
                Spacer(modifier = Modifier.width(16.dp))
                Text("Logging aktivieren", modifier = Modifier.weight(1f))
                Switch(checked = logConfigState.isLogEnabled, onCheckedChange = { onLogConfigChange(it, logConfigState.logFilePath) })
            }
            if (logConfigState.isLogEnabled) {
                TextButton(onClick = onPathClick) {
                    Text(logConfigState.logFilePath, maxLines = 1, overflow = TextOverflow.Ellipsis)
                }
            }
        }
    }
}
