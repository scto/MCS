package com.scto.mcs.feature.settings

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.Contrast
import androidx.compose.material.icons.filled.Create
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.FormatIndentIncrease
import androidx.compose.material.icons.filled.FormatSize
import androidx.compose.material.icons.filled.Gesture
import androidx.compose.material.icons.filled.Link
import androidx.compose.material.icons.filled.Numbers
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.PushPin
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Tab
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material.icons.filled.WrapText
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.scto.mcs.core.navigation.NavigationManager
import com.scto.mcs.core.ui.theme.McsTheme // Assuming McsTheme is available

// Define custom colors to match the dark theme in screenshots (Schwarz/Dunkelviolett-Stich)
val DarkPurpleBackground = Color(0xFF1E1624) // A dark purple-ish background
val AccentPurple = Color(0xFFBB86FC) // Light purple accent, similar to screenshots
val LightGrayText = Color(0xFFAAAAAA) // Lighter gray for sub-descriptions
val MediumGrayText = Color(0xFF888888) // Medium gray for some descriptions

sealed class SettingsScreenRoute(val route: String) {
    object Main : SettingsScreenRoute("main")
    object General : SettingsScreenRoute("general")
    object Editor : SettingsScreenRoute("editor")
    object FileExplorer : SettingsScreenRoute("file_explorer")
    object Plugins : SettingsScreenRoute("plugins")
    object AboutGitHub : SettingsScreenRoute("about_github")
    object OpenSourceLicenses : SettingsScreenRoute("open_source_licenses")
    object LoginGithub : SettingsScreenRoute("login_github") // Not a screen, but an action item
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    var currentScreen by remember { mutableStateOf<SettingsScreenRoute>(SettingsScreenRoute.Main) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Settings",
                        color = AccentPurple // Changed to AccentPurple as per screenshot
                    )
                },
                navigationIcon = {
                    if (currentScreen != SettingsScreenRoute.Main) {
                        IconButton(onClick = { currentScreen = SettingsScreenRoute.Main }) {
                            Icon(
                                imageVector = Icons.Default.ArrowBack,
                                contentDescription = "Back",
                                tint = AccentPurple // Changed to AccentPurple as per screenshot
                            )
                        }
                    } else {
                        IconButton(onClick = { viewModel.onBackClicked() }) {
                            Icon(
                                imageVector = Icons.Default.ArrowBack,
                                contentDescription = "Back",
                                tint = AccentPurple // Changed to AccentPurple as per screenshot
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = DarkPurpleBackground,
                    titleContentColor = AccentPurple,
                    actionIconContentColor = AccentPurple,
                    navigationIconContentColor = AccentPurple
                )
            )
        },
        containerColor = DarkPurpleBackground
    ) { paddingValues ->
        Box(modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues)
            .background(DarkPurpleBackground)) {
            Crossfade(targetState = currentScreen, label = "SettingsNav") { screen ->
                when (screen) {
                    SettingsScreenRoute.Main -> MainSettingsMenu(onNavigate = { currentScreen = it })
                    SettingsScreenRoute.General -> GeneralSettingsScreen(uiState.generalSettings, viewModel)
                    SettingsScreenRoute.Editor -> EditorSettingsScreen(uiState.editorSettings, viewModel)
                    SettingsScreenRoute.FileExplorer -> FileExplorerSettingsScreen(uiState.fileExplorerSettings, viewModel)
                    SettingsScreenRoute.Plugins -> PluginsSettingsScreen(uiState.isPluginsLoading, uiState.installedPlugins, viewModel)
                    SettingsScreenRoute.AboutGitHub -> { /* TODO: Implement About GitHub Screen */ }
                    SettingsScreenRoute.OpenSourceLicenses -> { /* TODO: Implement Open Source Licenses Screen */ }
                    SettingsScreenRoute.LoginGithub -> { /* TODO: Trigger Login with GitHub */ }
                }
            }
        }
    }
}

