package com.scto.mcs.feature.welcome

import android.os.Build

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver

import com.scto.mcs.core.resources.R
import com.scto.mcs.core.utils.PermissionManager
import com.scto.mcs.core.ui.theme.ThemeViewModel
import com.scto.mcs.core.ui.components.ColorPickerDialog
import com.scto.mcs.core.ui.components.McsIcon

// Neue Imports für die ausgelagerten Komponenten
import com.scto.mcs.core.ui.components.CustomThemeCard
import com.scto.mcs.core.ui.components.PermissionCard
import com.scto.mcs.core.ui.components.ThemePreviewCard
import com.scto.mcs.core.ui.components.WelcomeBackground
import com.scto.mcs.core.ui.components.WelcomeBottomBar

import kotlinx.coroutines.launch

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun WelcomeScreen(
    themeViewModel: ThemeViewModel,
    onWelcomeFinished: () -> Unit
) {
    val lifecycleOwner = androidx.lifecycle.compose.LocalLifecycleOwner.current
    val themeState by themeViewModel.themeState.collectAsState()
    val scope = rememberCoroutineScope()
    val pagerState = rememberPagerState(pageCount = { 3 })

    var storageGranted by remember { mutableStateOf(false) }
    var installGranted by remember { mutableStateOf(true) }

    var showColorPicker by remember { mutableStateOf(false) }
    var customColor by remember { mutableStateOf(themeState.customColor) }
    var selectedModeIndex by remember { mutableIntStateOf(themeState.selectedModeIndex) }
    var selectedThemeIndex by remember {
        mutableIntStateOf(if (themeState.isCustomTheme) themeColors.size else themeState.selectedThemeIndex)
    }
    var isMonetEnabled by remember { mutableStateOf(themeState.isMonetEnabled) }

    val systemDark = isSystemInDarkTheme()
    val isDarkTheme = remember(selectedModeIndex, systemDark) {
        when (selectedModeIndex) {
            1 -> false
            2 -> true
            else -> systemDark
        }
    }

    // --- Logik-Fix für WelcomeScreen.kt ---

    // 1. Berechne aktuelle Theme-Vorschau-Daten
    val currentPreviewTheme: ThemeColor? = remember(selectedThemeIndex, customColor, isMonetEnabled, isDarkTheme) {
        if (isMonetEnabled) {
            null
        } else if (selectedThemeIndex < themeColors.size) {
            themeColors[selectedThemeIndex]
        } else {
            // [Benutzerdefinierter Modus]
            // 1. Hintergrundfarbe bestimmen: Dunkel nutzt fast reines Schwarz, Hell nutzt reines Weiß mit etwas Grau
            val bgDark = Color(0xFF121212)
            val bgLight = Color(0xFFF8F9FA)

            // 2. Wichtig: customColor an Primary und Accent übergeben
            // So können die Lichtkugeln im WelcomeBackground die benutzerdefinierte Farbe lesen!
            val customSpecDark = ThemeColorSpec(
                background = bgDark,
                surface = Color(0xFF1E1E1E),
                primary = customColor,
                accent = customColor // Beide Kugeln nutzen die benutzerdefinierte Farbe
            )
            val customSpecLight = ThemeColorSpec(
                background = bgLight,
                surface = Color.White,
                primary = customColor,
                accent = customColor
            )

            ThemeColor("Custom", customSpecDark, customSpecLight)
        }
    }

    // 2. Ziel-Hintergrundfarbe berechnen (für Text-Kontrast)
    val targetBg = if (isMonetEnabled) {
        if (isDarkTheme) MaterialTheme.colorScheme.surface else MaterialTheme.colorScheme.surfaceContainerLowest
    } else if (selectedThemeIndex < themeColors.size) {
        val theme = themeColors[selectedThemeIndex]
        if (isDarkTheme) theme.dark.background else theme.light.background
    } else {
        // Benutzerdefinierter Farbmodus: Nutzt die in Theme.kt generierte Hintergrundfarbe
        MaterialTheme.colorScheme.background
    }

    val animatedBgColor by animateColorAsState(targetBg, tween(600), label = "bg_color")

    // 3. Intelligente Textfarbe: Schwellenwert erhöht, um weißen Text auf hellgrauem Hintergrund zu vermeiden
    val contentColor by animateColorAsState(
        if (animatedBgColor.luminance() > 0.45f) Color.Black else Color.White,
        tween(600),
        label = "content_color"
    )

    val permissionState = PermissionManager.rememberPermissionRequest(
        onPermissionGranted = { storageGranted = true },
        onPermissionDenied = { }
    )
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) storageGranted =
                permissionState.hasPermissions()
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
    }

    CompositionLocalProvider(LocalContentColor provides contentColor) {
        Scaffold(
            containerColor = Color.Transparent,
            contentWindowInsets = WindowInsets(0, 0, 0, 0),
            bottomBar = {
                // Highlight-Farbe für die untere Navigationsleiste berechnen
                val activeColor = when {
                    isMonetEnabled -> MaterialTheme.colorScheme.primary
                    // Falls benutzerdefinierter Modus (index == size), nutze customColor
                    selectedThemeIndex == themeColors.size -> customColor
                    // Ansonsten nutze die Theme-Farbe
                    else -> if (isDarkTheme) themeColors[selectedThemeIndex].dark.primary else themeColors[selectedThemeIndex].light.primary
                }

                WelcomeBottomBar(
                    pagerState = pagerState,
                    activeColor = activeColor,
                    isLastPage = pagerState.currentPage == 2,
                    onBack = { scope.launch { pagerState.animateScrollToPage(pagerState.currentPage - 1) } },
                    onNext = {
                        if (pagerState.currentPage < 2) {
                            scope.launch { pagerState.animateScrollToPage(pagerState.currentPage + 1) }
                        } else {
                            themeViewModel.saveThemeConfig(
                                selectedModeIndex, selectedThemeIndex, customColor, isMonetEnabled,
                                selectedThemeIndex == themeColors.size
                            )
                            onWelcomeFinished()
                        }
                    }
                )
            }
        ) { paddingValues ->
            Box(modifier = Modifier.fillMaxSize()) {
                // Hintergrund-Ebene
                WelcomeBackground(
                    currentTheme = currentPreviewTheme,
                    isDarkTheme = isDarkTheme,
                    monetPrimary = MaterialTheme.colorScheme.primary,
                    monetTertiary = MaterialTheme.colorScheme.tertiary
                )

                // Inhalts-Ebene
                HorizontalPager(
                    state = pagerState,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                ) { page ->
                    when (page) {
                        0 -> IntroContent()
                        1 -> PermissionsContent(
                            storageGranted = storageGranted,
                            installGranted = installGranted,
                            onRequestStoragePermission = { permissionState.requestPermissions() },
                            onRequestInstallPermission = { /* ... */ }
                        )

                        2 -> ThemeSetupContent(
                            selectedModeIndex = selectedModeIndex,
                            selectedThemeIndex = selectedThemeIndex,
                            isMonetEnabled = isMonetEnabled,
                            isDarkTheme = isDarkTheme, // Aktuellen Modus übergeben
                            onMonetToggle = { isMonetEnabled = it },
                            onModeSelected = { selectedModeIndex = it },
                            onThemeSelected = { selectedThemeIndex = it },
                            onCustomColorClick = {
                                selectedThemeIndex = themeColors.size
                                showColorPicker = true
                            }
                        )
                    }
                }
            }
        }
    }

    if (showColorPicker) {
        ColorPickerDialog(
            initialColor = customColor,
            onDismiss = { showColorPicker = false },
            onColorSelected = { color ->
                customColor = color
                showColorPicker = false
                selectedThemeIndex = themeColors.size
            }
        )
    }
}

