package com.weatherapp.data.remote.dto

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class WeatherApiResponse(
    val location: WapiLocation,
    val current: WapiCurrent,
    val forecast: WapiForecast? = null,
    val alerts: WapiAlerts? = null
)

@JsonClass(generateAdapter = true)
data class WapiLocation(
    val name: String,
    val region: String,
    val country: String,
    val lat: Double,
    val lon: Double,
    @Json(name = "localtime_epoch") val localtimeEpoch: Long
)

@JsonClass(generateAdapter = true)
data class WapiCurrent(
    @Json(name = "temp_c") val tempC: Double,
    @Json(name = "feelslike_c") val feelsLikeC: Double,
    val condition: WapiCondition,
    @Json(name = "wind_kph") val windKph: Double,
    @Json(name = "wind_dir") val windDir: String,
    val humidity: Int,
    @Json(name = "vis_km") val visKm: Double,
    @Json(name = "pressure_mb") val pressureMb: Double,
    val uv: Double,
    @Json(name = "dewpoint_c") val dewpointC: Double = 0.0,
    @Json(name = "is_day") val isDay: Int = 1
)

@JsonClass(generateAdapter = true)
data class WapiCondition(val text: String, val icon: String, val code: Int)

@JsonClass(generateAdapter = true)
data class WapiForecast(val forecastday: List<WapiForecastDay>)

@JsonClass(generateAdapter = true)
data class WapiForecastDay(
    val date: String,
    @Json(name = "date_epoch") val dateEpoch: Long,
    val day: WapiDay,
    val astro: WapiAstro,
    val hour: List<WapiHour>
)

@JsonClass(generateAdapter = true)
data class WapiDay(
    @Json(name = "maxtemp_c") val maxTempC: Double,
    @Json(name = "mintemp_c") val minTempC: Double,
    @Json(name = "avgtemp_c") val avgTempC: Double,
    @Json(name = "maxwind_kph") val maxWindKph: Double,
    @Json(name = "totalprecip_mm") val totalPrecipMm: Double,
    @Json(name = "daily_chance_of_rain") val chanceOfRain: Int,
    val condition: WapiCondition,
    val uv: Double
)

@JsonClass(generateAdapter = true)
data class WapiAstro(val sunrise: String, val sunset: String)

@JsonClass(generateAdapter = true)
data class WapiHour(
    @Json(name = "time_epoch") val timeEpoch: Long,
    @Json(name = "temp_c") val tempC: Double,
    @Json(name = "feelslike_c") val feelsLikeC: Double,
    val condition: WapiCondition,
    @Json(name = "precip_mm") val precipMm: Double,
    @Json(name = "chance_of_rain") val chanceOfRain: Int,
    @Json(name = "wind_kph") val windKph: Double,
    val humidity: Int,
    @Json(name = "is_day") val isDay: Int = 1
)

@JsonClass(generateAdapter = true)
data class WapiAlerts(val alert: List<WapiAlert> = emptyList())

@JsonClass(generateAdapter = true)
data class WapiAlert(
    val headline: String,
    val severity: String,
    val desc: String,
    val effective: String = "",
    val expires: String = ""
)

@JsonClass(generateAdapter = true)
data class WapiSearchResult(
    val name: String,
    val region: String,
    val country: String,
    val lat: Double,
    val lon: Double
)
