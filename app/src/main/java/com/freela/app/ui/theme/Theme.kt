package com.freela.app.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.graphics.Color

private val LightScheme = lightColorScheme(
    primary = LightAccentBase,
    onPrimary = Color.White,
    primaryContainer = LightAccentSoft,
    onPrimaryContainer = LightAccentBase,
    secondary = LightAccentInk,
    onSecondary = Color.White,
    background = LightBg,
    onBackground = LightInk,
    surface = LightSurface,
    onSurface = LightInk,
    surfaceVariant = LightSurfaceLow,
    onSurfaceVariant = LightMuted,
    surfaceContainer = LightSurfaceLow,
    surfaceContainerHigh = LightSurfaceHi,
    surfaceContainerHighest = LightSurfaceHi,
    outline = LightLine,
    outlineVariant = LightLineSoft,
    error = LightDanger,
    onError = Color.White,
)

private val DarkScheme = darkColorScheme(
    primary = DarkAccentBase,
    onPrimary = DarkBg,
    primaryContainer = DarkAccentSoft,
    onPrimaryContainer = DarkAccentInk,
    secondary = DarkAccentInk,
    onSecondary = DarkBg,
    background = DarkBg,
    onBackground = DarkInk,
    surface = DarkSurface,
    onSurface = DarkInk,
    surfaceVariant = DarkSurfaceLow,
    onSurfaceVariant = DarkMuted,
    surfaceContainer = DarkSurfaceLow,
    surfaceContainerHigh = DarkSurfaceHi,
    surfaceContainerHighest = DarkSurfaceHi,
    outline = DarkLine,
    outlineVariant = DarkLineSoft,
    error = DarkDanger,
    onError = DarkBg,
)

@Composable
fun FreelaTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit,
) {
    val colorScheme = if (darkTheme) DarkScheme else LightScheme
    val tokens = if (darkTheme) DarkFreelaTokens else LightFreelaTokens

    CompositionLocalProvider(LocalFreelaTokens provides tokens) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = FreelaTypography,
            shapes = FreelaShapes,
            content = content,
        )
    }
}
