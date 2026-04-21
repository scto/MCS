package com.scto.mcs.feature.settings.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.scto.mcs.feature.settings.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(id = R.string.settings_title)) },
                navigationIcon = {
                    IconButton(onClick = { viewModel.onBackClicked() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = stringResource(R.string.settings_nav_back_content_description))
                    }
                }
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            val groupedItems = state.items.groupBy { it.section }
            
            groupedItems.forEach { (section, items) ->
                item {
                    Text(
                        text = stringResource(id = section.titleRes),
                        style = MaterialTheme.typography.titleSmall,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(16.dp)
                    )
                }
                items(items) { item ->
                    ListItem(
                        headlineContent = { Text(stringResource(id = item.titleRes)) },
                        supportingContent = { Text(stringResource(id = item.descriptionRes)) },
                        leadingContent = {
                            Icon(
                                imageVector = getIconForId(item.id),
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        },
                        modifier = Modifier.fillMaxWidth(),
                        onClick = { viewModel.navigateTo(item.route) }
                    )
                }
            }
        }
    }
}

@Composable
private fun getIconForId(id: String) = when (id) {
    "app" -> Icons.Default.Android
    "theme" -> Icons.Default.Palette
    "editor" -> Icons.Default.Edit
    "keybinds" -> Icons.Default.Keyboard
    "git" -> Icons.Default.CallSplit
    "terminal" -> Icons.Default.Terminal
    "runners" -> Icons.Default.PlayArrow
    "extension" -> Icons.Default.Extension
    "debug" -> Icons.Default.BugReport
    "lsp" -> Icons.Default.Code
    "language" -> Icons.Default.Language
    "about" -> Icons.Default.Info
    "support" -> Icons.Default.Favorite
    else -> Icons.Default.Settings
}
