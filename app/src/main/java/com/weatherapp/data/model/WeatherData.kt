package com.weatherapp.data.model

import com.weatherapp.data.provider.WeatherSource

// ── Domain models ─────────────────────────────────────────────

data class WeatherData(
    val location: Location,
    val current: CurrentWeather,
    val hourly: List<HourlyWeather>,
    val daily: List<DailyWeather>,
    val alerts: List<WeatherAlert> = emptyList(),
    val source: WeatherSource,
    val updatedAt: Long = System.currentTimeMillis()
)

data class Location(
    val name: String,
    val region: String = "",
    val country: String = "",
    val latitude: Double,
    val longitude: Double
)

data class CurrentWeather(
    val tempC: Double,
    val feelsLikeC: Double,
    val condition: WeatherCondition,
    val humidity: Int,
    val windKph: Double,
    val windDirection: String,
    val uvIndex: Double,
    val visibilityKm: Double,
    val pressureMb: Double,
    val dewPointC: Double = 0.0,
    val isDay: Boolean = true
)

data class HourlyWeather(
    val timeEpoch: Long,
    val tempC: Double,
    val feelsLikeC: Double,
    val condition: WeatherCondition,
    val precipMm: Double,
    val precipProbability: Int,
    val windKph: Double,
    val humidity: Int,
    val isDay: Boolean = true
)

data class DailyWeather(
    val dateEpoch: Long,
    val maxTempC: Double,
    val minTempC: Double,
    val avgTempC: Double,
    val condition: WeatherCondition,
    val precipMm: Double,
    val precipProbability: Int,
    val maxWindKph: Double,
    val uvIndex: Double,
    val sunrise: String = "",
    val sunset: String = ""
)

data class WeatherAlert(
    val headline: String,
    val severity: AlertSeverity,
    val description: String,
    val effective: String = "",
    val expires: String = ""
)

data class WeatherCondition(
    val text: String,
    val icon: WeatherIcon
)

enum class AlertSeverity { MINOR, MODERATE, SEVERE, EXTREME }

enum class WeatherIcon(val emoji: String, val description: String) {
    CLEAR_DAY("☀️", "Clear"),
    CLEAR_NIGHT("🌙", "Clear Night"),
    PARTLY_CLOUDY_DAY("⛅", "Partly Cloudy"),
    PARTLY_CLOUDY_NIGHT("🌤️", "Partly Cloudy Night"),
    CLOUDY("☁️", "Cloudy"),
    OVERCAST("🌥️", "Overcast"),
    DRIZZLE("🌦️", "Drizzle"),
    RAIN("🌧️", "Rain"),
    HEAVY_RAIN("⛈️", "Heavy Rain"),
    SNOW("❄️", "Snow"),
    SLEET("🌨️", "Sleet"),
    THUNDERSTORM("⛈️", "Thunderstorm"),
    FOG("🌫️", "Fog"),
    WINDY("💨", "Windy"),
    UNKNOWN("🌡️", "Unknown");
}

enum class TemperatureUnit { CELSIUS, FAHRENHEIT }
enum class WindUnit { KPH, MPH, MS }

fun Double.toFahrenheit() = this * 9.0 / 5.0 + 32.0
fun Double.kphToMph() = this * 0.621371
fun Double.kphToMs() = this / 3.6

sealed class WeatherUiState {
    object Loading : WeatherUiState()
    data class Success(val data: WeatherData) : WeatherUiState()
    data class Error(val message: String) : WeatherUiState()
}
