package com.scto.mcs.core.terminalold

import android.content.Intent
import android.os.IBinder
import com.scto.mcs.core.terminal.SessionService as ModernSessionService

/**
 * Wrapper für den SessionService, der auf die moderne Implementierung verweist.
 */
class SessionService : android.app.Service() {
    private val modernService = ModernSessionService()

    override fun onBind(intent: Intent?): IBinder? = modernService.onBind(intent)
    override fun onCreate() = modernService.onCreate()
    override fun onDestroy() = modernService.onDestroy()
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int = 
        modernService.onStartCommand(intent, flags, startId)
}
