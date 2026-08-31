package com.example.core.auth

import android.content.Context
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKeys
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import com.example.shared.models.AuthUser
import com.example.shared.models.AuthUiState

class LocalAuthManager(private val context: Context) {
    
    private val prefs: android.content.SharedPreferences by lazy {
        try {
            val masterKey = MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC)
            EncryptedSharedPreferences.create(
                "family_ledger_auth_prefs",
                masterKey,
                context,
                EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
            )
        } catch (_: Exception) {
            context.getSharedPreferences("family_ledger_auth_prefs_plain", Context.MODE_PRIVATE)
        }
    }
    
    private fun hashPassword(password: String): String {
        val digest = java.security.MessageDigest.getInstance("SHA-256")
        val hashBytes = digest.digest(password.toByteArray(Charsets.UTF_8))
        return hashBytes.joinToString("") { "%02x".format(it) }
    }

    fun createAccount(userId: String, password: String): Boolean {
        val hashedPassword = hashPassword(password)
        prefs.edit().putString("${userId}_password", hashedPassword).apply()
        prefs.edit().putBoolean("${userId}_exists", true).apply()
        return true
    }
    
    fun authenticate(userId: String, password: String): Boolean {
        val storedPassword = prefs.getString("${userId}_password", null) ?: return false
        val hashedPassword = hashPassword(password)
        return storedPassword == hashedPassword
    }
    
    fun logout() {
        // Handled by wrapper
    }
    
    fun userExists(userId: String): Boolean {
        return prefs.getBoolean("${userId}_exists", false)
    }
}
