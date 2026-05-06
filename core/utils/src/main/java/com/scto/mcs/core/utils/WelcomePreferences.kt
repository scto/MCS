package com.scto.mcs.core.utils

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit


object WelcomePreferences {
    private const val PREFS_NAME = "welcome_prefs"
    private const val KEY_WELCOME_COMPLETED = "welcome_completed"
    
    private fun getPreferences(context: Context): SharedPreferences {
        return context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    }
    
    /**
     * Prüft, ob der Willkommensprozess abgeschlossen ist
     */
    fun isWelcomeCompleted(context: Context): Boolean {
        return getPreferences(context).getBoolean(KEY_WELCOME_COMPLETED, false)
    }
    
    /**
     * Markiert den Willkommensprozess als abgeschlossen
     */
    fun setWelcomeCompleted(context: Context) {
        getPreferences(context).edit { putBoolean(KEY_WELCOME_COMPLETED, true) }
        LogCatcher.i("WelcomePreferences", "Willkommensprozess als abgeschlossen markiert")
    }

}