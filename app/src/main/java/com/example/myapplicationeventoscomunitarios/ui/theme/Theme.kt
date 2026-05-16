package com.example.myapplicationeventoscomunitarios.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val AppDarkColorScheme = darkColorScheme(
    primary = DarkAppColors.primary,
    onPrimary = DarkAppColors.onPrimary,
    primaryContainer = DarkAppColors.primary,
    onPrimaryContainer = DarkAppColors.onPrimary,
    secondary = DarkAppColors.primary,
    onSecondary = DarkAppColors.onPrimary,
    background = DarkAppColors.background,
    onBackground = DarkAppColors.textPrimary,
    surface = DarkAppColors.surface,
    onSurface = DarkAppColors.textPrimary,
    surfaceVariant = DarkAppColors.surfaceVariant,
    onSurfaceVariant = DarkAppColors.textSecondary,
    outline = DarkAppColors.border,
    outlineVariant = DarkAppColors.divider
)

private val AppLightColorScheme = lightColorScheme(
    primary = LightAppColors.primary,
    onPrimary = LightAppColors.onPrimary,
    primaryContainer = LightAppColors.primary,
    onPrimaryContainer = LightAppColors.onPrimary,
    secondary = LightAppColors.primary,
    onSecondary = LightAppColors.onPrimary,
    background = LightAppColors.background,
    onBackground = LightAppColors.textPrimary,
    surface = LightAppColors.surface,
    onSurface = LightAppColors.textPrimary,
    surfaceVariant = LightAppColors.surfaceVariant,
    onSurfaceVariant = LightAppColors.textSecondary,
    outline = LightAppColors.border,
    outlineVariant = LightAppColors.divider
)

val LocalAppColors = staticCompositionLocalOf { DarkAppColors }

object AppTheme {
    val colors: AppColors
        @Composable
        @ReadOnlyComposable
        get() = LocalAppColors.current
}

@Composable
fun MyApplicationEventosComunitariosTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val appColors = if (darkTheme) DarkAppColors else LightAppColors
    val colorScheme = if (darkTheme) AppDarkColorScheme else AppLightColorScheme

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            val controller = WindowCompat.getInsetsController(window, view)
            controller.isAppearanceLightStatusBars = appColors.isLight
            controller.isAppearanceLightNavigationBars = appColors.isLight
        }
    }

    CompositionLocalProvider(LocalAppColors provides appColors) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = Typography,
            content = content
        )
    }
}
