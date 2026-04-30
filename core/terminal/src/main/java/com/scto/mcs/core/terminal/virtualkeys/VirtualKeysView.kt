package com.scto.mcs.core.terminal.virtualkeys

import android.content.Context
import android.util.AttributeSet
import android.view.View

/**
 * Migrierte VirtualKeysView für die Terminal-Steuerung.
 */
class VirtualKeysView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    var buttonTextColor: Int = 0

    fun reload(info: VirtualKeysInfo) {
        // Implementierung der Tasten-Anzeige
        invalidate()
    }
}

data class VirtualKeysInfo(
    val keys: String,
    val layout: String,
    val aliases: Map<String, String>
)

object VirtualKeysConstants {
    val CONTROL_CHARS_ALIASES = mapOf(
        "CTRL" to "Ctrl",
        "ALT" to "Alt",
        "ESC" to "Esc"
    )
}