@Composable
fun MainSettingsMenu(onNavigate: (SettingsScreenRoute) -> Unit) {
    LazyColumn(modifier = Modifier.fillMaxSize()) {
        item { SettingsCategoryHeader("Configure") }
        item {
            SettingsMenuItem(
                title = "General",
                description = "General application settings.",
                onClick = { onNavigate(SettingsScreenRoute.General) }
            )
        }
        item {
            SettingsMenuItem(
                title = "Editor",
                description = "General editor settings.",
                onClick = { onNavigate(SettingsScreenRoute.Editor) }
            )
        }
        item {
            SettingsMenuItem(
                title = "File Explorer",
                description = "General file explorer settings.",
                onClick = { onNavigate(SettingsScreenRoute.FileExplorer) }
            )
        }
        item {
            SettingsMenuItem(
                title = "Plugins",
                description = "Manage plugins",
                onClick = { onNavigate(SettingsScreenRoute.Plugins) }
            )
        }
        item {
            SettingsMenuItem(
                title = "Login with GitHub",
                description = null,
                onClick = { /* TODO: Implement GitHub Login */ onNavigate(SettingsScreenRoute.LoginGithub) }
            )
        }

        item { Spacer(modifier = Modifier.height(16.dp)) }

        item { SettingsCategoryHeader("About") }
        item {
            SettingsMenuItem(
                title = "GitHub",
                description = "Visual Code Space is open source!",
                onClick = { /* TODO: Open GitHub Link */ onNavigate(SettingsScreenRoute.AboutGitHub) }
            )
        }
        item {
            SettingsMenuItem(
                title = "Open Source Licences",
                description = null,
                onClick = { /* TODO: Show Licenses */ onNavigate(SettingsScreenRoute.OpenSourceLicenses) }
            )
        }
    }
}

@Composable
fun SettingsCategoryHeader(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.titleMedium,
        fontWeight = FontWeight.Bold,
        color = LightGrayText,
        modifier = Modifier.padding(start = 16.dp, top = 24.dp, bottom = 8.dp)
    )
}

@Composable
fun SettingsMenuItem(
    title: String,
    description: String?,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 4.dp)
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
            description?.let {
                Text(
                    text = it,
                    style = MaterialTheme.typography.bodySmall,
                    color = MediumGrayText
                )
            }
        }
    }
}


@Composable
fun GeneralSettingsScreen(
    generalSettings: GeneralSettings,
    viewModel: SettingsViewModel
) {
    LazyColumn(modifier = Modifier.fillMaxSize()) {
        item { SettingsCategoryHeader("General") }
        item {
            SettingsToggleItem(
                icon = Icons.Default.Palette,
                title = "Follow System Theme",
                description = "Following system theme settings",
                checked = generalSettings.followSystemTheme,
                onCheckedChange = viewModel::setFollowSystemTheme
            )
        }
        item {
            SettingsToggleItem(
                icon = Icons.Default.Settings,
                title = "Use Dark Mode",
                description = "Dark mode is enabled",
                checked = generalSettings.useDarkMode,
                onCheckedChange = viewModel::setUseDarkMode
            )
        }
        item {
            SettingsToggleItem(
                icon = Icons.Default.Contrast,
                title = "Use Amoled Mode",
                description = "AMOLED mode is disabled",
                checked = generalSettings.useAmoledMode,
                onCheckedChange = viewModel::setUseAmoledMode
            )
        }
        item {
            SettingsToggleItem(
                icon = Icons.Default.Palette,
                title = "Dynamic Colors",
                description = "Dynamic color mode is enabled",
                checked = generalSettings.dynamicColors,
                onCheckedChange = viewModel::setDynamicColors
            )
        }
        item {
            SettingsToggleItem(
                icon = Icons.Default.Gesture,
                title = "Enable gesture in drawer",
                description = "Enable gesture in file tree drawer",
                checked = generalSettings.enableGestureInDrawer,
                onCheckedChange = viewModel::setEnableGestureInDrawer
            )
        }
    }
}

