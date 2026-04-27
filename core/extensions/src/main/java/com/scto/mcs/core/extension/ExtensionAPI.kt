package com.scto.mcs.core.extension

import android.app.Application

abstract class ExtensionAPI : Application.ActivityLifecycleCallbacks {
    abstract fun onExtensionLoaded(extension: Extension)

    abstract fun onUninstalled(extension: Extension)
}
