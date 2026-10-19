package com.weatherapp.data.provider

import com.weatherapp.data.model.WeatherIcon

/**
 * Maps WMO weather interpretation codes (used by Open-Meteo) to our icon enum.
 * https://open-meteo.com/en/docs#weathervariables
 */
object WmoCodeMapper {
    fun toIcon(code: Int, isDay: Boolean = true): WeatherIcon = when (code) {
        0 -> if (isDay) WeatherIcon.CLEAR_DAY else WeatherIcon.CLEAR_NIGHT
        1, 2 -> if (isDay) WeatherIcon.PARTLY_CLOUDY_DAY else WeatherIcon.PARTLY_CLOUDY_NIGHT
        3 -> WeatherIcon.OVERCAST
        45, 48 -> WeatherIcon.FOG
        51, 53, 55 -> WeatherIcon.DRIZZLE
        56, 57 -> WeatherIcon.SLEET
        61, 63 -> WeatherIcon.RAIN
        65 -> WeatherIcon.HEAVY_RAIN
        66, 67 -> WeatherIcon.SLEET
        71, 73, 75, 77 -> WeatherIcon.SNOW
        80, 81, 82 -> WeatherIcon.RAIN
        85, 86 -> WeatherIcon.SNOW
        95 -> WeatherIcon.THUNDERSTORM
        96, 99 -> WeatherIcon.THUNDERSTORM
        else -> WeatherIcon.UNKNOWN
    }

    fun toDescription(code: Int): String = when (code) {
        0 -> "Clear sky"
        1 -> "Mainly clear"
        2 -> "Partly cloudy"
        3 -> "Overcast"
        45 -> "Fog"
        48 -> "Icy fog"
        51 -> "Light drizzle"
        53 -> "Moderate drizzle"
        55 -> "Dense drizzle"
        61 -> "Slight rain"
        63 -> "Moderate rain"
        65 -> "Heavy rain"
        71 -> "Slight snow"
        73 -> "Moderate snow"
        75 -> "Heavy snow"
        77 -> "Snow grains"
        80 -> "Slight showers"
        81 -> "Moderate showers"
        82 -> "Heavy showers"
        85 -> "Slight snow showers"
        86 -> "Heavy snow showers"
        95 -> "Thunderstorm"
        96 -> "Thunderstorm with hail"
        99 -> "Thunderstorm with heavy hail"
        else -> "Unknown"
    }

    fun degToCompass(deg: Int): String {
        val dirs = listOf("N","NNE","NE","ENE","E","ESE","SE","SSE","S","SSW","SW","WSW","W","WNW","NW","NNW")
        return dirs[((deg / 22.5) + 0.5).toInt() % 16]
    }
}
