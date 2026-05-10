package com.jaymin.newsaggregator.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Immutable
data class AppSpacing(
    val none: Dp = 0.dp,
    val extraSmall: Dp = 4.dp,
    val small: Dp = 8.dp,
    val medium: Dp = 16.dp,
    val large: Dp = 24.dp,
    val extraLarge: Dp = 32.dp,
    val huge: Dp = 48.dp
)

@Immutable
data class AppShapes(
    val small: RoundedCornerShape = RoundedCornerShape(8.dp),
    val medium: RoundedCornerShape = RoundedCornerShape(16.dp),
    val large: RoundedCornerShape = RoundedCornerShape(24.dp),
    val extraLarge: RoundedCornerShape = RoundedCornerShape(32.dp)
)

@Immutable
data class AppGradients(
    val primary: Brush = Brush.linearGradient(listOf(IndigoPrimary, IndigoLight)),
    val secondary: Brush = Brush.linearGradient(listOf(VioletSecondary, VioletLight)),
    val ai: Brush = Brush.linearGradient(
        colors = listOf(
            IndigoPrimary.copy(alpha = 0.15f),
            VioletSecondary.copy(alpha = 0.15f),
            EmeraldTertiary.copy(alpha = 0.15f)
        )
    ),
    val glass: Brush = Brush.verticalGradient(
        colors = listOf(
            Color.White.copy(alpha = 0.9f),
            Color.White.copy(alpha = 0.7f)
        )
    )
)

val LocalAppSpacing = staticCompositionLocalOf { AppSpacing() }
val LocalAppShapes = staticCompositionLocalOf { AppShapes() }
val LocalAppGradients = staticCompositionLocalOf { AppGradients() }

object AppTheme {
    val spacing: AppSpacing
        @Composable
        @ReadOnlyComposable
        get() = LocalAppSpacing.current

    val shapes: AppShapes
        @Composable
        @ReadOnlyComposable
        get() = LocalAppShapes.current

    val gradients: AppGradients
        @Composable
        @ReadOnlyComposable
        get() = LocalAppGradients.current
}
