package com.weatherapp.data.provider

import com.weatherapp.data.model.WeatherIcon

object OwmIconMapper {
    fun toIcon(id: Int, iconCode: String): WeatherIcon {
        val isDay = iconCode.endsWith("d")
        return when (id) {
            in 200..232 -> WeatherIcon.THUNDERSTORM
            in 300..321 -> WeatherIcon.DRIZZLE
            in 500..504 -> WeatherIcon.RAIN
            511 -> WeatherIcon.SLEET
            in 520..531 -> WeatherIcon.HEAVY_RAIN
            in 600..622 -> WeatherIcon.SNOW
            in 700..781 -> WeatherIcon.FOG
            800 -> if (isDay) WeatherIcon.CLEAR_DAY else WeatherIcon.CLEAR_NIGHT
            801 -> if (isDay) WeatherIcon.PARTLY_CLOUDY_DAY else WeatherIcon.PARTLY_CLOUDY_NIGHT
            802, 803 -> WeatherIcon.PARTLY_CLOUDY_DAY
            804 -> WeatherIcon.OVERCAST
            else -> WeatherIcon.UNKNOWN
        }
    }

    fun degToCompass(deg: Int): String {
        val dirs = listOf("N","NNE","NE","ENE","E","ESE","SE","SSE","S","SSW","SW","WSW","W","WNW","NW","NNW")
        return dirs[((deg / 22.5) + 0.5).toInt() % 16]
    }
}
