package com.example.myapplicationeventoscomunitarios.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.myapplicationeventoscomunitarios.ui.theme.AppTheme
import com.example.myapplicationeventoscomunitarios.ui.theme.Spacing

@Composable
fun EventCardSkeleton(modifier: Modifier = Modifier) {
    val colors = AppTheme.colors
    val transition = rememberInfiniteTransition(label = "shimmer")
    val alpha by transition.animateFloat(
        initialValue = 0.3f,
        targetValue = 0.7f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "shimmer-alpha"
    )

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = colors.surfaceVariant.copy(alpha = alpha))
    ) {
        Column(Modifier.padding(Spacing.lg)) {
            ShimmerBar(width = 220.dp, height = 18.dp, baseColor = colors.textPrimary, alpha = alpha)
            Spacer(Modifier.height(Spacing.sm))
            ShimmerBar(width = 120.dp, height = 12.dp, baseColor = colors.textPrimary, alpha = alpha)
            Spacer(Modifier.height(Spacing.sm))
            ShimmerBar(width = 280.dp, height = 12.dp, baseColor = colors.textPrimary, alpha = alpha)
        }
    }
}

@Composable
private fun ShimmerBar(width: Dp, height: Dp, baseColor: Color, alpha: Float) {
    Box(
        modifier = Modifier
            .width(width)
            .height(height)
            .clip(RoundedCornerShape(4.dp))
            .background(baseColor.copy(alpha = alpha * 0.3f))
    )
}
