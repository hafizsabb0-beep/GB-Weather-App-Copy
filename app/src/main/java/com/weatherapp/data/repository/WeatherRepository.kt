package com.weatherapp.data.repository

import com.weatherapp.data.model.WeatherData
import com.weatherapp.data.provider.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class WeatherRepository @Inject constructor(
    private val openMeteoProvider: OpenMeteoProvider,
    private val openWeatherMapProvider: OpenWeatherMapProvider,
    private val weatherApiProvider: WeatherApiProvider
) {
    private fun providerFor(source: WeatherSource): WeatherProvider = when (source) {
        WeatherSource.OPEN_METEO -> openMeteoProvider
        WeatherSource.OPEN_WEATHER_MAP -> openWeatherMapProvider
        WeatherSource.WEATHER_API -> weatherApiProvider
    }

    suspend fun getWeather(lat: Double, lon: Double, source: WeatherSource): Result<WeatherData> =
        providerFor(source).getWeather(lat, lon)

    suspend fun searchLocation(query: String, source: WeatherSource): Result<List<LocationResult>> =
        providerFor(source).searchLocation(query)
}
