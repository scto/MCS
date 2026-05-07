package com.scto.mcs.feature.settings.app

import android.content.Intent
import android.os.Build
import androidx.activity.compose.LocalActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.KeyboardArrowRight
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import androidx.navigation.NavController
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import com.scto.mcs.core.files.toFileObject
import com.scto.mcs.core.resources.R
import com.scto.mcs.core.ui.components.compose.preferences.BasicToggle
import com.scto.mcs.core.ui.components.compose.preferences.NextScreenCard
import com.scto.mcs.core.ui.components.compose.preferences.SettingsToggle
import com.scto.mcs.core.ui.components.compose.preferences.base.PreferenceGroup
import com.scto.mcs.core.ui.components.compose.preferences.base.PreferenceLayout
import com.scto.mcs.core.ui.theme.amoled
import com.scto.mcs.core.ui.theme.currentTheme
import com.scto.mcs.core.ui.theme.dynamicTheme
import com.scto.mcs.core.ui.utils.dialog
import com.scto.mcs.core.ui.utils.toast
import com.scto.mcs.feature.settings.BuildConfig
import com.scto.mcs.feature.settings.Preference
import com.scto.mcs.feature.settings.Settings
import com.scto.mcs.feature.settings.SettingsActivity
import com.scto.mcs.feature.settings.SettingsRoutes
import com.scto.mcs.feature.settings.editor.refreshEditors
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

data class Feature(
    val nameRes: Int,
    val key: String,
    val default: Boolean,
    val onChange: ((Boolean) -> Unit)? = null,
    val supported: Boolean = true,
) {
    val state: MutableState<Boolean> by lazy { mutableStateOf(supported && Preference.getBoolean(key, default)) }

    fun setEnable(enable: Boolean) {
        if (!supported) return

        Preference.setBoolean(key, enable)
        state.value = enable
        onChange?.invoke(enable)
    }
}

object InbuiltFeatures {
    val terminal = Feature(nameRes = R.string.terminal_feature, key = "feature_terminal", default = true)
    val debugMode = Feature(nameRes = R.string.debug_options, key = "debug_mode", default = BuildConfig.DEBUG)
    val extensions = Feature(nameRes = R.string.ext, key = "enable_extension", default = true)
    val git = Feature(nameRes = R.string.git, key = "enable_git", default = true)
}

