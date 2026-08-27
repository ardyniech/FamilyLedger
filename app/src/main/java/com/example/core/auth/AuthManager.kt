package com.example.core.auth

import android.content.Context
import com.example.shared.models.AuthUiState
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class AuthManager(private val googleAuthService: GoogleAuthService) {
    private val _authState = MutableStateFlow<AuthUiState>(AuthUiState.Unauthenticated)
    val authState: StateFlow<AuthUiState> = _authState.asStateFlow()

    init {
        try {
            val initialUser = googleAuthService.getCurrentAuthUser()
            if (initialUser != null) {
                _authState.value = AuthUiState.Authenticated(initialUser)
            }
            FirebaseAuth.getInstance().addAuthStateListener { firebaseAuth ->
                val user = firebaseAuth.currentUser
                if (user != null) {
                    _authState.value = AuthUiState.Authenticated(
                        googleAuthService.getCurrentAuthUser() ?: return@addAuthStateListener
                    )
                } else if (_authState.value !is AuthUiState.Authenticating) {
                    _authState.value = AuthUiState.Unauthenticated
                }
            }
        } catch (e: Exception) {
            _authState.value = AuthUiState.Unauthenticated
        }
    }

    fun signInWithGoogle(context: Context, scope: CoroutineScope, onSuccess: (() -> Unit)? = null) {
        _authState.value = AuthUiState.Authenticating
        scope.launch(Dispatchers.IO) {
            val result = googleAuthService.signInWithGoogle(context)
            result.fold(
                onSuccess = { user ->
                    _authState.value = AuthUiState.Authenticated(user)
                    onSuccess?.invoke()
                },
                onFailure = { error ->
                    _authState.value = AuthUiState.Error(error.message ?: "Gagal masuk dengan Google")
                }
            )
        }
    }

    fun signOut(context: Context, scope: CoroutineScope) {
        scope.launch(Dispatchers.IO) {
            googleAuthService.signOut(context)
            _authState.value = AuthUiState.Unauthenticated
        }
    }

    fun clearError() {
        if (_authState.value is AuthUiState.Error) {
            _authState.value = AuthUiState.Unauthenticated
        }
    }
}
