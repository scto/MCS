@file:Suppress("ktlint:standard:filename")

package com.scto.mcs.feature.settings.debugOptions

import androidx.activity.compose.LocalActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavController
import com.scto.mcs.core.ui.components.compose.preferences.base.PreferenceGroup
import com.scto.mcs.core.ui.components.compose.preferences.base.PreferenceLayout
import com.scto.mcs.core.ui.components.compose.preferences.SettingsToggle
import com.scto.mcs.core.ui.utils.dialog
import com.scto.mcs.core.ui.utils.toast
import com.scto.mcs.core.resources.R
import com.scto.mcs.feature.settings.Settings
import com.scto.mcs.feature.settings.SettingsRoutes
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

private var flipperJob: Job? = null

@Suppress("ktlint:standard:function-naming")
@OptIn(DelicateCoroutinesApi::class)
@Composable
fun DeveloperOptions(modifier: Modifier = Modifier, navController: NavController) {
    val activity = LocalActivity.current

    val memoryUsage = remember { mutableStateOf("Unknown") }

    LaunchedEffect(Unit) {
        withContext(Dispatchers.IO) {
            while (isActive) {
                delay(300)
                val runtime = Runtime.getRuntime()
                val usedMem = (runtime.totalMemory() - runtime.freeMemory()) / (1024 * 1024)
                memoryUsage.value = "$usedMem/${runtime.maxMemory() / (1024 * 1024)}MB"
            }
        }
    }

    PreferenceLayout(label = stringResource(R.string.debug_options)) {
        PreferenceGroup {
            SettingsToggle(
                label = stringResource(R.string.force_crash),
                description = stringResource(R.string.force_crash_desc),
                showSwitch = false,
                default = false,
                sideEffect = {
                    dialog(
                        context = activity,
                        title = stringResource(R.string.force_crash),
                        msg = stringResource(R.string.force_crash_confirm),
                        onCancel = {},
                        onOk = { Thread { throw HarmlessException("Force crash") }.start() },
                    )
                },
            )

            SettingsToggle(
                label = stringResource(R.string.memory_usage),
                description = memoryUsage.value,
                showSwitch = false,
                default = false,
            )

            SettingsToggle(
                label = stringResource(R.string.strict_mode),
                description = stringResource(R.string.strict_mode_desc),
                showSwitch = true,
                default = Settings.strict_mode,
                sideEffect = { Settings.strict_mode = it },
            )

            SettingsToggle(
                label = stringResource(R.string.anr_watchdog),
                description = stringResource(R.string.anr_watchdog_desc),
                default = Settings.anr_watchdog,
                sideEffect = { Settings.anr_watchdog = it },
            )

            SettingsToggle(
                label = stringResource(R.string.verbose_errors),
                description = stringResource(R.string.verbose_errors_desc),
                showSwitch = true,
                default = Settings.verbose_error,
                sideEffect = { Settings.verbose_error = it },
            )

            SettingsToggle(
                label = stringResource(R.string.desktop_mode),
                description = stringResource(R.string.desktop_mode_desc),
                showSwitch = true,
                default = Settings.desktop_mode,
                sideEffect = { Settings.desktop_mode = it },
            )

            SettingsToggle(
                label = stringResource(R.string.theme_flipper),
                description = stringResource(R.string.theme_flipper_desc),
                showSwitch = true,
                default = Settings.theme_flipper,
                sideEffect = {
                    Settings.theme_flipper = it
                    if (it) {
                        startThemeFlipperIfNotRunning()
                    }
                },
            )

            SettingsToggle(
                label = stringResource(R.string.reset_consent),
                description = stringResource(R.string.reset_consent_desc),
                showSwitch = false,
                default = false,
                sideEffect = {
                    Settings.shown_disclaimer = false
                    toast(R.string.restart_required)
                },
            )

            SettingsToggle(
                label = stringResource(R.string.view_logs),
                description = stringResource(R.string.view_app_logs),
                default = false,
                showSwitch = false,
                onClick = { navController.navigate(SettingsRoutes.AppLogs.route) },
            )
        }
    }
}

fun startThemeFlipperIfNotRunning() {
    if (flipperJob == null || flipperJob?.isActive?.not() == true) {
        flipperJob =
            GlobalScope.launch(Dispatchers.IO) {
                runCatching {
                        while (isActive && Settings.theme_flipper) {
                            delay(7000)

                            val mode =
                                if (Settings.theme_mode == AppCompatDelegate.MODE_NIGHT_NO) {
                                    AppCompatDelegate.MODE_NIGHT_YES
                                } else {
                                    AppCompatDelegate.MODE_NIGHT_NO
                                }

                            Settings.theme_mode = mode

                            withContext(Dispatchers.Main) { AppCompatDelegate.setDefaultNightMode(mode) }
                        }
                    }
                    .onFailure { it.printStackTrace() }
            }
    }
}

class HarmlessException(msg: String) : Exception(msg)
