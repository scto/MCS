package com.scto.mcs.core.terminalold

import android.app.Service
import android.content.Intent
import android.os.IBinder
import com.scto.mcs.core.terminal.SessionService as ModernSessionService
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

/**
 * Wrapper für den SessionService, der auf die moderne Implementierung verweist.
 * Nutzt @AndroidEntryPoint für Hilt-Injection.
 */
@AndroidEntryPoint
class SessionService : Service() {
    
    @Inject lateinit var modernService: ModernSessionService

    override fun onBind(intent: Intent?): IBinder? = modernService.onBind(intent)
    override fun onCreate() {
        super.onCreate()
        modernService.onCreate()
    }
    override fun onDestroy() {
        modernService.onDestroy()
        super.onDestroy()
    }
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int = 
        modernService.onStartCommand(intent, flags, startId)
}
