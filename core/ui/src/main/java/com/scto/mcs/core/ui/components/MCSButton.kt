package com.scto.mcs.core.ui.components

import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import com.scto.mcs.core.ui.theme.IdePrimary
import com.scto.mcs.core.ui.theme.IdeOnPrimary

@Composable
fun MCSButton(
    text: String,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        colors = ButtonDefaults.buttonColors(
            containerColor = IdePrimary,
            contentColor = IdeOnPrimary
        )
    ) {
        Text(text = text)
    }
}
