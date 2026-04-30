package com.scto.mcs.feature.terminal.ui

import android.graphics.Typeface
import android.view.KeyEvent
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController

import com.scto.mcs.core.resources.R
import com.scto.mcs.core.resources.strings
import com.scto.mcs.core.terminal.setup.TerminalSetupService.SetupState
import com.scto.mcs.core.terminal.virtualkeys.*
import com.scto.mcs.core.ui.components.setup.TerminalSetupView
import com.scto.mcs.core.ui.theme.LocalThemeHolder
import com.scto.mcs.core.ui.animations.NavigationAnimationTransitions
import com.scto.mcs.core.utils.dpToPx
import com.scto.mcs.feature.settings.Settings
import com.scto.mcs.feature.settings.SettingsRoutes
import com.scto.mcs.feature.terminal.TerminalViewModel
import com.termux.view.TerminalView
import kotlinx.coroutines.launch
import java.util.Properties

/**
 * Der modernisierte Terminal-Hauptbildschirm.
 * Verwaltet die Navigation zwischen Terminal-Ansicht und Einstellungen.
 */
@Composable
fun TerminalScreen(viewModel: TerminalViewModel = hiltViewModel()) {
    val navController = rememberNavController()
    
    NavHost(
        navController = navController,
        startDestination = "terminal_main",
        enterTransition = { NavigationAnimationTransitions.enterTransition },
        exitTransition = { NavigationAnimationTransitions.exitTransition }
    ) {
        composable("terminal_main") {
            TerminalContent(viewModel = viewModel, navController = navController)
        }
        // Integration der Terminal-spezifischen Einstellungen
        composable(SettingsRoutes.TerminalSettings.route) { /* SettingsTerminalScreen(navController) */ }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TerminalContent(
    viewModel: TerminalViewModel,
    navController: NavController
) {
    val setupState by viewModel.setupState.collectAsState()
    val scope = rememberCoroutineScope()
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val context = LocalContext.current
    val themeHolder = LocalThemeHolder.current
    val isDarkMode = isSystemInDarkTheme()

    // Falls das Terminal noch nicht installiert ist, Setup anzeigen
    if (setupState !is SetupState.Completed) {
        TerminalSetupView(setupState) { viewModel.startInstallation() }
        return
    }

    val configuration = LocalConfiguration.current
    val drawerWidth = (configuration.screenWidthDp * 0.85).dp

    BackHandler(enabled = drawerState.isOpen) {
        scope.launch { drawerState.close() }
    }

    ModalNavigationDrawer(
        drawerState = drawerState,
        gesturesEnabled = drawerState.isOpen,
        drawerContent = {
            TerminalSessionDrawer(drawerWidth, viewModel) {
                scope.launch { drawerState.close() }
            }
        }
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text(stringResource(strings.terminal)) },
                    navigationIcon = {
                        IconButton(onClick = { scope.launch { drawerState.open() } }) {
                            Icon(Icons.Default.Menu, contentDescription = "Menü")
                        }
                    },
                    actions = {
                        IconButton(onClick = { viewModel.createNewSession() }) {
                            Icon(Icons.Default.Add, contentDescription = "Neue Session")
                        }
                    }
                )
            }
        ) { padding ->
            Column(
                modifier = Modifier
                    .padding(padding)
                    .fillMaxSize()
                    .imePadding()
            ) {
                // Haupt-Emulator Ansicht
                TerminalEmulatorView(viewModel, isDarkMode, themeHolder)

                // Steuerung: Pager für Extra Keys und Befehlseingabe
                TerminalControlsPager(viewModel)
            }
        }
    }
}

