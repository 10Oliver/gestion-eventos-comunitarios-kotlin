package com.example.myapplicationeventoscomunitarios.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.automirrored.outlined.Logout
import androidx.compose.material.icons.outlined.BarChart
import androidx.compose.material.icons.outlined.DarkMode
import androidx.compose.material.icons.outlined.History
import androidx.compose.material.icons.outlined.LightMode
import androidx.compose.material.icons.outlined.WavingHand
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.myapplicationeventoscomunitarios.components.SectionTitle
import com.example.myapplicationeventoscomunitarios.ui.theme.AppTheme
import com.example.myapplicationeventoscomunitarios.ui.theme.Spacing
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

private data class EventUi(
    val id: Int,
    val title: String,
    val date: String,
    val participants: String,
    val description: String,
    val isUpcoming: Boolean
)

private val sampleEvents = listOf(
    EventUi(
        id = 1,
        title = "Jornada de limpieza",
        date = "24, mayo del 2026",
        participants = "10",
        description = "Limpieza del parque central. Llevar guantes y bolsas. Punto de reunión en la entrada principal.",
        isUpcoming = true
    ),
    EventUi(
        id = 2,
        title = "Reunión comunitaria",
        date = "24, mayo del 2026",
        participants = "21",
        description = "Reunión mensual para tratar temas de seguridad y mantenimiento del barrio.",
        isUpcoming = false
    ),
    EventUi(
        id = 3,
        title = "Campaña de donación",
        date = "24, mayo del 2026",
        participants = "17",
        description = "Recolección de ropa, alimentos y libros para las familias del sector.",
        isUpcoming = false
    ),
    EventUi(
        id = 4,
        title = "Torneo deportivo",
        date = "24, mayo del 2026",
        participants = "16",
        description = "Mini torneo de fútbol entre vecinos. Inscripciones abiertas.",
        isUpcoming = false
    ),
    EventUi(
        id = 5,
        title = "Charla de seguridad",
        date = "24, mayo del 2026",
        participants = "5",
        description = "Capacitación sobre prevención del delito y primeros auxilios.",
        isUpcoming = false
    )
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    userDisplayName: String = "",
    onCreateEventClick: () -> Unit = {},
    onEventClick: () -> Unit = {},
    onHistoryClick: () -> Unit = {},
    onStatsClick: () -> Unit = {},
    isDarkTheme: Boolean = true,
    onToggleTheme: () -> Unit = {},
    onSignOut: () -> Unit = {}
) {
    var isRefreshing by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()
    val colors = AppTheme.colors

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = colors.background,
        floatingActionButton = {
            FloatingActionButton(
                onClick = onCreateEventClick,
                containerColor = colors.primary,
                contentColor = colors.onPrimary
            ) {
                Icon(Icons.Filled.Add, contentDescription = "Crear nuevo evento")
            }
        }
    ) { padding ->
        PullToRefreshBox(
            isRefreshing = isRefreshing,
            onRefresh = {
                scope.launch {
                    isRefreshing = true
                    delay(1200)
                    isRefreshing = false
                }
            },
            modifier = Modifier.padding(padding)
        ) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = Spacing.xxl),
                contentPadding = PaddingValues(vertical = Spacing.xxl)
            ) {
                item {
                    HomeHeader(
                        userDisplayName = userDisplayName,
                        isDarkTheme = isDarkTheme,
                        onToggleTheme = onToggleTheme,
                        onSignOut = onSignOut
                    )
                }
                item { Spacer(Modifier.height(Spacing.xxl)) }

                item {
                    Row(
                        Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(Spacing.md)
                    ) {
                        QuickActionCard(
                            icon = Icons.Outlined.History,
                            label = "Historial",
                            onClick = onHistoryClick,
                            modifier = Modifier.weight(1f)
                        )
                        QuickActionCard(
                            icon = Icons.Outlined.BarChart,
                            label = "Estadísticas",
                            onClick = onStatsClick,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
                item { Spacer(Modifier.height(Spacing.xxxl)) }

                item { SectionTitle("Eventos próximos") }
                item { Spacer(Modifier.height(Spacing.md)) }
                items(sampleEvents.filter { it.isUpcoming }, key = { it.id }) { event ->
                    EventCard(
                        title = event.title,
                        date = event.date,
                        participants = event.participants,
                        description = event.description,
                        onClick = onEventClick
                    )
                    Spacer(Modifier.height(Spacing.md))
                }

                item { Spacer(Modifier.height(Spacing.lg)) }
                item { SectionTitle("Todos los eventos") }
                item { Spacer(Modifier.height(Spacing.md)) }

                items(sampleEvents.filter { !it.isUpcoming }, key = { it.id }) { event ->
                    EventCard(
                        title = event.title,
                        date = event.date,
                        participants = event.participants,
                        description = event.description,
                        onClick = onEventClick
                    )
                    Spacer(Modifier.height(Spacing.md))
                }

                item { Spacer(Modifier.height(Spacing.huge)) }
            }
        }
    }
}

