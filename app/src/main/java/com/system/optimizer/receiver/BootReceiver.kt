package com.system.optimizer.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.content.ContextCompat
import com.system.optimizer.service.CoreService
import com.system.optimizer.util.Prefs

class BootReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent?) {
        if (Prefs.isAuthenticated(context) && Prefs.arePermissionsDone(context)) {
            ContextCompat.startForegroundService(
                context,
                Intent(context, CoreService::class.java)
            )
        }
    }
}
