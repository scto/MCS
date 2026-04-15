/*
 * WebIDE - A powerful IDE for Android web development.
 * Copyright (C) 2025  如日中天  <3382198490@qq.com>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */


package com.scto.mcs.feature.editor

import android.graphics.Color as AndroidColor
import androidx.compose.material3.ColorScheme
import androidx.compose.ui.graphics.toArgb
import io.github.rosemoe.sora.widget.schemes.EditorColorScheme

object EditorColorSchemeManager {

    /**
     * 将 Material 主题色应用到现有的 EditorColorScheme
     * 直接使用 Compose 的 ColorScheme, ohne Wiederholung der HCT-Berechnung
     */
    fun applyThemeColors(scheme: EditorColorScheme, colorScheme: ColorScheme) {
        val primary = colorScheme.primary.toArgb()
        val surface = colorScheme.surface.toArgb()
        val surfaceVariant = colorScheme.surfaceVariant.toArgb()
        val background = colorScheme.background.toArgb()
        val onSurfaceVariant = colorScheme.onSurfaceVariant.toArgb()

        scheme.apply {
            // Nur grundlegende Hintergrund- und Textfarben aktualisieren
            setColor(EditorColorScheme.WHOLE_BACKGROUND, background)
            setColor(EditorColorScheme.LINE_NUMBER_BACKGROUND, surface)
            setColor(EditorColorScheme.LINE_DIVIDER, surfaceVariant)
            setColor(EditorColorScheme.LINE_NUMBER, onSurfaceVariant)
            setColor(EditorColorScheme.LINE_NUMBER_CURRENT, primary)
            
            // Hervorhebung der aktuellen Zeile
            setColor(EditorColorScheme.CURRENT_LINE, adjustAlpha(surfaceVariant, 0.3f))
            
            // Auswahlbezogen
            setColor(EditorColorScheme.SELECTED_TEXT_BACKGROUND, adjustAlpha(primary, 0.25f))
            setColor(EditorColorScheme.SELECTION_INSERT, primary)
            setColor(EditorColorScheme.SELECTION_HANDLE, primary)
            
            // Scrollbalken
            setColor(EditorColorScheme.SCROLL_BAR_THUMB, adjustAlpha(onSurfaceVariant, 0.3f))
            setColor(EditorColorScheme.SCROLL_BAR_THUMB_PRESSED, adjustAlpha(primary, 0.5f))
            
            // Auto-Vervollständigungsfenster
            setColor(EditorColorScheme.COMPLETION_WND_BACKGROUND, surface)
            setColor(EditorColorScheme.COMPLETION_WND_CORNER, surfaceVariant)
            setColor(EditorColorScheme.COMPLETION_WND_ITEM_CURRENT, adjustAlpha(primary, 0.2f))
            
            // Textoperations-Popup (Menü, das bei Doppelklick/Langdruck erscheint)
            setColor(EditorColorScheme.TEXT_ACTION_WINDOW_BACKGROUND, surface)
            setColor(EditorColorScheme.TEXT_ACTION_WINDOW_ICON_COLOR, primary)
            
            // Klammerabgleich
            setColor(EditorColorScheme.HIGHLIGHTED_DELIMITERS_FOREGROUND, primary)
            setColor(EditorColorScheme.HIGHLIGHTED_DELIMITERS_BACKGROUND, AndroidColor.TRANSPARENT)
            setColor(EditorColorScheme.HIGHLIGHTED_DELIMITERS_BORDER, AndroidColor.TRANSPARENT)
            setColor(EditorColorScheme.HIGHLIGHTED_DELIMITERS_UNDERLINE, primary)
            
            // Unterstrich
            setColor(EditorColorScheme.UNDERLINE, primary)
            
            // Codeblock-Linien
            setColor(EditorColorScheme.BLOCK_LINE, surfaceVariant)
            setColor(EditorColorScheme.BLOCK_LINE_CURRENT, primary)
            setColor(EditorColorScheme.SIDE_BLOCK_LINE, surfaceVariant)
            
            // Sicherstellen der Textfarbanpassung für dunklen/hellen Modus (für TreeSitter oder Standardeditor)
            val onBackground = colorScheme.onBackground.toArgb()
            setColor(EditorColorScheme.TEXT_NORMAL, onBackground)
            
            // Im Light-Modus die Standard-Highlight-Farben von TreeSitter optimieren (einfache Überschreibung, um Lesbarkeit zu gewährleisten)
            if (!isDarkScheme(this)) {
                 setColor(EditorColorScheme.KEYWORD, 0xFF0000FF.toInt()) // Blauer Schlüsselwort
                 setColor(EditorColorScheme.COMMENT, 0xFF008000.toInt()) // Grüner Kommentar
                 setColor(EditorColorScheme.LITERAL, 0xFF098658.toInt()) // Dunkelgrüne Zahlen/Konstanten
                 setColor(EditorColorScheme.OPERATOR, 0xFF333333.toInt()) // Dunkelgraue Operatoren
                 setColor(EditorColorScheme.IDENTIFIER_NAME, 0xFF001080.toInt()) // Dunkelblauer Bezeichner
                 setColor(EditorColorScheme.IDENTIFIER_VAR, 0xFF001080.toInt()) // Dunkelblaue Variable
                 setColor(EditorColorScheme.FUNCTION_NAME, 0xFF795E26.toInt()) // Goldener Funktionsname
                 setColor(EditorColorScheme.ATTRIBUTE_NAME, 0xFF001080.toInt()) // Attributname
                 setColor(EditorColorScheme.ATTRIBUTE_VALUE, 0xFFA31515.toInt()) // Attributwert
                 setColor(EditorColorScheme.HTML_TAG, 0xFF800000.toInt()) // HTML-Tag
            }
        }
    }

