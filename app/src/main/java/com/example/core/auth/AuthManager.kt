package com.example.core.auth

import android.content.Context
import com.example.shared.models.AuthUiState
import com.example.shared.models.AuthUser
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class AuthManager(private val localAuthManager: LocalAuthManager) {
    private val _authState = MutableStateFlow<AuthUiState>(AuthUiState.Unauthenticated)
    val authState: StateFlow<AuthUiState> = _authState.asStateFlow()

    fun signInLocal(userId: String, pass: String, context: Context, onSuccess: (() -> Unit)? = null) {
        _authState.value = AuthUiState.Authenticating
        if (localAuthManager.authenticate(userId, pass)) {
            val user = AuthUser(uid = userId, email = userId, displayName = userId)
            _authState.value = AuthUiState.Authenticated(user)
            onSuccess?.invoke()
        } else {
            _authState.value = AuthUiState.Error("Password salah atau akun tidak ditemukan")
        }
    }

    fun createLocalAccount(userId: String, pass: String, context: Context, onSuccess: (() -> Unit)? = null) {
        _authState.value = AuthUiState.Authenticating
        if (localAuthManager.createAccount(userId, pass)) {
            val user = AuthUser(uid = userId, email = userId, displayName = userId)
            _authState.value = AuthUiState.Authenticated(user)
            onSuccess?.invoke()
        } else {
            _authState.value = AuthUiState.Error("Gagal membuat akun")
        }
    }

    fun signOut(context: Context, scope: CoroutineScope) {
        localAuthManager.logout()
        _authState.value = AuthUiState.Unauthenticated
    }

    fun clearError() {
        if (_authState.value is AuthUiState.Error) {
            _authState.value = AuthUiState.Unauthenticated
        }
    }
}
