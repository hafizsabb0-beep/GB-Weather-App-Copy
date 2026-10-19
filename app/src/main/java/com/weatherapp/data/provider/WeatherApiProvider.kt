package com.weatherapp.data.provider

import com.weatherapp.data.model.*
import com.weatherapp.data.remote.api.WeatherApiService
import com.weatherapp.data.remote.dto.WapiAlert
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class WeatherApiProvider @Inject constructor(
    private val api: WeatherApiService,
    private val apiKey: String
) : WeatherProvider {

    override val source = WeatherSource.WEATHER_API

    override suspend fun getWeather(lat: Double, lon: Double): Result<WeatherData> = runCatching {
        val resp = api.getForecast(apiKey = apiKey, location = "$lat,$lon", days = 7, alerts = "yes")

        val c = resp.current
        val current = CurrentWeather(
            tempC = c.tempC,
            feelsLikeC = c.feelsLikeC,
            condition = WeatherCondition(
                text = c.condition.text,
                icon = wapiConditionToIcon(c.condition.code, c.isDay == 1)
            ),
            humidity = c.humidity,
            windKph = c.windKph,
            windDirection = c.windDir,
            uvIndex = c.uv,
            visibilityKm = c.visKm,
            pressureMb = c.pressureMb,
            dewPointC = c.dewpointC,
            isDay = c.isDay == 1
        )

        val hourly = resp.forecast?.forecastday?.flatMap { day ->
            day.hour.map { h ->
                HourlyWeather(
                    timeEpoch = h.timeEpoch,
                    tempC = h.tempC,
                    feelsLikeC = h.feelsLikeC,
                    condition = WeatherCondition(
                        text = h.condition.text,
                        icon = wapiConditionToIcon(h.condition.code, h.isDay == 1)
                    ),
                    precipMm = h.precipMm,
                    precipProbability = h.chanceOfRain,
                    windKph = h.windKph,
                    humidity = h.humidity,
                    isDay = h.isDay == 1
                )
            }
        } ?: emptyList()

        val daily = resp.forecast?.forecastday?.map { day ->
            DailyWeather(
                dateEpoch = day.dateEpoch,
                maxTempC = day.day.maxTempC,
                minTempC = day.day.minTempC,
                avgTempC = day.day.avgTempC,
                condition = WeatherCondition(
                    text = day.day.condition.text,
                    icon = wapiConditionToIcon(day.day.condition.code)
                ),
                precipMm = day.day.totalPrecipMm,
                precipProbability = day.day.chanceOfRain,
                maxWindKph = day.day.maxWindKph,
                uvIndex = day.day.uv,
                sunrise = day.astro.sunrise,
                sunset = day.astro.sunset
            )
        } ?: emptyList()

        val alerts = resp.alerts?.alert?.map { a ->
            WeatherAlert(
                headline = a.headline,
                severity = parseSeverity(a.severity),
                description = a.desc,
                effective = a.effective,
                expires = a.expires
            )
        } ?: emptyList()

        WeatherData(
            location = Location(
                name = resp.location.name,
                region = resp.location.region,
                country = resp.location.country,
                latitude = resp.location.lat,
                longitude = resp.location.lon
            ),
            current = current,
            hourly = hourly,
            daily = daily,
            alerts = alerts,
            source = WeatherSource.WEATHER_API
        )
    }

    override suspend fun searchLocation(query: String): Result<List<LocationResult>> = runCatching {
        api.searchLocation(apiKey, query).map {
            LocationResult(it.name, it.region, it.country, it.lat, it.lon)
        }
    }

    private fun wapiConditionToIcon(code: Int, isDay: Boolean = true): WeatherIcon = when (code) {
        1000 -> if (isDay) WeatherIcon.CLEAR_DAY else WeatherIcon.CLEAR_NIGHT
        1003 -> if (isDay) WeatherIcon.PARTLY_CLOUDY_DAY else WeatherIcon.PARTLY_CLOUDY_NIGHT
        1006 -> WeatherIcon.CLOUDY
        1009 -> WeatherIcon.OVERCAST
        1030, 1135, 1147 -> WeatherIcon.FOG
        1063, 1180, 1183 -> WeatherIcon.RAIN
        1066, 1210, 1213 -> WeatherIcon.SNOW
        1069, 1204, 1207 -> WeatherIcon.SLEET
        1072, 1150, 1153 -> WeatherIcon.DRIZZLE
        1087, 1273, 1276 -> WeatherIcon.THUNDERSTORM
        1114, 1216, 1219, 1222, 1225 -> WeatherIcon.SNOW
        1186, 1189, 1192, 1195 -> WeatherIcon.RAIN
        1198, 1201 -> WeatherIcon.SLEET
        1240, 1243, 1246 -> WeatherIcon.HEAVY_RAIN
        1279, 1282 -> WeatherIcon.THUNDERSTORM
        else -> WeatherIcon.UNKNOWN
    }

    private fun parseSeverity(s: String): AlertSeverity = when (s.lowercase()) {
        "extreme" -> AlertSeverity.EXTREME
        "severe" -> AlertSeverity.SEVERE
        "moderate" -> AlertSeverity.MODERATE
        else -> AlertSeverity.MINOR
    }
}