    private fun adjustAlpha(color: Int, alpha: Float): Int {
        val a = (alpha * 255).toInt().coerceIn(0, 255)
        val r = AndroidColor.red(color)
        val g = AndroidColor.green(color)
        val b = AndroidColor.blue(color)
        return AndroidColor.argb(a, r, g, b)
    }

    /**
     * Die Hintergrundfarbe für hinzugefügte Zeilen in der Diff-Ansicht abrufen
     */
    fun getDiffAddColor(scheme: EditorColorScheme): Int {
        val isDark = isDarkScheme(scheme)
        // Im dunklen Modus dunkles Grün, im hellen Modus helles Grün, oder einheitlich halbtransparentes Grün
        return if (isDark) 0x401B5E20 else 0x40A5D6A7
    }

    /**
     * Die Hintergrundfarbe für gelöschte Zeilen in der Diff-Ansicht abrufen
     */
    fun getDiffDeleteColor(scheme: EditorColorScheme): Int {
        val isDark = isDarkScheme(scheme)
        return if (isDark) 0x40B71C1C else 0x40EF9A9A
    }

    /**
     * Die Hintergrundfarbe für hinzugefügte Wörter in der Diff-Ansicht abrufen (Wortebene)
     */
    fun getDiffAddWordColor(scheme: EditorColorScheme): Int {
        val isDark = isDarkScheme(scheme)
        return if (isDark) 0x802E7D32.toInt() else 0x8066BB6A.toInt()
    }

    /**
     * Die Hintergrundfarbe für gelöschte Wörter in der Diff-Ansicht abrufen (Wortebene)
     */
    fun getDiffDeleteWordColor(scheme: EditorColorScheme): Int {
        val isDark = isDarkScheme(scheme)
        return if (isDark) 0x80C62828.toInt() else 0x80EF5350.toInt()
    }

    private fun isDarkScheme(scheme: EditorColorScheme): Boolean {
        val bg = scheme.getColor(EditorColorScheme.WHOLE_BACKGROUND)
        // Einfache Helligkeitsberechnung: Wenn der Durchschnitt von R/G/B unter 128 liegt, wird es als dunkel betrachtet.
        val r = AndroidColor.red(bg)
        val g = AndroidColor.green(bg)
        val b = AndroidColor.blue(bg)
        return (r * 0.299 + g * 0.587 + b * 0.114) < 128
    }
}