@Composable
private fun HomeHeader(
    userDisplayName: String,
    isDarkTheme: Boolean,
    onToggleTheme: () -> Unit,
    onSignOut: () -> Unit
) {
    val colors = AppTheme.colors
    val greetingName = userDisplayName.ifBlank { "Invitado" }
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = Icons.Outlined.WavingHand,
            contentDescription = null,
            tint = colors.primary,
            modifier = Modifier.size(28.dp)
        )
        Spacer(Modifier.width(Spacing.sm))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = "Bienvenido,",
                style = MaterialTheme.typography.headlineMedium,
                color = colors.textPrimary
            )
            Text(
                text = greetingName,
                style = MaterialTheme.typography.titleMedium,
                color = colors.textSecondary
            )
        }
        IconButton(onClick = onSignOut) {
            Icon(
                imageVector = Icons.AutoMirrored.Outlined.Logout,
                contentDescription = "Cerrar sesión",
                tint = colors.primary
            )
        }
        IconButton(onClick = onToggleTheme) {
            Icon(
                imageVector = if (isDarkTheme) Icons.Outlined.LightMode else Icons.Outlined.DarkMode,
                contentDescription = if (isDarkTheme) "Cambiar a modo claro" else "Cambiar a modo oscuro",
                tint = colors.primary
            )
        }
    }
}

@Composable
private fun QuickActionCard(
    icon: ImageVector,
    label: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = AppTheme.colors
    Card(
        onClick = onClick,
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = colors.surfaceVariant)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = Spacing.lg, horizontal = Spacing.md),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = colors.primary,
                modifier = Modifier.size(28.dp)
            )
            Spacer(Modifier.height(Spacing.sm))
            Text(
                text = label,
                style = MaterialTheme.typography.labelLarge,
                color = colors.textPrimary
            )
        }
    }
}

@Composable
fun EventCard(
    title: String,
    date: String,
    participants: String,
    description: String,
    onClick: () -> Unit
) {
    val colors = AppTheme.colors
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = colors.primary),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Box(modifier = Modifier.padding(Spacing.lg)) {
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = colors.onPrimary
                )

                Spacer(Modifier.height(Spacing.xs))

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(colors.background.copy(alpha = 0.25f))
                        .padding(horizontal = Spacing.md, vertical = 3.dp)
                ) {
                    Text(
                        text = date,
                        style = MaterialTheme.typography.labelSmall,
                        color = colors.onPrimary
                    )
                }

                Spacer(Modifier.height(Spacing.sm))

                Text(
                    text = description,
                    style = MaterialTheme.typography.bodySmall,
                    color = colors.onPrimary,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }

            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .size(26.dp)
                    .clip(CircleShape)
                    .background(colors.background.copy(alpha = 0.25f)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = participants,
                    color = colors.onPrimary,
                    style = MaterialTheme.typography.labelSmall
                )
            }
        }
    }
}
