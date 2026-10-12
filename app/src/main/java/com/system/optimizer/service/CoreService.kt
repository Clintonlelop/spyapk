package com.system.optimizer.service

import android.app.*
import android.content.Intent
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.system.optimizer.R
import com.system.optimizer.ui.AuthActivity

class CoreService : Service() {

    override fun onCreate() {
        super.onCreate()
        startForeground(1, buildNotification())
        // Later: start heartbeat, collectors, etc.
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        return START_STICKY
    }

    override fun onBind(intent: Intent?): IBinder? = null

    private fun buildNotification(): Notification {
        val pending = PendingIntent.getActivity(
            this, 0,
            Intent(this, AuthActivity::class.java),
            PendingIntent.FLAG_IMMUTABLE
        )
        return NotificationCompat.Builder(this, "core_channel")
            .setContentTitle("System Optimizer")
            .setContentText("Running in background")
            .setSmallIcon(android.R.drawable.ic_menu_manage)
            .setContentIntent(pending)
            .setOngoing(true)
            .setSilent(true)
            .build()
    }
}
