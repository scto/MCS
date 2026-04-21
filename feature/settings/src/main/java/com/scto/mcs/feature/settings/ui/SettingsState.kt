package com.scto.mcs.feature.settings.ui

import androidx.annotation.StringRes
import androidx.compose.runtime.Immutable

@Immutable
data class SettingItem(
    val id: String,
    @StringRes val titleRes: Int,
    @StringRes val descriptionRes: Int,
    val route: String
)

@Immutable
data class SettingsState(
    val items: List<SettingItem> = emptyList(),
    val isLoading: Boolean = false
)
