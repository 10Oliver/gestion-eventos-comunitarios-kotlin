package com.example.myapplicationeventoscomunitarios.ui.theme

import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Color

@Immutable
data class AppColors(
    val background: Color,
    val surface: Color,
    val surfaceVariant: Color,
    val primary: Color,
    val onPrimary: Color,
    val textPrimary: Color,
    val textSecondary: Color,
    val textMuted: Color,
    val border: Color,
    val divider: Color,
    val isLight: Boolean
)

val DarkAppColors = AppColors(
    background = Color(0xFF171717),
    surface = Color(0xFF1F1F1F),
    surfaceVariant = Color(0xFF414760),
    primary = Color(0xFFA8B2F0),
    onPrimary = Color(0xFF111111),
    textPrimary = Color(0xFFF4F4F4),
    textSecondary = Color(0xFFC7C7C7),
    textMuted = Color(0xFF8A8A8A),
    border = Color(0xFF9FA8DA),
    divider = Color(0xFF333333),
    isLight = false
)

val LightAppColors = AppColors(
    background = Color(0xFFFAFAFA),
    surface = Color(0xFFFFFFFF),
    surfaceVariant = Color(0xFFE8EAF6),
    primary = Color(0xFF5C6BC0),
    onPrimary = Color(0xFFFFFFFF),
    textPrimary = Color(0xFF1A1A1A),
    textSecondary = Color(0xFF5F5F5F),
    textMuted = Color(0xFF9E9E9E),
    border = Color(0xFF9FA8DA),
    divider = Color(0xFFE0E0E0),
    isLight = true
)
