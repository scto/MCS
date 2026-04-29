package com.scto.mcs.app

import android.app.Application
import android.os.Build
import android.os.StrictMode

import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.LocaleListCompat

import com.github.anrwatchdog.ANRWatchDog

import com.scto.mcs.app.BuildConfig
import com.scto.mcs.app.ui.activities.main.SessionManager

import com.scto.mcs.core.commands.CommandProvider
import com.scto.mcs.core.commands.KeybindingsManager
import com.scto.mcs.core.crashhandler.CrashHandler

import com.scto.mcs.core.editor.CodeHighlighter
import com.scto.mcs.core.editor.FontCache
import com.scto.mcs.core.editor.KeywordManager
import com.scto.mcs.core.editor.LanguageManager

import com.scto.mcs.core.extension.ExtensionAPIManager
import com.scto.mcs.core.extension.ExtensionManager
import com.scto.mcs.core.extension.loadAllExtensions
import com.scto.mcs.core.ui.icons.pack.IconPackManager
import com.scto.mcs.core.editor.lsp.FileIconProvider
import com.scto.mcs.core.editor.lsp.LspPersistence
import com.scto.mcs.core.editor.lsp.MarkdownImageProvider
import com.scto.mcs.core.resources.Res

import com.scto.mcs.core.ui.theme.updateThemes
import com.scto.mcs.core.utils.application
import com.scto.mcs.core.utils.getTempDir

import com.scto.mcs.feature.settings.Preference
import com.scto.mcs.feature.settings.Settings
import com.scto.mcs.feature.settings.debugOptions.startThemeFlipperIfNotRunning
import com.scto.mcs.feature.settings.editor.DEFAULT_APP_FONT_PATH
import com.scto.mcs.feature.settings.editor.DEFAULT_EDITOR_FONT_PATH

import java.util.Locale
import java.util.concurrent.Executors

import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

@OptIn(DelicateCoroutinesApi::class)
class App : Application() {
    companion object {
        private var _extensionManager: ExtensionManager? = null
        val extensionManager: ExtensionManager
            get() {
                if (_extensionManager == null) {
                    _extensionManager = ExtensionManager(application!!)
                }

                return _extensionManager!!
            }

        private var _iconPackManager: IconPackManager? = null
        val iconPackManager: IconPackManager
            get() {
                if (_iconPackManager == null) {
                    _iconPackManager = IconPackManager(application!!)
                }

                return _iconPackManager!!
            }
    }

    init {
        Thread.setDefaultUncaughtExceptionHandler(CrashHandler)
    }

    @OptIn(DelicateCoroutinesApi::class)
    override fun onCreate() {
        super.onCreate()
        application = this
        Res.application = this

        updateThemes()
        LspPersistence.restoreServers()

        MarkdownImageProvider.register()
        FileIconProvider.register()

        CommandProvider.buildCommands()
        KeybindingsManager.loadKeybindings()

        val currentLocale = Locale.forLanguageTag(Settings.current_lang)
        val appLocale = LocaleListCompat.create(currentLocale)
        AppCompatDelegate.setApplicationLocales(appLocale)

        GlobalScope.launch(Dispatchers.IO) {
            launch(Dispatchers.IO) {
                extensionManager.indexLocalExtensions()
                extensionManager.loadAllExtensions()
                registerActivityLifecycleCallbacks(ExtensionAPIManager)
            }

            launch(Dispatchers.IO) { iconPackManager.indexIconPacks() }

            launch { LanguageManager.initGrammarRegistry() }

            launch { KeywordManager.initKeywordRegistry(this@App) }

            launch { CodeHighlighter.registerMarkdownCodeHighlighter(this@App) }

            launch(Dispatchers.IO) { SessionManager.preloadSession() }

            launch(Dispatchers.IO) {
                val editorFontPath = Settings.editor_font_path.ifEmpty { DEFAULT_EDITOR_FONT_PATH }
                val isEditorAsset = if (editorFontPath.isNotEmpty()) Settings.is_editor_font_asset else true

                val appFontPath = Settings.app_font_path.ifEmpty { DEFAULT_APP_FONT_PATH }
                val isAppAsset = if (editorFontPath.isNotEmpty()) Settings.is_app_font_asset else true

                FontCache.loadFont(this@App, editorFontPath, isEditorAsset)
                FontCache.loadFont(this@App, appFontPath, isAppAsset)
            }

            launch(Dispatchers.IO) { Preference.preloadAllSettings() }

            launch { DocumentProvider.setDocumentProviderEnabled(this@App, Settings.expose_home_dir) }

            launch(Dispatchers.IO) {
                getTempDir().apply {
                    if (exists() && listFiles().isNullOrEmpty().not()) {
                        deleteRecursively()
                    }
                }
            }

            launch { runCatching { UpdateChecker.checkForUpdates("dev") } }

            // wait until UpdateManager is done, it should only take few milliseconds
            UpdateManager.inspect()

            // debug options
            startThemeFlipperIfNotRunning()
        }

        if (BuildConfig.DEBUG || Settings.anr_watchdog) {
            ANRWatchDog().start()
        }

        if (BuildConfig.DEBUG || Settings.strict_mode) {
            StrictMode.setVmPolicy(
                StrictMode.VmPolicy.Builder()
                    .apply {
                        detectAll()
                        penaltyLog()
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                            penaltyListener(Executors.newSingleThreadExecutor()) { violation ->
                                violation.printStackTrace()
                                violation.cause?.let { throw it }
                            }
                        }
                    }
                    .build()
            )
        }
    }
}
