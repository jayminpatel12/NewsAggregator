package com.jaymin.newsaggregator.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
    primary = IndigoLight,
    secondary = VioletLight,
    tertiary = EmeraldLight,
    surface = DarkSurface,
    background = DarkBackground,
    onPrimary = Neutral900,
    onSecondary = Neutral900,
    onTertiary = Neutral900,
    onBackground = DarkOnSurface,
    onSurface = DarkOnSurface
)

private val LightColorScheme = lightColorScheme(
    primary = IndigoPrimary,
    secondary = VioletSecondary,
    tertiary = EmeraldTertiary,
    surface = Neutral50,
    background = SurfaceTint,
    onPrimary = Neutral50,
    onSecondary = Neutral50,
    onTertiary = Neutral50,
    onBackground = Neutral900,
    onSurface = Neutral900
)

@Composable
fun NewsAggregatorTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    CompositionLocalProvider(
        LocalAppSpacing provides AppSpacing(),
        LocalAppShapes provides AppShapes(),
        LocalAppGradients provides AppGradients()
    ) {
        MaterialTheme(
            colorScheme = colorScheme,
            content = content
        )
    }
}
