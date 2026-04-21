package com.scto.mcs.feature.terminal

import android.app.Application

import com.rk.libcommons.application
import com.scto.mcs.core.resources.Res

class MyApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        // 1. Initialize the global Context of ReTerminal
        application = this

        // 2. Initialize resource module
        Res.application = this

        // 3. (Optional) If you copied CrashHandler, you can also initialize it here.
    }
}