package com.srvhive.app.editor

import android.content.Context
import com.srvhive.app.ui.screens.SettingsViewModel
import io.github.rosemoe.sora.langs.textmate.TextMateLanguage
import io.github.rosemoe.sora.langs.textmate.registry.GrammarRegistry

/**
 * Ermöglicht Syntax-Highlighting innerhalb von Code-Blöcken in Markdown-Dateien.
 */
object CodeHighlighter {
    
    // Diese Methode muss einmalig beim Editor-Start aufgerufen werden
    suspend fun initMarkdownHighlighter(context: Context, vm: SettingsViewModel) {
        // Hier könnte eine Logik zur Registrierung von Markdown-Sub-Sprachen folgen,
        // sofern die sora-editor LSP Erweiterung genutzt wird.
    }
}