package com.example.myapplicationeventoscomunitarios.screens

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
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.EventAvailable
import androidx.compose.material.icons.outlined.HistoryToggleOff
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import com.example.myapplicationeventoscomunitarios.events.HistoryEventRowUi
import com.example.myapplicationeventoscomunitarios.ui.theme.AppTheme
import com.example.myapplicationeventoscomunitarios.ui.theme.Spacing

@Composable
fun HistoryScreen(
    modifier: Modifier = Modifier,
    upcomingAttendances: List<HistoryEventRowUi> = emptyList(),
    pastAttendances: List<HistoryEventRowUi> = emptyList(),
    isSignedIn: Boolean = false,
    onBackClick: () -> Unit = {},
    onEventClick: (String) -> Unit = {},
) {
    val colors = AppTheme.colors
    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = colors.background,
        topBar = { AppTopBar(title = "Historial", onBackClick = onBackClick) }
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
                text = "Eventos en los que marcaste participación",
                style = MaterialTheme.typography.bodyLarge,
                color = colors.textSecondary
            )

            Spacer(Modifier.height(Spacing.xxl))

            SectionTitle("Eventos próximos")

            Spacer(Modifier.height(Spacing.md))

            if (!isSignedIn) {
                Text(
                    text = "Inicia sesión para ver tu historial.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = colors.textMuted
                )
            } else if (upcomingAttendances.isEmpty()) {
                Text(
                    text = "No tienes eventos próximos con asistencia marcada.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = colors.textMuted
                )
            } else {
                upcomingAttendances.forEach { item ->
                    HistoryItem(
                        title = item.title,
                        date = item.dateLabel,
                        status = item.statusLabel,
                        statusIcon = Icons.Outlined.CheckCircle,
                        onClick = { onEventClick(item.eventId) }
                    )
                    Spacer(Modifier.height(Spacing.md))
                }
            }

            Spacer(Modifier.height(Spacing.lg))

            SectionTitle("Asistencias pasadas")

            Spacer(Modifier.height(Spacing.lg))

            if (!isSignedIn) {
                Spacer(Modifier.height(Spacing.xs))
            } else if (pastAttendances.isEmpty()) {
                Text(
                    text = "No hay asistencias pasadas registradas.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = colors.textMuted
                )
            } else {
                pastAttendances.forEach { item ->
                    HistoryItem(
                        title = item.title,
                        date = item.dateLabel,
                        status = item.statusLabel,
                        statusIcon = Icons.Outlined.HistoryToggleOff,
                        onClick = { onEventClick(item.eventId) }
                    )
                    Spacer(Modifier.height(Spacing.md))
                }
            }

            Spacer(Modifier.height(Spacing.xxl))
        }
    }
}

@Composable
private fun HistoryItem(
    title: String,
    date: String,
    status: String,
    statusIcon: ImageVector,
    onClick: () -> Unit,
) {
    val colors = AppTheme.colors
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = colors.surfaceVariant)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(Spacing.lg),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Outlined.EventAvailable,
                contentDescription = null,
                tint = colors.primary,
                modifier = Modifier.size(32.dp)
            )

            Spacer(Modifier.width(Spacing.lg))

            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleSmall,
                    color = colors.textPrimary
                )

                Spacer(Modifier.height(Spacing.xs))

                Text(
                    text = date,
                    style = MaterialTheme.typography.bodyMedium,
                    color = colors.textSecondary
                )

                Spacer(Modifier.height(Spacing.sm))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = statusIcon,
                        contentDescription = null,
                        tint = colors.primary,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(Modifier.width(Spacing.xs))
                    Text(
                        text = status,
                        style = MaterialTheme.typography.labelMedium,
                        color = colors.primary
                    )
                }
            }
        }
    }
}