@Composable
private fun ColumnScope.TerminalEmulatorView(
    viewModel: TerminalViewModel,
    isDarkMode: Boolean,
    themeHolder: com.scto.mcs.core.ui.theme.ThemeHolder
) {
    val context = LocalContext.current
    val activeSession by viewModel.activeSession.collectAsState()
    val surfaceColor = MaterialTheme.colorScheme.surface.toArgb()
    val onSurfaceColor = MaterialTheme.colorScheme.onSurface.toArgb()

    AndroidView(
        factory = { ctx ->
            TerminalView(ctx, null).apply {
                val colors = if (isDarkMode) themeHolder.darkTerminalColors else themeHolder.lightTerminalColors
                // Farben und Textgröße anwenden
                applyMcsColors(surfaceColor, onSurfaceColor, colors)
                setTextSize(dpToPx(Settings.terminal_font_size.toFloat(), ctx))
                
                // Session binden
                activeSession?.let { attachSession(it) }
                
                post { requestFocus() }
            }
        },
        modifier = Modifier.weight(1f).fillMaxWidth(),
        update = { view ->
            activeSession?.let { 
                if (view.mTermSession != it) view.attachSession(it) 
            }
        }
    )
}

@Composable
private fun TerminalControlsPager(viewModel: TerminalViewModel) {
    val pagerState = rememberPagerState(pageCount = { 2 })
    val onSurfaceColor = MaterialTheme.colorScheme.onSurface.toArgb()

    HorizontalPager(
        state = pagerState,
        modifier = Modifier.fillMaxWidth().height(80.dp)
    ) { page ->
        when (page) {
            0 -> { // Seite 1: Virtuelle Tasten (Extra Keys)
                AndroidView(
                    factory = { ctx ->
                        VirtualKeysView(ctx, null).apply {
                            buttonTextColor = onSurfaceColor
                            reload(VirtualKeysInfo(
                                Settings.terminal_extra_keys,
                                "",
                                VirtualKeysConstants.CONTROL_CHARS_ALIASES
                            ))
                        }
                    },
                    modifier = Modifier.fillMaxSize()
                )
            }
            1 -> { // Seite 2: Schnelleingabe für Befehle
                var text by remember { mutableStateOf("") }
                val focusRequester = remember { FocusRequester() }
                
                TextField(
                    value = text,
                    onValueChange = { text = it },
                    modifier = Modifier.fillMaxSize().focusRequester(focusRequester),
                    placeholder = { Text("Befehl eingeben...") },
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Send),
                    keyboardActions = KeyboardActions(onSend = {
                        if (text.isNotBlank()) {
                            viewModel.runCommand(text)
                            text = ""
                        }
                    }),
                    singleLine = true
                )
                LaunchedEffect(Unit) { focusRequester.requestFocus() }
            }
        }
    }
}

@Composable
private fun TerminalSessionDrawer(
    width: androidx.compose.ui.unit.Dp,
    viewModel: TerminalViewModel,
    onClose: () -> Unit
) {
    val sessions by viewModel.sessions.collectAsState()
    val activeId by viewModel.activeSessionId.collectAsState()

    ModalDrawerSheet(modifier = Modifier.width(width)) {
        Text(
            text = stringResource(strings.sessions),
            modifier = Modifier.padding(16.dp),
            style = MaterialTheme.typography.titleLarge
        )
        
        sessions.forEach { session ->
            val isSelected = session.mHandle == activeId
            NavigationDrawerItem(
                label = { Text(session.mHandle ?: "Unbenannte Session") },
                selected = isSelected,
                onClick = { 
                    viewModel.switchSession(session.mHandle)
                    onClose()
                },
                modifier = Modifier.padding(horizontal = 12.dp),
                trailingContent = {
                    IconButton(onClick = { viewModel.removeSession(session.mHandle) }) {
                        Icon(Icons.Outlined.Delete, contentDescription = "Löschen")
                    }
                }
            )
        }
    }
}

private fun TerminalView.applyMcsColors(surfaceColor: Int, onSurfaceColor: Int, terminalColors: Properties) {
    // Implementierung der Farbanwendung
    this.onScreenUpdated()
    // ... (weitere Logik zur Farbanwendung)
}
