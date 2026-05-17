package com.example.myapplicationeventoscomunitarios.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.Send
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.myapplicationeventoscomunitarios.components.AppTextField
import com.example.myapplicationeventoscomunitarios.components.AppTopBar
import com.example.myapplicationeventoscomunitarios.components.MainButton
import com.example.myapplicationeventoscomunitarios.components.StarRating
import com.example.myapplicationeventoscomunitarios.components.StarRatingReadOnly
import com.example.myapplicationeventoscomunitarios.events.EventReview
import com.example.myapplicationeventoscomunitarios.ui.theme.AppTheme
import com.example.myapplicationeventoscomunitarios.ui.theme.Spacing

@Composable
fun CommentsScreen(
    modifier: Modifier = Modifier,
    eventTitle: String,
    hasParticipated: Boolean,
    reviews: List<EventReview>,
    isPublishing: Boolean,
    onBackClick: () -> Unit = {},
    onPublish: (text: String, rating: Int, onSuccess: () -> Unit) -> Unit,
    onValidationError: (String) -> Unit = {},
) {
    var comment by remember { mutableStateOf("") }
    var rating by remember { mutableIntStateOf(5) }
    val colors = AppTheme.colors
    val topTitle = eventTitle.ifBlank { "Comentarios" }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = colors.background,
        topBar = {
            AppTopBar(title = topTitle, onBackClick = onBackClick)
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = Spacing.xxl)
        ) {
            Spacer(Modifier.height(Spacing.lg))

            Text(
                text = "Califica el evento y deja tu opinión",
                style = MaterialTheme.typography.bodyLarge,
                color = colors.textSecondary
            )

            Spacer(Modifier.height(Spacing.xxxl))

            Text(
                text = "Calificación del evento",
                style = MaterialTheme.typography.titleSmall,
                color = colors.textPrimary
            )

            Spacer(Modifier.height(Spacing.md))

            StarRating(
                rating = rating,
                onRatingChange = { rating = it },
                enabled = hasParticipated && !isPublishing
            )

            Spacer(Modifier.height(Spacing.xxl))

            AppTextField(
                value = comment,
                onValueChange = { comment = it },
                label = "Escribe tu comentario",
                modifier = Modifier.height(130.dp),
                singleLine = false,
                enabled = hasParticipated && !isPublishing
            )

            Spacer(Modifier.height(Spacing.xxl))

            MainButton(
                text = "Publicar comentario",
                onClick = {
                    if (!hasParticipated) {
                        onValidationError("Solo pueden comentar quienes marcaron participación")
                        return@MainButton
                    }
                    if (comment.isBlank()) {
                        onValidationError("Escribe un comentario")
                        return@MainButton
                    }
                    onPublish(comment.trim(), rating) {
                        comment = ""
                        rating = 5
                    }
                },
                leadingIcon = Icons.AutoMirrored.Outlined.Send,
                enabled = hasParticipated && !isPublishing
            )

            if (!hasParticipated) {
                Spacer(Modifier.height(Spacing.md))
                Text(
                    text = "Debes haber marcado tu participación en el detalle del evento para publicar aquí.",
                    style = MaterialTheme.typography.bodySmall,
                    color = colors.textMuted,
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center
                )
            }

            Spacer(Modifier.height(Spacing.xxxl))

            Text(
                text = "Comentarios recientes",
                style = MaterialTheme.typography.titleMedium,
                color = colors.textPrimary
            )

            Spacer(Modifier.height(Spacing.md))

            if (reviews.isEmpty()) {
                Text(
                    text = "Aún no hay comentarios.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = colors.textSecondary
                )
            } else {
                reviews.forEach { c ->
                    CommentItem(user = c.authorName, rating = c.rating, text = c.text)
                    Spacer(Modifier.height(Spacing.md))
                }
            }

            Spacer(Modifier.height(Spacing.xxl))
        }
    }
}

@Composable
private fun CommentItem(
    user: String,
    rating: Int,
    text: String
) {
    val colors = AppTheme.colors
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = colors.surfaceVariant)
    ) {
        Column(modifier = Modifier.padding(Spacing.lg)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = user,
                    style = MaterialTheme.typography.titleSmall,
                    color = colors.textPrimary
                )

                StarRatingReadOnly(rating = rating)
            }

            Spacer(Modifier.height(Spacing.sm))

            Text(
                text = text,
                style = MaterialTheme.typography.bodyMedium,
                color = colors.textSecondary
            )
        }
    }
}
