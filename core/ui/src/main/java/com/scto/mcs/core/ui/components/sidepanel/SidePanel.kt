package com.scto.mcs.core.ui.components.sidepanel

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp

import com.scto.mcs.core.ui.components.sidepanel.filetree.FileTreeContent
import com.scto.mcs.core.ui.components.sidepanel.git.GitManagerContent
import com.scto.mcs.core.navigation.NavRoutes

/**
 * SidePanel mit Navigations-Leiste (10%) und Inhaltsbereich (90%).
 */
@Composable
fun SidePanel(
    modifier: Modifier = Modifier,
    viewModel: SidePanelViewModel,
    currentRoute: String?,
    onNavigate: (String) -> Unit
) {
    val activePanel by viewModel.activePanel.collectAsState()

    Row(modifier = modifier.fillMaxSize().background(MaterialTheme.colorScheme.surface)) {
        // Navigations- und Tool-Leiste (links)
        Column(
            modifier = Modifier
                .fillMaxHeight()
                .width(60.dp)
                .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f))
                .padding(vertical = 12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // App-Navigation
            NavIconButton(Icons.Default.Edit, currentRoute == NavRoutes.EDITOR) { onNavigate(NavRoutes.EDITOR) }
            NavIconButton(Icons.Default.Settings, currentRoute == NavRoutes.SETTINGS) { onNavigate(NavRoutes.SETTINGS) }

            Spacer(modifier = Modifier.weight(1f))
            HorizontalDivider(modifier = Modifier.padding(horizontal = 8.dp, vertical = 8.dp))

            // IDE-Tools
            ToolIconButton(Icons.Default.Folder, activePanel == SidePanelType.FILE_TREE) { viewModel.setPanel(SidePanelType.FILE_TREE) }
            ToolIconButton(Icons.Default.AccountTree, activePanel == SidePanelType.GIT_MANAGER) { viewModel.setPanel(SidePanelType.GIT_MANAGER) }
        }

        // Dynamischer Inhaltsbereich (rechts)
        Column(modifier = Modifier.weight(1f).fillMaxHeight().padding(8.dp)) {
            when (activePanel) {
                SidePanelType.FILE_TREE -> FileTreeContent()
                SidePanelType.GIT_MANAGER -> GitManagerContent()
                else -> {}
            }
        }
    }
}

@Composable
private fun NavIconButton(icon: ImageVector, isSelected: Boolean, onClick: () -> Unit) {
    IconButton(onClick = onClick) {
        Icon(icon, null, tint = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant)
    }
}

@Composable
private fun ToolIconButton(icon: ImageVector, isSelected: Boolean, onClick: () -> Unit) {
    IconButton(
        onClick = onClick,
        modifier = Modifier.padding(vertical = 4.dp)
            .background(
                color = if (isSelected) MaterialTheme.colorScheme.primaryContainer else Color.Transparent,
                shape = RoundedCornerShape(8.dp)
            )
    ) {
        Icon(icon, null, tint = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant)
    }
}