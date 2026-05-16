package com.example.myapplicationeventoscomunitarios.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AccessTime
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material.icons.outlined.ChatBubbleOutline
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.EventAvailable
import androidx.compose.material.icons.outlined.Groups
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.example.myapplicationeventoscomunitarios.components.AppTopBar
import com.example.myapplicationeventoscomunitarios.components.MainButton
import com.example.myapplicationeventoscomunitarios.components.SecondaryButton
import com.example.myapplicationeventoscomunitarios.ui.theme.AppTheme
import com.example.myapplicationeventoscomunitarios.ui.theme.Spacing

@Composable
fun EventDetailScreen(
    modifier: Modifier = Modifier,
    onBackClick: () -> Unit = {},
    onCommentClick: () -> Unit = {},
    onParticipate: () -> Unit = {}
) {
    val colors = AppTheme.colors
    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = colors.background,
        topBar = {
            AppTopBar(title = "Detalle del evento", onBackClick = onBackClick)
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

            Row(verticalAlignment = Alignment.Top) {
                Box(
                    modifier = Modifier
                        .size(58.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(colors.primary),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Outlined.EventAvailable,
                        contentDescription = null,
                        tint = colors.onPrimary,
                        modifier = Modifier.size(32.dp)
                    )
                }

                Spacer(Modifier.width(Spacing.lg))

                Column {
                    Text(
                        text = "Nombre del evento",
                        style = MaterialTheme.typography.titleLarge,
                        color = colors.textPrimary
                    )

                    Spacer(Modifier.height(Spacing.sm))

                    Text(
                        text = "Evento comunitario para informar y reunir a los vecinos de la comunidad.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = colors.textSecondary
                    )
                }
            }

            Spacer(Modifier.height(Spacing.xxxl))

            Text(
                text = "Detalles adicionales",
                style = MaterialTheme.typography.titleMedium,
                color = colors.textSecondary
            )

            Spacer(Modifier.height(Spacing.xl))

            DetailRow(
                icon = Icons.Outlined.CalendarMonth,
                label = "Fecha",
                value = "Martes 26 de mayo de 2026"
            )
            Spacer(Modifier.height(Spacing.md))
            DetailRow(
                icon = Icons.Outlined.AccessTime,
                label = "Hora",
                value = "10:00 a.m."
            )
            Spacer(Modifier.height(Spacing.md))
            DetailRow(
                icon = Icons.Outlined.Groups,
                label = "Participantes confirmados",
                value = "27"
            )

            Spacer(Modifier.height(Spacing.xxl))

            Row(verticalAlignment = Alignment.Top) {
                Icon(
                    imageVector = Icons.Outlined.LocationOn,
                    contentDescription = null,
                    tint = colors.primary,
                    modifier = Modifier.size(22.dp)
                )
                Spacer(Modifier.width(Spacing.md))
                Column {
                    Text(
                        text = "Ubicación",
                        style = MaterialTheme.typography.titleSmall,
                        color = colors.textPrimary
                    )
                    Spacer(Modifier.height(Spacing.xs))
                    Text(
                        text = "Parque central de la comunidad. Punto de reunión cerca de la entrada principal.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = colors.textSecondary
                    )
                }
            }

            Spacer(Modifier.height(Spacing.huge))

            MainButton(
                text = "Marcar participación",
                onClick = onParticipate,
                leadingIcon = Icons.Outlined.CheckCircle
            )

            Spacer(Modifier.height(Spacing.md))

            SecondaryButton(
                text = "Comentar y calificar",
                onClick = onCommentClick,
                leadingIcon = Icons.Outlined.ChatBubbleOutline
            )

            Spacer(Modifier.height(Spacing.xxl))
        }
    }
}

@Composable
private fun DetailRow(
    icon: ImageVector,
    label: String,
    value: String
) {
    val colors = AppTheme.colors
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = colors.primary,
            modifier = Modifier.size(20.dp)
        )
        Spacer(Modifier.width(Spacing.md))
        Column {
            Text(
                text = label,
                style = MaterialTheme.typography.labelMedium,
                color = colors.textSecondary
            )
            Text(
                text = value,
                style = MaterialTheme.typography.bodyLarge,
                color = colors.textPrimary
            )
        }
    }
}
