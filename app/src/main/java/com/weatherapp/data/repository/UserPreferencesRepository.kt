package com.weatherapp.data.repository

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import com.weatherapp.data.model.TemperatureUnit
import com.weatherapp.data.model.WindUnit
import com.weatherapp.data.provider.WeatherSource
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore("weather_prefs")

data class UserPreferences(
    val weatherSource: WeatherSource = WeatherSource.OPEN_METEO,
    val temperatureUnit: TemperatureUnit = TemperatureUnit.CELSIUS,
    val windUnit: WindUnit = WindUnit.KPH,
    val savedLat: Double? = null,
    val savedLon: Double? = null,
    val savedLocationName: String = "",
    val useDeviceLocation: Boolean = true,
    val owmApiKey: String = "",
    val weatherApiKey: String = ""
)

@Singleton
class UserPreferencesRepository @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private object Keys {
        val SOURCE = stringPreferencesKey("weather_source")
        val TEMP_UNIT = stringPreferencesKey("temp_unit")
        val WIND_UNIT = stringPreferencesKey("wind_unit")
        val LAT = doublePreferencesKey("saved_lat")
        val LON = doublePreferencesKey("saved_lon")
        val LOCATION_NAME = stringPreferencesKey("location_name")
        val USE_DEVICE_LOCATION = booleanPreferencesKey("use_device_location")
        val OWM_API_KEY = stringPreferencesKey("owm_api_key")
        val WAPI_API_KEY = stringPreferencesKey("wapi_api_key")
    }

    val preferences: Flow<UserPreferences> = context.dataStore.data.map { prefs ->
        UserPreferences(
            weatherSource = runCatching {
                WeatherSource.valueOf(prefs[Keys.SOURCE] ?: WeatherSource.OPEN_METEO.name)
            }.getOrDefault(WeatherSource.OPEN_METEO),
            temperatureUnit = runCatching {
                TemperatureUnit.valueOf(prefs[Keys.TEMP_UNIT] ?: TemperatureUnit.CELSIUS.name)
            }.getOrDefault(TemperatureUnit.CELSIUS),
            windUnit = runCatching {
                WindUnit.valueOf(prefs[Keys.WIND_UNIT] ?: WindUnit.KPH.name)
            }.getOrDefault(WindUnit.KPH),
            savedLat = prefs[Keys.LAT],
            savedLon = prefs[Keys.LON],
            savedLocationName = prefs[Keys.LOCATION_NAME] ?: "",
            useDeviceLocation = prefs[Keys.USE_DEVICE_LOCATION] ?: true,
            owmApiKey = prefs[Keys.OWM_API_KEY] ?: "",
            weatherApiKey = prefs[Keys.WAPI_API_KEY] ?: ""
        )
    }

    suspend fun setWeatherSource(source: WeatherSource) { context.dataStore.edit { it[Keys.SOURCE] = source.name } }
    suspend fun setTemperatureUnit(unit: TemperatureUnit) { context.dataStore.edit { it[Keys.TEMP_UNIT] = unit.name } }
    suspend fun setWindUnit(unit: WindUnit) { context.dataStore.edit { it[Keys.WIND_UNIT] = unit.name } }
    suspend fun setSavedLocation(lat: Double, lon: Double, name: String) {
        context.dataStore.edit {
            it[Keys.LAT] = lat; it[Keys.LON] = lon
            it[Keys.LOCATION_NAME] = name; it[Keys.USE_DEVICE_LOCATION] = false
        }
    }
    suspend fun setUseDeviceLocation(use: Boolean) { context.dataStore.edit { it[Keys.USE_DEVICE_LOCATION] = use } }
    suspend fun setOwmApiKey(key: String) { context.dataStore.edit { it[Keys.OWM_API_KEY] = key } }
    suspend fun setWeatherApiKey(key: String) { context.dataStore.edit { it[Keys.WAPI_API_KEY] = key } }
}
