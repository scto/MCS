package com.scto.mcs.core.ui.components

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import com.scto.mcs.core.ui.theme.IdeSurface
import com.scto.mcs.core.ui.theme.IdeTextPrimary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MCSToolbar(title: String) {
    TopAppBar(
        title = { Text(text = title, color = IdeTextPrimary) },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = IdeSurface
        )
    )
}
