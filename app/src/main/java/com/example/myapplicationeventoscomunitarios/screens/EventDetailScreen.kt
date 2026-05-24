package com.example.myapplicationeventoscomunitarios.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
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
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.outlined.AccessTime
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material.icons.outlined.ChatBubbleOutline
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.EventAvailable
import androidx.compose.material.icons.outlined.Groups
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.myapplicationeventoscomunitarios.components.AppTopBar
import com.example.myapplicationeventoscomunitarios.components.MainButton
import com.example.myapplicationeventoscomunitarios.components.SecondaryButton
import com.example.myapplicationeventoscomunitarios.events.Event
import com.example.myapplicationeventoscomunitarios.events.computeStartDateTimeMillis
import com.example.myapplicationeventoscomunitarios.ui.theme.AppTheme
import com.example.myapplicationeventoscomunitarios.ui.theme.Spacing
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun EventDetailScreen(
    modifier: Modifier = Modifier,
    event: Event?,
    participantCount: Int,
    isParticipating: Boolean,
    isSignedIn: Boolean,
    isOwner: Boolean,
    actionBusy: Boolean,
    onBackClick: () -> Unit = {},
    onEditClick: () -> Unit = {},
    onDeleteClick: () -> Unit = {},
    onToggleParticipation: () -> Unit = {},
    onCommentClick: () -> Unit = {},
) {
    val colors = AppTheme.colors
    var menuExpanded by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf(false) }

    val dateFmt = remember {
        SimpleDateFormat("EEEE d 'de' MMMM 'de' yyyy", Locale("es", "ES"))
    }
    val timeFmt = remember {
        SimpleDateFormat("h:mm a", Locale("es", "ES"))
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = colors.background,
        topBar = {
            AppTopBar(
                title = "Detalle del evento",
                onBackClick = onBackClick,
                actions = if (isOwner && event != null) {
                    {
                        IconButton(onClick = { menuExpanded = true }) {
                            Icon(Icons.Filled.MoreVert, contentDescription = "Opciones del evento")
                        }
                        DropdownMenu(
                            expanded = menuExpanded,
                            onDismissRequest = { menuExpanded = false }
                        ) {
                            DropdownMenuItem(
                                text = { Text("Editar") },
                                onClick = {
                                    menuExpanded = false
                                    onEditClick()
                                }
                            )
                            DropdownMenuItem(
                                text = { Text("Eliminar") },
                                onClick = {
                                    menuExpanded = false
                                    showDeleteDialog = true
                                }
                            )
                        }
                    }
                } else null
            )
        }
    ) { padding ->
        when {
            event == null -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        CircularProgressIndicator(color = colors.primary)
                        Spacer(Modifier.height(Spacing.lg))
                        Text(
                            text = "Cargando evento…",
                            style = MaterialTheme.typography.bodyMedium,
                            color = colors.textSecondary
                        )
                    }
                }
            }

            else -> {
                val e = event
                val dateLabel = dateFmt.format(Date(e.dateMillis)).replaceFirstChar {
                    if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString()
                }
                val startMillis = computeStartDateTimeMillis(e.dateMillis, e.timeHour, e.timeMinute)
                val timeLabel = timeFmt.format(Date(startMillis))

                if (showDeleteDialog) {
                    AlertDialog(
                        onDismissRequest = { showDeleteDialog = false },
                        title = { Text("Eliminar evento", color = colors.textPrimary) },
                        text = {
                            Text(
                                "¿Seguro que deseas eliminar «${e.title}»? Esta acción no se puede deshacer.",
                                color = colors.textSecondary
                            )
                        },
                        confirmButton = {
                            TextButton(
                                onClick = {
                                    showDeleteDialog = false
                                    onDeleteClick()
                                },
                                enabled = !actionBusy
                            ) { Text("Eliminar", color = colors.primary) }
                        },
                        dismissButton = {
                            TextButton(onClick = { showDeleteDialog = false }) {
                                Text("Cancelar", color = colors.textSecondary)
                            }
                        },
                        containerColor = colors.surface
                    )
                }

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
                                text = e.title,
                                style = MaterialTheme.typography.titleLarge,
                                color = colors.textPrimary
                            )

                            Spacer(Modifier.height(Spacing.sm))

                            Text(
                                text = e.description,
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
                        value = dateLabel
                    )
                    Spacer(Modifier.height(Spacing.md))
                    DetailRow(
                        icon = Icons.Outlined.AccessTime,
                        label = "Hora",
                        value = timeLabel
                    )
                    Spacer(Modifier.height(Spacing.md))
                    DetailRow(
                        icon = Icons.Outlined.Groups,
                        label = "Participantes confirmados",
                        value = participantCount.toString()
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
                                text = e.location,
                                style = MaterialTheme.typography.bodyMedium,
                                color = colors.textSecondary
                            )
                        }
                    }

                    Spacer(Modifier.height(Spacing.huge))

                    MainButton(
                        text = if (isParticipating) "Quitar participación" else "Marcar participación",
                        onClick = onToggleParticipation,
                        leadingIcon = Icons.Outlined.CheckCircle,
                        enabled = isSignedIn && !actionBusy
                    )

                    if (!isSignedIn) {
                        Spacer(Modifier.height(Spacing.sm))
                        Text(
                            text = "Inicia sesión para marcar tu participación.",
                            style = MaterialTheme.typography.bodySmall,
                            color = colors.textMuted,
                            modifier = Modifier.fillMaxWidth(),
                            textAlign = TextAlign.Center
                        )
                    }

                    Spacer(Modifier.height(Spacing.md))

                    SecondaryButton(
                        text = "Comentar y calificar",
                        onClick = onCommentClick,
                        leadingIcon = Icons.Outlined.ChatBubbleOutline,
                        enabled = isParticipating && isSignedIn && !actionBusy
                    )

                    if (isSignedIn && !isParticipating) {
                        Spacer(Modifier.height(Spacing.sm))
                        Text(
                            text = "Marca tu participación para poder comentar y calificar.",
                            style = MaterialTheme.typography.bodySmall,
                            color = colors.textMuted,
                            modifier = Modifier.fillMaxWidth(),
                            textAlign = TextAlign.Center
                        )
                    }

                    Spacer(Modifier.height(Spacing.xxl))
                }
            }
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
