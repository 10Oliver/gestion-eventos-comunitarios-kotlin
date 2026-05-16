package com.example.myapplicationeventoscomunitarios.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AccessTime
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material.icons.outlined.Description
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material.icons.outlined.Save
import androidx.compose.material.icons.outlined.Title
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDefaults
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimePicker
import androidx.compose.material3.TimePickerDefaults
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.example.myapplicationeventoscomunitarios.components.AppTextField
import com.example.myapplicationeventoscomunitarios.components.AppTopBar
import com.example.myapplicationeventoscomunitarios.components.MainButton
import com.example.myapplicationeventoscomunitarios.ui.theme.AppTheme
import com.example.myapplicationeventoscomunitarios.ui.theme.Spacing
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateEventScreen(
    modifier: Modifier = Modifier,
    onBackClick: () -> Unit = {},
    onEventSaved: () -> Unit = {}
) {
    var title by remember { mutableStateOf("") }
    var dateMillis by remember { mutableStateOf<Long?>(null) }
    var hour by remember { mutableIntStateOf(-1) }
    var minute by remember { mutableIntStateOf(-1) }
    var location by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }

    var showDatePicker by remember { mutableStateOf(false) }
    var showTimePicker by remember { mutableStateOf(false) }

    val dateFormatter = remember { SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()) }
    val dateText = dateMillis?.let { dateFormatter.format(Date(it)) } ?: ""
    val timeText = if (hour >= 0 && minute >= 0) "%02d:%02d".format(hour, minute) else ""
    val colors = AppTheme.colors

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = colors.background,
        topBar = { AppTopBar(title = "Crear evento", onBackClick = onBackClick) }
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
                text = "Completa los detalles del evento",
                style = MaterialTheme.typography.bodyLarge,
                color = colors.textSecondary
            )

            Spacer(Modifier.height(Spacing.xxl))

            AppTextField(
                value = title,
                onValueChange = { title = it },
                label = "Nombre del evento",
                leadingIcon = Icons.Outlined.Title
            )

            Spacer(Modifier.height(Spacing.md))

            ClickableField(
                value = dateText,
                label = "Fecha",
                leadingIcon = Icons.Outlined.CalendarMonth,
                onClick = { showDatePicker = true }
            )

            Spacer(Modifier.height(Spacing.md))

            ClickableField(
                value = timeText,
                label = "Hora",
                leadingIcon = Icons.Outlined.AccessTime,
                onClick = { showTimePicker = true }
            )

            Spacer(Modifier.height(Spacing.md))

            AppTextField(
                value = location,
                onValueChange = { location = it },
                label = "Ubicación",
                leadingIcon = Icons.Outlined.LocationOn
            )

            Spacer(Modifier.height(Spacing.md))

            AppTextField(
                value = description,
                onValueChange = { description = it },
                label = "Descripción",
                modifier = Modifier.height(120.dp),
                singleLine = false,
                leadingIcon = Icons.Outlined.Description
            )

            Spacer(Modifier.height(Spacing.xxxl))

            MainButton(
                text = "Guardar evento",
                onClick = onEventSaved,
                leadingIcon = Icons.Outlined.Save
            )

            Spacer(Modifier.height(Spacing.xxl))
        }
    }

    if (showDatePicker) {
        val datePickerState = rememberDatePickerState(initialSelectedDateMillis = dateMillis)
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    dateMillis = datePickerState.selectedDateMillis
                    showDatePicker = false
                }) { Text("Aceptar", color = colors.primary) }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) {
                    Text("Cancelar", color = colors.textSecondary)
                }
            },
            colors = DatePickerDefaults.colors(
                containerColor = colors.surface
            )
        ) {
            DatePicker(
                state = datePickerState,
                colors = DatePickerDefaults.colors(
                    containerColor = colors.surface,
                    titleContentColor = colors.textPrimary,
                    headlineContentColor = colors.textPrimary,
                    weekdayContentColor = colors.textSecondary,
                    subheadContentColor = colors.textSecondary,
                    yearContentColor = colors.textPrimary,
                    currentYearContentColor = colors.primary,
                    selectedYearContentColor = colors.onPrimary,
                    selectedYearContainerColor = colors.primary,
                    dayContentColor = colors.textPrimary,
                    selectedDayContentColor = colors.onPrimary,
                    selectedDayContainerColor = colors.primary,
                    todayContentColor = colors.primary,
                    todayDateBorderColor = colors.primary,
                    navigationContentColor = colors.textPrimary
                )
            )
        }
    }

    if (showTimePicker) {
        val timePickerState = rememberTimePickerState(
            initialHour = if (hour >= 0) hour else 10,
            initialMinute = if (minute >= 0) minute else 0,
            is24Hour = false
        )
        AlertDialog(
            onDismissRequest = { showTimePicker = false },
            containerColor = colors.surface,
            titleContentColor = colors.textPrimary,
            confirmButton = {
                TextButton(onClick = {
                    hour = timePickerState.hour
                    minute = timePickerState.minute
                    showTimePicker = false
                }) { Text("Aceptar", color = colors.primary) }
            },
            dismissButton = {
                TextButton(onClick = { showTimePicker = false }) {
                    Text("Cancelar", color = colors.textSecondary)
                }
            },
            title = {
                Text("Selecciona la hora", style = MaterialTheme.typography.titleMedium, color = colors.textPrimary)
            },
            text = {
                TimePicker(
                    state = timePickerState,
                    colors = TimePickerDefaults.colors(
                        clockDialColor = colors.background,
                        clockDialSelectedContentColor = colors.onPrimary,
                        clockDialUnselectedContentColor = colors.textPrimary,
                        selectorColor = colors.primary,
                        periodSelectorBorderColor = colors.primary,
                        periodSelectorSelectedContainerColor = colors.primary,
                        periodSelectorUnselectedContainerColor = colors.background,
                        periodSelectorSelectedContentColor = colors.onPrimary,
                        periodSelectorUnselectedContentColor = colors.textPrimary,
                        timeSelectorSelectedContainerColor = colors.primary,
                        timeSelectorUnselectedContainerColor = colors.background,
                        timeSelectorSelectedContentColor = colors.onPrimary,
                        timeSelectorUnselectedContentColor = colors.textPrimary
                    )
                )
            }
        )
    }
}

@Composable
private fun ClickableField(
    value: String,
    label: String,
    leadingIcon: ImageVector,
    onClick: () -> Unit
) {
    Box {
        AppTextField(
            value = value,
            onValueChange = {},
            label = label,
            enabled = false,
            leadingIcon = leadingIcon
        )
        Box(
            Modifier
                .matchParentSize()
                .clickable { onClick() }
        )
    }
}
