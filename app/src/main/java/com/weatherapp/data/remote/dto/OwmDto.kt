package com.weatherapp.data.remote.dto

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class OwmCurrentResponse(
    val name: String,
    val sys: OwmSys,
    val coord: OwmCoord,
    val main: OwmMain,
    val weather: List<OwmWeather>,
    val wind: OwmWind,
    val visibility: Int = 10000,
    val dt: Long
)

@JsonClass(generateAdapter = true)
data class OwmForecastResponse(
    val city: OwmCity,
    val list: List<OwmForecastItem>
)

@JsonClass(generateAdapter = true)
data class OwmCity(
    val name: String,
    val country: String,
    val coord: OwmCoord,
    val sunrise: Long,
    val sunset: Long
)

@JsonClass(generateAdapter = true)
data class OwmForecastItem(
    val dt: Long,
    val main: OwmMain,
    val weather: List<OwmWeather>,
    val wind: OwmWind,
    val pop: Double = 0.0,
    @Json(name = "dt_txt") val dtTxt: String,
    val rain: OwmRain? = null
)

@JsonClass(generateAdapter = true)
data class OwmMain(
    val temp: Double,
    @Json(name = "feels_like") val feelsLike: Double,
    @Json(name = "temp_min") val tempMin: Double,
    @Json(name = "temp_max") val tempMax: Double,
    val pressure: Double,
    val humidity: Int
)

@JsonClass(generateAdapter = true)
data class OwmWeather(val id: Int, val main: String, val description: String, val icon: String)

@JsonClass(generateAdapter = true)
data class OwmWind(val speed: Double, val deg: Int = 0)

@JsonClass(generateAdapter = true)
data class OwmSys(val country: String = "", val sunrise: Long = 0, val sunset: Long = 0)

@JsonClass(generateAdapter = true)
data class OwmCoord(val lat: Double, val lon: Double)

@JsonClass(generateAdapter = true)
data class OwmRain(@Json(name = "3h") val threeH: Double = 0.0)

@JsonClass(generateAdapter = true)
data class OwmGeoResult(
    val name: String,
    val lat: Double,
    val lon: Double,
    val country: String = "",
    val state: String = ""
)
