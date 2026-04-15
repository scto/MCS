/*
 * Copyright (C) 2024-2025 SC T O., Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.scto.mcs.feature.settings.ui

import android.content.Context
import android.os.Build
import android.widget.Toast
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.core.content.edit
import androidx.navigation.NavController
import com.scto.mcs.core.utils.LogConfigState // Assuming this is available from core.utils
import com.scto.mcs.core.utils.ThemeState // Assuming this is available from core.utils
import com.scto.mcs.core.utils.WorkspaceManager // Assuming this is available from core.utils
// import com.web.webide.safeNavigate // This needs to be replaced or provided by the navigation setup
import com.scto.mcs.ui.components.DirectorySelector // Assuming DirectorySelector is available in a common UI module
import com.scto.mcs.ui.components.ColorPickerDialog // Assuming ColorPickerDialog is available in a common UI module
// import com.web.webide.core.utils.themeColors // Assuming themeColors is available or needs to be provided
import com.scto.mcs.ui.theme.themeColors // Assuming themeColors is now in com.scto.mcs.ui.theme

// Placeholder for safeNavigate extension function, replaced with direct navigate
fun NavController.safeNavigate(route: String) {
    this.navigate(route)
}

// Placeholder for EditorViewModel if it's not directly available in this module
// In a real scenario, this would likely be injected or accessed differently.
object EditorViewModelPlaceholder {
    fun reloadAllEditors(context: Context) {
        // Placeholder implementation
        Toast.makeText(context, "Reloading all editors (placeholder)", Toast.LENGTH_SHORT).show()
    }
}

// Extension to access the ViewModel if needed. Adjust as per your DI setup.
@Composable
fun rememberEditorViewModel(context: Context): com.scto.mcs.ui.editor.viewmodel.EditorViewModel? {
    // This is a placeholder. In a real app, you'd use Hilt ViewModel injection or similar.
    // For now, we return null or a mock if needed for previewing.
    // If EditorViewModel is part of a shared module and can be accessed globally:
    // return hiltViewModel<com.scto.mcs.ui.editor.viewmodel.EditorViewModel>()
    return null // Return null if not directly accessible or needed for this screen logic.
}

// Enum for auto-save intervals
enum class AutoSaveOption(val label: String, val interval: Long) {
    OFF("Off", 0L),
    SEC_30("Every 30 seconds", 30_000L),
    MIN_1("Every 1 minute", 60_000L),
    MIN_5("Every 5 minutes", 300_000L),
    MIN_10("Every 10 minutes", 600_000L)
}

// Extension function to address luminance error for Color
fun Color.luminance(): Float {
    return 0.2126f * this.red + 0.7152f * this.green + 0.0722f * this.blue
}

private val PRESET_FONTS = listOf(
    "Default Font" to "", // Using English for consistency with other labels
    "JetBrains Mono" to "ttf/JetBrainsMono-Regular.ttf",
    "Roboto Mono" to "ttf/RobotoMono-Regular.ttf",
    "Source Code Pro" to "ttf/SourceCodePro-Regular.ttf",
    "Comic Sans" to "ttf/Comic-Sans-MS-Regular-2.ttf"
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    navController: NavController,
    currentThemeState: ThemeState,
    logConfigState: LogConfigState,
    onThemeChange: (modeIndex: Int, themeIndex: Int, customColor: Color, isMonet: Boolean, isCustom: Boolean) -> Unit,
    onLogConfigChange: (enabled: Boolean, filePath: String) -> Unit,
    // editorViewModel: com.scto.mcs.ui.editor.viewmodel.EditorViewModel? = null // Removed direct ViewModel parameter
) {
    val context = LocalContext.current
    val prefs = remember { context.getSharedPreferences("WebIDE_Editor_Settings", Context.MODE_PRIVATE) }

    // Use the same SharedPreferences file name as in the ViewModel for consistency
    val generalPrefs = remember { context.getSharedPreferences("WebIDE_Settings", Context.MODE_PRIVATE) }

    val fontSize = prefs.getFloat("editor_font_size", 14f)

    var tabWidth by remember { mutableIntStateOf(prefs.getInt("editor_tab_width", 4)) }
    var wordWrap by remember { mutableStateOf(prefs.getBoolean("editor_word_wrap", false)) }
    var showInvisibles by remember { mutableStateOf(prefs.getBoolean("editor_show_invisibles", false)) }
    var codeFolding by remember { mutableStateOf(prefs.getBoolean("editor_code_folding", true)) }
    var showToolbar by remember { mutableStateOf(prefs.getBoolean("editor_show_toolbar", true)) }
    var showHistory by remember { mutableStateOf(prefs.getBoolean("editor_show_history", true)) }
    var lspEnabled by remember { mutableStateOf(prefs.getBoolean("editor_lsp_enabled", false)) }
    var aiEnabled by remember { mutableStateOf(prefs.getBoolean("editor_ai_enabled", true)) }
    var fontPath by remember { mutableStateOf(prefs.getString("editor_font_path", "") ?: "") }
    var customSymbols by remember { mutableStateOf(prefs.getString("editor_custom_symbols", "Tab,<,>,/,=,\"','!,?,;,:,{,},[,],(,+,-,*,_,&,|") ?: "") } // Fixed missing closing quote
    var autoSaveInterval by remember { mutableLongStateOf(generalPrefs.getLong("auto_save_interval", 0L)) }
    var showAutoSaveDialog by remember { mutableStateOf(false) }
    // Save previous LSP state to detect changes
    var previousLspEnabled by remember { mutableStateOf(lspEnabled) }

    // EditorViewModel instance - use placeholder for now or inject properly
    val editorViewModel = rememberEditorViewModel(context) // This will be null unless you have a DI setup

    // Auto-save effect
    LaunchedEffect(tabWidth, wordWrap, showInvisibles, codeFolding, showToolbar, showHistory, lspEnabled, aiEnabled, fontPath, customSymbols) {
        prefs.edit {
            putFloat("editor_font_size", fontSize)
            putInt("editor_tab_width", tabWidth)
            putBoolean("editor_word_wrap", wordWrap)
            putBoolean("editor_show_invisibles", showInvisibles)
            putBoolean("editor_code_folding", codeFolding)
            putBoolean("editor_show_toolbar", showToolbar)
            putBoolean("editor_show_history", showHistory)
            putBoolean("editor_lsp_enabled", lspEnabled)
            putBoolean("editor_ai_enabled", aiEnabled)
            putString("editor_font_path", fontPath)
            putString("editor_custom_symbols", customSymbols)
        }

        // Detect LSP state change and reload all editors
        if (lspEnabled != previousLspEnabled) {
            // editorViewModel?.reloadAllEditors(context) // Call the actual ViewModel method if available
            EditorViewModelPlaceholder.reloadAllEditors(context) // Use placeholder
            previousLspEnabled = lspEnabled
        }
    }

    var selectedWorkspace by remember { mutableStateOf(WorkspaceManager.getWorkspacePath(context)) }
    var showFileSelector by remember { mutableStateOf(false) }
    var showLogPathSelector by remember { mutableStateOf(false) }
    var showColorPicker by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Settings", fontWeight = FontWeight.SemiBold) }, // Changed "设置" to "Settings"
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back") // Changed "返回" to "Back"
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item(key = "theme_settings") {
                ThemeSettingsItem(
                    currentThemeState = currentThemeState,
                    onThemeChange = onThemeChange,
                    onCustomColorClick = { showColorPicker = true }
                )
            }

            item(key = "editor_settings") {
                EditorSettingsItem(
                    tabWidth = tabWidth,
                    onTabWidthChange = { tabWidth = it },
                    wordWrap = wordWrap,
                    onWordWrapChange = { wordWrap = it },
                    showInvisibles = showInvisibles,
                    onShowInvisiblesChange = { showInvisibles = it },
                    codeFolding = codeFolding,
                    onCodeFoldingChange = { codeFolding = it },
                    showToolbar = showToolbar,
                    onShowToolbarChange = { showToolbar = it },
                    showHistory = showHistory,
                    onShowHistoryChange = { showHistory = it },
                    lspEnabled = lspEnabled,
                    onLspEnabledChange = { lspEnabled = it },
                    isAiEnabled = aiEnabled,
                    onIsAiEnabledChange = { aiEnabled = it },
                    fontPath = fontPath,
                    onFontPathChange = { fontPath = it },
                    customSymbols = customSymbols,
                    onCustomSymbolsChange = { customSymbols = it }
                )
            }

            item {
                Text(
                    text = "General", // Changed "常规" to "General"
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(start = 4.dp, top = 8.dp)
                )
            }
            // 🔥 New: Auto-save settings entry
            item {
                val currentOption = AutoSaveOption.entries.find { it.interval == autoSaveInterval } ?: AutoSaveOption.OFF
                SimpleSettingsCard(
                    icon = Icons.Outlined.SaveAs, // Ensure this icon exists, use Icons.Default.Save if not
                    title = "Auto-Save", // Changed "自动保存与备份" to "Auto-Save"
                    subtitle = if (currentOption == AutoSaveOption.OFF) "Off" else "Frequency: ${currentOption.label}", // Updated subtitle
                    onClick = { showAutoSaveDialog = true }
                )
            }
            item {
                SimpleSettingsCard(
                    icon = Icons.Outlined.Folder,
                    title = "Workspace", // Changed "工作目录" to "Workspace"
                    subtitle = selectedWorkspace,
                    onClick = { showFileSelector = true }
                )
            }

            item {
                LogSettingsItem(
                    logConfigState = logConfigState,
                    onLogConfigChange = onLogConfigChange,
                    onPathClick = { showLogPathSelector = true }
                )
            }

            item {
                SimpleSettingsCard(
                    icon = Icons.Outlined.WavingHand,
                    title = "Welcome Page", // Changed "欢迎页" to "Welcome Page"
                    subtitle = "View feature introduction", // Changed "查看功能介绍" to "View feature introduction"
                    onClick = { navController.safeNavigate("welcome") } // Using safeNavigate placeholder
                )
            }

            item {
                SimpleSettingsCard(
                    icon = Icons.Outlined.Info,
                    title = "About", // Changed "关于" to "About"
                    subtitle = "Version information and introduction", // Changed "版本信息与介绍" to "Version information and introduction"
                    onClick = { navController.safeNavigate("about") } // Using safeNavigate placeholder
                )
            }

            item { Spacer(modifier = Modifier.height(32.dp)) }
        }
    }

    // Dialogs
    if (showFileSelector) {
        DirectorySelector(
            initialPath = selectedWorkspace,
            onPathSelected = { path ->
                selectedWorkspace = path
                WorkspaceManager.saveWorkspacePath(context, path)
                showFileSelector = false
                Toast.makeText(context, "Workspace directory updated", Toast.LENGTH_SHORT).show() // Updated message
            },
            onDismissRequest = { showFileSelector = false }
        )
    }

    if (showLogPathSelector) {
        DirectorySelector(
            initialPath = logConfigState.logFilePath,
            onPathSelected = { path ->
                onLogConfigChange(logConfigState.isLogEnabled, path)
                showLogPathSelector = false
                Toast.makeText(context, "Log path updated", Toast.LENGTH_SHORT).show() // Updated message
            },
            onDismissRequest = { showLogPathSelector = false }
        )
    }
    if (showAutoSaveDialog) {
        AlertDialog(
            onDismissRequest = { showAutoSaveDialog = false },
            title = { Text("Auto-Save Frequency") }, // Updated title
            text = {
                Column {
                    Text(
                        "When enabled, the system will periodically save all open files and package the project in the background to a private directory (keeping the last 5 versions).", // Updated description
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(Modifier.height(16.dp))
                    AutoSaveOption.entries.forEach { option ->
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    // 1. Update state
                                    autoSaveInterval = option.interval
                                    // 2. Write to SharedPreferences
                                    generalPrefs.edit { putLong("auto_save_interval", option.interval) }
                                    // 3. Close dialog
                                    showAutoSaveDialog = false
                                    // 4. Show message
                                    val msg = if (option == AutoSaveOption.OFF) "Auto-save disabled" else "Auto-save set to ${option.label}"
                                    Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
                                }
                                .padding(vertical = 12.dp)
                        ) {
                            RadioButton(
                                selected = (option.interval == autoSaveInterval),
                                onClick = null // Click on Row triggers click
                            )
                            Spacer(Modifier.width(8.dp))
                            Text(option.label, style = MaterialTheme.typography.bodyLarge)
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showAutoSaveDialog = false }) { Text("Cancel") } // Changed "取消" to "Cancel"
            }
        )
    }

    if (showColorPicker) {
        ColorPickerDialog(
            initialColor = currentThemeState.customColor,
            onDismiss = { showColorPicker = false },
            onColorSelected = { color ->
                // themeColors.size represents the number of predefined themes.
                // For custom colors, we often use a specific index or a flag.
                // Assuming themeColors.size is used to indicate a custom selection.
                onThemeChange(currentThemeState.selectedModeIndex, themeColors.size, color, false, true)
                showColorPicker = false
            }
        )
    }
}

// ================= Editor Settings Component (Refactored & Optimized) =================
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditorSettingsItem(
    tabWidth: Int,
    onTabWidthChange: (Int) -> Unit,
    wordWrap: Boolean,
    onWordWrapChange: (Boolean) -> Unit,
    showInvisibles: Boolean,
    onShowInvisiblesChange: (Boolean) -> Unit,
    codeFolding: Boolean,
    onCodeFoldingChange: (Boolean) -> Unit,
    showToolbar: Boolean,
    onShowToolbarChange: (Boolean) -> Unit,
    showHistory: Boolean,
    onShowHistoryChange: (Boolean) -> Unit,
    lspEnabled: Boolean,
    onLspEnabledChange: (Boolean) -> Unit,
    isAiEnabled: Boolean,
    onIsAiEnabledChange: (Boolean) -> Unit,
    fontPath: String,
    onFontPathChange: (String) -> Unit,
    customSymbols: String,
    onCustomSymbolsChange: (String) -> Unit
) {
    var expanded by rememberSaveable { mutableStateOf(false) }
    val expandDuration = 200
    val textFadeDuration = 200
    val snappyEasing = LinearOutSlowInEasing

    var isFontDropdownExpanded by remember { mutableStateOf(false) }

    Card(
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainer),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.animateContentSize(
                animationSpec = tween(durationMillis = expandDuration, easing = snappyEasing)
            )
        ) {
            // Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { expanded = !expanded }
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Outlined.Code,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.width(16.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Editor Configuration", // Changed "编辑器配置" to "Editor Configuration"
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                    )
                    AnimatedVisibility(
                        visible = !expanded,
                        enter = fadeIn(tween(textFadeDuration)) + expandVertically(tween(textFadeDuration), expandFrom = Alignment.Top),
                        exit = fadeOut(tween(textFadeDuration)) + shrinkVertically(tween(textFadeDuration), shrinkTowards = Alignment.Top)
                    ) {
                        val displayFont = if(fontPath.isBlank()) "Default Font" else fontPath.substringAfterLast("/") // Changed "系统默认"
                        Text(
                            text = "${tabWidth} Spaces · $displayFont", // Changed "空格"
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(top = 2.dp),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
                val rotation by animateFloatAsState(
                    targetValue = if (expanded) 180f else 0f,
                    label = "ArrowRotation",
                    animationSpec = tween(expandDuration)
                )
                Icon(
                    imageVector = Icons.Filled.ExpandMore,
                    contentDescription = null,
                    modifier = Modifier.rotate(rotation)
                )
            }

            // Expanded Content
            AnimatedVisibility(
                visible = expanded,
                enter = fadeIn(tween(expandDuration)) + expandVertically(animationSpec = tween(expandDuration, easing = snappyEasing), expandFrom = Alignment.Top),
                exit = fadeOut(tween(textFadeDuration)) + shrinkVertically(animationSpec = tween(textFadeDuration, easing = snappyEasing), shrinkTowards = Alignment.Top)
            ) {
                Column(modifier = Modifier.padding(start = 16.dp, end = 16.dp, bottom = 16.dp)) {
                    HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
                    Spacer(modifier = Modifier.height(16.dp))

                    Text("Intelligent Assistance", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.primary) // Changed "智能辅助"
                    CompactSwitchRow("AI Assistant", isAiEnabled, onIsAiEnabledChange) // Changed "AI 编程助手"
                    CompactSwitchRow("LSP Code Completion", lspEnabled, onLspEnabledChange) // Changed "LSP 代码补全"
                    Spacer(modifier = Modifier.height(24.dp))

                    // === 1. Indentation Settings (Segmented Style) ===
                    Text("Indentation Width", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.primary) // Changed "缩进设置" and "缩进宽度"
                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        val options = listOf(2, 4, 8)
                        options.forEach { option ->
                            val isSelected = tabWidth == option

                            val containerColor by animateColorAsState(
                                targetValue = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceContainerHigh,
                                animationSpec = tween(200),
                                label = "ButtonContainer"
                            )
                            val contentColor by animateColorAsState(
                                targetValue = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface,
                                animationSpec = tween(200),
                                label = "ButtonContent"
                            )

                            Surface(
                                onClick = { onTabWidthChange(option) },
                                modifier = Modifier
                                    .weight(1f)
                                    .height(32.dp),
                                shape = RoundedCornerShape(4.dp),
                                color = containerColor,
                                contentColor = contentColor
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Text(
                                        text = "$option Spaces", // Changed "空格"
                                        style = MaterialTheme.typography.labelMedium,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // === 2. Font Settings (Combo Box Mode) ===
                    Text("Editor Font", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.primary) // Changed "编辑器字体"
                    Spacer(modifier = Modifier.height(8.dp))

                    Box(modifier = Modifier.fillMaxWidth()) {
                        OutlinedTextField(
                            value = fontPath,
                            onValueChange = onFontPathChange,
                            modifier = Modifier.fillMaxWidth(),
                            label = { Text("Enter path or select preset...") }, // Changed label
                            singleLine = true,
                            trailingIcon = {
                                IconButton(onClick = { isFontDropdownExpanded = !isFontDropdownExpanded }) {
                                    Icon(Icons.Filled.ArrowDropDown, "Select Preset") // Changed "选择预设"
                                }
                            }
                        )

                        DropdownMenu(
                            expanded = isFontDropdownExpanded,
                            onDismissRequest = { isFontDropdownExpanded = false },
                            offset = DpOffset(0.dp, 0.dp),
                            modifier = Modifier.fillMaxWidth(0.9f)
                        ) {
                            PRESET_FONTS.forEach { (name, file) ->
                                DropdownMenuItem(
                                    text = {
                                        Column {
                                            Text(name, style = MaterialTheme.typography.bodyLarge)
                                            if (file.isNotEmpty()) {
                                                Text(file, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.outline)
                                            }
                                        }
                                    },
                                    onClick = {
                                        onFontPathChange(file)
                                        isFontDropdownExpanded = false
                                    }
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // === 3. Behavior Toggles ===
                    Text("Behavior", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.primary) // Changed "行为"
                    CompactSwitchRow("Show Toolbar", showToolbar, onShowToolbarChange) // Changed "显示工具栏"
                    CompactSwitchRow("History Tabs", showHistory, onShowHistoryChange) // Changed "历史标签页"
                    CompactSwitchRow("Word Wrap", wordWrap, onWordWrapChange) // Changed "自动换行"
                    CompactSwitchRow("Show Whitespace", showInvisibles, onShowInvisiblesChange) // Changed "显示空白符"
                    CompactSwitchRow("Code Folding", codeFolding, onCodeFoldingChange) // Changed "代码折叠"

                    HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp), thickness = 0.5.dp)

                    // === 4. Symbol Bar ===
                    Text("Custom Symbols", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.primary) // Changed "自定义符号"
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = customSymbols,
                        onValueChange = onCustomSymbolsChange,
                        modifier = Modifier.fillMaxWidth(),
                        textStyle = MaterialTheme.typography.bodyMedium,
                        maxLines = 2,
                        placeholder = { Text("Tab, <, >, ...") } // Changed placeholder
                    )
                }
            }
        }
    }
}

@Composable
fun CompactSwitchRow(title: String, checked: Boolean, onCheckedChange: (Boolean) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onCheckedChange(!checked) }
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(title, style = MaterialTheme.typography.bodyMedium)
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
        )
    }
}

// Helper
fun Modifier.scale(scale: Float) = this.graphicsLayer(scaleX = scale, scaleY = scale)

// --- Existing components from the original file, translated and adapted ---

@Composable
fun ThemeSettingsItem(
    currentThemeState: ThemeState,
    onThemeChange: (Int, Int, Color, Boolean, Boolean) -> Unit,
    onCustomColorClick: () -> Unit
) {
    var expanded by rememberSaveable { mutableStateOf(false) }
    val expandDuration = 200
    val textFadeDuration = 200
    val snappyEasing = LinearOutSlowInEasing

    Card(
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainer),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.animateContentSize(
                animationSpec = tween(durationMillis = expandDuration, easing = snappyEasing)
            )
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { expanded = !expanded }
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Outlined.Palette,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.width(16.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Appearance & Theme", // Changed "外观与主题"
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                    )
                    AnimatedVisibility(
                        visible = !expanded,
                        enter = fadeIn(tween(textFadeDuration)) + expandVertically(tween(textFadeDuration), expandFrom = Alignment.Top),
                        exit = fadeOut(tween(textFadeDuration)) + shrinkVertically(tween(textFadeDuration), shrinkTowards = Alignment.Top)
                    ) {
                        Text(
                            text = if (currentThemeState.isMonetEnabled) "Dynamic Color" else "Custom Appearance", // Changed "动态色彩" and "自定义外观"
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(top = 2.dp)
                        )
                    }
                }
                val rotation by animateFloatAsState(
                    targetValue = if (expanded) 180f else 0f,
                    label = "ArrowRotation",
                    animationSpec = tween(expandDuration)
                )
                Icon(
                    imageVector = Icons.Filled.ExpandMore,
                    contentDescription = null,
                    modifier = Modifier.rotate(rotation)
                )
            }

            AnimatedVisibility(
                visible = expanded,
                enter = fadeIn(tween(expandDuration)) +
                        expandVertically(animationSpec = tween(expandDuration, easing = snappyEasing), expandFrom = Alignment.Top),
                exit = fadeOut(tween(textFadeDuration)) +
                        shrinkVertically(animationSpec = tween(textFadeDuration, easing = snappyEasing), shrinkTowards = Alignment.Top),
            ) {
                Column(modifier = Modifier.padding(start = 16.dp, end = 16.dp, bottom = 16.dp)) {
                    HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
                    Spacer(modifier = Modifier.height(16.dp))

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text("Dynamic Color", style = MaterialTheme.typography.bodyMedium) // Changed "动态色彩"
                            }
                            Switch(
                                checked = currentThemeState.isMonetEnabled,
                                onCheckedChange = {
                                    val newIsCustom = if (it) false else currentThemeState.isCustomTheme
                                    onThemeChange(currentThemeState.selectedModeIndex, currentThemeState.selectedThemeIndex, currentThemeState.customColor, it, newIsCustom)
                                }
                            )
                        }
                    }

                    AnimatedVisibility(visible = !currentThemeState.isMonetEnabled) {
                        Column {
                            Text("Accent Color", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.primary) // Changed "主题色"
                            LazyRow(
                                horizontalArrangement = Arrangement.spacedBy(12.dp),
                                contentPadding = PaddingValues(top = 8.dp, bottom = 16.dp)
                            ) {
                                itemsIndexed(themeColors) { index, theme ->
                                    val isSelected = !currentThemeState.isCustomTheme && currentThemeState.selectedThemeIndex == index
                                    ColorSelectionItem(
                                        color = theme.primaryColor,
                                        name = theme.name,
                                        isSelected = isSelected,
                                        onClick = {
                                            onThemeChange(currentThemeState.selectedModeIndex, index, currentThemeState.customColor, false, false)
                                        }
                                    )
                                }
                                item {
                                    CustomColorButton(
                                        isSelected = currentThemeState.isCustomTheme,
                                        customColor = currentThemeState.customColor,
                                        onClick = onCustomColorClick
                                    )
                                }
                            }
                        }
                    }

                    Text("Display Mode", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.primary) // Changed "显示模式"
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        val modes = listOf("System", "Light", "Dark") // Changed to English
                        modes.forEachIndexed { index, label ->
                            SmoothFilterChip(
                                selected = currentThemeState.selectedModeIndex == index,
                                label = label,
                                onClick = { onThemeChange(index, currentThemeState.selectedThemeIndex, currentThemeState.customColor, currentThemeState.isMonetEnabled, currentThemeState.isCustomTheme) },
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }
            }
        }
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
fun LogSettingsItem(
    logConfigState: LogConfigState,
    onLogConfigChange: (Boolean, String) -> Unit,
    onPathClick: () -> Unit
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainer),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp).animateContentSize()
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Outlined.BugReport, null, tint = MaterialTheme.colorScheme.primary)
                Spacer(modifier = Modifier.width(16.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text("Enable Logging", style = MaterialTheme.typography.titleMedium) // Changed "启用日志"
                }
                Switch(checked = logConfigState.isLogEnabled, onCheckedChange = { onLogConfigChange(it, logConfigState.logFilePath) })
            }
            AnimatedVisibility(visible = logConfigState.isLogEnabled) {
                Column {
                    Spacer(modifier = Modifier.height(16.dp))
                    OutlinedButton(
                        onClick = onPathClick,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(logConfigState.logFilePath, maxLines = 1, overflow = TextOverflow.Ellipsis, modifier = Modifier.weight(1f))
                        Icon(Icons.Outlined.Edit, null, modifier = Modifier.size(16.dp))
                    }
                }
            }
        }
    }
}
@Composable
fun SmoothFilterChip(
    selected: Boolean,
    label: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val duration = 200
    val fastEasing = LinearEasing
    val colorAnimSpec = tween<Color>(durationMillis = duration, easing = fastEasing)
    val containerColor by animateColorAsState(if (selected) MaterialTheme.colorScheme.secondaryContainer else MaterialTheme.colorScheme.surface, colorAnimSpec, "Container")
    val borderColor by animateColorAsState(if (selected) Color.Transparent else MaterialTheme.colorScheme.outline, colorAnimSpec, "Border")
    val contentColor by animateColorAsState(if (selected) MaterialTheme.colorScheme.onSecondaryContainer else MaterialTheme.colorScheme.onSurface, colorAnimSpec, "Content")

    Surface(
        onClick = onClick,
        modifier = modifier.height(36.dp),
        shape = CircleShape,
        color = containerColor,
        border = if (!selected) BorderStroke(1.dp, borderColor) else null,
    ) {
        Row(
            modifier = Modifier.fillMaxSize().padding(horizontal = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            AnimatedVisibility(visible = selected) {
                Row {
                    Icon(Icons.Default.Check, null, modifier = Modifier.size(16.dp), tint = contentColor)
                    Spacer(modifier = Modifier.width(6.dp))
                }
            }
            Text(label, style = MaterialTheme.typography.labelMedium, color = contentColor)
        }
    }
}
@Composable
fun ColorSelectionItem(color: Color, name: String, isSelected: Boolean, onClick: () -> Unit) {
    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.clickable(onClick = onClick).padding(4.dp)) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.size(48.dp).border(if (isSelected) 3.dp else 0.dp, if (isSelected) MaterialTheme.colorScheme.primary else Color.Transparent, CircleShape).padding(4.dp).clip(CircleShape).background(color)
        ) {
            if (isSelected) Icon(Icons.Default.Check, null, tint = if (color.luminance() > 0.5f) Color.Black else Color.White, modifier = Modifier.size(24.dp))
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(name, style = MaterialTheme.typography.labelSmall, color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant)
    }
}

@Composable
fun CustomColorButton(isSelected: Boolean, customColor: Color, onClick: () -> Unit) {
    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.clickable(onClick = onClick).padding(4.dp)) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.size(48.dp).border(if (isSelected) 3.dp else 0.dp, if (isSelected) MaterialTheme.colorScheme.primary else Color.Transparent, CircleShape).padding(4.dp).clip(CircleShape).background(MaterialTheme.colorScheme.surfaceVariant)
        ) {
            if (isSelected) {
                Box(Modifier.fillMaxSize().background(customColor))
                Icon(Icons.Default.Edit, null, tint = if (customColor.luminance() > 0.5f) Color.Black else Color.White, modifier = Modifier.size(20.dp))
            } else {
                Icon(Icons.Default.Add, "Custom", tint = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text("Custom", style = MaterialTheme.typography.labelSmall, color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant) // Changed "自定义"
    }
}
