package com.weatherapp.data.remote.api

import com.weatherapp.data.remote.dto.WapiSearchResult
import com.weatherapp.data.remote.dto.WeatherApiResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface WeatherApiService {
    @GET("v1/forecast.json")
    suspend fun getForecast(
        @Query("key") apiKey: String,
        @Query("q") location: String,
        @Query("days") days: Int = 7,
        @Query("aqi") aqi: String = "no",
        @Query("alerts") alerts: String = "yes"
    ): WeatherApiResponse

    @GET("v1/search.json")
    suspend fun searchLocation(
        @Query("key") apiKey: String,
        @Query("q") query: String
    ): List<WapiSearchResult>
}
