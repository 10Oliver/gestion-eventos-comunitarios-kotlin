package com.example.myapplicationeventoscomunitarios

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedContentTransitionScope
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.example.myapplicationeventoscomunitarios.screens.CommentsScreen
import com.example.myapplicationeventoscomunitarios.screens.CreateEventScreen
import com.example.myapplicationeventoscomunitarios.screens.EventDetailScreen
import com.example.myapplicationeventoscomunitarios.screens.HistoryScreen
import com.example.myapplicationeventoscomunitarios.screens.HomeScreen
import com.example.myapplicationeventoscomunitarios.screens.LoginScreen
import com.example.myapplicationeventoscomunitarios.screens.RegisterScreen
import com.example.myapplicationeventoscomunitarios.screens.StatsScreen
import com.example.myapplicationeventoscomunitarios.ui.theme.MyApplicationEventosComunitariosTheme
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

            MyApplicationEventosComunitariosTheme(darkTheme = isDarkTheme) {
                var currentScreen by remember { mutableStateOf(Screen.Login) }
                val snackbarHostState = remember { SnackbarHostState() }
                val scope = rememberCoroutineScope()

                val showSnackbar: (String) -> Unit = { msg ->
                    scope.launch { snackbarHostState.showSnackbar(msg) }
                }

                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    snackbarHost = { SnackbarHost(snackbarHostState) },
                    containerColor = MaterialTheme.colorScheme.background
                ) { innerPadding ->
                    AnimatedContent(
                        targetState = currentScreen,
                        transitionSpec = {
                            val direction = if (targetState.ordinal > initialState.ordinal) {
                                AnimatedContentTransitionScope.SlideDirection.Left
                            } else {
                                AnimatedContentTransitionScope.SlideDirection.Right
                            }
                            (slideIntoContainer(direction, tween(280)) + fadeIn(tween(280))) togetherWith
                                (slideOutOfContainer(direction, tween(280)) + fadeOut(tween(280)))
                        },
                        label = "screen-transition"
                    ) { screen ->
                        val mod = Modifier.padding(innerPadding)
                        when (screen) {
                            Screen.Login -> LoginScreen(
                                modifier = mod,
                                onRegisterClick = { currentScreen = Screen.Register },
                                onLoginClick = { currentScreen = Screen.Home }
                            )

                            Screen.Register -> RegisterScreen(
                                modifier = mod,
                                onBackToLoginClick = { currentScreen = Screen.Login },
                                onRegisterSuccess = { currentScreen = Screen.Home }
                            )

                            Screen.Home -> HomeScreen(
                                modifier = mod,
                                onCreateEventClick = { currentScreen = Screen.CreateEvent },
                                onEventClick = { currentScreen = Screen.EventDetail },
                                onHistoryClick = { currentScreen = Screen.History },
                                onStatsClick = { currentScreen = Screen.Stats },
                                isDarkTheme = isDarkTheme,
                                onToggleTheme = { isDarkTheme = !isDarkTheme }
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
