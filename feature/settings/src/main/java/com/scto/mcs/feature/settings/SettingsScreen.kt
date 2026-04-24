package com.scto.mcs.feature.settings

import android.content.Context
import android.net.Uri
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.*
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp

import androidx.lifecycle.ViewModel

import org.json.JSONObject

import java.io.File

// --- Enums für Zustände und Navigation ---
enum class ThemeMode { SYSTEM, LIGHT, DARK }
enum class FileSortBy { NAME, TYPE, SIZE }
enum class FileSortOrder { ASCENDING, DESCENDING }
enum class SettingsScreenLevel { MAIN, APPEARANCE, FILE_TREE }

/**
 * Datenmodell für ein benutzerdefiniertes Theme.
 */
data class CustomTheme(
    val id: String,
    val name: String,
    val lightScheme: ColorScheme,
    val darkScheme: ColorScheme,
    val isRemovable: Boolean = true
)

/**
 * ViewModel zur Verwaltung aller App-Einstellungen und Themes.
 */
class SettingsViewModel : ViewModel() {
    // UI-Zustände für das Design
    var themeMode by mutableStateOf(ThemeMode.SYSTEM)
    var isDynamicColorEnabled by mutableStateOf(true)
    var isAmoledEnabled by mutableStateOf(false)
    val installedThemes = mutableStateListOf<CustomTheme>()
    var activeThemeId by mutableStateOf<String?>(null)

    // Zustände für die Dateibaum-Konfiguration
    var fileSortBy by mutableStateOf(FileSortBy.NAME)
    var fileSortOrder by mutableStateOf(FileSortOrder.ASCENDING)
    var showHiddenFiles by mutableStateOf(false)

    // --- Theme-Management-Logik ---

    /**
     * Lädt alle verfügbaren Themes aus den Assets (System) und dem internen Speicher (Benutzer).
     */
    fun loadAllThemes(context: Context) {
        if (installedThemes.isNotEmpty()) return
        
        // 1. System-Themes aus Assets laden
        try {
            context.assets.list("themes")?.filter { it.endsWith(".json") }?.forEach { fileName ->
                val json = context.assets.open("themes/$fileName").bufferedReader().use { it.readText() }
                installThemeFromJson(json, isRemovable = false)
            }
        } catch (e: Exception) { e.printStackTrace() }

        // 2. Benutzer-Themes aus internem Speicher laden
        val themeDir = File(context.filesDir, "themes")
        if (themeDir.exists()) {
            themeDir.listFiles { f -> f.extension == "json" }?.forEach { file ->
                installThemeFromJson(file.readText(), isRemovable = true)
            }
        }
    }

