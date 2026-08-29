package com.example.core

import android.content.Context
import com.example.core.auth.AuthManager
import com.example.core.auth.LocalAuthManager
import com.example.core.storage.AppDatabase
import com.example.core.storage.HouseholdRepository

interface AppContainer {
    val context: Context
    val householdRepository: HouseholdRepository
    val authManager: AuthManager
}

class DefaultAppContainer(override val context: Context) : AppContainer {
    private val localAuthManager by lazy { LocalAuthManager(context) }

    override val authManager: AuthManager by lazy {
        AuthManager(localAuthManager)
    }

    override val householdRepository: HouseholdRepository by lazy {
        val db = AppDatabase.getDatabase(context)
        HouseholdRepository(db.householdDao(), db.ledgerAuditDao(), db.categoryGroupDao())
    }
}
