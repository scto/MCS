package com.scto.mcs.feature.editor.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Class
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.scto.mcs.core.domain.model.Symbol
import com.scto.mcs.core.domain.model.SymbolKind

/**
 * Dialog für die projektweite Suche nach Klassen und Symbolen.
 */
@Composable
fun SymbolSearchDialog(
    onDismiss: () -> Unit,
    onSymbolSelected: (Symbol) -> Unit,
    onSearch: (String) -> Unit,
    results: List<Symbol>
) {
    var query by remember { mutableStateOf("") }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier.fillMaxWidth().fillMaxHeight(0.7f),
            shape = androidx.compose.foundation.shape.RoundedCornerShape(12.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("Symbol suchen", style = MaterialTheme.typography.titleLarge)
                
                OutlinedTextField(
                    value = query,
                    onValueChange = { 
                        query = it
                        onSearch(it)
                    },
                    modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                    placeholder = { Text("Name eingeben...") },
                    leadingIcon = { Icon(Icons.Default.Search, null) },
                    singleLine = true
                )

                LazyColumn(modifier = Modifier.weight(1f)) {
                    items(results) { symbol ->
                        SymbolItem(symbol) { onSymbolSelected(symbol) }
                    }
                }
            }
        }
    }
}

@Composable
private fun SymbolItem(symbol: Symbol, onClick: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth().clickable { onClick() }.padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = getSymbolIcon(symbol.kind),
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(20.dp)
        )
        Spacer(Modifier.width(12.dp))
        Column {
            Text(symbol.name, style = MaterialTheme.typography.bodyLarge)
            Text(symbol.filePath.split("/").last(), style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.outline)
        }
    }
}

private fun getSymbolIcon(kind: SymbolKind): ImageVector = when(kind) {
    SymbolKind.CLASS, SymbolKind.INTERFACE -> Icons.Default.Class
    else -> Icons.Default.Code
}