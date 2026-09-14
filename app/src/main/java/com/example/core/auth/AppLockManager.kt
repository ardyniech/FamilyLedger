package com.example.core.auth

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKeys

class AppLockManager(private val context: Context) {
    private val prefs: SharedPreferences by lazy {
        initEncryptedPrefs(context)
    }

    private fun initEncryptedPrefs(context: Context): SharedPreferences {
        return try {
            val masterKeyAlias = MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC)
            EncryptedSharedPreferences.create(
                "secure_app_lock_prefs",
                masterKeyAlias,
                context,
                EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
            )
        } catch (e: Exception) {
            Log.e("AppLockManager", "EncryptedSharedPreferences init error: ${e.message}", e)
            context.getSharedPreferences("secure_app_lock_prefs_fallback", Context.MODE_PRIVATE)
        }
    }

    fun isLockEnabled(): Boolean = prefs.getBoolean("is_lock_enabled", false)

    fun setLockEnabled(enabled: Boolean) {
        prefs.edit().putBoolean("is_lock_enabled", enabled).apply()
    }

    fun isBiometricEnabled(): Boolean = prefs.getBoolean("is_biometric_enabled", false)

    fun setBiometricEnabled(enabled: Boolean) {
        prefs.edit().putBoolean("is_biometric_enabled", enabled).apply()
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

    fun getMaskedPin(): String {
        val pin = prefs.getString("app_pin", "1234") ?: "1234"
        return "•".repeat(pin.length)
    }

    fun getLockTimeoutSeconds(): Int = prefs.getInt("lock_timeout_seconds", 30)

    fun setLockTimeoutSeconds(seconds: Int) {
        prefs.edit().putInt("lock_timeout_seconds", seconds).apply()
    }

    fun recordActivity() {
        prefs.edit().putLong("last_active_timestamp", System.currentTimeMillis()).apply()
    }

    fun isLockRequired(): Boolean {
        if (!isLockEnabled()) return false
        val lastActive = prefs.getLong("last_active_timestamp", 0L)
        if (lastActive == 0L) return true
        val timeoutMillis = getLockTimeoutSeconds() * 1000L
        return (System.currentTimeMillis() - lastActive) > timeoutMillis
    }

    fun unlockSession() {
        recordActivity()
    }
}
