package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

private val GothicColorScheme = darkColorScheme(
    primary = GothicBloodRed,
    onPrimary = GothicTextSilver,
    secondary = GothicGold,
    onSecondary = GothicDarkBackground,
    background = GothicDarkBackground,
    onBackground = GothicTextSilver,
    surface = GothicDarkSurface,
    onSurface = GothicTextSilver,
    surfaceVariant = GothicLightSurface,
    onSurfaceVariant = GothicTextGold,
    outline = GothicBorderGray,
    error = GothicBloodRed
)

@Composable
fun MyApplicationTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = GothicColorScheme,
        typography = Typography,
        content = content
    )
}
