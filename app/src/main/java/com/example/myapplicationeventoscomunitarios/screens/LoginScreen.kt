package com.example.myapplicationeventoscomunitarios.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.Login
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.example.myapplicationeventoscomunitarios.components.AppTextField
import com.example.myapplicationeventoscomunitarios.components.MainButton
import com.example.myapplicationeventoscomunitarios.ui.theme.AppTheme
import com.example.myapplicationeventoscomunitarios.ui.theme.Spacing

@Composable
fun LoginScreen(
    modifier: Modifier = Modifier,
    onRegisterClick: () -> Unit = {},
    onLoginClick: () -> Unit = {}
) {
    var userName by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    val colors = AppTheme.colors

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(colors.background)
            .padding(horizontal = Spacing.xxxl, vertical = Spacing.xxl),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.Start
    ) {
        Text(
            text = "Bienvenido de vuelta",
            style = MaterialTheme.typography.headlineLarge,
            color = colors.textPrimary
        )

        Spacer(Modifier.height(Spacing.lg))

        Text(
            text = "Ingresa los detalles a continuación",
            style = MaterialTheme.typography.bodyLarge,
            color = colors.textSecondary
        )

        Spacer(Modifier.height(Spacing.huge * 2))

        AppTextField(
            value = userName,
            onValueChange = { userName = it },
            label = "Nombre de usuario",
            leadingIcon = Icons.Outlined.Person
        )

        Spacer(Modifier.height(Spacing.md))

        AppTextField(
            value = password,
            onValueChange = { password = it },
            label = "Contraseña",
            isPassword = true,
            leadingIcon = Icons.Outlined.Lock
        )

        Spacer(Modifier.height(Spacing.huge))

        MainButton(
            text = "Iniciar sesión",
            onClick = onLoginClick,
            leadingIcon = Icons.AutoMirrored.Outlined.Login
        )

        Spacer(Modifier.height(Spacing.md))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "¿Aún no tienes cuenta?",
                style = MaterialTheme.typography.bodyMedium,
                color = colors.textSecondary
            )
            TextButton(onClick = onRegisterClick) {
                Text(
                    text = "Regístrate",
                    style = MaterialTheme.typography.bodyMedium,
                    color = colors.primary
                )
            }
        }
    }
}
