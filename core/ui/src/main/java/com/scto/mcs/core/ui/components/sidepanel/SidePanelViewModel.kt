package com.scto.mcs.core.ui.components.sidepanel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope

import dagger.hilt.android.lifecycle.HiltViewModel

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

import javax.inject.Inject

/**
 * Zustandsverwaltung für das SidePanel und die globale Navigation.
 */
@HiltViewModel
class SidePanelViewModel @Inject constructor() : ViewModel() {

    // Aktuell angezeigtes Tool (Explorer oder Git)
    private val _activePanel = MutableStateFlow(SidePanelType.FILE_TREE)
    val activePanel: StateFlow<SidePanelType> = _activePanel.asStateFlow()

    /**
     * Schaltet zwischen den Tool-Ansichten um.
     */
    fun setPanel(type: SidePanelType) {
        viewModelScope.launch {
            _activePanel.value = type
        }
    }
}