package com.scto.mcs.feature.git.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.scto.mcs.feature.git.GitViewModel
import com.scto.mcs.core.domain.model.GitCommit
import com.scto.mcs.core.domain.model.GitBranch
import com.scto.mcs.core.resources.R

@Composable
fun GitManagerContent(
    viewModel: GitViewModel = hiltViewModel()
) {
    val status by viewModel.status.collectAsState()
    val history by viewModel.history.collectAsState()
    val branches by viewModel.branches.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    
    var commitMessage by remember { mutableStateOf("") }
    var showBranchMenu by remember { mutableStateOf(false) }
    var showNewBranchDialog by remember { mutableStateOf(false) }

    val currentBranch = branches.find { it.isCurrent }?.name ?: stringResource(id = R.string.feature_git_no_branch)

    Column(modifier = Modifier.fillMaxSize().padding(8.dp)) {
        // Branch Selector Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { showBranchMenu = true }
                .padding(vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(Icons.Default.AccountTree, null, modifier = Modifier.size(18.dp), tint = MaterialTheme.colorScheme.primary)
            Spacer(Modifier.width(8.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(stringResource(id = R.string.feature_git_current_branch), style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.outline)
                Text(currentBranch, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold)
            }
            Icon(Icons.Default.ArrowDropDown, null)

            DropdownMenu(expanded = showBranchMenu, onDismissRequest = { showBranchMenu = false }) {
                branches.forEach { branch ->
                    DropdownMenuItem(
                        text = { Text(branch.name) },
                        onClick = {
                            viewModel.checkoutBranch(branch.name)
                            showBranchMenu = false
                        },
                        leadingIcon = {
                            if (branch.isCurrent) Icon(Icons.Default.Check, null, Modifier.size(16.dp))
                        }
                    )
                }
                Divider()
                DropdownMenuItem(
                    text = { Text(stringResource(id = R.string.feature_git_new_branch_ellipsis)) },
                    onClick = {
                        showNewBranchDialog = true
                        showBranchMenu = false
                    },
                    leadingIcon = { Icon(Icons.Default.Add, null) }
                )
            }
        }

        Spacer(Modifier.height(8.dp))

        // Commit Area
        OutlinedTextField(
            value = commitMessage,
            onValueChange = { commitMessage = it },
            modifier = Modifier.fillMaxWidth(),
            placeholder = { Text(stringResource(id = R.string.feature_git_commit_message_placeholder)) },
            trailingIcon = {
                IconButton(onClick = { 
                    viewModel.commit(commitMessage)
                    commitMessage = ""
                }, enabled = commitMessage.isNotBlank() && !isLoading) {
                    Icon(Icons.Default.Check, stringResource(id = R.string.feature_git_commit))
                }
            },
            maxLines = 3,
            textStyle = MaterialTheme.typography.bodySmall
        )

        if (isLoading) {
            LinearProgressIndicator(modifier = Modifier.fillMaxWidth().padding(top = 4.dp))
        }

        LazyColumn(modifier = Modifier.weight(1f)) {
            // Staged Changes
            status?.staged?.let { staged ->
                if (staged.isNotEmpty()) {
                    item { GitSectionHeader(stringResource(id = R.string.feature_git_staged_changes, staged.size)) }
                    items(staged) { file ->
                        GitFileItem(file, Color(0xFF4CAF50), Icons.Default.RemoveCircleOutline) {
                            viewModel.unstageFile(file)
                        }
                    }
                }
            }

            // Unstaged Changes
            status?.let { s ->
                val unstaged = s.unstaged + s.untracked
                if (unstaged.isNotEmpty()) {
                    item { GitSectionHeader(stringResource(id = R.string.feature_git_changes, unstaged.size)) }
                    items(unstaged) { file ->
                        val color = if (s.untracked.contains(file)) Color(0xFFE91E63) else Color(0xFFFFC107)
                        GitFileItem(file, color, Icons.Default.AddCircleOutline) {
                            viewModel.stageFile(file)
                        }
                    }
                }
            }

            // History
            if (history.isNotEmpty()) {
                item { GitSectionHeader(stringResource(id = R.string.feature_git_history)) }
                items(history) { commit ->
                    CommitHistoryItem(commit)
                }
            }
        }
    }

    if (showNewBranchDialog) {
        NewBranchDialog(
            onDismiss = { showNewBranchDialog = false },
            onConfirm = { name ->
                viewModel.createBranch(name)
                showNewBranchDialog = false
            }
        )
    }
}

@Composable
fun GitSectionHeader(title: String) {
    Text(
        text = title.uppercase(),
        style = MaterialTheme.typography.labelSmall,
        fontWeight = FontWeight.Bold,
        color = MaterialTheme.colorScheme.outline,
        modifier = Modifier.padding(top = 16.dp, bottom = 4.dp)
    )
}

@Composable
fun GitFileItem(file: String, indicatorColor: Color, actionIcon: androidx.compose.ui.graphics.vector.ImageVector, onAction: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 2.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(Modifier.size(4.dp, 16.dp).background(indicatorColor))
        Spacer(Modifier.width(8.dp))
        Text(file.split("/").last(), modifier = Modifier.weight(1f), fontSize = 13.sp)
        IconButton(onClick = onAction, modifier = Modifier.size(24.dp)) {
            Icon(actionIcon, null, modifier = Modifier.size(16.dp), tint = MaterialTheme.colorScheme.primary)
        }
    }
}

@Composable
fun CommitHistoryItem(commit: GitCommit) {
    Column(modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.History, null, modifier = Modifier.size(14.dp), tint = MaterialTheme.colorScheme.outline)
            Spacer(Modifier.width(8.dp))
            Text(commit.message, style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Medium)
        }
        Text(
            stringResource(id = R.string.feature_git_commit_info, commit.id.take(7), commit.author),
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.outline,
            modifier = Modifier.padding(start = 22.dp)
        )
    }
}

@Composable
fun NewBranchDialog(onDismiss: () -> Unit, onConfirm: (String) -> Unit) {
    var name by remember { mutableStateOf("") }
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(id = R.string.feature_git_create_new_branch)) },
        text = {
            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text(stringResource(id = R.string.feature_git_branch_name)) },
                singleLine = true
            )
        },
        confirmButton = {
            Button(onClick = { onConfirm(name) }, enabled = name.isNotBlank()) {
                Text(stringResource(id = R.string.feature_git_create))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text(stringResource(id = R.string.feature_git_cancel)) }
        }
    )
}
