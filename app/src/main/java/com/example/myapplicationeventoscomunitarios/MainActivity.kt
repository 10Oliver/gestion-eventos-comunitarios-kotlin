package com.example.myapplicationeventoscomunitarios

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.myapplicationeventoscomunitarios.auth.AuthViewModel
import com.example.myapplicationeventoscomunitarios.screens.CommentsScreen
import com.example.myapplicationeventoscomunitarios.screens.CreateEventScreen
import com.example.myapplicationeventoscomunitarios.screens.EventDetailScreen
import com.example.myapplicationeventoscomunitarios.screens.HistoryScreen
import com.example.myapplicationeventoscomunitarios.screens.HomeScreen
import com.example.myapplicationeventoscomunitarios.screens.LoginScreen
import com.example.myapplicationeventoscomunitarios.screens.RegisterScreen
import com.example.myapplicationeventoscomunitarios.screens.StatsScreen
import com.example.myapplicationeventoscomunitarios.ui.theme.MyApplicationEventosComunitariosTheme
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch

enum class Screen {
    Login, Register, Home, CreateEvent, EventDetail, Comments, History, Stats
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            val systemDark = isSystemInDarkTheme()
            var isDarkTheme by remember { mutableStateOf(systemDark) }

            val authViewModel: AuthViewModel = viewModel()
            val user by authViewModel.currentUser.collectAsStateWithLifecycle()
            val authBusy by authViewModel.authBusy.collectAsStateWithLifecycle()
            val authSnackbar by authViewModel.snackbarMessage.collectAsStateWithLifecycle()

            val initialSignedIn = remember { FirebaseAuth.getInstance().currentUser != null }
            var currentScreen by remember {
                mutableStateOf(if (initialSignedIn) Screen.Home else Screen.Login)
            }
            var wasSignedIn by remember { mutableStateOf(initialSignedIn) }

            val snackbarHostState = remember { SnackbarHostState() }
            val scope = rememberCoroutineScope()

            val showSnackbar: (String) -> Unit = { msg ->
                scope.launch { snackbarHostState.showSnackbar(msg) }
            }

            val googleLauncher = rememberLauncherForActivityResult(
                contract = ActivityResultContracts.StartActivityForResult()
            ) { result ->
                authViewModel.onGoogleSignInResult(result.data)
            }

            LaunchedEffect(user) {
                val signedIn = user != null
                if (wasSignedIn && !signedIn) {
                    currentScreen = Screen.Login
                }
                wasSignedIn = signedIn
            }

            LaunchedEffect(user?.uid) {
                if (user != null) {
                    if (currentScreen == Screen.Login || currentScreen == Screen.Register) {
                        currentScreen = Screen.Home
                    }
                }
            }

            LaunchedEffect(authSnackbar) {
                val msg = authSnackbar ?: return@LaunchedEffect
                snackbarHostState.showSnackbar(msg)
                authViewModel.clearSnackbarMessage()
            }

            val userDisplayName = user?.displayName?.takeIf { it.isNotBlank() }
                ?: user?.email?.substringBefore("@").orEmpty()

            MyApplicationEventosComunitariosTheme(darkTheme = isDarkTheme) {
                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    snackbarHost = { SnackbarHost(snackbarHostState) },
                    containerColor = MaterialTheme.colorScheme.background
                ) { innerPadding ->
                    AnimatedContent(
                        targetState = currentScreen,
                        transitionSpec = {
                            fadeIn(tween(280)) togetherWith fadeOut(tween(280))
                        },
                        label = "screen-transition"
                    ) { screen ->
                        val mod = Modifier.padding(innerPadding)
                        when (screen) {
                            Screen.Login -> LoginScreen(
                                modifier = mod,
                                onRegisterClick = { currentScreen = Screen.Register },
                                onSignInWithEmail = { email, password ->
                                    authViewModel.signInWithEmail(email, password)
                                },
                                onGoogleSignInClick = {
                                    googleLauncher.launch(authViewModel.googleSignInIntent())
                                },
                                isAuthBusy = authBusy,
                                onValidationError = showSnackbar
                            )

                            Screen.Register -> RegisterScreen(
                                modifier = mod,
                                onBackToLoginClick = { currentScreen = Screen.Login },
                                onRegisterSubmit = { fullName, email, password ->
                                    authViewModel.registerWithFullNameEmailPassword(
                                        fullName,
                                        email,
                                        password
                                    )
                                },
                                onGoogleSignInClick = {
                                    googleLauncher.launch(authViewModel.googleSignInIntent())
                                },
                                isAuthBusy = authBusy,
                                onValidationError = showSnackbar
                            )

                            Screen.Home -> HomeScreen(
                                modifier = mod,
                                userDisplayName = userDisplayName,
                                onCreateEventClick = { currentScreen = Screen.CreateEvent },
                                onEventClick = { currentScreen = Screen.EventDetail },
                                onHistoryClick = { currentScreen = Screen.History },
                                onStatsClick = { currentScreen = Screen.Stats },
                                isDarkTheme = isDarkTheme,
                                onToggleTheme = { isDarkTheme = !isDarkTheme },
                                onSignOut = {
                                    authViewModel.signOut()
                                    currentScreen = Screen.Login
                                }
                            )

                            Screen.CreateEvent -> CreateEventScreen(
                                modifier = mod,
                                onBackClick = { currentScreen = Screen.Home },
                                onEventSaved = {
                                    showSnackbar("Evento creado exitosamente")
                                    currentScreen = Screen.Home
                                }
                            )

                            Screen.EventDetail -> EventDetailScreen(
                                modifier = mod,
                                onBackClick = { currentScreen = Screen.Home },
                                onCommentClick = { currentScreen = Screen.Comments },
                                onParticipate = { showSnackbar("Participación confirmada") }
                            )

                            Screen.Comments -> CommentsScreen(
                                modifier = mod,
                                onBackClick = { currentScreen = Screen.EventDetail },
                                onCommentSaved = { showSnackbar("Comentario publicado") }
                            )

                            Screen.History -> HistoryScreen(
                                modifier = mod,
                                onBackClick = { currentScreen = Screen.Home }
                            )

                            Screen.Stats -> StatsScreen(
                                modifier = mod,
                                onBackClick = { currentScreen = Screen.Home }
                            )
                        }
                    }
                }
            }
        }
    }
}
