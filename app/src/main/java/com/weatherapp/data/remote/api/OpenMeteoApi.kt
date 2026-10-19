package com.weatherapp.data.remote.api

import com.weatherapp.data.remote.dto.OpenMeteoGeoResponse
import com.weatherapp.data.remote.dto.OpenMeteoResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface OpenMeteoApi {
    @GET("v1/forecast")
    suspend fun getForecast(
        @Query("latitude") lat: Double,
        @Query("longitude") lon: Double,
        @Query("current") current: String = "temperature_2m,apparent_temperature,relative_humidity_2m,weather_code,wind_speed_10m,wind_direction_10m,surface_pressure,visibility,dew_point_2m,is_day",
        @Query("hourly") hourly: String = "temperature_2m,apparent_temperature,weather_code,precipitation_probability,precipitation,wind_speed_10m,relative_humidity_2m,is_day",
        @Query("daily") daily: String = "temperature_2m_max,temperature_2m_min,weather_code,precipitation_sum,precipitation_probability_max,wind_speed_10m_max,uv_index_max,sunrise,sunset",
        @Query("forecast_days") forecastDays: Int = 7,
        @Query("timezone") timezone: String = "auto"
    ): OpenMeteoResponse
}

interface OpenMeteoGeoApi {
    @GET("v1/search")
    suspend fun search(
        @Query("name") name: String,
        @Query("count") count: Int = 10,
        @Query("language") language: String = "en",
        @Query("format") format: String = "json"
    ): OpenMeteoGeoResponse
}
