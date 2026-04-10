package com.scto.mcs.core.ui.components

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.font.FontFamily
import com.scto.mcs.core.ui.theme.IdeTextPrimary

@Composable
fun TerminalText(text: String) {
    Text(
        text = text,
        fontFamily = FontFamily.Monospace,
        color = IdeTextPrimary
    )
}
