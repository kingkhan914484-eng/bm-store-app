package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = FlipkartBlue,
    secondary = FlipkartYellow,
    tertiary = FlipkartOrange,
    background = DarkBackground,
    surface = DarkSurface,
    onPrimary = Color.White,
    onSecondary = Color.Black,
    onBackground = DarkTextPrimary,
    onSurface = DarkTextPrimary
)

private val LightColorScheme = lightColorScheme(
    primary = FlipkartBlue,
    onPrimary = Color.White,
    primaryContainer = Color(0xFFE3EDFD),
    onPrimaryContainer = FlipkartDarkBlue,
    secondary = FlipkartOrange,
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFFFEAD8),
    onSecondaryContainer = Color(0xFF7A2A00),
    tertiary = FlipkartGreen,
    onTertiary = Color.White,
    background = FlipkartBackground,
    onBackground = TextPrimary,
    surface = SurfaceWhite,
    onSurface = TextPrimary,
    surfaceVariant = Color(0xFFF7F9FC),
    onSurfaceVariant = TextSecondary,
    outline = DividerGray
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
