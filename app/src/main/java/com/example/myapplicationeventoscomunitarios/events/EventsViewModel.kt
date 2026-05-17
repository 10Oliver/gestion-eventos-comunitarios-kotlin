@file:OptIn(kotlinx.coroutines.FlowPreview::class)

package com.example.myapplicationeventoscomunitarios.events

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplicationeventoscomunitarios.BuildConfig
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.collectLatest
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

data class HomeEventCardUi(
    val id: String,
    val title: String,
    val dateLabel: String,
    val participantsLabel: String?,
    val description: String,
    val isUpcoming: Boolean,
)

data class HistoryEventRowUi(
    val eventId: String,
    val title: String,
    val dateLabel: String,
    val statusLabel: String,
)

data class StatsUiState(
    val loading: Boolean = true,
    val totalEvents: Int = 0,
    val attendedEvents: Int = 0,
    val myReviewsCount: Int = 0,
    val myAverageRatingText: String = "—",
    val totalParticipantsRegistered: Int = 0,
    val topEventCard: HomeEventCardUi? = null,
)

class EventsViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = EventsRepository()

    private val auth: FirebaseAuth = FirebaseAuth.getInstance()

    private val _authUid = MutableStateFlow(auth.currentUser?.uid.orEmpty())
    private val authListener = FirebaseAuth.AuthStateListener { fa ->
        _authUid.value = fa.currentUser?.uid.orEmpty()
    }
    private val dateFormat = SimpleDateFormat("d 'de' MMMM yyyy", Locale("es", "ES"))
    private val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())

    private val _events = MutableStateFlow<List<Event>>(emptyList())
    private val _participatedIds = MutableStateFlow<Set<String>>(emptySet())
    private val _myReviewDocs = MutableStateFlow<List<DocumentSnapshot>>(emptyList())
    private val _statsUi = MutableStateFlow(StatsUiState())

    private val _saving = MutableStateFlow(false)
    val saving: StateFlow<Boolean> = _saving.asStateFlow()

    private val _actionBusy = MutableStateFlow(false)
    val actionBusy: StateFlow<Boolean> = _actionBusy.asStateFlow()

    private val _snackbar = MutableSharedFlow<String>(extraBufferCapacity = 1)
    val snackbar = _snackbar

    val events: StateFlow<List<Event>> = _events.asStateFlow()
    val statsUi: StateFlow<StatsUiState> = _statsUi.asStateFlow()

    val homeCards: StateFlow<List<HomeEventCardUi>> = _events
        .map { list -> list.map { it.toHomeCardUi() } }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    val historyUpcoming: StateFlow<List<HistoryEventRowUi>> = combine(_events, _participatedIds) { events, ids ->
        events
            .filter { it.id in ids && it.isUpcoming() }
            .sortedBy { it.startDateTimeMillis }
            .map { it.toHistoryRowUi(upcoming = true) }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    val historyPast: StateFlow<List<HistoryEventRowUi>> = combine(_events, _participatedIds) { events, ids ->
        events
            .filter { it.id in ids && !it.isUpcoming() }
            .sortedByDescending { it.startDateTimeMillis }
            .map { it.toHistoryRowUi(upcoming = false) }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    init {
        auth.addAuthStateListener(authListener)

        viewModelScope.launch {
            repository.observeEvents()
                .catch { e ->
                    if (BuildConfig.DEBUG) e.printStackTrace()
                    _snackbar.tryEmit("No se pudieron cargar los eventos: ${e.message ?: "error"}")
                    emit(emptyList())
                }
                .collect { _events.value = it }
        }

        viewModelScope.launch {
            combine(_events, _authUid) { events, uid -> events to uid }
                .debounce(350)
                .collectLatest { (events, uid) ->
                    if (uid.isEmpty()) {
                        _participatedIds.value = emptySet()
                        _myReviewDocs.value = emptyList()
                    } else {
                        val ids = events.map { it.id }
                        _participatedIds.value = repository.fetchParticipatedEventIdsForUser(uid, ids)
                        _myReviewDocs.value = repository.fetchMyReviewDocumentsForUser(uid, ids)
                    }
                }
        }

        viewModelScope.launch {
            combine(_events, _participatedIds, _myReviewDocs) { events, partIds, myReviewDocs ->
                Triple(events, partIds, myReviewDocs)
            }
                .debounce(400)
                .collectLatest { (events, partIds, myReviewDocs) ->
                    _statsUi.update { it.copy(loading = true) }
                    try {
                        val counts = if (events.isEmpty()) {
                            emptyMap()
                        } else {
                            repository.fetchParticipantCountsForEventIds(events.map { it.id })
                        }
                        val totalPart = counts.values.sum()
                        val topEntry = events.maxByOrNull { counts[it.id] ?: 0 }
                        val topCount = topEntry?.let { counts[it.id] ?: 0 } ?: 0
                        val ratings = myReviewDocs.mapNotNull { doc ->
                            doc.getLong("rating")?.toInt()?.takeIf { r -> r in 1..5 }
                        }
                        val avgText = if (ratings.isEmpty()) {
                            "—"
                        } else {
                            String.format(Locale.US, "%.1f/5", ratings.average())
                        }
                        val attended = partIds.size
                        val topCard = if (topCount > 0 && topEntry != null) {
                            val ev = topEntry
                            val datePart = dateFormat.format(Date(ev.dateMillis))
                            val timePart = timeFormat.format(
                                Date(computeStartDateTimeMillis(ev.dateMillis, ev.timeHour, ev.timeMinute))
                            )
                            HomeEventCardUi(
                                id = ev.id,
                                title = ev.title,
                                dateLabel = "$datePart · $timePart",
                                participantsLabel = topCount.toString(),
                                description = ev.description,
                                isUpcoming = ev.isUpcoming(),
                            )
                        } else {
                            null
                        }
                        _statsUi.value = StatsUiState(
                            loading = false,
                            totalEvents = events.size,
                            attendedEvents = attended,
                            myReviewsCount = myReviewDocs.size,
                            myAverageRatingText = avgText,
                            totalParticipantsRegistered = totalPart,
                            topEventCard = topCard,
                        )
                    } catch (e: Exception) {
                        if (BuildConfig.DEBUG) e.printStackTrace()
                        _statsUi.update {
                            it.copy(
                                loading = false,
                            )
                        }
                        _snackbar.tryEmit("No se pudieron calcular las estadísticas: ${e.message ?: "error"}")
                    }
                }
        }
    }

    override fun onCleared() {
        auth.removeAuthStateListener(authListener)
        super.onCleared()
    }

    private suspend fun refreshUserEngagementFromFirestore() {
        val uid = auth.currentUser?.uid.orEmpty()
        if (uid.isEmpty()) {
            _participatedIds.value = emptySet()
            _myReviewDocs.value = emptyList()
            return
        }
        val eventIds = _events.value.map { it.id }
        _participatedIds.value = repository.fetchParticipatedEventIdsForUser(uid, eventIds)
        _myReviewDocs.value = repository.fetchMyReviewDocumentsForUser(uid, eventIds)
    }

    private fun Event.toHomeCardUi(): HomeEventCardUi {
        val datePart = dateFormat.format(Date(dateMillis))
        val timePart = timeFormat.format(
            Date(computeStartDateTimeMillis(dateMillis, timeHour, timeMinute))
        )
        return HomeEventCardUi(
            id = id,
            title = title,
            dateLabel = "$datePart · $timePart",
            participantsLabel = null,
            description = description,
            isUpcoming = isUpcoming(),
        )
    }

    private fun Event.toHistoryRowUi(upcoming: Boolean): HistoryEventRowUi {
        val datePart = dateFormat.format(Date(dateMillis))
        val timePart = timeFormat.format(
            Date(computeStartDateTimeMillis(dateMillis, timeHour, timeMinute))
        )
        return HistoryEventRowUi(
            eventId = id,
            title = title,
            dateLabel = "$datePart · $timePart",
            statusLabel = if (upcoming) "Asistencia confirmada" else "Evento finalizado",
        )
    }

    fun observeEvent(eventId: String) = repository.observeEvent(eventId)

    fun observeParticipantCount(eventId: String) = repository.observeParticipantCount(eventId)

    fun observeIsParticipating(eventId: String, uid: String) =
        repository.observeIsParticipating(eventId, uid)

    fun observeReviews(eventId: String) = repository.observeReviews(eventId)

    fun createEvent(
        title: String,
        description: String,
        location: String,
        dateMillis: Long,
        timeHour: Int,
        timeMinute: Int,
        onSuccess: (String) -> Unit,
    ) {
        val uid = auth.currentUser?.uid ?: run {
            viewModelScope.launch { _snackbar.emit("Inicia sesión para crear un evento") }
            return
        }
        viewModelScope.launch {
            _saving.value = true
            try {
                val id = repository.createEvent(
                    title = title,
                    description = description,
                    location = location,
                    dateMillis = dateMillis,
                    timeHour = timeHour,
                    timeMinute = timeMinute,
                    createdBy = uid,
                )
                onSuccess(id)
            } catch (e: Exception) {
                if (BuildConfig.DEBUG) e.printStackTrace()
                _snackbar.emit("No se pudo guardar: ${e.message ?: "error"}")
            } finally {
                _saving.value = false
            }
        }
    }

    fun updateEvent(
        eventId: String,
        title: String,
        description: String,
        location: String,
        dateMillis: Long,
        timeHour: Int,
        timeMinute: Int,
        onSuccess: () -> Unit,
    ) {
        viewModelScope.launch {
            _saving.value = true
            try {
                repository.updateEvent(
                    eventId = eventId,
                    title = title,
                    description = description,
                    location = location,
                    dateMillis = dateMillis,
                    timeHour = timeHour,
                    timeMinute = timeMinute,
                )
                onSuccess()
            } catch (e: Exception) {
                if (BuildConfig.DEBUG) e.printStackTrace()
                _snackbar.emit("No se pudo actualizar: ${e.message ?: "error"}")
            } finally {
                _saving.value = false
            }
        }
    }

    fun deleteEvent(eventId: String, onSuccess: () -> Unit) {
        viewModelScope.launch {
            _actionBusy.value = true
            try {
                repository.deleteEvent(eventId)
                onSuccess()
            } catch (e: Exception) {
                if (BuildConfig.DEBUG) e.printStackTrace()
                _snackbar.emit("No se pudo eliminar: ${e.message ?: "error"}")
            } finally {
                _actionBusy.value = false
            }
        }
    }

    fun setParticipating(eventId: String, participating: Boolean, onDone: () -> Unit = {}) {
        val uid = auth.currentUser?.uid ?: return
        viewModelScope.launch {
            _actionBusy.value = true
            try {
                repository.setParticipating(eventId, uid, participating)
                onDone()
                refreshUserEngagementFromFirestore()
            } catch (e: Exception) {
                if (BuildConfig.DEBUG) e.printStackTrace()
                _snackbar.emit("No se pudo actualizar la participación: ${e.message ?: "error"}")
            } finally {
                _actionBusy.value = false
            }
        }
    }

    fun publishReview(
        eventId: String,
        text: String,
        rating: Int,
        onSuccess: () -> Unit,
    ) {
        val user = auth.currentUser ?: run {
            viewModelScope.launch { _snackbar.emit("Inicia sesión para comentar") }
            return
        }
        val name = user.displayName?.takeIf { it.isNotBlank() }
            ?: user.email?.substringBefore("@").orEmpty()
                .ifBlank { "Usuario" }
        viewModelScope.launch {
            _actionBusy.value = true
            try {
                repository.upsertReview(
                    eventId = eventId,
                    uid = user.uid,
                    authorName = name,
                    text = text,
                    rating = rating,
                )
                onSuccess()
                refreshUserEngagementFromFirestore()
            } catch (e: Exception) {
                if (BuildConfig.DEBUG) e.printStackTrace()
                _snackbar.emit("No se pudo publicar: ${e.message ?: "error"}")
            } finally {
                _actionBusy.value = false
            }
        }
    }

    fun deleteReview(eventId: String, onSuccess: () -> Unit) {
        val uid = auth.currentUser?.uid ?: return
        viewModelScope.launch {
            _actionBusy.value = true
            try {
                repository.deleteReview(eventId, uid)
                onSuccess()
                refreshUserEngagementFromFirestore()
            } catch (e: Exception) {
                if (BuildConfig.DEBUG) e.printStackTrace()
                _snackbar.emit("No se pudo eliminar el comentario: ${e.message ?: "error"}")
            } finally {
                _actionBusy.value = false
            }
        }
    }
}
