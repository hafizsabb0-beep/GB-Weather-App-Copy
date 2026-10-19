package com.weatherapp.data.provider

import com.weatherapp.data.model.*
import com.weatherapp.data.remote.api.OwmApi
import com.weatherapp.data.remote.dto.OwmForecastItem
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class OpenWeatherMapProvider @Inject constructor(
    private val api: OwmApi,
    private val apiKey: String
) : WeatherProvider {

    override val source = WeatherSource.OPEN_WEATHER_MAP

    override suspend fun getWeather(lat: Double, lon: Double): Result<WeatherData> = runCatching {
        val current = api.getCurrentWeather(lat, lon, apiKey)
        val forecast = api.getForecast(lat, lon, apiKey)

        val w = current.weather.firstOrNull()
        val isDay = System.currentTimeMillis() / 1000 in current.sys.sunrise..current.sys.sunset

        val currentWeather = CurrentWeather(
            tempC = current.main.temp,
            feelsLikeC = current.main.feelsLike,
            condition = WeatherCondition(
                text = w?.description?.replaceFirstChar { it.uppercase() } ?: "Unknown",
                icon = OwmIconMapper.toIcon(w?.id ?: 800, w?.icon ?: "01d")
            ),
            humidity = current.main.humidity,
            windKph = current.wind.speed * 3.6,
            windDirection = OwmIconMapper.degToCompass(current.wind.deg),
            uvIndex = 0.0,
            visibilityKm = current.visibility / 1000.0,
            pressureMb = current.main.pressure,
            isDay = isDay
        )

        val hourlyItems = forecast.list.take(16)
        val hourly = hourlyItems.map { item ->
            val iw = item.weather.firstOrNull()
            HourlyWeather(
                timeEpoch = item.dt,
                tempC = item.main.temp,
                feelsLikeC = item.main.feelsLike,
                condition = WeatherCondition(
                    text = iw?.description?.replaceFirstChar { it.uppercase() } ?: "",
                    icon = OwmIconMapper.toIcon(iw?.id ?: 800, iw?.icon ?: "01d")
                ),
                precipMm = item.rain?.threeH ?: 0.0,
                precipProbability = (item.pop * 100).toInt(),
                windKph = item.wind.speed * 3.6,
                humidity = item.main.humidity
            )
        }

        val daily = forecast.list
            .groupBy { it.dtTxt.substringBefore(" ") }
            .entries.take(7)
            .map { (date, items) ->
                val dayW = items.maxByOrNull { it.main.temp }?.weather?.firstOrNull()
                DailyWeather(
                    dateEpoch = items.first().dt,
                    maxTempC = items.maxOf { it.main.tempMax },
                    minTempC = items.minOf { it.main.tempMin },
                    avgTempC = items.map { it.main.temp }.average(),
                    condition = WeatherCondition(
                        text = dayW?.description?.replaceFirstChar { it.uppercase() } ?: "",
                        icon = OwmIconMapper.toIcon(dayW?.id ?: 800, dayW?.icon ?: "01d")
                    ),
                    precipMm = items.sumOf { it.rain?.threeH ?: 0.0 },
                    precipProbability = (items.maxOf { it.pop } * 100).toInt(),
                    maxWindKph = items.maxOf { it.wind.speed } * 3.6,
                    uvIndex = 0.0
                )
            }

        WeatherData(
            location = Location(name = current.name, country = current.sys.country, latitude = lat, longitude = lon),
            current = currentWeather,
            hourly = hourly,
            daily = daily,
            source = WeatherSource.OPEN_WEATHER_MAP
        )
    }

    override suspend fun searchLocation(query: String): Result<List<LocationResult>> = runCatching {
        api.searchLocation(query, apiKey = apiKey).map {
            LocationResult(it.name, it.state, it.country, it.lat, it.lon)
        }
    }
}
