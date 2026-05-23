package com.example.myapplicationeventoscomunitarios.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material.icons.outlined.ChatBubbleOutline
import androidx.compose.material.icons.outlined.EmojiEvents
import androidx.compose.material.icons.outlined.Groups
import androidx.compose.material.icons.outlined.HowToReg
import androidx.compose.material.icons.outlined.StarRate
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.example.myapplicationeventoscomunitarios.components.AppTopBar
import com.example.myapplicationeventoscomunitarios.components.SectionTitle
import com.example.myapplicationeventoscomunitarios.events.StatsUiState
import com.example.myapplicationeventoscomunitarios.ui.theme.AppTheme
import com.example.myapplicationeventoscomunitarios.ui.theme.Spacing

@Composable
fun StatsScreen(
    modifier: Modifier = Modifier,
    stats: StatsUiState,
    onBackClick: () -> Unit = {},
    onTopEventClick: (String) -> Unit = {},
) {
    val colors = AppTheme.colors
    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = colors.background,
        topBar = { AppTopBar(title = "Estadísticas", onBackClick = onBackClick) }
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
                text = "Resumen general de participación",
                style = MaterialTheme.typography.bodyLarge,
                color = colors.textSecondary
            )

            Spacer(Modifier.height(Spacing.xxl))

            if (stats.loading) {
                Row(
                    Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    CircularProgressIndicator(color = colors.primary)
                }
                Spacer(Modifier.height(Spacing.xxl))
            }

            StatCard(
                icon = Icons.Outlined.CalendarMonth,
                title = "Total de eventos",
                value = stats.totalEvents.toString()
            )
            Spacer(Modifier.height(Spacing.md))
            StatCard(
                icon = Icons.Outlined.HowToReg,
                title = "Eventos asistidos",
                value = stats.attendedEvents.toString()
            )
            Spacer(Modifier.height(Spacing.md))
            StatCard(
                icon = Icons.Outlined.ChatBubbleOutline,
                title = "Reseñas publicadas (tuyas)",
                value = stats.myReviewsCount.toString()
            )
            Spacer(Modifier.height(Spacing.md))
            StatCard(
                icon = Icons.Outlined.StarRate,
                title = "Promedio de tus calificaciones",
                value = stats.myAverageRatingText
            )
            Spacer(Modifier.height(Spacing.md))
            StatCard(
                icon = Icons.Outlined.Groups,
                title = "Participantes registrados (total)",
                value = stats.totalParticipantsRegistered.toString()
            )

            Spacer(Modifier.height(Spacing.xxxl))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Outlined.EmojiEvents,
                    contentDescription = null,
                    tint = colors.primary,
                    modifier = Modifier.size(22.dp)
                )
                Spacer(Modifier.width(Spacing.sm))
                SectionTitle("Evento con más participación")
            }

            Spacer(Modifier.height(Spacing.md))

            val top = stats.topEventCard
            if (top == null) {
                Text(
                    text = if (stats.totalEvents == 0) {
                        "Aún no hay eventos en la comunidad."
                    } else {
                        "No hay datos de participación para comparar."
                    },
                    style = MaterialTheme.typography.bodyMedium,
                    color = colors.textMuted
                )
            } else {
                EventCard(
                    title = top.title,
                    date = top.dateLabel,
                    participants = top.participantsLabel,
                    description = top.description,
                    onClick = { onTopEventClick(top.id) }
                )
            }

            Spacer(Modifier.height(Spacing.xxl))
        }
    }
}

@Composable
private fun StatCard(
    icon: ImageVector,
    title: String,
    value: String
) {
    val colors = AppTheme.colors
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = colors.primary)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(Spacing.lg),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                modifier = Modifier.weight(1f),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = colors.onPrimary,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(Modifier.width(Spacing.md))
                Text(
                    text = title,
                    style = MaterialTheme.typography.bodyLarge,
                    color = colors.onPrimary
                )
            }

            Text(
                text = value,
                style = MaterialTheme.typography.titleLarge,
                color = colors.onPrimary
            )
        }
    }
}