    /**
     * Installiert ein Theme aus einer gewählten Datei (URI).
     */
    fun installThemeFromUri(context: Context, uri: Uri): Boolean {
        return try {
            val jsonString = context.contentResolver.openInputStream(uri)?.bufferedReader()?.use { it.readText() }
            if (jsonString != null && installThemeFromJson(jsonString, isRemovable = true)) {
                saveThemeToInternalStorage(context, jsonString)
                true
            } else false
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    private fun saveThemeToInternalStorage(context: Context, jsonString: String) {
        try {
            val json = JSONObject(jsonString)
            val id = json.optString("id", "theme_${System.currentTimeMillis()}")
            val themeDir = File(context.filesDir, "themes")
            if (!themeDir.exists()) themeDir.mkdirs()
            File(themeDir, "$id.json").writeText(jsonString)
        } catch (e: Exception) { e.printStackTrace() }
    }

    /**
     * Parst den JSON-Inhalt eines Themes und fügt es der Liste hinzu.
     */
    fun installThemeFromJson(jsonString: String, isRemovable: Boolean): Boolean {
        return try {
            val json = JSONObject(jsonString)
            val id = json.getString("id")
            val name = json.getString("name")
            
            val lightJson = json.getJSONObject("light")
            val darkJson = json.getJSONObject("dark")

            // Unterstützt flache Struktur und verschachtelte "baseColors"
            val lightBase = if (lightJson.has("baseColors")) lightJson.getJSONObject("baseColors") else lightJson
            val darkBase = if (darkJson.has("baseColors")) darkJson.getJSONObject("baseColors") else darkJson

            val newTheme = CustomTheme(
                id = id,
                name = name,
                lightScheme = parseColors(lightBase, isDark = false),
                darkScheme = parseColors(darkBase, isDark = true),
                isRemovable = isRemovable
            )
            // Dubletten vermeiden
            installedThemes.removeAll { it.id == id }
            installedThemes.add(newTheme)
            true
        } catch (e: Exception) {
            false
        }
    }

    private fun parseColors(obj: JSONObject, isDark: Boolean): ColorScheme {
        val getColor = { key: String, fallback: Color -> 
            try { Color(android.graphics.Color.parseColor(obj.getString(key))) } catch(e: Exception) { fallback }
        }
        return if (isDark) {
            darkColorScheme(
                primary = getColor("primary", Color(0xFFAAC7FF)),
                onPrimary = getColor("onPrimary", Color(0xFF0A305F)),
                background = getColor("background", Color(0xFF111318)),
                onBackground = getColor("onBackground", Color(0xFFE2E2E9)),
                surface = getColor("surface", Color(0xFF111318)),
                onSurface = getColor("onSurface", Color(0xFFE2E2E9))
            )
        } else {
            lightColorScheme(
                primary = getColor("primary", Color(0xFF415F91)),
                onPrimary = getColor("onPrimary", Color(0xFFFFFFFF)),
                background = getColor("background", Color(0xFFF9F9FF)),
                onBackground = getColor("onBackground", Color(0xFF1A1B21)),
                surface = getColor("surface", Color(0xFFF9F9FF)),
                onSurface = getColor("onSurface", Color(0xFF1A1B21))
            )
        }
    }

    @Composable
    fun getActiveCustomColorScheme(): ColorScheme? {
        val theme = installedThemes.find { it.id == activeThemeId } ?: return null
        val isDark = when(themeMode) {
            ThemeMode.DARK -> true
            ThemeMode.LIGHT -> false
            ThemeMode.SYSTEM -> isSystemInDarkTheme()
        }
        return if (isDark) theme.darkScheme else theme.lightScheme
    }

    /**
     * Löscht ein benutzerdefiniertes Theme.
     */
    fun deleteTheme(context: Context, theme: CustomTheme) {
        if (!theme.isRemovable) return
        val themeDir = File(context.filesDir, "themes")
        File(themeDir, "${theme.id}.json").apply { if (exists()) delete() }
        installedThemes.remove(theme)
        if (activeThemeId == theme.id) activeThemeId = null
    }

    // Hilfsmethoden für Updates
    fun setTheme(mode: ThemeMode) { themeMode = mode }
    fun toggleDynamicColor(enabled: Boolean) { isDynamicColorEnabled = enabled }
    fun toggleAmoled(enabled: Boolean) { isAmoledEnabled = enabled }
    fun updateFileSort(sortBy: FileSortBy) { fileSortBy = sortBy }
    fun updateFileOrder(order: FileSortOrder) { fileSortOrder = order }
    fun toggleHiddenFiles(show: Boolean) { showHiddenFiles = show }
}

/**
 * Hauptbildschirm für die Einstellungen mit Navigationslogik.
 */
@Composable
fun SettingsScreen(vm: SettingsViewModel) {
    val context = LocalContext.current
    var currentLevel by remember { mutableStateOf(SettingsScreenLevel.MAIN) }

    // Themes beim ersten Laden synchronisieren
    LaunchedEffect(Unit) {
        vm.loadAllThemes(context)
    }

    // Zurück-Taste innerhalb der Sub-Menüs abfangen
    BackHandler(enabled = currentLevel != SettingsScreenLevel.MAIN) {
        currentLevel = SettingsScreenLevel.MAIN
    }

    AnimatedContent(
        targetState = currentLevel,
        transitionSpec = {
            if (targetState == SettingsScreenLevel.MAIN) {
                (slideInHorizontally { -it } + fadeIn()) togetherWith (slideOutHorizontally { it } + fadeOut())
            } else {
                (slideInHorizontally { it } + fadeIn()) togetherWith (slideOutHorizontally { -it } + fadeOut())
            }
        },
        label = "SettingsNavTransition"
    ) { level ->
        when (level) {
            SettingsScreenLevel.MAIN -> MainSettingsList(
                onNavAppearance = { currentLevel = SettingsScreenLevel.APPEARANCE },
                onNavFileTree = { currentLevel = SettingsScreenLevel.FILE_TREE }
            )
            SettingsScreenLevel.APPEARANCE -> AppearanceSettings(vm, onBack = { currentLevel = SettingsScreenLevel.MAIN })
            SettingsScreenLevel.FILE_TREE -> FileTreeSettings(vm, onBack = { currentLevel = SettingsScreenLevel.MAIN })
        }
    }
}

/**
 * Übersicht der Einstellungskategorien.
 */
@Composable
fun MainSettingsList(onNavAppearance: () -> Unit, onNavFileTree: () -> Unit) {
    Column(modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(16.dp)) {
        Text("Einstellungen", style = MaterialTheme.typography.headlineMedium)
        Spacer(Modifier.height(16.dp))
        
        SettingsSectionHeader("Editor & UI")
        SettingsEntry(Icons.Default.Palette, "Erscheinungsbild", "Designs, AMOLED, Farben", onNavAppearance)
        SettingsEntry(Icons.Default.AccountTree, "Dateibaum", "Sortierung & Ansicht", onNavFileTree)
        
        SettingsSectionHeader("System")
        SettingsEntry(Icons.Default.Info, "Über SrvHive", "Version 1.0.0", {})
    }
}

/**
 * Untermenü für Design-Einstellungen.
 */
@Composable
fun AppearanceSettings(vm: SettingsViewModel, onBack: () -> Unit) {
    val context = LocalContext.current
    var isDeleteMode by remember { mutableStateOf(false) }
    val filePicker = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        uri?.let { vm.installThemeFromUri(context, it) }
    }

    Column(modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(16.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
            IconButton(onClick = onBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, "Zurück") }
            Text("Design", style = MaterialTheme.typography.headlineMedium, modifier = Modifier.weight(1f))
            // Button zum Umschalten des Lösch-Modus
            IconButton(onClick = { isDeleteMode = !isDeleteMode }) {
                Icon(if (isDeleteMode) Icons.Default.Close else Icons.Default.Delete, "Löschmodus")
            }
        }

        SettingsSectionHeader("Modus")
        ThemeOption("System folgen", vm.themeMode == ThemeMode.SYSTEM) { vm.setTheme(ThemeMode.SYSTEM) }
        ThemeOption("Hell", vm.themeMode == ThemeMode.LIGHT) { vm.setTheme(ThemeMode.LIGHT) }
        ThemeOption("Dunkel", vm.themeMode == ThemeMode.DARK) { vm.setTheme(ThemeMode.DARK) }

        HorizontalDivider(Modifier.padding(vertical = 12.dp))

        SettingsSectionHeader("Installierte Themes")
        ListItem(
            modifier = Modifier.clickable { vm.activeThemeId = null },
            headlineContent = { Text("Standard Material 3") },
            trailingContent = { RadioButton(vm.activeThemeId == null, null) }
        )

        vm.installedThemes.forEach { theme ->
            ListItem(
                modifier = Modifier.clickable { vm.activeThemeId = theme.id },
                headlineContent = { Text(theme.name) },
                supportingContent = { Text(if (theme.isRemovable) "Benutzerdefiniert" else "System") },
                leadingContent = { Icon(Icons.Default.ColorLens, null) },
                trailingContent = {
                    if (isDeleteMode && theme.isRemovable) {
                        IconButton(onClick = { vm.deleteTheme(context, theme) }) {
                            Icon(Icons.Default.DeleteForever, null, tint = MaterialTheme.colorScheme.error)
                        }
                    } else {
                        RadioButton(vm.activeThemeId == theme.id, null)
                    }
                }
            )
        }

        Button(onClick = { filePicker.launch("application/json") }, modifier = Modifier.fillMaxWidth().padding(top = 8.dp)) {
            Icon(Icons.Default.FileUpload, null)
            Spacer(Modifier.width(8.dp))
            Text("Theme importieren (.json)")
        }

        HorizontalDivider(Modifier.padding(vertical = 12.dp))

        SettingsSectionHeader("Erweitert")
        ListItem(
            headlineContent = { Text("Dynamische Farben") },
            trailingContent = { Switch(vm.isDynamicColorEnabled, { vm.toggleDynamicColor(it) }, enabled = vm.activeThemeId == null) }
        )
        ListItem(
            headlineContent = { Text("AMOLED Modus") },
            trailingContent = { Switch(vm.isAmoledEnabled, { vm.toggleAmoled(it) }, enabled = vm.themeMode != ThemeMode.LIGHT) }
        )
    }
}

