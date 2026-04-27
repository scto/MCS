package com.scto.mcs.app.scope

import androidx.lifecycle.lifecycleScope

import com.scto.mcs.app.activities.main.MainActivity

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope

// same as MainActivity.lifeCycleScope
@OptIn(DelicateCoroutinesApi::class)
val DefaultScope: CoroutineScope
    get() {
        return MainActivity.instance?.lifecycleScope ?: GlobalScope
    }
