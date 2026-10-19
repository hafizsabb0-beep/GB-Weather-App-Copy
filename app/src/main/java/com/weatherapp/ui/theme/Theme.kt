package com.weatherapp.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DayBlue = Color(0xFF1565C0)
private val DayBlueLight = Color(0xFF42A5F5)
private val SunsetOrange = Color(0xFFFFA726)
private val NightNavy = Color(0xFF0D1B2A)
private val NightBlue = Color(0xFF1A237E)

private val LightColorScheme = lightColorScheme(
    primary = DayBlue,
    onPrimary = Color.White,
    primaryContainer = Color(0xFFBBDEFB),
    secondary = SunsetOrange,
    secondaryContainer = Color(0xFFFFE0B2),
    background = Color(0xFFF0F7FF),
    surface = Color.White,
    surfaceVariant = Color(0xFFE3F2FD),
    onBackground = Color(0xFF1A1C1E),
    onSurface = Color(0xFF1A1C1E)
)

private val DarkColorScheme = darkColorScheme(
    primary = DayBlueLight,
    onPrimary = NightNavy,
    primaryContainer = NightBlue,
    secondary = SunsetOrange,
    secondaryContainer = Color(0xFF7B4F00),
    background = NightNavy,
    surface = Color(0xFF162032),
    surfaceVariant = Color(0xFF1E2D40),
    onBackground = Color(0xFFE2E2E6),
    onSurface = Color(0xFFE2E2E6)
)

@Composable
fun WeatherAppTheme(darkTheme: Boolean = isSystemInDarkTheme(), content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme,
        typography = Typography(),
        content = content
    )
}
