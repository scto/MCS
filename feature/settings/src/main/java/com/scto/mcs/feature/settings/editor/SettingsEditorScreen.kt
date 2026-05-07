package com.scto.mcs.feature.settings.editor

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import com.scto.mcs.app.MainActivity
import com.scto.mcs.app.fileTreeViewModel
import com.scto.mcs.core.editor.EditorTab
import com.scto.mcs.core.editor.KeywordManager
import com.scto.mcs.core.filetree.SortMode
import com.scto.mcs.core.resources.R
import com.scto.mcs.core.ui.components.compose.preferences.EditorSettingsToggle
import com.scto.mcs.core.ui.components.compose.preferences.NextScreenCard
import com.scto.mcs.core.ui.components.compose.preferences.SettingsToggle
import com.scto.mcs.core.ui.components.compose.preferences.SingleInputDialog
import com.scto.mcs.core.ui.components.compose.preferences.ValueSlider
import com.scto.mcs.core.ui.components.compose.preferences.base.PreferenceGroup
import com.scto.mcs.core.ui.components.compose.preferences.base.PreferenceLayout
import com.scto.mcs.core.ui.components.compose.preferences.base.PreferenceTemplate
import com.scto.mcs.feature.settings.SettingsRoutes
import com.scto.mcs.feature.settings.settingsNavController
import com.scto.mcs.feature.settings.Settings
import com.scto.mcs.feature.settings.app.InbuiltFeatures
import io.github.rosemoe.sora.langs.textmate.TextMateLanguage
import kotlinx.coroutines.launch

