package com.weatherapp.di

import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import com.weatherapp.BuildConfig
import com.weatherapp.data.provider.OpenWeatherMapProvider
import com.weatherapp.data.provider.WeatherApiProvider
import com.weatherapp.data.remote.api.*
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides @Singleton
    fun provideMoshi(): Moshi = Moshi.Builder().addLast(KotlinJsonAdapterFactory()).build()

    @Provides @Singleton
    fun provideOkHttp(): OkHttpClient = OkHttpClient.Builder()
        .addInterceptor(HttpLoggingInterceptor().apply { level = HttpLoggingInterceptor.Level.BASIC })
        .build()

    @Provides @Singleton
    fun provideOpenMeteoApi(client: OkHttpClient, moshi: Moshi): OpenMeteoApi =
        Retrofit.Builder().baseUrl("https://api.open-meteo.com/").client(client)
            .addConverterFactory(MoshiConverterFactory.create(moshi)).build().create(OpenMeteoApi::class.java)

    @Provides @Singleton
    fun provideOpenMeteoGeoApi(client: OkHttpClient, moshi: Moshi): OpenMeteoGeoApi =
        Retrofit.Builder().baseUrl("https://geocoding-api.open-meteo.com/").client(client)
            .addConverterFactory(MoshiConverterFactory.create(moshi)).build().create(OpenMeteoGeoApi::class.java)

    @Provides @Singleton
    fun provideOwmApi(client: OkHttpClient, moshi: Moshi): OwmApi =
        Retrofit.Builder().baseUrl("https://api.openweathermap.org/").client(client)
            .addConverterFactory(MoshiConverterFactory.create(moshi)).build().create(OwmApi::class.java)

    @Provides @Singleton
    fun provideOpenWeatherMapProvider(api: OwmApi): OpenWeatherMapProvider =
        OpenWeatherMapProvider(api, BuildConfig.OPEN_WEATHER_MAP_API_KEY)

    @Provides @Singleton
    fun provideWeatherApiService(client: OkHttpClient, moshi: Moshi): WeatherApiService =
        Retrofit.Builder().baseUrl("https://api.weatherapi.com/").client(client)
            .addConverterFactory(MoshiConverterFactory.create(moshi)).build().create(WeatherApiService::class.java)

    @Provides @Singleton
    fun provideWeatherApiProvider(api: WeatherApiService): WeatherApiProvider =
        WeatherApiProvider(api, BuildConfig.WEATHER_API_KEY)
}