@Composable
fun EditorSettingsScreen(
    editorSettings: EditorSettings,
    viewModel: SettingsViewModel
) {
    LazyColumn(modifier = Modifier.fillMaxSize()) {
        item { SettingsCategoryHeader("Editor") }
        item {
            SettingsDisplayItem(
                icon = Icons.Default.Edit,
                title = "Current Editor (Sora)",
                description = "Prioritizes stability for a smooth editing experience."
            )
        }
        item {
            SettingsDisplayItem(
                icon = Icons.Default.Create,
                title = "Typing Tip",
                description = "For optimal typing efficiency, explore keyboard customization options."
            )
        }
        item {
            SettingsToggleItem(
                icon = Icons.Default.Settings, // Changed to Settings icon
                title = "Show Input Method Picker at Start",
                description = "Show the input method picker at start.",
                checked = editorSettings.showInputMethodPickerAtStart,
                onCheckedChange = viewModel::setShowInputMethodPickerAtStart
            )
        }

        item { SettingsCategoryHeader("Editor Settings") }
        item {
            SettingsSliderItem(
                icon = Icons.Default.FormatSize, // Changed to FormatSize icon
                title = "Font Size",
                value = editorSettings.fontSize,
                range = 8f..30f,
                steps = 21,
                onValueChange = viewModel::setFontSize,
                valueLabel = "${editorSettings.fontSize.toInt()} sp"
            )
        }
        item {
            SettingsDisplayItem(
                icon = Icons.Default.FormatIndentIncrease, // Changed to FormatIndentIncrease icon
                title = "Indent Size",
                description = "Indent size: ${editorSettings.indentSize} spaces."
            )
        }
        item {
            SettingsDisplayItem(
                icon = Icons.Default.Create,
                title = "Font Family",
                description = "Choose the font family for the editor"
            )
        }
        item {
            SettingsDisplayItem(
                icon = Icons.Default.Palette,
                title = "Color Scheme",
                description = "Choose a color scheme for the editor"
            )
        }
        item {
            SettingsDisplayItem(
                icon = Icons.Default.Code, // Changed to Code icon
                title = "Symbols",
                description = "!@#\$%^&*()-_+=[]{}|\\;:'\",.<>/?"
            )
        }

        item {
            SettingsToggleItem(
                icon = Icons.Default.Link, // Changed to Link icon
                title = "Font Ligatures",
                description = "Ligatures disabled",
                checked = editorSettings.fontLigatures,
                onCheckedChange = viewModel::setFontLigatures
            )
        }
        item {
            SettingsToggleItem(
                icon = Icons.Default.PushPin, // Changed to PushPin icon
                title = "Sticky Scroll",
                description = "Sticky scroll disabled",
                checked = editorSettings.stickyScroll,
                onCheckedChange = viewModel::setStickyScroll
            )
        }
        item {
            SettingsToggleItem(
                icon = Icons.Default.WrapText, // Changed to WrapText icon
                title = "Word Wrap",
                description = "Word wrap disabled",
                checked = editorSettings.wordWrap,
                onCheckedChange = viewModel::setWordWrap
            )
        }
        item {
            SettingsToggleItem(
                icon = Icons.Default.Numbers, // Changed to Numbers icon
                title = "Show Line Numbers",
                description = "Line numbers displayed",
                checked = editorSettings.showLineNumbers,
                onCheckedChange = viewModel::setShowLineNumbers
            )
        }
        item {
            SettingsToggleItem(
                icon = Icons.Default.Tab, // Changed to Tab icon
                title = "Use Tabs",
                description = "Tabs are used",
                checked = editorSettings.useTabs,
                onCheckedChange = viewModel::setUseTabs
            )
        }
        item {
            SettingsToggleItem(
                icon = Icons.Default.Delete, // Changed to actual Delete icon
                title = "Delete Line on Backspace",
                description = "Delete entire line when backspace is pressed",
                checked = editorSettings.deleteLineOnBackspace,
                onCheckedChange = viewModel::setDeleteLineOnBackspace
            )
        }
        item {
            SettingsToggleItem(
                icon = Icons.Default.Delete, // Changed to actual Delete icon
                title = "Delete Indent on Backspace",
                description = "Normal backspace behavior",
                checked = editorSettings.deleteIndentOnBackspace,
                onCheckedChange = viewModel::setDeleteIndentOnBackspace
            )
        }
        item {
            SettingsDisplayItem(
                icon = Icons.Default.Build,
                title = "Editor Text Action Window Expand Threshold",
                description = "Threshold for expanding the text action window"
            )
        }
    }
}

@Composable
fun FileExplorerSettingsScreen(
    fileExplorerSettings: FileExplorerSettings,
    viewModel: SettingsViewModel
) {
    LazyColumn(modifier = Modifier.fillMaxSize()) {
        item { SettingsCategoryHeader("File Settings") }
        item {
            SettingsToggleItem(
                icon = Icons.Default.VisibilityOff,
                title = "Show Hidden Files",
                description = "Hidden files are not displayed",
                checked = fileExplorerSettings.showHiddenFiles,
                onCheckedChange = viewModel::setShowHiddenFiles
            )
        }
    }
}

