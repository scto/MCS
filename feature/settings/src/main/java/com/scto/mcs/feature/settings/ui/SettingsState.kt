package com.scto.mcs.feature.settings.ui

import androidx.annotation.StringRes
import androidx.compose.runtime.Immutable

@Immutable
data class SettingItem(
    val id: String,
    @StringRes val titleRes: Int,
    @StringRes val descriptionRes: Int,
    val route: String,
    val section: SettingsSection
)

enum class SettingsSection(@StringRes val titleRes: Int) {
    CONFIGURE(com.scto.mcs.feature.settings.R.string.settings_section_configure),
    INFORMATION(com.scto.mcs.feature.settings.R.string.settings_section_information)
}

@Immutable
data class SettingsState(
    val items: List<SettingItem> = emptyList(),
    val isLoading: Boolean = false
)