/**
 * Untermenü für Dateibaum-Einstellungen.
 */
@Composable
fun FileTreeSettings(vm: SettingsViewModel, onBack: () -> Unit) {
    Column(modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(16.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = onBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, "Zurück") }
            Text("Dateibaum", style = MaterialTheme.typography.headlineMedium)
        }

        SettingsSectionHeader("Sortierung")
        SortOption("Name", vm.fileSortBy == FileSortBy.NAME) { vm.updateFileSort(FileSortBy.NAME) }
        SortOption("Dateityp", vm.fileSortBy == FileSortBy.TYPE) { vm.updateFileSort(FileSortBy.TYPE) }
        SortOption("Größe", vm.fileSortBy == FileSortBy.SIZE) { vm.updateFileSort(FileSortBy.SIZE) }

        HorizontalDivider(Modifier.padding(vertical = 12.dp))

        SettingsSectionHeader("Reihenfolge")
        SortOption("Aufsteigend", vm.fileSortOrder == FileSortOrder.ASCENDING) { vm.updateFileOrder(FileSortOrder.ASCENDING) }
        SortOption("Absteigend", vm.fileSortOrder == FileSortOrder.DESCENDING) { vm.updateFileOrder(FileSortOrder.DESCENDING) }

        HorizontalDivider(Modifier.padding(vertical = 12.dp))

        SettingsSectionHeader("Ansicht")
        ListItem(
            headlineContent = { Text("Versteckte Dateien anzeigen") },
            supportingContent = { Text("Dateien, die mit '.' beginnen") },
            trailingContent = { Switch(vm.showHiddenFiles, { vm.toggleHiddenFiles(it) }) }
        )
    }
}

// --- Hilfskomponenten für das UI ---

@Composable
private fun SettingsEntry(icon: ImageVector, title: String, sub: String, onClick: () -> Unit) {
    ListItem(
        modifier = Modifier.clickable { onClick() },
        headlineContent = { Text(title) },
        supportingContent = { Text(sub) },
        leadingContent = { Icon(icon, null) },
        trailingContent = { Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, null) }
    )
}

@Composable
private fun SortOption(title: String, selected: Boolean, onClick: () -> Unit) {
    ListItem(modifier = Modifier.clickable { onClick() }, headlineContent = { Text(title) }, trailingContent = { RadioButton(selected, null) })
}

@Composable
private fun ThemeOption(title: String, selected: Boolean, onClick: () -> Unit) {
    ListItem(modifier = Modifier.clickable { onClick() }, headlineContent = { Text(title) }, trailingContent = { RadioButton(selected, null) })
}

@Composable
private fun SettingsSectionHeader(title: String) {
    Text(
        text = title, 
        style = MaterialTheme.typography.labelLarge, 
        color = MaterialTheme.colorScheme.primary, 
        modifier = Modifier.padding(top = 16.dp, bottom = 8.dp)
    )
}