@Composable
fun PluginsSettingsScreen(
    isLoading: Boolean,
    installedPlugins: List<String>,
    viewModel: SettingsViewModel
) {
    Box(modifier = Modifier.fillMaxSize()) {
        if (isLoading) {
            Text(
                text = "Loading plugins...",
                style = MaterialTheme.typography.bodyLarge,
                color = LightGrayText,
                modifier = Modifier.align(Alignment.Center)
            )
        } else if (installedPlugins.isEmpty()) {
            Text(
                text = "No plugins found",
                style = MaterialTheme.typography.bodyLarge,
                color = LightGrayText,
                modifier = Modifier.align(Alignment.Center)
            )
        } else {
            // Display installed plugins list
            LazyColumn(modifier = Modifier.fillMaxSize()) {
                items(installedPlugins) { pluginName ->
                    SettingsMenuItem(title = pluginName, description = null, onClick = { /* View plugin details */ })
                }
            }
        }

        FloatingActionButton(
            onClick = { /* TODO: Implement New Plugin action */ },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp),
            containerColor = AccentPurple,
            contentColor = MaterialTheme.colorScheme.onPrimary
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(imageVector = Icons.Default.Add, contentDescription = "New Plugin")
                Spacer(Modifier.width(8.dp))
                Text("New Plugin")
            }
        }
    }
}

// Reusable Composable for a toggle setting item
@Composable
fun SettingsToggleItem(
    icon: ImageVector,
    title: String,
    description: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 4.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(imageVector = icon, contentDescription = null, tint = AccentPurple, modifier = Modifier.size(24.dp))
                Spacer(modifier = Modifier.width(16.dp))
                Column {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = description,
                        style = MaterialTheme.typography.bodySmall,
                        color = MediumGrayText
                    )
                }
            }
            Switch(checked = checked, onCheckedChange = onCheckedChange, colors = SwitchDefaults.colors(
                checkedThumbColor = AccentPurple,
                checkedTrackColor = AccentPurple.copy(alpha = 0.5f)
            ))
        }
    }
}

// Reusable Composable for a display-only setting item
@Composable
fun SettingsDisplayItem(
    icon: ImageVector,
    title: String,
    description: String,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 4.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(imageVector = icon, contentDescription = null, tint = AccentPurple, modifier = Modifier.size(24.dp))
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = description,
                    style = MaterialTheme.typography.bodySmall,
                    color = MediumGrayText
                )
            }
        }
    }
}

// Reusable Composable for a slider setting item
@Composable
fun SettingsSliderItem(
    icon: ImageVector,
    title: String,
    value: Float,
    range: ClosedFloatingPointRange<Float>,
    steps: Int,
    onValueChange: (Float) -> Unit,
    valueLabel: String,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 4.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(imageVector = icon, contentDescription = null, tint = AccentPurple, modifier = Modifier.size(24.dp))
                    Spacer(modifier = Modifier.width(16.dp))
                    Text(
                        text = title,
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
                Text(
                    text = valueLabel,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Slider(
                value = value,
                onValueChange = onValueChange,
                valueRange = range,
                steps = steps,
                colors = SliderDefaults.colors(
                    thumbColor = AccentPurple,
                    activeTrackColor = AccentPurple,
                    inactiveTrackColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.24f)
                )
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewSettingsScreen() {
    McsTheme {
        MainSettingsMenu(onNavigate = {})
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewGeneralSettingsScreen() {
    McsTheme {
        GeneralSettingsScreen(generalSettings = GeneralSettings(), viewModel = hiltViewModel())
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewEditorSettingsScreen() {
    McsTheme {
        EditorSettingsScreen(editorSettings = EditorSettings(), viewModel = hiltViewModel())
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewFileExplorerSettingsScreen() {
    McsTheme {
        FileExplorerSettingsScreen(fileExplorerSettings = FileExplorerSettings(), viewModel = hiltViewModel())
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewPluginsSettingsScreen() {
    McsTheme {
        PluginsSettingsScreen(isLoading = false, installedPlugins = listOf(), viewModel = hiltViewModel())
    }
}
