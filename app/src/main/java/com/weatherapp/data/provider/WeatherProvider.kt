package com.weatherapp.data.provider

import com.weatherapp.data.model.WeatherData

/**
 * Every weather data source implements this interface.
 * Add a new source by implementing WeatherProvider + registering it in AppModule.
 */
interface WeatherProvider {
    val source: WeatherSource
    suspend fun getWeather(lat: Double, lon: Double): Result<WeatherData>
    suspend fun searchLocation(query: String): Result<List<LocationResult>>
}

data class LocationResult(
    val name: String,
    val region: String,
    val country: String,
    val lat: Double,
    val lon: Double
)

enum class WeatherSource(
    val displayName: String,
    val description: String,
    val requiresApiKey: Boolean,
    val apiKeyUrl: String = "",
    val freeTier: String = ""
) {
    OPEN_METEO(
        displayName = "Open-Meteo",
        description = "Free & open-source. No API key needed.",
        requiresApiKey = false,
        freeTier = "Unlimited"
    ),
    OPEN_WEATHER_MAP(
        displayName = "OpenWeatherMap",
        description = "Large dataset, global coverage.",
        requiresApiKey = true,
        apiKeyUrl = "https://openweathermap.org/api",
        freeTier = "1,000 calls/day"
    ),
    WEATHER_API(
        displayName = "WeatherAPI.com",
        description = "Detailed forecasts + air quality.",
        requiresApiKey = true,
        apiKeyUrl = "https://www.weatherapi.com/",
        freeTier = "1,000,000 calls/month"
    );
}
