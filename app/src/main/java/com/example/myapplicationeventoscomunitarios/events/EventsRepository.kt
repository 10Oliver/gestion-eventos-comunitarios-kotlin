package com.example.myapplicationeventoscomunitarios.events

import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.SetOptions
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.tasks.await

class EventsRepository(
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance(),
) {

    private fun eventsCollection() = db.collection(COLLECTION_EVENTS)

    fun observeEvents(): Flow<List<Event>> = callbackFlow {
        val registration = eventsCollection()
            .orderBy("startDateTimeMillis", Query.Direction.ASCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    trySend(emptyList())
                    return@addSnapshotListener
                }
                val list = snapshot?.documents?.mapNotNull { it.toEvent() }.orEmpty()
                trySend(list)
            }
        awaitClose { registration.remove() }
    }

    fun observeEvent(eventId: String): Flow<Event?> = callbackFlow {
        val reg = eventsCollection().document(eventId)
            .addSnapshotListener { snap, _ ->
                trySend(if (snap != null && snap.exists()) snap.toEvent() else null)
            }
        awaitClose { reg.remove() }
    }

    fun observeParticipantCount(eventId: String): Flow<Int> = callbackFlow {
        val reg = eventsCollection().document(eventId).collection(COLLECTION_PARTICIPANTS)
            .addSnapshotListener { snap, _ ->
                trySend(snap?.size() ?: 0)
            }
        awaitClose { reg.remove() }
    }

    fun observeIsParticipating(eventId: String, uid: String): Flow<Boolean> {
        if (uid.isEmpty()) return flowOf(false)
        return callbackFlow {
            val reg = eventsCollection().document(eventId).collection(COLLECTION_PARTICIPANTS).document(uid)
                .addSnapshotListener { snap, _ ->
                    trySend(snap?.exists() == true)
                }
            awaitClose { reg.remove() }
        }
    }

    fun observeReviews(eventId: String): Flow<List<EventReview>> = callbackFlow {
        val reg = eventsCollection().document(eventId).collection(COLLECTION_REVIEWS)
            .orderBy("updatedAt", Query.Direction.DESCENDING)
            .addSnapshotListener { snap, _ ->
                val list = snap?.documents?.mapNotNull { it.toEventReview() }.orEmpty()
                trySend(list)
            }
        awaitClose { reg.remove() }
    }

    /**
     * IDs of events in [eventIds] where `participants/{uid}` exists (works with legacy docs without [FIELD_PARTICIPANT_UID]).
     */
    suspend fun fetchParticipatedEventIdsForUser(uid: String, eventIds: List<String>): Set<String> = coroutineScope {
        if (uid.isEmpty()) return@coroutineScope emptySet()
        eventIds.distinct().chunked(PARTICIPANT_COUNT_CONCURRENCY).flatMap { chunk ->
            chunk.map { eventId ->
                async {
                    val snap = eventsCollection().document(eventId).collection(COLLECTION_PARTICIPANTS).document(uid).get().await()
                    if (snap.exists()) eventId else null
                }
            }.awaitAll().filterNotNull()
        }.toSet()
    }

    /**
     * Review docs `reviews/{uid}` for known [eventIds] (legacy-safe).
     */
    suspend fun fetchMyReviewDocumentsForUser(uid: String, eventIds: List<String>): List<DocumentSnapshot> = coroutineScope {
        if (uid.isEmpty()) return@coroutineScope emptyList()
        eventIds.distinct().chunked(PARTICIPANT_COUNT_CONCURRENCY).flatMap { chunk ->
            chunk.map { eventId ->
                async {
                    eventsCollection().document(eventId).collection(COLLECTION_REVIEWS).document(uid).get().await()
                }
            }.awaitAll().filter { it.exists() }
        }
    }

    suspend fun fetchParticipantCountsForEventIds(eventIds: List<String>): Map<String, Int> = coroutineScope {
        val distinct = eventIds.distinct()
        val pairs = mutableListOf<Pair<String, Int>>()
        for (chunk in distinct.chunked(PARTICIPANT_COUNT_CONCURRENCY)) {
            pairs += chunk.map { id ->
                async {
                    val size = eventsCollection().document(id).collection(COLLECTION_PARTICIPANTS).get().await().documents.size
                    id to size
                }
            }.awaitAll()
        }
        pairs.toMap()
    }

    suspend fun deleteReview(eventId: String, uid: String) {
        eventsCollection().document(eventId).collection(COLLECTION_REVIEWS).document(uid).delete().await()
    }

    suspend fun createEvent(
        title: String,
        description: String,
        location: String,
        dateMillis: Long,
        timeHour: Int,
        timeMinute: Int,
        createdBy: String,
    ): String {
        val doc = eventsCollection().document()
        val data = eventPayload(
            title = title.trim(),
            description = description.trim(),
            location = location.trim(),
            dateMillis = dateMillis,
            timeHour = timeHour,
            timeMinute = timeMinute,
            createdBy = createdBy,
            createdAt = FieldValue.serverTimestamp(),
        )
        doc.set(data).await()
        return doc.id
    }

    suspend fun updateEvent(
        eventId: String,
        title: String,
        description: String,
        location: String,
        dateMillis: Long,
        timeHour: Int,
        timeMinute: Int,
    ) {
        val data = hashMapOf<String, Any>(
            "title" to title.trim(),
            "description" to description.trim(),
            "location" to location.trim(),
            "dateMillis" to dateMillis,
            "timeHour" to timeHour.toLong(),
            "timeMinute" to timeMinute.toLong(),
            "startDateTimeMillis" to computeStartDateTimeMillis(dateMillis, timeHour, timeMinute),
        )
        eventsCollection().document(eventId).update(data).await()
    }

    suspend fun setParticipating(eventId: String, uid: String, participating: Boolean) {
        val ref = eventsCollection().document(eventId).collection(COLLECTION_PARTICIPANTS).document(uid)
        if (participating) {
            ref.set(
                hashMapOf(
                    FIELD_PARTICIPANT_UID to uid,
                    "joinedAt" to FieldValue.serverTimestamp(),
                ),
            ).await()
        } else {
            ref.delete().await()
            val review = eventsCollection().document(eventId).collection(COLLECTION_REVIEWS).document(uid)
            review.delete().await()
        }
    }

    suspend fun upsertReview(
        eventId: String,
        uid: String,
        authorName: String,
        text: String,
        rating: Int,
    ) {
        val data = hashMapOf(
            FIELD_REVIEWER_UID to uid,
            "text" to text.trim(),
            "rating" to rating.toLong(),
            "authorName" to authorName.trim(),
            "updatedAt" to FieldValue.serverTimestamp(),
        )
        eventsCollection().document(eventId).collection(COLLECTION_REVIEWS).document(uid)
            .set(data, SetOptions.merge()).await()
    }

    suspend fun deleteEvent(eventId: String) {
        val eventRef = eventsCollection().document(eventId)
        val participants = eventRef.collection(COLLECTION_PARTICIPANTS).get().await()
        val reviews = eventRef.collection(COLLECTION_REVIEWS).get().await()
        val allDocs = participants.documents + reviews.documents
        var batch = db.batch()
        var ops = 0
        for (d in allDocs) {
            batch.delete(d.reference)
            ops++
            if (ops >= MAX_BATCH_OPS) {
                batch.commit().await()
                batch = db.batch()
                ops = 0
            }
        }
        batch.delete(eventRef)
        batch.commit().await()
    }

    companion object {
        private const val COLLECTION_EVENTS = "events"
        private const val COLLECTION_PARTICIPANTS = "participants"
        private const val COLLECTION_REVIEWS = "reviews"
        private const val MAX_BATCH_OPS = 450
        private const val PARTICIPANT_COUNT_CONCURRENCY = 10

        /** For collectionGroup queries (documentId cannot be used with only uid). */
        internal const val FIELD_PARTICIPANT_UID = "participantUid"
        internal const val FIELD_REVIEWER_UID = "reviewerUid"
    }
}