// --- Seite 1: Intro ---
@Composable
private fun IntroContent() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Box(modifier = Modifier.size(250.dp)) { McsIcon() }
           // Spacer(Modifier.height(20.dp))
            Text(
                stringResource(R.string.app_name),
                style = MaterialTheme.typography.displayMedium.copy(
                    fontWeight = FontWeight.ExtraBold,
                    letterSpacing = 2.sp
                )
            )
            Spacer(Modifier.height(16.dp))
            Text(
                stringResource(R.string.welcome_tagline),
                style = MaterialTheme.typography.titleMedium,
                color = LocalContentColor.current.copy(alpha = 0.8f)
            )
        }
    }
}

// --- Seite 2: Berechtigungen ---
@Composable
private fun PermissionsContent(
    storageGranted: Boolean,
    installGranted: Boolean,
    onRequestStoragePermission: () -> Unit,
    onRequestInstallPermission: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            stringResource(R.string.welcome_permissions_title),
            style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold)
        )
        Spacer(Modifier.height(8.dp))
        Text(
            stringResource(R.string.welcome_permissions_description),
            style = MaterialTheme.typography.bodyMedium,
            color = LocalContentColor.current.copy(alpha = 0.8f)
        )

        Spacer(Modifier.height(32.dp))

        PermissionCard(
            Icons.Default.Folder,
            stringResource(R.string.welcome_permission_storage_title),
            stringResource(R.string.welcome_permission_storage_description),
            storageGranted,
            onRequestStoragePermission
        )
        Spacer(Modifier.height(12.dp))
        PermissionCard(
            Icons.Default.Download,
            stringResource(R.string.welcome_permission_install_title),
            stringResource(R.string.welcome_permission_install_description),
            installGranted,
            onRequestInstallPermission
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ThemeSetupContent(
    selectedModeIndex: Int,
    selectedThemeIndex: Int,
    isMonetEnabled: Boolean,
    isDarkTheme: Boolean,
    onMonetToggle: (Boolean) -> Unit,
    onModeSelected: (Int) -> Unit,
    onThemeSelected: (Int) -> Unit,
    onCustomColorClick: () -> Unit
) {
    val modeOptions = listOf(
        stringResource(R.string.action_follow_system),
        stringResource(R.string.action_light),
        stringResource(R.string.action_dark)
    )

    // 1. Padding vom Eltern-Container entfernen, nur vertikales Scrollen behalten
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.Center
    ) {
        // 2. Padding nur für interne Elemente hinzufügen
        Text(
            stringResource(R.string.welcome_appearance_title),
            style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
            modifier = Modifier.padding(horizontal = 24.dp) // <--- Hier hinzufügen
        )
        Spacer(Modifier.height(32.dp))

        // Modus-Auswahl-Buttons
        SingleChoiceSegmentedButtonRow(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp) // <--- Hier hinzufügen
        ) {
            modeOptions.forEachIndexed { index, label ->
                SegmentedButton(
                    selected = selectedModeIndex == index,
                    onClick = { onModeSelected(index) },
                    shape = SegmentedButtonDefaults.itemShape(index = index, count = modeOptions.size),
                    colors = SegmentedButtonDefaults.colors(
                        activeContainerColor = MaterialTheme.colorScheme.secondaryContainer,
                        activeContentColor = MaterialTheme.colorScheme.onSecondaryContainer,
                        inactiveContainerColor = Color.Transparent,
                        inactiveContentColor = LocalContentColor.current
                    )
                ) { Text(label) }
            }
        }

        Spacer(Modifier.height(24.dp))

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            ListItem(
                headlineContent = { Text(stringResource(R.string.welcome_dynamic_color)) },
                trailingContent = { Switch(checked = isMonetEnabled, onCheckedChange = onMonetToggle) },
                colors = ListItemDefaults.colors(
                    containerColor = Color.Transparent,
                    headlineColor = LocalContentColor.current,
                    trailingIconColor = LocalContentColor.current
                ),
                modifier = Modifier.padding(horizontal = 8.dp) // ListItem hat bereits etwas Padding, hier nur leicht anpassen
            )
        }

        // 3. Theme-Liste: Nutzt LazyRow, um Abschneiden zu verhindern
        AnimatedVisibility(visible = !isMonetEnabled) {
            Column {
                Spacer(Modifier.height(24.dp))

                // LazyRow anstelle von Row + Scroll verwenden
                LazyRow(
                    // Wichtig: contentPadding erlaubt Scrollen bis zum Bildschirmrand, startet aber eingerückt
                    contentPadding = PaddingValues(horizontal = 24.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Vordefinierte Themes rendern
                    itemsIndexed(themeColors) { index, theme ->
                        ThemePreviewCard(
                            theme = theme,
                            isSelected = selectedThemeIndex == index,
                            isDarkTheme = isDarkTheme,
                            onClick = { onThemeSelected(index) }
                        )
                    }

                    // Custom-Button rendern
                    item {
                        CustomThemeCard(
                            isSelected = selectedThemeIndex == themeColors.size,
                            onClick = onCustomColorClick
                        )
                    }
                }
            }
        }
    }
}