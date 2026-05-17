package com.example.myapplicationeventoscomunitarios.events

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplicationeventoscomunitarios.BuildConfig
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
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

class EventsViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = EventsRepository()

    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val dateFormat = SimpleDateFormat("d 'de' MMMM yyyy", Locale("es", "ES"))
    private val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())

    private val _events = MutableStateFlow<List<Event>>(emptyList())
    private val _saving = MutableStateFlow(false)
    val saving: StateFlow<Boolean> = _saving.asStateFlow()

    private val _actionBusy = MutableStateFlow(false)
    val actionBusy: StateFlow<Boolean> = _actionBusy.asStateFlow()

    private val _snackbar = MutableSharedFlow<String>(extraBufferCapacity = 1)
    val snackbar = _snackbar

    val events: StateFlow<List<Event>> = _events.asStateFlow()

    val homeCards: StateFlow<List<HomeEventCardUi>> = _events
        .map { list -> list.map { it.toHomeCardUi() } }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    init {
        viewModelScope.launch {
            repository.observeEvents()
                .catch { e ->
                    if (BuildConfig.DEBUG) e.printStackTrace()
                    _snackbar.tryEmit("No se pudieron cargar los eventos: ${e.message ?: "error"}")
                    emit(emptyList())
                }
                .collect { _events.value = it }
        }
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
            } catch (e: Exception) {
                if (BuildConfig.DEBUG) e.printStackTrace()
                _snackbar.emit("No se pudo publicar: ${e.message ?: "error"}")
            } finally {
                _actionBusy.value = false
            }
        }
    }
}
