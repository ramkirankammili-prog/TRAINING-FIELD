package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

private val DarkColorScheme = darkColorScheme(
    primary = CyberPrimary,
    onPrimary = CyberTextPrimary,
    primaryContainer = CyberOmrSelected,
    onPrimaryContainer = CyberTextPrimary,
    secondary = CyberSecondary,
    onSecondary = CyberBg,
    secondaryContainer = CyberSurfaceVariant,
    onSecondaryContainer = CyberTextPrimary,
    tertiary = CyberTertiary,
    background = CyberBg,
    onBackground = CyberTextPrimary,
    surface = CyberSurface,
    onSurface = CyberTextPrimary,
    surfaceVariant = CyberSurfaceVariant,
    onSurfaceVariant = CyberTextSecondary,
    outline = CyberBorder,
    outlineVariant = CyberBorderActive,
    error = CyberDanger,
    onError = CyberTextPrimary
)

@Composable
fun MyApplicationTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = DarkColorScheme,
        typography = Typography,
        content = content
    )
}
