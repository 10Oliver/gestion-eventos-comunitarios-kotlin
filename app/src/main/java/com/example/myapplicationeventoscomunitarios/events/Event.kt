package com.example.myapplicationeventoscomunitarios.events

import com.google.firebase.firestore.DocumentSnapshot
import java.util.Calendar
import java.util.Locale

data class Event(
    val id: String,
    val title: String,
    val description: String,
    val location: String,
    val dateMillis: Long,
    val timeHour: Int,
    val timeMinute: Int,
    val startDateTimeMillis: Long,
    val createdBy: String,
    val createdAtMillis: Long?,
) {
    fun isUpcoming(): Boolean = startDateTimeMillis >= System.currentTimeMillis()
}

data class EventReview(
    val uid: String,
    val authorName: String,
    val text: String,
    val rating: Int,
    val updatedAtMillis: Long?,
)

fun computeStartDateTimeMillis(dateMillis: Long, timeHour: Int, timeMinute: Int): Long {
    val cal = Calendar.getInstance(Locale.getDefault()).apply {
        timeInMillis = dateMillis
        set(Calendar.HOUR_OF_DAY, timeHour)
        set(Calendar.MINUTE, timeMinute)
        set(Calendar.SECOND, 0)
        set(Calendar.MILLISECOND, 0)
    }
    return cal.timeInMillis
}

fun DocumentSnapshot.toEvent(): Event? {
    if (!exists()) return null
    val title = getString("title") ?: return null
    val description = getString("description") ?: ""
    val location = getString("location") ?: ""
    val dateMillis = getLong("dateMillis") ?: return null
    val timeHour = (getLong("timeHour") ?: 0L).toInt()
    val timeMinute = (getLong("timeMinute") ?: 0L).toInt()
    val createdBy = getString("createdBy") ?: return null
    val start = getLong("startDateTimeMillis")
        ?: computeStartDateTimeMillis(dateMillis, timeHour, timeMinute)
    val createdAt = getTimestamp("createdAt")
    return Event(
        id = id,
        title = title,
        description = description,
        location = location,
        dateMillis = dateMillis,
        timeHour = timeHour,
        timeMinute = timeMinute,
        startDateTimeMillis = start,
        createdBy = createdBy,
        createdAtMillis = createdAt?.toDate()?.time,
    )
}

fun DocumentSnapshot.toEventReview(): EventReview? {
    if (!exists()) return null
    return EventReview(
        uid = id,
        authorName = getString("authorName") ?: "Usuario",
        text = getString("text") ?: "",
        rating = (getLong("rating") ?: 0L).toInt().coerceIn(0, 5),
        updatedAtMillis = getTimestamp("updatedAt")?.toDate()?.time,
    )
}

fun eventPayload(
    title: String,
    description: String,
    location: String,
    dateMillis: Long,
    timeHour: Int,
    timeMinute: Int,
    createdBy: String,
    createdAt: Any,
): HashMap<String, Any> {
    val start = computeStartDateTimeMillis(dateMillis, timeHour, timeMinute)
    return hashMapOf(
        "title" to title,
        "description" to description,
        "location" to location,
        "dateMillis" to dateMillis,
        "timeHour" to timeHour.toLong(),
        "timeMinute" to timeMinute.toLong(),
        "startDateTimeMillis" to start,
        "createdBy" to createdBy,
        "createdAt" to createdAt,
    )
}
