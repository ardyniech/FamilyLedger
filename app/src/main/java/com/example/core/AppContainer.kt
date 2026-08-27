package com.example.core

import android.content.Context
import com.example.core.auth.AuthManager
import com.example.core.auth.GoogleAuthService
import com.example.core.storage.AppDatabase
import com.example.core.storage.HouseholdRepository

interface AppContainer {
    val context: Context
    val householdRepository: HouseholdRepository
    val authManager: AuthManager
}

class DefaultAppContainer(override val context: Context) : AppContainer {
    private val googleAuthService by lazy { GoogleAuthService() }

    override val authManager: AuthManager by lazy {
        AuthManager(googleAuthService)
    }

    override val householdRepository: HouseholdRepository by lazy {
        HouseholdRepository(AppDatabase.getDatabase(context).householdDao())
    }
}
