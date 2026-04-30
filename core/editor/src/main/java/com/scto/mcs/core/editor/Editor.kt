package com.scto.mcs.core.editor

import android.content.Context
import android.graphics.Typeface
import android.util.AttributeSet
import androidx.compose.material3.ColorScheme
import androidx.compose.ui.graphics.toArgb

import com.scto.mcs.core.editor.intelligent.IntelligentFeatureRegistry
import com.scto.mcs.feature.settings.SettingsViewModel

import io.github.rosemoe.sora.event.ContentChangeEvent
import io.github.rosemoe.sora.event.EditorKeyEvent
import io.github.rosemoe.sora.widget.CodeEditor
import io.github.rosemoe.sora.widget.EditorColorScheme
import io.github.rosemoe.sora.widget.component.EditorAutoCompletion
import io.github.rosemoe.sora.widget.subscribeEvent

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch

class Editor @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : CodeEditor(context, attrs, defStyleAttr) {

    private val scope = CoroutineScope(Dispatchers.Main)

    data class PatchArgs(
        val isDarkMode: Boolean,
        val editorSurface: Int,
        val onSurface: Int,
        val colorPrimary: Int,
        val selectionBg: Int,
        val gutterColor: Int,
        val currentLine: Int,
        val errorColor: Int,
        val matchColor: Int // Farbe für Suchergebnisse
    )

    init {
        getComponent(EditorAutoCompletion::class.java).setEnabledAnimation(true)
        applyBaseSettings()
        setupIntelligentFeatures()
    }

    private fun applyBaseSettings() {
        props.deleteMultiSpaces = 4
        tabWidth = 4
        isLineNumberEnabled = true
        isCursorAnimationEnabled = true
        setTextSize(14f)
        setWordwrap(false)
        typefaceText = Typeface.MONOSPACE
        typefaceLineNumber = Typeface.MONOSPACE
        lineSpacingMultiplier = 1.1f
    }

    private fun setupIntelligentFeatures() {
        subscribeEvent<ContentChangeEvent> { event, _ ->
            if (event.action == ContentChangeEvent.ACTION_INSERT) {
                val insertedText = event.changedText
                if (insertedText.length == 1) {
                    IntelligentFeatureRegistry.allFeatures.forEach { feature ->
                        if (feature.isEnabled()) {
                            feature.handleInsertChar(insertedText[0], this)
                        }
                    }
                }
            }
        }

        subscribeEvent<EditorKeyEvent> { event, _ ->
            IntelligentFeatureRegistry.allFeatures.forEach { feature ->
                if (feature.isEnabled()) {
                    feature.handleKeyEvent(event, this)
                }
            }
        }
    }

    fun syncWithTheme(vm: SettingsViewModel, colorScheme: ColorScheme, isDark: Boolean) {
        val patchArgs = PatchArgs(
            isDarkMode = isDark,
            editorSurface = colorScheme.surface.toArgb(),
            onSurface = colorScheme.onSurface.toArgb(),
            colorPrimary = colorScheme.primary.toArgb(),
            selectionBg = colorScheme.primary.copy(alpha = 0.3f).toArgb(),
            gutterColor = colorScheme.surfaceVariant.toArgb(),
            currentLine = colorScheme.primary.copy(alpha = 0.1f).toArgb(),
            errorColor = colorScheme.error.toArgb(),
            matchColor = colorScheme.tertiaryContainer.toArgb() // Suchergebnisse hervorheben
        )

        scope.launch {
            val createdColorScheme = ThemeManager.createColorScheme(context, vm, patchArgs)
            // Zusätzliche Farbanpassungen für Suche
            createdColorScheme.setColor(EditorColorScheme.MATCHED_TEXT_BACKGROUND, patchArgs.matchColor)
            this@Editor.colorScheme = createdColorScheme
        }
    }

    suspend fun setLanguage(textmateScope: String) {
        val language = LanguageManager.createLanguage(textmateScope)
        val keywords = KeywordManager.getKeywords(textmateScope)
        keywords?.let { language.setCompleterKeywords(it.toTypedArray()) }
        
        post { setEditorLanguage(language) }
    }

    override fun release() {
        scope.cancel()
        super.release()
    }
}