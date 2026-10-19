package com.weatherapp.data.remote.api

import com.weatherapp.data.remote.dto.OwmCurrentResponse
import com.weatherapp.data.remote.dto.OwmForecastResponse
import com.weatherapp.data.remote.dto.OwmGeoResult
import retrofit2.http.GET
import retrofit2.http.Query

interface OwmApi {
    @GET("data/2.5/weather")
    suspend fun getCurrentWeather(
        @Query("lat") lat: Double,
        @Query("lon") lon: Double,
        @Query("appid") apiKey: String,
        @Query("units") units: String = "metric"
    ): OwmCurrentResponse

    @GET("data/2.5/forecast")
    suspend fun getForecast(
        @Query("lat") lat: Double,
        @Query("lon") lon: Double,
        @Query("appid") apiKey: String,
        @Query("units") units: String = "metric"
    ): OwmForecastResponse

    @GET("geo/1.0/direct")
    suspend fun searchLocation(
        @Query("q") query: String,
        @Query("limit") limit: Int = 10,
        @Query("appid") apiKey: String
    ): List<OwmGeoResult>
}