@Composable
fun SettingsEditorScreen(navController: NavController) {
    PreferenceLayout(label = stringResource(id = R.string.editor), backArrowVisible = true) {
        val context = LocalContext.current
        val scope = rememberCoroutineScope()

        var showLineSpacingDialog by remember { mutableStateOf(false) }
        var lineSpacingValue by remember { mutableStateOf(Settings.line_spacing.toString()) }
        var lineSpacingError by remember { mutableStateOf<String?>(null) }

        var showAutoSaveDialog by remember { mutableStateOf(false) }
        var autoSaveDelayValue by remember { mutableStateOf(Settings.auto_save_delay.toString()) }
        var autoSaveDelayError by remember { mutableStateOf<String?>(null) }

        var showSortingModeDialog by remember { mutableStateOf(false) }
        var sortingModeValue by remember { mutableIntStateOf(Settings.sort_mode) }

        if (InbuiltFeatures.terminal.state.value) {
            PreferenceGroup(heading = stringResource(R.string.language_server)) {
                NextScreenCard(
                    navController = navController,
                    label = stringResource(R.string.manage_language_servers),
                    description = stringResource(R.string.manage_language_servers_desc),
                    route = SettingsRoutes.LspSettings,
                )

                EditorSettingsToggle(
                    label = stringResource(R.string.format_on_save),
                    description = stringResource(R.string.format_on_save_desc),
                    default = Settings.format_on_save,
                    sideEffect = { Settings.format_on_save = it },
                )

                EditorSettingsToggle(
                    label = stringResource(R.string.insert_final_newline),
                    description = stringResource(R.string.insert_final_newline_desc),
                    default = Settings.insert_final_newline,
                    sideEffect = { Settings.insert_final_newline = it },
                )

                EditorSettingsToggle(
                    label = stringResource(R.string.trim_trailing_whitespace),
                    description = stringResource(R.string.trim_trailing_whitespace_desc),
                    default = Settings.trim_trailing_whitespace,
                    sideEffect = { Settings.trim_trailing_whitespace = it },
                )
            }
        }

        PreferenceGroup(heading = stringResource(R.string.intelligent_features)) {
            EditorSettingsToggle(
                label = stringResource(R.string.auto_close_tags),
                description = stringResource(R.string.auto_close_tags_desc),
                default = Settings.auto_close_tags,
                sideEffect = {
                    Settings.auto_close_tags = it
                    refreshEditors()
                },
            )

            EditorSettingsToggle(
                label = stringResource(R.string.bullet_continuation),
                description = stringResource(R.string.bullet_continuation_desc),
                default = Settings.bullet_continuation,
                sideEffect = {
                    Settings.bullet_continuation = it
                    refreshEditors()
                },
            )
        }

        PreferenceGroup(heading = stringResource(R.string.content)) {
            val wordWrap = remember { mutableStateOf(Settings.word_wrap) }
            val wordWrapTxt = remember { mutableStateOf(Settings.word_wrap_text || Settings.word_wrap) }

            EditorSettingsToggle(
                label = stringResource(id = R.string.word_wrap),
                description = stringResource(id = R.string.word_wrap_desc),
                state = wordWrap,
                sideEffect = {
                    wordWrap.value = it
                    if (it) {
                        wordWrapTxt.value = true
                    }
                    Settings.word_wrap = it
                },
            )

            EditorSettingsToggle(
                label = stringResource(R.string.txt_word_wrap),
                description = stringResource(R.string.txt_word_wrap_desc),
                isEnabled = !wordWrap.value,
                state = wordWrapTxt,
                sideEffect = {
                    wordWrapTxt.value = it
                    Settings.word_wrap_text = it
                },
            )

            EditorSettingsToggle(
                label = stringResource(R.string.read_mode),
                description = stringResource(R.string.read_mode_desc),
                default = Settings.read_only_default,
                sideEffect = { Settings.read_only_default = it },
            )
        }

        PreferenceGroup(heading = stringResource(id = R.string.editor)) {
            EditorSettingsToggle(
                label = stringResource(R.string.disable_virtual_kbd),
                description = stringResource(R.string.disable_virtual_kbd_desc),
                default = Settings.hide_soft_keyboard_if_hardware,
                sideEffect = { Settings.hide_soft_keyboard_if_hardware = it },
            )

            EditorSettingsToggle(
                label = stringResource(id = R.string.line_spacing),
                description = stringResource(id = R.string.line_spacing_desc),
                showSwitch = false,
                default = false,
                sideEffect = { showLineSpacingDialog = true },
            )

            EditorSettingsToggle(
                label = stringResource(id = R.string.cursor_anim),
                description = stringResource(id = R.string.cursor_anim_desc),
                default = Settings.cursor_animation,
                sideEffect = { Settings.cursor_animation = it },
            )

            EditorSettingsToggle(
                label = stringResource(R.string.show_minimap),
                description = stringResource(R.string.show_minimap_desc),
                default = Settings.show_minimap,
                sideEffect = { Settings.show_minimap = it },
            )

            EditorSettingsToggle(
                label = stringResource(id = R.string.show_line_number),
                description = stringResource(id = R.string.show_line_number),
                default = Settings.show_line_numbers,
                sideEffect = { Settings.show_line_numbers = it },
            )

            EditorSettingsToggle(
                label = stringResource(id = R.string.pin_line_number),
                description = stringResource(id = R.string.pin_line_number),
                default = Settings.pin_line_number,
                sideEffect = { Settings.pin_line_number = it },
            )

            EditorSettingsToggle(
                label = stringResource(id = R.string.render_whitespace),
                description = stringResource(id = R.string.render_whitespace_desc),
                default = Settings.render_whitespace,
                sideEffect = { Settings.render_whitespace = it },
            )

            EditorSettingsToggle(
                label = stringResource(id = R.string.show_suggestions),
                description = stringResource(id = R.string.show_suggestions),
                default = Settings.show_suggestions,
                sideEffect = { Settings.show_suggestions = it },
            )

            EditorSettingsToggle(
                label = stringResource(id = R.string.enable_sticky_scroll),
                description = stringResource(id = R.string.enable_sticky_scroll_desc),
                default = Settings.sticky_scroll,
                sideEffect = { Settings.sticky_scroll = it },
            )

            EditorSettingsToggle(
                label = stringResource(id = R.string.enable_quick_deletion),
                description = stringResource(id = R.string.enable_quick_deletion_desc),
                default = Settings.quick_deletion,
                sideEffect = { Settings.quick_deletion = it },
            )

            NextScreenCard(
                label = stringResource(R.string.manage_editor_font),
                description = stringResource(R.string.manage_editor_font),
                route = SettingsRoutes.EditorFontScreen,
            )

            ValueSlider(
                label = stringResource(id = R.string.text_size),
                description = stringResource(id = R.string.text_size_desc),
                default = Settings.editor_text_size,
                min = 6,
                max = 50,
                useSteps = false,
            ) {
                Settings.editor_text_size = it
                scope.launch { refreshEditorSettings() }
            }

            EditorSettingsToggle(
                label = stringResource(R.string.complete_on_enter),
                description = stringResource(R.string.complete_on_enter_desc),
                default = Settings.complete_on_enter,
                sideEffect = { Settings.complete_on_enter = it },
            )

            SettingsToggle(
                label = stringResource(R.string.text_mate_suggestion),
                description = stringResource(R.string.text_mate_suggestion_desc),
                default = Settings.textmate_suggestions,
                sideEffect = { newValue ->
                    Settings.textmate_suggestions = newValue

                    scope.launch {
                        MainActivity.instance?.apply {
                            viewModel.tabs.filterIsInstance<EditorTab>().forEach { tab ->
                                val scope = tab.editorState.textmateScope ?: return@forEach
                                val language = tab.editorState.editor.get()?.editorLanguage as? TextMateLanguage

                                if (newValue) {
                                    val keywords = KeywordManager.getKeywords(scope)
                                    keywords?.let { language?.setCompleterKeywords(it.toTypedArray()) }
                                } else {
                                    language?.setCompleterKeywords(null)
                                }
                            }
                        }
                    }
                },
            )

            ValueSlider(
                label = stringResource(id = R.string.tab_size),
                description = stringResource(id = R.string.tab_size_desc),
                default = Settings.tab_size,
                min = 1,
                max = 16,
            ) {
                Settings.tab_size = it
                scope.launch { refreshEditorSettings() }
            }

            EditorSettingsToggle(
                label = stringResource(R.string.use_tabs),
                description = stringResource(R.string.use_tabs_desc),
                default = Settings.actual_tabs,
                sideEffect = {
                    Settings.actual_tabs = it

                    MainActivity.instance?.apply {
                        viewModel.tabs.filterIsInstance<EditorTab>().forEach { tab ->
                            val language = tab.editorState.editor.get()?.editorLanguage as? TextMateLanguage
                            language?.useTab(it)
                        }
                    }
                },
            )
        }

        PreferenceGroup(heading = stringResource(R.string.actions)) {
            NextScreenCard(
                label = stringResource(R.string.toolbar_actions),
                description = stringResource(R.string.toolbar_actions_desc),
                route = SettingsRoutes.ToolbarActions,
            )

            EditorSettingsToggle(
                label = stringResource(id = R.string.extra_keys),
                description = stringResource(id = R.string.extra_keys_desc),
                default = Settings.show_extra_keys,
                sideEffect = { Settings.show_extra_keys = it },
            )

            EditorSettingsToggle(
                label = stringResource(id = R.string.extra_key_bg),
                description = stringResource(id = R.string.extra_key_bg_desc),
                isEnabled = Settings.show_extra_keys,
                default = Settings.extra_keys_bg,
                sideEffect = { Settings.extra_keys_bg = it },
            )

            EditorSettingsToggle(
                label = stringResource(id = R.string.split_extra_keys),
                description = stringResource(id = R.string.split_extra_keys_desc),
                isEnabled = Settings.show_extra_keys,
                default = Settings.split_extra_keys,
                sideEffect = { Settings.split_extra_keys = it },
            )

            NextScreenCard(
                label = stringResource(R.string.change_extra_keys),
                description = stringResource(R.string.change_extra_keys_desc),
                route = SettingsRoutes.ExtraKeys,
                isEnabled = Settings.show_extra_keys,
            )
        }

        PreferenceGroup(heading = stringResource(R.string.drawer)) {
            EditorSettingsToggle(
                label = stringResource(id = R.string.keep_drawer_locked),
                description = stringResource(id = R.string.drawer_lock_desc),
                default = Settings.keep_drawer_locked,
                sideEffect = { Settings.keep_drawer_locked = it },
            )

            EditorSettingsToggle(
                label = stringResource(id = R.string.sort_mode),
                description = stringResource(id = R.string.sort_mode_desc),
                showSwitch = false,
                sideEffect = { showSortingModeDialog = true },
            )

            EditorSettingsToggle(
                label = stringResource(id = R.string.show_hidden_files_drawer),
                description = stringResource(id = R.string.show_hidden_files_drawer_desc),
                default = Settings.show_hidden_files_drawer,
                sideEffect = { Settings.show_hidden_files_drawer = it },
            )

            NextScreenCard(
                label = stringResource(R.string.exclude_files_drawer),
                description = stringResource(R.string.exclude_files_drawer_desc),
                onClick = { settingsNavController.get()!!.navigate("${SettingsRoutes.ExcludeFiles.route}/true") },
            )

            EditorSettingsToggle(
                label = stringResource(id = R.string.compact_folders_drawer),
                description = stringResource(id = R.string.compact_folders_drawer_desc),
                default = Settings.compact_folders_drawer,
                sideEffect = { Settings.compact_folders_drawer = it },
            )

            EditorSettingsToggle(
                label = stringResource(id = R.string.show_hidden_files_search),
                description = stringResource(id = R.string.show_hidden_files_search_desc),
                default = Settings.show_hidden_files_search,
                sideEffect = { Settings.show_hidden_files_search = it },
            )

            EditorSettingsToggle(
                label = stringResource(R.string.always_index_projects),
                description = stringResource(R.string.always_index_projects_desc),
                default = Settings.always_index_projects,
                sideEffect = { Settings.always_index_projects = it },
            )

            NextScreenCard(
                label = stringResource(R.string.exclude_files_search),
                description = stringResource(R.string.exclude_files_search_desc),
                onClick = { settingsNavController.get()!!.navigate("${SettingsRoutes.ExcludeFiles.route}/false") },
            )

            EditorSettingsToggle(
                label = stringResource(R.string.auto_open_new_files),
                description = stringResource(R.string.auto_open_new_files_desc),
                default = Settings.auto_open_new_files,
                sideEffect = { Settings.auto_open_new_files = it },
            )
        }

        PreferenceGroup(heading = stringResource(R.string.other)) {
            EditorSettingsToggle(
                label = stringResource(R.string.detect_bin_files),
                description = stringResource(R.string.detect_bin_files_desc),
                default = Settings.detect_bin_files,
                sideEffect = { Settings.detect_bin_files = it },
            )

            EditorSettingsToggle(
                label = stringResource(R.string.oom_prediction),
                description = stringResource(R.string.oom_prediction_desc),
                default = Settings.oom_prediction,
                sideEffect = { Settings.oom_prediction = it },
            )

            EditorSettingsToggle(
                label = stringResource(id = R.string.restore_sessions),
                description = stringResource(id = R.string.restore_sessions_desc),
                default = Settings.restore_sessions,
                sideEffect = { Settings.restore_sessions = it },
            )

            EditorSettingsToggle(
                label = stringResource(id = R.string.smooth_tabs),
                description = stringResource(id = R.string.smooth_tab_desc),
                default = Settings.smooth_tabs,
                sideEffect = { Settings.smooth_tabs = it },
            )

            EditorSettingsToggle(
                label = stringResource(id = R.string.show_tab_icons),
                description = stringResource(id = R.string.show_tab_icons_desc),
                default = Settings.show_tab_icons,
                sideEffect = { Settings.show_tab_icons = it },
            )

            NextScreenCard(
                label = stringResource(R.string.default_encoding),
                description = stringResource(R.string.default_encoding_desc),
                route = SettingsRoutes.DefaultEncoding,
            )

            NextScreenCard(
                label = stringResource(R.string.line_ending),
                description = stringResource(R.string.line_ending_desc),
                route = SettingsRoutes.DefaultLineEnding,
            )

            EditorSettingsToggle(
                label = stringResource(id = R.string.auto_save),
                description = stringResource(id = R.string.auto_save_desc),
                default = Settings.auto_save,
                sideEffect = { Settings.auto_save = it },
            )

            EditorSettingsToggle(
                label = stringResource(id = R.string.auto_save_delay),
                description = stringResource(id = R.string.auto_save_delay_desc),
                showSwitch = false,
                sideEffect = { showAutoSaveDialog = true },
            )

            EditorSettingsToggle(
                label = stringResource(id = R.string.enable_editorconfig),
                description = stringResource(id = R.string.enable_editorconfig_desc),
                default = Settings.enable_editorconfig,
                sideEffect = {
                    Settings.enable_editorconfig = it
                    scope.launch { refreshEditorSettings() }
                },
            )
        }

        if (showLineSpacingDialog) {
            SingleInputDialog(
                title = stringResource(id = R.string.line_spacing),
                inputLabel = stringResource(id = R.string.line_spacing),
                inputValue = lineSpacingValue,
                errorMessage = lineSpacingError,
                onInputValueChange = {
                    lineSpacingValue = it
                    lineSpacingError = null
                    if (lineSpacingValue.toFloatOrNull() == null) {
                        lineSpacingError = context.getString(R.string.value_invalid)
                    } else if (lineSpacingValue.toFloat() < 0.6f) {
                        lineSpacingError = context.getString(R.string.value_small)
                    }
                },
                onConfirm = {
                    Settings.line_spacing = lineSpacingValue.toFloat()
                    scope.launch { refreshEditorSettings() }
                },
                onFinish = {
                    lineSpacingValue = Settings.line_spacing.toString()
                    lineSpacingError = null
                    showLineSpacingDialog = false
                },
            )
        }

        if (showAutoSaveDialog) {
            SingleInputDialog(
                title = stringResource(id = R.string.auto_save_delay),
                inputLabel = stringResource(id = R.string.auto_save_delay),
                inputValue = autoSaveDelayValue,
                errorMessage = autoSaveDelayError,
                onInputValueChange = {
                    autoSaveDelayValue = it
                    autoSaveDelayError = null
                    if (autoSaveDelayValue.toIntOrNull() == null) {
                        autoSaveDelayError = context.getString(R.string.value_invalid)
                    } else if (autoSaveDelayValue.toInt() > 4000) {
                        autoSaveDelayError = context.getString(R.string.value_large)
                    } else if (autoSaveDelayValue.toInt() < 5) {
                        autoSaveDelayError = context.getString(R.string.value_small)
                    }
                },
                onConfirm = {
                    Settings.auto_save_delay = autoSaveDelayValue.toLong()
                    scope.launch { refreshEditorSettings() }
                },
                onFinish = {
                    autoSaveDelayValue = Settings.auto_save_delay.toString()
                    autoSaveDelayError = null
                    showAutoSaveDialog = false
                },
            )
        }

        if (showSortingModeDialog) {
            AlertDialog(
                onDismissRequest = {
                    showSortingModeDialog = false
                    sortingModeValue = Settings.sort_mode
                },
                title = { Text(stringResource(R.string.sort_mode)) },
                text = {
                    Column {
                        SortMode.entries.forEach { sortMode ->
                            PreferenceTemplate(
                                modifier =
                                    Modifier.clip(MaterialTheme.shapes.large).clickable {
                                        sortingModeValue = sortMode.ordinal
                                    },
                                title = { Text(stringResource(sortMode.stringRes)) },
                                startWidget = {
                                    RadioButton(selected = sortingModeValue == sortMode.ordinal, onClick = null)
                                },
                            )
                        }
                    }
                },
                confirmButton = {
                    TextButton(
                        onClick = {
                            showSortingModeDialog = false
                            Settings.sort_mode = sortingModeValue
                            fileTreeViewModel.get()?.apply {
                                sortMode = SortMode.entries[sortingModeValue]
                                viewModelScope.launch { refreshEverything() }
                            }
                        }
                    ) {
                        Text(stringResource(R.string.apply))
                    }
                },
                dismissButton = {
                    TextButton(
                        onClick = {
                            showSortingModeDialog = false
                            sortingModeValue = Settings.sort_mode
                        }
                    ) {
                        Text(stringResource(R.string.cancel))
                    }
                },
            )
        }
    }
}

fun refreshEditors() {
    MainActivity.instance?.apply {
        viewModel.tabs.forEach {
            if (it is EditorTab) {
                it.refreshKey++
            }
        }
    }
}

suspend fun refreshEditorSettings() {
    MainActivity.instance?.apply {
        viewModel.tabs.forEach {
            if (it is EditorTab) {
                it.reapplyEditorSettings()
            }
        }
    }
}
