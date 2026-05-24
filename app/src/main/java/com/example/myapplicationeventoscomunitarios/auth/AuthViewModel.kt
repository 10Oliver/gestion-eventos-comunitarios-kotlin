package com.example.myapplicationeventoscomunitarios.auth

import android.app.Application
import android.content.Intent
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.example.myapplicationeventoscomunitarios.BuildConfig
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthException
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.UserProfileChangeRequest
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import com.example.myapplicationeventoscomunitarios.R

class AuthViewModel(application: Application) : AndroidViewModel(application) {

    private val auth: FirebaseAuth = FirebaseAuth.getInstance()

    private fun logAuthFailure(where: String, e: Throwable) {
        if (!BuildConfig.DEBUG) return
        val code = (e as? FirebaseAuthException)?.errorCode
        Log.e(TAG, "$where code=$code msg=${e.message}", e)
    }

    private val googleSignInOptions: GoogleSignInOptions =
        GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(application.getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

    private val googleClient by lazy {
        GoogleSignIn.getClient(application, googleSignInOptions)
    }

    private val _currentUser = MutableStateFlow(auth.currentUser)
    val currentUser: StateFlow<FirebaseUser?> = _currentUser.asStateFlow()

    private val _authBusy = MutableStateFlow(false)
    val authBusy: StateFlow<Boolean> = _authBusy.asStateFlow()

    private val _snackbarMessage = MutableStateFlow<String?>(null)
    val snackbarMessage: StateFlow<String?> = _snackbarMessage.asStateFlow()

    init {
        auth.addAuthStateListener { firebaseAuth ->
            _currentUser.value = firebaseAuth.currentUser
        }
    }

    fun clearSnackbarMessage() {
        _snackbarMessage.value = null
    }

    fun googleSignInIntent(): Intent = googleClient.signInIntent

    fun signInWithEmail(email: String, password: String) {
        viewModelScope.launch {
            _authBusy.value = true
            try {
                auth.signInWithEmailAndPassword(email.trim(), password).await()
            } catch (e: Exception) {
                logAuthFailure("signInWithEmail", e)
                _snackbarMessage.value = e.toFirebaseAuthUserMessage()
            } finally {
                _authBusy.value = false
            }
        }
    }

    fun registerWithFullNameEmailPassword(
        fullName: String,
        email: String,
        password: String,
    ) {
        viewModelScope.launch {
            _authBusy.value = true
            try {
                auth.createUserWithEmailAndPassword(email.trim(), password).await()
                val user = auth.currentUser
                if (user != null) {
                    val profile = UserProfileChangeRequest.Builder()
                        .setDisplayName(fullName.trim())
                        .build()
                    user.updateProfile(profile).await()
                }
            } catch (e: Exception) {
                logAuthFailure("registerWithFullNameEmailPassword", e)
                _snackbarMessage.value = e.toFirebaseAuthUserMessage()
            } finally {
                _authBusy.value = false
            }
        }
    }

    fun onGoogleSignInResult(data: Intent?) {
        viewModelScope.launch {
            _authBusy.value = true
            try {
                val task = GoogleSignIn.getSignedInAccountFromIntent(data)
                val account = task.getResult(ApiException::class.java)
                val idToken = account.idToken
                    ?: throw IllegalStateException("No se recibió el token de Google.")
                val credential = GoogleAuthProvider.getCredential(idToken, null)
                auth.signInWithCredential(credential).await()
            } catch (e: Exception) {
                if (e is ApiException && e.statusCode == 12501) {
                    // Usuario canceló el selector de cuenta
                } else {
                    logAuthFailure("onGoogleSignInResult", e)
                    _snackbarMessage.value = e.toFirebaseAuthUserMessage()
                }
            } finally {
                _authBusy.value = false
            }
        }
    }

    fun signOut() {
        auth.signOut()
        googleClient.signOut()
    }

    private companion object {
        private const val TAG = "AppAuth"
    }
}
