package com.scto.mcs.core.terminal

import android.annotation.SuppressLint
import android.app.*
import android.content.Intent
import android.os.Binder
import android.os.IBinder
import android.os.PowerManager
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.core.app.NotificationCompat

import com.scto.mcs.app.ui.activities.terminal.TerminalActivity
import com.scto.mcs.core.resources.R
import com.scto.mcs.feature.settings.Settings
import com.termux.terminal.TerminalSession
import com.termux.terminal.TerminalSessionClient

import kotlinx.coroutines.*

/**
 * Ein Foreground-Service, der Terminal-Sessions am Leben erhält, 
 * auch wenn die Activity im Hintergrund ist.
 */
class SessionService : Service() {
    private val sessions = hashMapOf<SessionId, TerminalSession>()
    private val sessionWorkDirs = mutableMapOf<SessionId, SessionPwd>()
    val sessionList = mutableStateListOf<String>()
    var currentSession = mutableStateOf("main")
    private var deamonRunning = false
    private var wakeLock: PowerManager.WakeLock? = null

    inner class SessionBinder : Binder() {
        fun getService(): SessionService = this@SessionService

        fun createSession(id: SessionId, client: TerminalSessionClient, activity: TerminalActivity): SessionInfo {
            val (session, pwd) = MkSession.createSession(activity, client, id)
            sessions[id] = session
            sessionWorkDirs[id] = pwd
            sessionList.add(id)
            updateNotification()
            return SessionInfo(id, pwd, session)
        }

        fun getSession(id: SessionId): TerminalSession? = sessions[id]

        fun terminateSession(id: SessionId) {
            sessions[id]?.finishIfRunning()
            sessions.remove(id)
            sessionList.remove(id)
            sessionWorkDirs.remove(id)

            if (sessions.isEmpty()) {
                stopSelf()
                deamonRunning = false
            } else {
                updateNotification()
            }
        }
    }

    private val binder = SessionBinder()
    private val notificationManager by lazy { getSystemService(NotificationManager::class.java) }

    override fun onBind(intent: Intent?): IBinder = binder

    override fun onDestroy() {
        sessions.values.forEach { it.finishIfRunning() }
        deamonRunning = false
        wakeLock?.let { if (it.isHeld) it.release() }
        super.onDestroy()
    }

    @OptIn(DelicateCoroutinesApi::class)
    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
        startForeground(1, createNotification())

        if (!deamonRunning) {
            GlobalScope.launch(Dispatchers.IO) { deamonRunning = true }
        }

        if (wakeLock == null) {
            val pm = getSystemService(POWER_SERVICE) as PowerManager
            wakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "MCS::TerminalService")
        }
    }

    fun actionExit() {
        sessions.values.forEach { it.finishIfRunning() }
        deamonRunning = false
        stopSelf()
    }

    @SuppressLint("WakelockTimeout")
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            "ACTION_EXIT" -> actionExit()
            "ACTION_WAKE_LOCK" -> {
                wakeLock?.let { if (it.isHeld) it.release() else it.acquire() }
                updateNotification()
            }
        }
        return START_NOT_STICKY
    }

    private fun createNotification(): Notification {
        val intent = Intent(this, TerminalActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_IMMUTABLE)
        
        val exitIntent = Intent(this, SessionService::class.java).apply { action = "ACTION_EXIT" }
        val wakeIntent = Intent(this, SessionService::class.java).apply { action = "ACTION_WAKE_LOCK" }

        return NotificationCompat.Builder(this, "terminal_channel")
            .setContentTitle("MCS Terminal")
            .setContentText(getNotificationText())
            .setSmallIcon(R.drawable.ic_terminal) // Nutzt zentrales Drawable
            .setContentIntent(pendingIntent)
            .addAction(0, "Beenden", PendingIntent.getService(this, 1, exitIntent, PendingIntent.FLAG_IMMUTABLE))
            .addAction(0, if (wakeLock?.isHeld == true) "WakeLock aus" else "WakeLock an", 
                PendingIntent.getService(this, 2, wakeIntent, PendingIntent.FLAG_IMMUTABLE))
            .setOngoing(true)
            .build()
    }

    private fun createNotificationChannel() {
        val channel = NotificationChannel("terminal_channel", "Terminal Service", NotificationManager.IMPORTANCE_LOW)
        notificationManager.createNotificationChannel(channel)
    }

    private fun updateNotification() {
        notificationManager.notify(1, createNotification())
    }

    private fun getNotificationText(): String {
        val held = if (wakeLock?.isHeld == true) "(WakeLock aktiv)" else ""
        return "${sessions.size} Sitzungen aktiv $held"
    }
}

typealias SessionId = String
typealias SessionPwd = String
data class SessionInfo(val id: SessionId, val pwd: SessionPwd, val session: TerminalSession)