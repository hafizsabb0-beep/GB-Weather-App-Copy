package com.weatherapp.data.remote.dto

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class OpenMeteoResponse(
    val latitude: Double,
    val longitude: Double,
    @Json(name = "current") val current: OpenMeteoCurrent,
    @Json(name = "hourly") val hourly: OpenMeteoHourly,
    @Json(name = "daily") val daily: OpenMeteoDaily
)

@JsonClass(generateAdapter = true)
data class OpenMeteoCurrent(
    @Json(name = "temperature_2m") val temperature: Double,
    @Json(name = "apparent_temperature") val feelsLike: Double,
    @Json(name = "weather_code") val weatherCode: Int,
    @Json(name = "wind_speed_10m") val windSpeed: Double,
    @Json(name = "wind_direction_10m") val windDirection: Int,
    @Json(name = "relative_humidity_2m") val humidity: Int,
    @Json(name = "surface_pressure") val pressure: Double,
    @Json(name = "visibility") val visibility: Double,
    @Json(name = "dew_point_2m") val dewPoint: Double = 0.0,
    @Json(name = "is_day") val isDay: Int = 1
)

@JsonClass(generateAdapter = true)
data class OpenMeteoHourly(
    val time: List<String>,
    @Json(name = "temperature_2m") val temperature: List<Double>,
    @Json(name = "apparent_temperature") val feelsLike: List<Double>,
    @Json(name = "weather_code") val weatherCode: List<Int>,
    @Json(name = "precipitation_probability") val precipProb: List<Int>,
    @Json(name = "precipitation") val precip: List<Double>,
    @Json(name = "wind_speed_10m") val windSpeed: List<Double>,
    @Json(name = "relative_humidity_2m") val humidity: List<Int>,
    @Json(name = "is_day") val isDay: List<Int>
)

@JsonClass(generateAdapter = true)
data class OpenMeteoDaily(
    val time: List<String>,
    @Json(name = "temperature_2m_max") val maxTemp: List<Double>,
    @Json(name = "temperature_2m_min") val minTemp: List<Double>,
    @Json(name = "weather_code") val weatherCode: List<Int>,
    @Json(name = "precipitation_sum") val precipSum: List<Double>,
    @Json(name = "precipitation_probability_max") val precipProbMax: List<Int>,
    @Json(name = "wind_speed_10m_max") val maxWindSpeed: List<Double>,
    @Json(name = "uv_index_max") val uvIndexMax: List<Double>,
    @Json(name = "sunrise") val sunrise: List<String>,
    @Json(name = "sunset") val sunset: List<String>
)

@JsonClass(generateAdapter = true)
data class OpenMeteoGeoResponse(
    val results: List<OpenMeteoGeoResult>? = null
)

@JsonClass(generateAdapter = true)
data class OpenMeteoGeoResult(
    val name: String,
    val latitude: Double,
    val longitude: Double,
    val country: String = "",
    val admin1: String = ""
)
