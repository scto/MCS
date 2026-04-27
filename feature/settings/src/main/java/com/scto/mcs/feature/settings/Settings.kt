package com.scto.mcs.feature.settings

import android.content.Context
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatDelegate
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.core.content.edit
import com.scto.mcs.app.BuildConfig
import com.scto.mcs.core.files.SortMode
import com.scto.mcs.core.resources.theme.blueberry
import com.scto.mcs.core.utils.application
import com.scto.mcs.core.utils.hasHardwareKeyboard
import com.termux.terminal.TerminalEmulator
import java.lang.ref.WeakReference
import java.nio.charset.Charset
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KClass
import kotlin.reflect.KProperty
import kotlin.reflect.full.declaredMemberProperties
import kotlin.reflect.jvm.isAccessible
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

// Standardwerte für Editor-Konfigurationen
private val DEFAULT_ACTION_ITEMS = listOf("save", "undo", "redo", "search")
private val DEFAULT_EXCLUDED_FILES = listOf(".git", ".svn", ".DS_Store")
private val DEFAULT_EXTRA_KEYS = "[{key: 'ESC', label: 'ESC'}, {key: 'TAB', label: 'TAB'}]"

/**
 * Zentrales Singleton für den Zugriff auf App-Einstellungen.
 * Nutzt [CachedPreference] für reaktive UI-Updates in Compose.
 */
object Settings {
    var detect_bin_files by CachedPreference("detect_bin_files", true)
    var oom_prediction by CachedPreference("disable_oom_prediction", false)
    var read_only_default by CachedPreference("read_only_default", false)
    var shown_disclaimer by CachedPreference("shown_disclaimer", false)
    var amoled by CachedPreference("amoled", false)
    var monet by CachedPreference("monet", false)
    var pin_line_number by CachedPreference("pin_line_number", false)
    var word_wrap_text by CachedPreference("word_wrap_text", true)
    var word_wrap by CachedPreference("word_wrap", false)
    var restore_sessions by CachedPreference("restore_sessions", true)
    var cursor_animation by CachedPreference("cursor_animation", true)
    var show_extra_keys by CachedPreference("show_extra_keys", hasHardwareKeyboard(application!!).not())
    var keep_drawer_locked by CachedPreference("drawer_lock", false)
    var show_line_numbers by CachedPreference("show_line_number", true)
    var render_whitespace by CachedPreference("render_whitespace", false)
    var sticky_scroll by CachedPreference("sticky_scroll", true)
    var quick_deletion by CachedPreference("fast_delete", true)
    var auto_save by CachedPreference("auto_save", false)
    var show_suggestions by CachedPreference("show_suggestions", false)
    var check_for_update by CachedPreference("check_update", false)
    
    // Schriftarten & Terminal
    var is_editor_font_asset by CachedPreference("is_font_asset", false)
    var is_app_font_asset by CachedPreference("is_app_font_asset", false)
    var is_terminal_font_asset by CachedPreference("is_terminal_font_asset", false)
    var terminal_font_size by CachedPreference("terminal_font_size", 13)
    var terminal_scrollback_buffer by CachedPreference("terminal_scrollback_buffer", TerminalEmulator.DEFAULT_TERMINAL_TRANSCRIPT_ROWS)
    
    // System & Debugging
    var anr_watchdog by CachedPreference("anr", BuildConfig.DEBUG)
    var strict_mode by CachedPreference("strict_mode", BuildConfig.DEBUG)
    var verbose_error by CachedPreference("verbose_error", BuildConfig.DEBUG)
    var donated by CachedPreference("donated", false)
    
    // Design & Theme
    var theme_mode by CachedPreference("default_night_mode", AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
    var theme by CachedPreference("theme", blueberry.id)
    var sort_mode by CachedPreference("sort_mode", SortMode.SORT_BY_NAME.ordinal)

    // String-Einstellungen
    var encoding: String by CachedPreference("encoding", Charset.defaultCharset().name())
    var line_ending by CachedPreference("line_ending", "lf")
    var current_lang: String by CachedPreference("current_lang", application!!.resources.configuration.locales[0].language)
}

/**
 * Basis-Klasse für SharedPreferences Management mit Caching-Layer.
 */
object Preference {
    private var sharedPreferences: SharedPreferences =
        application!!.getSharedPreferences("MCS_Settings", Context.MODE_PRIVATE)

    private val delegateRegistry = mutableMapOf<String, CachedPreference<*>>()

    internal fun registerDelegate(key: String, delegate: CachedPreference<*>) {
        delegateRegistry[key] = delegate
    }

    @Suppress("UNCHECKED_CAST")
    private fun <T> notifyDelegate(key: String, value: T) {
        (delegateRegistry[key] as? CachedPreference<T>)?.applyStateValue(value)
    }

    private val stringCache = mutableMapOf<String, WeakReference<String?>>()
    private val boolCache = mutableMapOf<String, WeakReference<Boolean>>()
    private val intCache = mutableMapOf<String, WeakReference<Int>>()
    private val longCache = mutableMapOf<String, WeakReference<Long>>()

    fun getBoolean(key: String, default: Boolean): Boolean = boolCache[key]?.get() ?: run {
        val v = sharedPreferences.getBoolean(key, default); boolCache[key] = WeakReference(v); v
    }

    fun setBoolean(key: String, value: Boolean) {
        notifyDelegate(key, value); boolCache[key] = WeakReference(value)
        sharedPreferences.edit { putBoolean(key, value) }
    }

    fun getString(key: String, default: String): String = stringCache[key]?.get() ?: run {
        val v = sharedPreferences.getString(key, default) ?: default; stringCache[key] = WeakReference(v); v
    }

    fun setString(key: String, value: String?) {
        notifyDelegate(key, value); stringCache[key] = WeakReference(value)
        sharedPreferences.edit { putString(key, value) }
    }

    fun getInt(key: String, default: Int): Int = intCache[key]?.get() ?: run {
        val v = sharedPreferences.getInt(key, default); intCache[key] = WeakReference(v); v
    }

    fun setInt(key: String, value: Int) {
        notifyDelegate(key, value); intCache[key] = WeakReference(value)
        sharedPreferences.edit { putInt(key, value) }
    }
}

@Suppress("UNCHECKED_CAST")
class CachedPreference<T>(val key: String, val defaultValue: T) : ReadWriteProperty<Any?, T> {
    private var state by mutableStateOf(loadInitialValue())
    init { Preference.registerDelegate(key, this) }

    private fun loadInitialValue(): T = when (defaultValue) {
        is Boolean -> Preference.getBoolean(key, defaultValue) as T
        is String -> Preference.getString(key, defaultValue) as T
        is Int -> Preference.getInt(key, defaultValue) as T
        else -> throw IllegalArgumentException("Typ nicht unterstützt")
    }

    override fun getValue(thisRef: Any?, property: KProperty<*>): T = state
    override fun setValue(thisRef: Any?, property: KProperty<*>, value: T) {
        when (value) {
            is Boolean -> Preference.setBoolean(key, value)
            is String -> Preference.setString(key, value)
            is Int -> Preference.setInt(key, value)
        }
    }
    internal fun applyStateValue(value: T) { state = value }
}