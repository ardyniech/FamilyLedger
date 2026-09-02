package com.example.core.auth

import android.content.Context
import android.content.SharedPreferences

class AppLockManager(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences("app_lock_prefs", Context.MODE_PRIVATE)

    fun isLockEnabled(): Boolean = prefs.getBoolean("is_lock_enabled", false)

    fun setLockEnabled(enabled: Boolean) {
        prefs.edit().putBoolean("is_lock_enabled", enabled).apply()
    }

    fun setPin(pin: String) {
        prefs.edit().putString("app_pin", pin).apply()
        setLockEnabled(true)
    }

    fun verifyPin(pin: String): Boolean {
        val storedPin = prefs.getString("app_pin", "1234")
        return storedPin == pin
    }

    fun hasPinSet(): Boolean = prefs.contains("app_pin")
}
