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
import androidx.compose.ui.unit.dp
import com.example.myapplicationeventoscomunitarios.components.AppTextField
import com.example.myapplicationeventoscomunitarios.components.AppTopBar
import com.example.myapplicationeventoscomunitarios.components.MainButton
import com.example.myapplicationeventoscomunitarios.components.StarRating
import com.example.myapplicationeventoscomunitarios.components.StarRatingReadOnly
import com.example.myapplicationeventoscomunitarios.ui.theme.AppTheme
import com.example.myapplicationeventoscomunitarios.ui.theme.Spacing

private data class CommentUi(val user: String, val rating: Int, val text: String)

private val sampleComments = listOf(
    CommentUi("Usuario 1", 5, "Muy buena actividad, estuvo bien organizada."),
    CommentUi("Usuario 2", 4, "Me gustó el evento, sería bueno agregar más horarios.")
)

@Composable
fun CommentsScreen(
    modifier: Modifier = Modifier,
    onBackClick: () -> Unit = {},
    onCommentSaved: () -> Unit = {}
) {
    var comment by remember { mutableStateOf("") }
    var rating by remember { mutableIntStateOf(5) }
    val colors = AppTheme.colors

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = colors.background,
        topBar = {
            AppTopBar(title = "Comentarios", onBackClick = onBackClick)
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
                onRatingChange = { rating = it }
            )

            Spacer(Modifier.height(Spacing.xxl))

            AppTextField(
                value = comment,
                onValueChange = { comment = it },
                label = "Escribe tu comentario",
                modifier = Modifier.height(130.dp),
                singleLine = false
            )

            Spacer(Modifier.height(Spacing.xxl))

            MainButton(
                text = "Publicar comentario",
                onClick = {
                    onCommentSaved()
                    comment = ""
                },
                leadingIcon = Icons.AutoMirrored.Outlined.Send
            )

            Spacer(Modifier.height(Spacing.xxxl))

            Text(
                text = "Comentarios recientes",
                style = MaterialTheme.typography.titleMedium,
                color = colors.textPrimary
            )

            Spacer(Modifier.height(Spacing.md))

            sampleComments.forEach { c ->
                CommentItem(user = c.user, rating = c.rating, text = c.text)
                Spacer(Modifier.height(Spacing.md))
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