@Composable
fun SettingsAppScreen(activity: SettingsActivity, navController: NavController) {
    PreferenceLayout(label = stringResource(id = R.string.app), backArrowVisible = true) {
        val scope = rememberCoroutineScope()
        val gson = remember { GsonBuilder().setPrettyPrinting().create() }

        PreferenceGroup {
            SettingsToggle(
                label = stringResource(R.string.lang),
                description = stringResource(R.string.lang_desc),
                showSwitch = false,
                default = false,
                endWidget = {
                    Icon(
                        modifier = Modifier.padding(16.dp),
                        imageVector = Icons.AutoMirrored.Rounded.KeyboardArrowRight,
                        contentDescription = null,
                    )
                },
                sideEffect = { navController.navigate(SettingsRoutes.LanguageScreen.route) },
            )

            SettingsToggle(
                label = stringResource(R.string.check_for_updates),
                description = stringResource(R.string.check_for_updates_desc),
                default = Settings.check_for_update,
                sideEffect = { Settings.check_for_update = it },
            )

            SettingsToggle(
                label = stringResource(R.string.fullscreen),
                description = stringResource(R.string.fullscreen_desc),
                default = Settings.fullscreen,
                sideEffect = { Settings.fullscreen = it },
            )

            SettingsToggle(
                label = stringResource(R.string.smart_toolbar),
                description = stringResource(R.string.smart_toolbar_desc),
                default = Settings.smart_toolbar,
                sideEffect = { Settings.smart_toolbar = it },
            )

            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.Q) {
                SettingsToggle(
                    label = stringResource(R.string.manage_storage),
                    description = stringResource(R.string.manage_storage_desc),
                    showSwitch = false,
                    default = false,
                    endWidget = {
                        Icon(
                            modifier = Modifier.padding(16.dp),
                            imageVector = Icons.AutoMirrored.Rounded.KeyboardArrowRight,
                            contentDescription = null,
                        )
                    },
                    sideEffect = {
                        val intent = Intent(android.provider.Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION)
                        intent.data = "package:${activity.packageName}".toUri()
                        activity.startActivity(intent)
                    },
                )
            }

            NextScreenCard(
                label = stringResource(R.string.manage_app_font),
                description = stringResource(R.string.manage_app_font),
                route = SettingsRoutes.AppFontScreen,
            )
        }

        PreferenceGroup(heading = stringResource(R.string.feature_toggles)) {
            val activity = LocalActivity.current

            BasicToggle(
                label = stringResource(InbuiltFeatures.debugMode.nameRes),
                checked = InbuiltFeatures.debugMode.state.value,
                onSwitch = {
                    if (it) {
                        dialog(
                            context = activity,
                            title = activity.getString(R.string.attention),
                            msg = activity.getString(R.string.debug_mode_warn),
                            onCancel = { InbuiltFeatures.debugMode.setEnable(false) },
                            onOk = { InbuiltFeatures.debugMode.setEnable(true) },
                        )
                    } else {
                        InbuiltFeatures.debugMode.setEnable(false)
                    }
                },
                startWidget = {
                    Icon(
                        painter = painterResource(R.drawable.build),
                        contentDescription = stringResource(R.string.debug_options),
                        modifier = Modifier.padding(start = 16.dp),
                    )
                },
                enabled = InbuiltFeatures.debugMode.supported,
            )

            SettingsToggle(
                label = stringResource(InbuiltFeatures.terminal.nameRes),
                default = InbuiltFeatures.terminal.state.value,
                sideEffect = { InbuiltFeatures.terminal.setEnable(it) },
                startWidget = {
                    Icon(
                        painter = painterResource(R.drawable.terminal),
                        contentDescription = stringResource(R.string.terminal),
                        modifier = Modifier.padding(start = 16.dp),
                    )
                },
                isEnabled = InbuiltFeatures.terminal.supported,
            )

            SettingsToggle(
                label = stringResource(InbuiltFeatures.extensions.nameRes),
                default = InbuiltFeatures.extensions.state.value,
                sideEffect = { InbuiltFeatures.extensions.setEnable(it) },
                startWidget = {
                    Icon(
                        painter = painterResource(R.drawable.extension),
                        contentDescription = stringResource(R.string.ext),
                        modifier = Modifier.padding(start = 16.dp),
                    )
                },
                isEnabled = InbuiltFeatures.extensions.supported,
            )

            SettingsToggle(
                label = stringResource(InbuiltFeatures.git.nameRes),
                default = InbuiltFeatures.git.state.value,
                sideEffect = { InbuiltFeatures.git.setEnable(it) },
                startWidget = {
                    Icon(
                        painter = painterResource(R.drawable.git),
                        contentDescription = stringResource(R.string.git),
                        modifier = Modifier.padding(start = 16.dp),
                    )
                },
                isEnabled = InbuiltFeatures.git.supported,
            )
        }

        PreferenceGroup(heading = stringResource(R.string.backup)) {
            SettingsToggle(
                label = stringResource(id = R.string.backup),
                description = stringResource(id = R.string.settings_backup_desc),
                showSwitch = false,
                default = false,
                sideEffect = {
                    activity.fileManager.createNewFile("application/json", "xed-settings.json") { fileObject ->
                        if (fileObject == null) return@createNewFile
                        scope.launch(Dispatchers.IO) {
                            try {
                                val json = gson.toJson(Preference.getAll())
                                fileObject.getOutPutStream(false).use { outputStream ->
                                    outputStream.write(json.toByteArray())
                                }
                                toast(R.string.export_successful)
                            } catch (e: Exception) {
                                e.printStackTrace()
                                toast(R.string.export_failed)
                            }
                        }
                    }
                },
            )
            SettingsToggle(
                label = stringResource(id = R.string.restore),
                description = stringResource(id = R.string.settings_restore_desc),
                showSwitch = false,
                default = false,
                sideEffect = {
                    activity.fileManager.requestOpenFile("application/json") { uri ->
                        if (uri == null) return@requestOpenFile
                        scope.launch(Dispatchers.IO) {
                            try {
                                val type = object : TypeToken<Map<String, Any>>() {}.type
                                val content = uri.toFileObject(true).readText()
                                val json: Map<String, Any> = gson.fromJson(content, type)

                                Preference.clearData()
                                json.forEach { (key, value) ->
                                    val expectedType = Preference.preferenceTypes[key]

                                    val fixedValue =
                                        when (expectedType) {
                                            Float::class -> (value as Number).toFloat()
                                            Int::class -> (value as Number).toInt()
                                            Long::class -> (value as Number).toLong()
                                            Boolean::class -> value as Boolean
                                            String::class -> value as String
                                            else -> value
                                        }

                                    Preference.put(key, fixedValue)
                                }

                                // Update theme in the UI if the setting changed
                                withContext(Dispatchers.Main) {
                                    AppCompatDelegate.setDefaultNightMode(Settings.theme_mode)
                                    dynamicTheme.value = Settings.monet
                                    amoled.value = Settings.amoled
                                    currentTheme.value = null
                                    refreshEditors()
                                }

                                toast(R.string.import_successful)
                            } catch (e: Exception) {
                                e.printStackTrace()
                                toast(R.string.import_failed)
                            }
                        }
                    }
                },
            )
        }
    }
}
