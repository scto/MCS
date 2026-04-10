package com.scto.mcs.core.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

// Dark Mode Palette for IDE
private val DarkColors = darkColorScheme(
    primary = Color(0xFF0D47A1), // Deep Blue
    onPrimary = Color.White,
    background = Color(0xFF1E1E1E),
    surface = Color(0xFF1E1E1E),
    onSurface = Color.White
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
