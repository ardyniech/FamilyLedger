package com.example.shared.models

data class AuthUser(
    val uid: String,
    val email: String,
    val displayName: String,
    val photoUrl: String? = null
)

sealed interface AuthUiState {
    object Unauthenticated : AuthUiState
    object Authenticating : AuthUiState
    data class Authenticated(val user: AuthUser) : AuthUiState
    data class Error(val message: String) : AuthUiState
}
