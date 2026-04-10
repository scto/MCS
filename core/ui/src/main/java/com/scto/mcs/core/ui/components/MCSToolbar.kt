package com.scto.mcs.core.ui.components

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MCSToolbar(
    title: String,
    actions: @Composable () -> Unit = {}
) {
    TopAppBar(
        title = { 
            Text(
                text = title, 
                color = MaterialTheme.colorScheme.onSurface 
            ) 
        },
        actions = { actions() },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    )
}
