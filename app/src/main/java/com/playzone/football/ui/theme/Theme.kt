package com.playzone.football.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection

private val DarkColorScheme = darkColorScheme(
    primary = GreenAccent,
    onPrimary = NavyDarkest,
    primaryContainer = GreenContainer,
    onPrimaryContainer = GreenOnContainer,
    secondary = CyanAccent,
    onSecondary = NavyDarkest,
    secondaryContainer = CyanContainer,
    onSecondaryContainer = TextWhite,
    tertiary = GoldAccent,
    onTertiary = NavyDarkest,
    background = NavyBackground,
    onBackground = TextWhite,
    surface = NavySurface,
    onSurface = TextWhite,
    surfaceVariant = NavyCardElevated,
    onSurfaceVariant = TextGrayLight,
    outline = NavyBorder,
    error = LiveRed
)

@Composable
fun PlayZoneTheme(
    content: @Composable () -> Unit
) {
    // Provide Iranian/Persian RTL layout direction globally across the app
    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
        MaterialTheme(
            colorScheme = DarkColorScheme,
            typography = AppTypography,
            content = content
        )
    }
}
