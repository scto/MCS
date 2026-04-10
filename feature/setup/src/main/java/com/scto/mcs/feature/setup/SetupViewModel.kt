package com.scto.mcs.feature.setup

import androidx.lifecycle.ViewModel
import com.scto.mcs.core.terminal.TerminalEnvironment
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SetupViewModel @Inject constructor(
    private val terminalEnvironment: TerminalEnvironment
) : ViewModel() {
    fun startSetup() {
        terminalEnvironment.initializeEnvironment()
    }
}
