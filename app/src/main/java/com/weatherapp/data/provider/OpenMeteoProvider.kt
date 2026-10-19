package com.weatherapp.data.provider

import com.weatherapp.data.model.*
import com.weatherapp.data.remote.api.OpenMeteoApi
import com.weatherapp.data.remote.api.OpenMeteoGeoApi
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class OpenMeteoProvider @Inject constructor(
    private val api: OpenMeteoApi,
    private val geoApi: OpenMeteoGeoApi
) : WeatherProvider {

    override val source = WeatherSource.OPEN_METEO

    override suspend fun getWeather(lat: Double, lon: Double): Result<WeatherData> = runCatching {
        val resp = api.getForecast(lat, lon)
        val c = resp.current
        val isDay = c.isDay == 1

        val current = CurrentWeather(
            tempC = c.temperature,
            feelsLikeC = c.feelsLike,
            condition = WeatherCondition(
                text = WmoCodeMapper.toDescription(c.weatherCode),
                icon = WmoCodeMapper.toIcon(c.weatherCode, isDay)
            ),
            humidity = c.humidity,
            windKph = c.windSpeed,
            windDirection = WmoCodeMapper.degToCompass(c.windDirection),
            uvIndex = 0.0,
            visibilityKm = c.visibility / 1000.0,
            pressureMb = c.pressure,
            dewPointC = c.dewPoint,
            isDay = isDay
        )

        val hourly = resp.hourly.time.mapIndexed { i, timeStr ->
            HourlyWeather(
                timeEpoch = parseIsoTime(timeStr),
                tempC = resp.hourly.temperature[i],
                feelsLikeC = resp.hourly.feelsLike[i],
                condition = WeatherCondition(
                    text = WmoCodeMapper.toDescription(resp.hourly.weatherCode[i]),
                    icon = WmoCodeMapper.toIcon(resp.hourly.weatherCode[i], resp.hourly.isDay[i] == 1)
                ),
                precipMm = resp.hourly.precip[i],
                precipProbability = resp.hourly.precipProb[i],
                windKph = resp.hourly.windSpeed[i],
                humidity = resp.hourly.humidity[i],
                isDay = resp.hourly.isDay[i] == 1
            )
        }.take(48)

        val daily = resp.daily.time.mapIndexed { i, dateStr ->
            DailyWeather(
                dateEpoch = parseIsoDate(dateStr),
                maxTempC = resp.daily.maxTemp[i],
                minTempC = resp.daily.minTemp[i],
                avgTempC = (resp.daily.maxTemp[i] + resp.daily.minTemp[i]) / 2.0,
                condition = WeatherCondition(
                    text = WmoCodeMapper.toDescription(resp.daily.weatherCode[i]),
                    icon = WmoCodeMapper.toIcon(resp.daily.weatherCode[i])
                ),
                precipMm = resp.daily.precipSum[i],
                precipProbability = resp.daily.precipProbMax[i],
                maxWindKph = resp.daily.maxWindSpeed[i],
                uvIndex = resp.daily.uvIndexMax[i],
                sunrise = resp.daily.sunrise[i].substringAfter("T"),
                sunset = resp.daily.sunset[i].substringAfter("T")
            )
        }

        WeatherData(
            location = Location("", "", "", lat, lon),
            current = current,
            hourly = hourly,
            daily = daily,
            source = WeatherSource.OPEN_METEO
        )
    }

    override suspend fun searchLocation(query: String): Result<List<LocationResult>> = runCatching {
        val resp = geoApi.search(query)
        resp.results?.map {
            LocationResult(it.name, it.admin1, it.country, it.latitude, it.longitude)
        } ?: emptyList()
    }

    private fun parseIsoTime(s: String): Long {
        return LocalDateTime.parse(s, DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm"))
            .atZone(ZoneId.systemDefault()).toEpochSecond()
    }

    private fun parseIsoDate(s: String): Long {
        return LocalDateTime.parse("${s}T00:00", DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm"))
            .atZone(ZoneId.systemDefault()).toEpochSecond()
    }
}
