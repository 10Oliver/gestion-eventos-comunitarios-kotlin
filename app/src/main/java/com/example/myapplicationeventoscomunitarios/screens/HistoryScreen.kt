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
import androidx.compose.material.icons.outlined.CalendarMonth
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
import com.example.myapplicationeventoscomunitarios.ui.theme.AppTheme
import com.example.myapplicationeventoscomunitarios.ui.theme.Spacing

private data class HistoryUi(
    val title: String,
    val date: String,
    val status: String,
    val attended: Boolean
)

private val attendedEvents = listOf(
    HistoryUi("Jornada de limpieza", "20 de mayo de 2026", "Asistencia confirmada", true),
    HistoryUi("Reunión comunitaria", "18 de mayo de 2026", "Participaste", true)
)

private val pastEvents = listOf(
    HistoryUi("Campaña de donación", "12 de mayo de 2026", "Finalizado", false),
    HistoryUi("Torneo deportivo", "05 de mayo de 2026", "Finalizado", false)
)

@Composable
fun HistoryScreen(
    modifier: Modifier = Modifier,
    onBackClick: () -> Unit = {}
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
                text = "Eventos pasados y asistencias registradas",
                style = MaterialTheme.typography.bodyLarge,
                color = colors.textSecondary
            )

            Spacer(Modifier.height(Spacing.xxl))

            SectionTitle("Eventos asistidos")

            Spacer(Modifier.height(Spacing.md))

            attendedEvents.forEach { item ->
                HistoryItem(
                    title = item.title,
                    date = item.date,
                    status = item.status,
                    statusIcon = Icons.Outlined.CheckCircle
                )
                Spacer(Modifier.height(Spacing.md))
            }

            Spacer(Modifier.height(Spacing.lg))

            SectionTitle("Eventos pasados")

            Spacer(Modifier.height(Spacing.md))

            pastEvents.forEach { item ->
                HistoryItem(
                    title = item.title,
                    date = item.date,
                    status = item.status,
                    statusIcon = Icons.Outlined.HistoryToggleOff
                )
                Spacer(Modifier.height(Spacing.md))
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
    statusIcon: ImageVector
) {
    val colors = AppTheme.colors
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
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
                modifier = Modifier.size(28.dp)
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
