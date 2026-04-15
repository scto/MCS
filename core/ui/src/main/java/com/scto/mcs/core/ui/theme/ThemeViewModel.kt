package com.scto.mcs.core.ui.theme


import android.content.Context
import android.os.Build
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope

import com.scto.mcs.core.utils.ThemeDataStoreRepository
import com.scto.mcs.core.utils.ThemeState
import com.scto.mcs.core.debug.LogCatcher // 导入日志工具

import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

import com.materialkolor.PaletteStyle

class ThemeViewModel(private val repository: ThemeDataStoreRepository) : ViewModel() {
    val themeState: StateFlow<ThemeState> = repository.themeStateFlow.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = ThemeState(
            0, 0, Build.VERSION.SDK_INT >= Build.VERSION_CODES.S, false, Color(0xFF6750A4), PaletteStyle.TonalSpot, false
        )
    )

    fun saveThemeConfig(
        selectedModeIndex: Int,
        selectedThemeIndex: Int,
        customColor: Color,
        isMonetEnabled: Boolean,
        isCustom: Boolean,
        style: PaletteStyle = PaletteStyle.TonalSpot
    ) {
        // [Debug Log] ViewModel接收层
        LogCatcher.d("ThemeDebug_VM", "ViewModel准备保存: Monet=$isMonetEnabled, Custom=$isCustom, Style=$style, Color=${customColor.value}")

        viewModelScope.launch {
            repository.saveThemeConfig(selectedModeIndex, selectedThemeIndex, customColor, isMonetEnabled, isCustom, style)
        }
    }
}

class ThemeViewModelFactory(private val context: Context) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ThemeViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ThemeViewModel(ThemeDataStoreRepository(context.applicationContext)) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}