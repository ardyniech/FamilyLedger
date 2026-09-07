package com.ardyniech.familyledger.security

import android.content.Context
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKeys

object SecurityManager {
    private const val PREF_NAME = "secure_ledger_prefs"

    fun getEncryptedPrefs(context: Context) = EncryptedSharedPreferences.create(
        PREF_NAME,
        MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC),
        context,
        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
    )

    fun saveSensitiveData(context: Context, key: String, value: String) {
        getEncryptedPrefs(context).edit().putString(key, value).apply()
    }

    fun getSensitiveData(context: Context, key: String): String? {
        return getEncryptedPrefs(context).getString(key, null)
    }
}