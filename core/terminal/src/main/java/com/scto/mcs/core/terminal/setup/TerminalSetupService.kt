package com.scto.mcs.core.terminal.setup

import android.content.Context
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Service zur Einrichtung der Terminal-Umgebung.
 */
@Singleton
class TerminalSetupService @Inject constructor() {

    sealed class SetupState {
        object Completed : SetupState()
        data class Progress(val message: String) : SetupState()
        data class Error(val message: String) : SetupState()
    }

    fun runFullSetup(context: Context): Flow<SetupState> = flow {
        emit(SetupState.Progress("Initialisiere Umgebung..."))
        // Hier würde die eigentliche Setup-Logik folgen
        emit(SetupState.Completed)
    }
}
