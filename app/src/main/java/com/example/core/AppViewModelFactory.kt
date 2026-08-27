package com.example.core

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.modules.dashboard.DashboardViewModel

class AppViewModelFactory(
    private val appContainer: AppContainer
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(DashboardViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return DashboardViewModel(
                repository = appContainer.householdRepository,
                authManager = appContainer.authManager,
                context = appContainer.context
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
