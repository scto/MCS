package com.scto.mcs.core.terminal

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.IBinder
import android.os.PowerManager
import com.scto.mcs.core.domain.repository.FileRepository
import com.scto.mcs.core.terminal.session.TerminalSessionManager
import com.termux.terminal.TerminalSession
import com.termux.terminal.TerminalSessionClient
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class SessionService : Service(), TerminalSessionClient {

    @Inject lateinit var sessionManager: TerminalSessionManager
    @Inject lateinit var fileRepository: FileRepository
    
    private var wakeLock: PowerManager.WakeLock? = null

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
        startForeground(1, createNotification())
        
        val powerManager = getSystemService(Context.POWER_SERVICE) as PowerManager
        wakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "MCS:TerminalService")
        wakeLock?.acquire()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        return START_STICKY
    }

    override fun onDestroy() {
        wakeLock?.release()
        super.onDestroy()
    }

    override fun onBind(intent: Intent?): IBinder? = null

    private fun createNotificationChannel() {
        val channel = NotificationChannel("terminal_service", "Terminal Service", NotificationManager.IMPORTANCE_LOW)
        getSystemService(NotificationManager::class.java).createNotificationChannel(channel)
    }

    private fun createNotification(): Notification {
        return Notification.Builder(this, "terminal_service")
            .setContentTitle("Terminal läuft")
            .setSmallIcon(android.R.drawable.ic_menu_mylocation)
            .build()
    }

    // TerminalSessionClient Implementierung
    override fun onSessionFinished(session: TerminalSession?) {
        session?.let { sessionManager.closeSession(it.mHandle) }
    }

    override fun onCopyTextToClipboard(session: TerminalSession?, text: String?) {}
    override fun onPasteTextFromClipboard(session: TerminalSession?) {}
    override fun onBell(session: TerminalSession?) {}
    override fun onColorsChanged(session: TerminalSession?) {}
    override fun getEmulatorDimensions(session: TerminalSession?): IntArray = intArrayOf(80, 24)
    override fun onTextChanged(changedSession: TerminalSession?) {}
    override fun onTitleChanged(changedSession: TerminalSession?) {}
}
