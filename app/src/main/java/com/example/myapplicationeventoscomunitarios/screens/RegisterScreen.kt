package com.example.myapplicationeventoscomunitarios.screens

import android.util.Patterns
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AlternateEmail
import androidx.compose.material.icons.outlined.Badge
import androidx.compose.material.icons.outlined.HowToReg
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.LockReset
import androidx.compose.material3.HorizontalDivider
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.myapplicationeventoscomunitarios.components.AppTextField
import com.example.myapplicationeventoscomunitarios.components.MainButton
import com.example.myapplicationeventoscomunitarios.ui.theme.AppTheme
import com.example.myapplicationeventoscomunitarios.ui.theme.Spacing

@Composable
fun RegisterScreen(
    modifier: Modifier = Modifier,
    onBackToLoginClick: () -> Unit = {},
    onRegisterSubmit: (fullName: String, email: String, password: String) -> Unit = { _, _, _ -> },
    onGoogleSignInClick: () -> Unit = {},
    isAuthBusy: Boolean = false,
    onValidationError: (String) -> Unit = {},
) {
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    val colors = AppTheme.colors

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(colors.background)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = Spacing.xxxl, vertical = Spacing.xxl),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.Start
    ) {
        Spacer(Modifier.height(Spacing.xxl))

        Text(
            text = "Registro de usuario",
            style = MaterialTheme.typography.headlineLarge,
            color = colors.textPrimary
        )

        Spacer(Modifier.height(Spacing.lg))

        Text(
            text = "Ingresa los detalles a continuación",
            style = MaterialTheme.typography.bodyLarge,
            color = colors.textSecondary
        )

        Spacer(Modifier.height(Spacing.xxxl))

        AppTextField(
            value = name,
            onValueChange = { name = it },
            label = "Nombre completo",
            leadingIcon = Icons.Outlined.Badge,
            enabled = !isAuthBusy
        )

        Spacer(Modifier.height(Spacing.md))

        AppTextField(
            value = email,
            onValueChange = { email = it },
            label = "Correo electrónico",
            leadingIcon = Icons.Outlined.AlternateEmail,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
            enabled = !isAuthBusy
        )

        Spacer(Modifier.height(Spacing.md))

        AppTextField(
            value = password,
            onValueChange = { password = it },
            label = "Contraseña",
            isPassword = true,
            leadingIcon = Icons.Outlined.Lock,
            enabled = !isAuthBusy
        )

        Spacer(Modifier.height(Spacing.md))

        AppTextField(
            value = confirmPassword,
            onValueChange = { confirmPassword = it },
            label = "Confirmar contraseña",
            isPassword = true,
            leadingIcon = Icons.Outlined.LockReset,
            enabled = !isAuthBusy
        )

        Spacer(Modifier.height(Spacing.xxxl))

        MainButton(
            text = "Registrarme",
            onClick = {
                val trimmedName = name.trim()
                val trimmedEmail = email.trim()
                when {
                    trimmedName.isBlank() ->
                        onValidationError("Ingresa tu nombre completo.")
                    trimmedEmail.isBlank() ->
                        onValidationError("Ingresa tu correo electrónico.")
                    !Patterns.EMAIL_ADDRESS.matcher(trimmedEmail).matches() ->
                        onValidationError("El correo electrónico no es válido.")
                    password.length < 6 ->
                        onValidationError("La contraseña debe tener al menos 6 caracteres.")
                    password != confirmPassword ->
                        onValidationError("Las contraseñas no coinciden.")
                    else -> onRegisterSubmit(trimmedName, trimmedEmail, password)
                }
            },
            leadingIcon = Icons.Outlined.HowToReg,
            enabled = !isAuthBusy
        )

        Spacer(Modifier.height(Spacing.sm))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "¿Ya tienes cuenta?",
                style = MaterialTheme.typography.bodyMedium,
                color = colors.textSecondary
            )
            TextButton(onClick = onBackToLoginClick, enabled = !isAuthBusy) {
                Text(
                    text = "Inicia sesión",
                    style = MaterialTheme.typography.bodyMedium,
                    color = colors.primary
                )
            }
        }

        Spacer(Modifier.height(Spacing.lg))
        HorizontalDivider(color = colors.divider, thickness = 1.dp)
        Spacer(Modifier.height(Spacing.lg))

        Text(
            text = "O continúa con",
            style = MaterialTheme.typography.bodySmall,
            color = colors.textSecondary,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )

        Spacer(Modifier.height(Spacing.md))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            SocialButton(
                letter = "G",
                background = Color.White,
                contentColor = Color(0xFF4285F4),
                contentDescription = "Continuar con Google",
                onClick = onGoogleSignInClick,
                enabled = !isAuthBusy
            )
            Spacer(Modifier.size(Spacing.lg))
            SocialButton(
                letter = "𝕏",
                background = Color.Black,
                contentColor = Color.White,
                contentDescription = "Continuar con X",
                bordered = true,
                onClick = {},
                enabled = !isAuthBusy
            )
        }

        Spacer(Modifier.height(Spacing.xxl))
    }
}

@Composable
private fun SocialButton(
    letter: String,
    background: Color,
    contentColor: Color,
    contentDescription: String,
    onClick: () -> Unit,
    bordered: Boolean = false,
    enabled: Boolean = true
) {
    val borderColor = AppTheme.colors.divider
    Box(
        modifier = Modifier
            .size(44.dp)
            .clip(RoundedCornerShape(10.dp))
            .background(background)
            .then(if (bordered) Modifier.border(1.dp, borderColor, RoundedCornerShape(10.dp)) else Modifier)
            .clickable(
                role = Role.Button,
                onClickLabel = contentDescription,
                enabled = enabled,
                onClick = onClick
            ),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = letter,
            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
            color = contentColor
        )
    }
}
