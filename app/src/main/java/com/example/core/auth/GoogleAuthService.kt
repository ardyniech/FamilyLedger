package com.example.core.auth

import android.content.Context
import android.util.Log
import androidx.credentials.ClearCredentialStateRequest
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.credentials.exceptions.GetCredentialCancellationException
import androidx.credentials.exceptions.GetCredentialException
import com.example.shared.models.AuthUser
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.coroutines.tasks.await

class GoogleAuthService {
    private val auth: FirebaseAuth by lazy { FirebaseAuth.getInstance() }

    suspend fun signInWithGoogle(context: Context): Result<AuthUser> {
        return try {
            val credentialManager = CredentialManager.create(context)
            val resId = context.resources.getIdentifier("default_web_client_id", "string", context.packageName)
            val serverClientId = if (resId != 0) {
                context.getString(resId)
            } else {
                com.example.BuildConfig.GOOGLE_WEB_CLIENT_ID.ifBlank { "family-ledger.apps.googleusercontent.com" }
            }

            val googleIdOption = GetGoogleIdOption.Builder()
                .setFilterByAuthorizedAccounts(false)
                .setServerClientId(serverClientId)
                .setAutoSelectEnabled(false)
                .build()

            val request = GetCredentialRequest.Builder()
                .addCredentialOption(googleIdOption)
                .build()

            val result = credentialManager.getCredential(context = context, request = request)
            val credential = result.credential

            if (credential is CustomCredential && credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {
                val googleIdTokenCredential = GoogleIdTokenCredential.createFrom(credential.data)
                val idToken = googleIdTokenCredential.idToken
                val authCredential = GoogleAuthProvider.getCredential(idToken, null)
                val authResult = auth.signInWithCredential(authCredential).await()
                val user = authResult.user

                if (user != null) {
                    Result.success(
                        AuthUser(
                            uid = user.uid,
                            email = user.email ?: googleIdTokenCredential.id,
                            displayName = user.displayName ?: googleIdTokenCredential.displayName ?: "Family Member",
                            photoUrl = user.photoUrl?.toString() ?: googleIdTokenCredential.profilePictureUri?.toString()
                        )
                    )
                } else {
                    Result.failure(Exception("Firebase user is null after sign in"))
                }
            } else {
                Result.failure(Exception("Unsupported credential type returned"))
            }
        } catch (e: GetCredentialCancellationException) {
            Log.d("GoogleAuthService", "Sign in was cancelled by user")
            Result.failure(Exception("Masuk dengan Google dibatalkan."))
        } catch (e: GetCredentialException) {
            Log.w("GoogleAuthService", "Credential Manager failure: ${e.message}")
            Result.failure(Exception("Gagal mengambil kredensial Google: ${e.message}"))
        } catch (e: Exception) {
            Log.e("GoogleAuthService", "Firebase authentication error: ${e.message}", e)
            Result.failure(Exception(e.message ?: "Autentikasi gagal"))
        }
    }

    suspend fun signOut(context: Context) {
        try {
            auth.signOut()
            val credentialManager = CredentialManager.create(context)
            credentialManager.clearCredentialState(ClearCredentialStateRequest())
        } catch (e: Exception) {
            Log.w("GoogleAuthService", "Sign out error: ${e.message}")
        }
    }

    fun getCurrentAuthUser(): AuthUser? {
        val user = auth.currentUser ?: return null
        return AuthUser(
            uid = user.uid,
            email = user.email ?: "",
            displayName = user.displayName ?: "Family Member",
            photoUrl = user.photoUrl?.toString()
        )
    }
}
