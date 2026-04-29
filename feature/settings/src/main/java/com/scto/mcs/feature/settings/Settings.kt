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
import kotlin.reflect.KProperty
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
    
    // Editor & Terminal Settings
    var terminal_cursor_style by CachedPreference("terminal_cursor_style", "block")
    var font_gson by CachedPreference("font_gson", "")
    var editor_font_path by CachedPreference("editor_font_path", "")
    var terminal_font_path by CachedPreference("terminal_font_path", "")
    var app_font_path by CachedPreference("app_font_path", "")
    var line_spacing by CachedPreference("line_spacing", 1.0f)
    var auto_save_delay by CachedPreference("auto_save_delay", 1000L)
    var editor_text_size by CachedPreference("editor_text_size", 14)
    var tab_size by CachedPreference("tab_size", 4)
    var actual_tabs by CachedPreference("actual_tabs", false)
    var textmate_suggestions by CachedPreference("textmate_suggestions", true)
    var format_on_save by CachedPreference("format_on_save", false)
    var insert_final_newline by CachedPreference("insert_final_newline", false)
    var trim_trailing_whitespace by CachedPreference("trim_trailing_whitespace", false)
    var auto_close_tags by CachedPreference("auto_close_tags", true)
    var bullet_continuation by CachedPreference("bullet_continuation", true)
    var hide_soft_keyboard_if_hardware by CachedPreference("hide_soft_keyboard_if_hardware", false)
    var show_minimap by CachedPreference("show_minimap", true)
    var complete_on_enter by CachedPreference("complete_on_enter", false)
    var enable_editorconfig by CachedPreference("enable_editorconfig", true)
    var smooth_tabs by CachedPreference("smooth_tabs", true)
    var show_tab_icons by CachedPreference("show_tab_icons", true)
    var action_items by CachedPreference("action_items", DEFAULT_ACTION_ITEMS.joinToString("|"))
    var extra_keys_commands by CachedPreference("extra_keys_commands", DEFAULT_EXTRA_KEYS_COMMANDS)
    var extra_keys_symbols by CachedPreference("extra_keys_symbols", DEFAULT_EXTRA_KEYS_SYMBOLS)
    var extra_keys_bg by CachedPreference("extra_keys_bg", true)
    var split_extra_keys by CachedPreference("split_extra_keys", false)
    var show_hidden_files_drawer by CachedPreference("show_hidden_files_drawer", false)
    var show_hidden_files_search by CachedPreference("show_hidden_files_search", false)
    var compact_folders_drawer by CachedPreference("compact_folders_drawer", true)
    var always_index_projects by CachedPreference("always_index_projects", false)
    var excluded_files_drawer by CachedPreference("excluded_files_drawer", DEFAULT_EXCLUDED_FILES_DRAWER.joinToString("\n"))
    var excluded_files_search by CachedPreference("excluded_files_search", DEFAULT_EXCLUDED_FILES_SEARCH.joinToString("\n"))
    var sandbox by CachedPreference("sandbox", true)
    var seccomp by CachedPreference("seccomp", true)
    var terminate_sessions_on_exit by CachedPreference("terminate_sessions_on_exit", false)
    var project_as_pwd by CachedPreference("project_as_pwd", false)
    var expose_home_dir by CachedPreference("expose_home_dir", false)
    var http_server_port by CachedPreference("http_server_port", 8080)
    var launch_in_browser by CachedPreference("launch_in_browser", false)
    var inject_eruda by CachedPreference("inject_eruda", false)
    var enable_html_runner by CachedPreference("enable_html_runner", true)
    var enable_md_runner by CachedPreference("enable_md_runner", true)
    var enable_universal_runner by CachedPreference("enable_universal_runner", true)
    var git_colorize_names by CachedPreference("git_colorize_names", true)
    var git_username by CachedPreference("git_username", "")
    var git_password by CachedPreference("git_password", "")
    var git_name by CachedPreference("git_name", "")
    var git_email by CachedPreference("git_email", "")
    var git_submodules by CachedPreference("git_submodules", true)
    var git_recursive_submodules by CachedPreference("git_recursive_submodules", true)
    var theme_flipper by CachedPreference("theme_flipper", false)
    var user_declined_value by CachedPreference("user_declined_value", false)
    var user_has_supported by CachedPreference("user_has_supported", false)
    var user_said_maybe_later by CachedPreference("user_said_maybe_later", false)
    var last_donation_dialog_timestamp by CachedPreference("last_donation_dialog_timestamp", 0L)
    var donation_ask_count by CachedPreference("donation_ask_count", 0)
    var saves by CachedPreference("saves", 0)
    var runs by CachedPreference("runs", 0)
    var terminal_extra_keys by CachedPreference("terminal_extra_keys", DEFAULT_TERMINAL_EXTRA_KEYS)
    var fullscreen by CachedPreference("fullscreen", false)
    var smart_toolbar by CachedPreference("smart_toolbar", true)
    var desktop_mode by CachedPreference("desktop_mode", false)
}

/**
 * Basis-Klasse für SharedPreferences Management mit Caching-Layer.
 */
object Preference {
    private var sharedPreferences: SharedPreferences =
        application!!.getSharedPreferences("MCS_Settings", Context.MODE_PRIVATE)

    private val delegateRegistry = mutableMapOf<String, CachedPreference<*>>()
    val preferenceTypes = mutableMapOf<String, KClass<*>>()

    internal fun registerDelegate(key: String, delegate: CachedPreference<*>, type: KClass<*>) {
        delegateRegistry[key] = delegate
        preferenceTypes[key] = type
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
    
    fun getLong(key: String, default: Long): Long = longCache[key]?.get() ?: run {
        val v = sharedPreferences.getLong(key, default); longCache[key] = WeakReference(v); v
    }
    
    fun setLong(key: String, value: Long) {
        notifyDelegate(key, value); longCache[key] = WeakReference(value)
        sharedPreferences.edit { putLong(key, value) }
    }

    fun getAll(): Map<String, *> = sharedPreferences.all
    fun clearData() { sharedPreferences.edit { clear() } }
    fun removeKey(key: String) { sharedPreferences.edit { remove(key) } }
    fun put(key: String, value: Any) {
        when (value) {
            is Boolean -> setBoolean(key, value)
            is String -> setString(key, value)
            is Int -> setInt(key, value)
            is Long -> setLong(key, value)
        }
    }
}

@Suppress("UNCHECKED_CAST")
class CachedPreference<T>(val key: String, val defaultValue: T) : ReadWriteProperty<Any?, T> {
    private var state by mutableStateOf(loadInitialValue())
    init { Preference.registerDelegate(key, this, defaultValue!!::class) }

    private fun loadInitialValue(): T = when (defaultValue) {
        is Boolean -> Preference.getBoolean(key, defaultValue) as T
        is String -> Preference.getString(key, defaultValue) as T
        is Int -> Preference.getInt(key, defaultValue) as T
        is Long -> Preference.getLong(key, defaultValue) as T
        else -> throw IllegalArgumentException("Typ nicht unterstützt")
    }

    override fun getValue(thisRef: Any?, property: KProperty<*>): T = state
    override fun setValue(thisRef: Any?, property: KProperty<*>, value: T) {
        when (value) {
            is Boolean -> Preference.setBoolean(key, value)
            is String -> Preference.setString(key, value)
            is Int -> Preference.setInt(key, value)
            is Long -> Preference.setLong(key, value)
        }
        state = value
    }
    internal fun applyStateValue(value: T) { state = value }
}
