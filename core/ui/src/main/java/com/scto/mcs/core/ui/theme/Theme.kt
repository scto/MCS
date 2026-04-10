package com.scto.mcs.core.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

private val DarkColors = darkColorScheme(
    primary = IdePrimary,
    onPrimary = IdeOnPrimary,
    background = IdeBackground,
    surface = IdeSurface,
    onSurface = IdeTextPrimary
)

@Composable
fun MCSTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = DarkColors,
        content = content
    )
}
