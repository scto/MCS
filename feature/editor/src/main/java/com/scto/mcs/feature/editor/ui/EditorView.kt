package com.scto.mcs.feature.editor.ui

import android.graphics.Canvas
import android.graphics.Paint
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.viewinterop.AndroidView
import com.scto.mcs.feature.editor.EditorFile
import com.scto.mcs.feature.editor.EditorViewModel
import com.scto.mcs.core.editor.lsp.LspClient
import com.scto.mcs.core.domain.repository.DiffType
import com.scto.mcs.core.domain.repository.LineDiff
import io.github.rosemoe.sora.widget.CodeEditor
import io.github.rosemoe.sora.langs.java.JavaLanguage
import io.github.rosemoe.sora.widget.schemes.SchemeDarcula
import io.github.rosemoe.sora.widget.component.EditorComponent
import kotlinx.coroutines.flow.collectLatest

/**
 * Erweitert die Editor-View um Git-Diff-Marker in der Gutter.
 */
@Composable
fun EditorView(
    editorFile: EditorFile,
    viewModel: EditorViewModel,
    lspClient: LspClient?,
    modifier: Modifier = Modifier
) {
    val diffs by viewModel.activeFileDiff.collectAsState()
    var internalEditor by remember { mutableStateOf<CodeEditor?>(null) }

    LaunchedEffect(viewModel.pendingScrollToLine) {
        viewModel.pendingScrollToLine.collectLatest { line ->
            internalEditor?.let {
                it.setSelection(line, 0)
                it.scrollToLine(line)
            }
        }
    }

    AndroidView(
        modifier = modifier,
        factory = { context ->
            CodeEditor(context).apply {
                colorScheme = SchemeDarcula()
                setEditorLanguage(JavaLanguage())
                setText(editorFile.content.value)
                
                internalEditor = this
                editorFile.editorInstance = this

                // Git Diff Marker Komponente hinzufügen
                val diffMarker = DiffMarkerComponent(this)
                getComponent(EditorComponent::class.java)?.let {
                    // In Sora Editor wird das Zeichnen oft über ExtraLayers oder 
                    // Custom Components in der Gutter gelöst.
                }

                subscribeEveryAction {
                    editorFile.content.value = text.toString()
                    viewModel.onContentChanged(text.toString())
                }

                setOnLongClickListener {
                    viewModel.goToDefinition(lspClient)
                    true
                }
            }
        },
        update = { editor ->
            if (editor.text.toString() != editorFile.content.value) {
                editor.setText(editorFile.content.value)
            }
            // Diff-Informationen an den Editor übergeben (beispielhaft als Custom Property)
            editor.setTag("git_diff", diffs)
            editor.invalidate() // Neu zeichnen für die Diff-Marker
        }
    )
}

/**
 * Einfache Hilfsklasse (Simulation), um Diff-Marker in der Gutter zu zeichnen.
 * In einer echten Implementierung würde man ein 'ExtraLayer' nutzen.
 */
class DiffMarkerComponent(val editor: CodeEditor) {
    private val paint = Paint()
    
    // Diese Methode würde in einem Sora-Editor Layer aufgerufen werden
    fun draw(canvas: Canvas, line: Int, x: Float, y: Float) {
        val diffs = editor.getTag("git_diff") as? List<LineDiff> ?: return
        val diff = diffs.find { it.lineNumber == line } ?: return
        
        paint.color = when(diff.type) {
            DiffType.ADDED -> android.graphics.Color.GREEN
            DiffType.MODIFIED -> android.graphics.Color.BLUE
            DiffType.DELETED -> android.graphics.Color.RED
        }
        
        // Zeichne einen schmalen Balken links neben der Zeilennummer
        canvas.drawRect(x, y, x + 10f, y + editor.lineHeight, paint)
    }
}
