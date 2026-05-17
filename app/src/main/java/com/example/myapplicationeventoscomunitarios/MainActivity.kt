package com.example.myapplicationeventoscomunitarios

import android.app.Application
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
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.myapplicationeventoscomunitarios.auth.AuthViewModel
import com.example.myapplicationeventoscomunitarios.events.EventsViewModel
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
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.platform.LocalContext

sealed class AppRoute {
    data object Login : AppRoute()
    data object Register : AppRoute()
    data object Home : AppRoute()
    data object CreateEvent : AppRoute()
    data class EditEvent(val eventId: String) : AppRoute()
    data class EventDetail(val eventId: String) : AppRoute()
    data class Comments(val eventId: String, val eventTitle: String) : AppRoute()
    data object History : AppRoute()
    data object Stats : AppRoute()
}

private fun AppRoute.contentKey(): String = when (this) {
    is AppRoute.EditEvent -> "edit|$eventId"
    is AppRoute.EventDetail -> "detail|$eventId"
    is AppRoute.Comments -> "comments|$eventId"
    is AppRoute.Login -> "login"
    is AppRoute.Register -> "register"
    is AppRoute.Home -> "home"
    is AppRoute.CreateEvent -> "create"
    is AppRoute.History -> "history"
    is AppRoute.Stats -> "stats"
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            val systemDark = isSystemInDarkTheme()
            var isDarkTheme by remember { mutableStateOf(systemDark) }

            val context = LocalContext.current
            val application = context.applicationContext as Application
            val eventsViewModel: EventsViewModel = viewModel(
                factory = ViewModelProvider.AndroidViewModelFactory.getInstance(application)
            )

            val authViewModel: AuthViewModel = viewModel()
            val user by authViewModel.currentUser.collectAsStateWithLifecycle()
            val authBusy by authViewModel.authBusy.collectAsStateWithLifecycle()
            val authSnackbar by authViewModel.snackbarMessage.collectAsStateWithLifecycle()

            val initialSignedIn = remember { FirebaseAuth.getInstance().currentUser != null }
            var currentRoute by remember {
                mutableStateOf<AppRoute>(if (initialSignedIn) AppRoute.Home else AppRoute.Login)
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
                    currentRoute = AppRoute.Login
                }
                wasSignedIn = signedIn
            }

            LaunchedEffect(user?.uid) {
                if (user != null) {
                    if (currentRoute is AppRoute.Login || currentRoute is AppRoute.Register) {
                        currentRoute = AppRoute.Home
                    }
                }
            }

            LaunchedEffect(authSnackbar) {
                val msg = authSnackbar ?: return@LaunchedEffect
                snackbarHostState.showSnackbar(msg)
                authViewModel.clearSnackbarMessage()
            }

            LaunchedEffect(Unit) {
                eventsViewModel.snackbar.collect { msg ->
                    snackbarHostState.showSnackbar(msg)
                }
            }

            val userDisplayName = user?.displayName?.takeIf { it.isNotBlank() }
                ?: user?.email?.substringBefore("@").orEmpty()

            val homeCards by eventsViewModel.homeCards.collectAsStateWithLifecycle()
            val saving by eventsViewModel.saving.collectAsStateWithLifecycle()
            val actionBusy by eventsViewModel.actionBusy.collectAsStateWithLifecycle()

            MyApplicationEventosComunitariosTheme(darkTheme = isDarkTheme) {
                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    snackbarHost = { SnackbarHost(snackbarHostState) },
                    containerColor = MaterialTheme.colorScheme.background
                ) { innerPadding ->
                    AnimatedContent(
                        targetState = currentRoute,
                        transitionSpec = {
                            fadeIn(tween(280)) togetherWith fadeOut(tween(280))
                        },
                        label = "screen-transition"
                    ) { route ->
                        key(route.contentKey()) {
                            val mod = Modifier.padding(innerPadding)
                            when (route) {
                                AppRoute.Login -> LoginScreen(
                                    modifier = mod,
                                    onRegisterClick = { currentRoute = AppRoute.Register },
                                    onSignInWithEmail = { email, password ->
                                        authViewModel.signInWithEmail(email, password)
                                    },
                                    onGoogleSignInClick = {
                                        googleLauncher.launch(authViewModel.googleSignInIntent())
                                    },
                                    isAuthBusy = authBusy,
                                    onValidationError = showSnackbar
                                )

                                AppRoute.Register -> RegisterScreen(
                                    modifier = mod,
                                    onBackToLoginClick = { currentRoute = AppRoute.Login },
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

                                AppRoute.Home -> HomeScreen(
                                    modifier = mod,
                                    userDisplayName = userDisplayName,
                                    upcomingEvents = homeCards.filter { it.isUpcoming },
                                    pastEvents = homeCards.filter { !it.isUpcoming },
                                    onCreateEventClick = { currentRoute = AppRoute.CreateEvent },
                                    onEventClick = { id -> currentRoute = AppRoute.EventDetail(id) },
                                    onHistoryClick = { currentRoute = AppRoute.History },
                                    onStatsClick = { currentRoute = AppRoute.Stats },
                                    isDarkTheme = isDarkTheme,
                                    onToggleTheme = { isDarkTheme = !isDarkTheme },
                                    onSignOut = {
                                        authViewModel.signOut()
                                        currentRoute = AppRoute.Login
                                    }
                                )

                                AppRoute.CreateEvent -> CreateEventScreen(
                                    modifier = mod,
                                    topBarTitle = "Crear evento",
                                    prefill = null,
                                    isSaving = saving,
                                    onBackClick = { currentRoute = AppRoute.Home },
                                    onSubmit = { title, desc, loc, date, h, m ->
                                        eventsViewModel.createEvent(
                                            title = title,
                                            description = desc,
                                            location = loc,
                                            dateMillis = date,
                                            timeHour = h,
                                            timeMinute = m,
                                            onSuccess = {
                                                showSnackbar("Evento creado")
                                                currentRoute = AppRoute.Home
                                            }
                                        )
                                    },
                                    onValidationError = showSnackbar
                                )

                                is AppRoute.EditEvent -> {
                                    val prefill by eventsViewModel.observeEvent(route.eventId)
                                        .collectAsStateWithLifecycle(initialValue = null)
                                    CreateEventScreen(
                                        modifier = mod,
                                        topBarTitle = "Editar evento",
                                        prefill = prefill,
                                        isSaving = saving,
                                        onBackClick = { currentRoute = AppRoute.EventDetail(route.eventId) },
                                        onSubmit = { title, desc, loc, date, h, m ->
                                            eventsViewModel.updateEvent(
                                                eventId = route.eventId,
                                                title = title,
                                                description = desc,
                                                location = loc,
                                                dateMillis = date,
                                                timeHour = h,
                                                timeMinute = m,
                                                onSuccess = {
                                                    showSnackbar("Cambios guardados")
                                                    currentRoute = AppRoute.EventDetail(route.eventId)
                                                }
                                            )
                                        },
                                        onValidationError = showSnackbar
                                    )
                                }

                                is AppRoute.EventDetail -> {
                                    val uid = user?.uid.orEmpty()
                                    val detail by eventsViewModel.observeEvent(route.eventId)
                                        .collectAsStateWithLifecycle(initialValue = null)
                                    val participantCount by eventsViewModel.observeParticipantCount(route.eventId)
                                        .collectAsStateWithLifecycle(initialValue = 0)
                                    val participating by eventsViewModel.observeIsParticipating(route.eventId, uid)
                                        .collectAsStateWithLifecycle(initialValue = false)
                                    val isOwner = uid.isNotEmpty() && detail?.createdBy == uid
                                    EventDetailScreen(
                                        modifier = mod,
                                        event = detail,
                                        participantCount = participantCount,
                                        isParticipating = participating,
                                        isSignedIn = uid.isNotEmpty(),
                                        isOwner = isOwner,
                                        actionBusy = actionBusy,
                                        onBackClick = { currentRoute = AppRoute.Home },
                                        onEditClick = { currentRoute = AppRoute.EditEvent(route.eventId) },
                                        onDeleteClick = {
                                            eventsViewModel.deleteEvent(route.eventId) {
                                                showSnackbar("Evento eliminado")
                                                currentRoute = AppRoute.Home
                                            }
                                        },
                                        onToggleParticipation = {
                                            if (uid.isEmpty()) {
                                                showSnackbar("Inicia sesión para participar")
                                            } else {
                                                eventsViewModel.setParticipating(
                                                    route.eventId,
                                                    participating = !participating
                                                )
                                            }
                                        },
                                        onCommentClick = {
                                            if (!participating) {
                                                showSnackbar("Debes marcar tu participación para comentar")
                                            } else {
                                                currentRoute = AppRoute.Comments(
                                                    route.eventId,
                                                    detail?.title.orEmpty()
                                                )
                                            }
                                        }
                                    )
                                }

                                is AppRoute.Comments -> {
                                    val uid = user?.uid.orEmpty()
                                    val participating by eventsViewModel.observeIsParticipating(route.eventId, uid)
                                        .collectAsStateWithLifecycle(initialValue = false)
                                    val reviews by eventsViewModel.observeReviews(route.eventId)
                                        .collectAsStateWithLifecycle(initialValue = emptyList())
                                    CommentsScreen(
                                        modifier = mod,
                                        eventTitle = route.eventTitle,
                                        hasParticipated = participating,
                                        reviews = reviews,
                                        isPublishing = actionBusy,
                                        onBackClick = { currentRoute = AppRoute.EventDetail(route.eventId) },
                                        onPublish = { text, rating, onDone ->
                                            eventsViewModel.publishReview(
                                                eventId = route.eventId,
                                                text = text,
                                                rating = rating,
                                                onSuccess = {
                                                    showSnackbar("Comentario publicado")
                                                    onDone()
                                                }
                                            )
                                        },
                                        onValidationError = showSnackbar
                                    )
                                }

                                AppRoute.History -> HistoryScreen(
                                    modifier = mod,
                                    onBackClick = { currentRoute = AppRoute.Home }
                                )

                                AppRoute.Stats -> StatsScreen(
                                    modifier = mod,
                                    onBackClick = { currentRoute = AppRoute.Home }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
