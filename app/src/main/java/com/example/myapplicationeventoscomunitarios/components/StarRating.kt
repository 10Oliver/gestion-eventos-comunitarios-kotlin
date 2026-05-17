package com.example.myapplicationeventoscomunitarios.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.StarBorder
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.myapplicationeventoscomunitarios.ui.theme.AppTheme
import com.example.myapplicationeventoscomunitarios.ui.theme.Spacing

@Composable
fun StarRating(
    rating: Int,
    onRatingChange: (Int) -> Unit,
    modifier: Modifier = Modifier,
    starSize: Dp = 36.dp,
    enabled: Boolean = true,
) {
    val colors = AppTheme.colors
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(Spacing.sm)
    ) {
        (1..5).forEach { index ->
            val selected = index <= rating
            Icon(
                imageVector = if (selected) Icons.Filled.Star else Icons.Outlined.StarBorder,
                contentDescription = "Calificar con $index estrella${if (index > 1) "s" else ""}",
                tint = if (selected) colors.primary else colors.textMuted,
                modifier = Modifier
                    .size(starSize)
                    .then(
                        if (enabled) {
                            Modifier.clickable(role = Role.Button) { onRatingChange(index) }
                        } else {
                            Modifier
                        }
                    )
            )
        }
    }
}

@Composable
fun StarRatingReadOnly(
    rating: Int,
    modifier: Modifier = Modifier,
    starSize: Dp = 16.dp
) {
    val colors = AppTheme.colors
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(Spacing.xxs)
    ) {
        (1..5).forEach { index ->
            val selected = index <= rating
            Icon(
                imageVector = if (selected) Icons.Filled.Star else Icons.Outlined.StarBorder,
                contentDescription = null,
                tint = if (selected) colors.primary else colors.textMuted,
                modifier = Modifier.size(starSize)
            )
        }
    }
}
