package com.abanana.abattery.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

private val AppDarkColors = darkColorScheme(
    background = BgDark,
    surface = SurfaceLow,
    surfaceVariant = SurfaceHigh,
    surfaceContainerLow = SurfaceLow,
    surfaceContainerHigh = SurfaceHigh,
    primary = PrimaryGreen,
    primaryContainer = PrimaryContainer,
    secondary = SecondaryColor,
    onBackground = OnSurface,
    onSurface = OnSurface,
    onSurfaceVariant = OnSurfaceVar,
    outline = OutlineColor,
    outlineVariant = OutlineVariant,
    error = ErrorColor,
)

@Composable
fun ABatteryTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = AppDarkColors,
        content = content,
    )
}
