package com.weatherapp.ui.viewmodel

import android.annotation.SuppressLint
import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationTokenSource
import com.weatherapp.data.model.*
import com.weatherapp.data.provider.LocationResult
import com.weatherapp.data.provider.WeatherSource
import com.weatherapp.data.repository.UserPreferencesRepository
import com.weatherapp.data.repository.WeatherRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class WeatherViewModel @Inject constructor(
    application: Application,
    private val repository: WeatherRepository,
    private val prefsRepository: UserPreferencesRepository
) : AndroidViewModel(application) {

    val preferences = prefsRepository.preferences.stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(5_000), null
    )

    private val _weatherState = MutableStateFlow<WeatherUiState>(WeatherUiState.Loading)
    val weatherState: StateFlow<WeatherUiState> = _weatherState.asStateFlow()

    private val _searchResults = MutableStateFlow<List<LocationResult>>(emptyList())
    val searchResults: StateFlow<List<LocationResult>> = _searchResults.asStateFlow()

    private val _isRefreshing = MutableStateFlow(false)
    val isRefreshing: StateFlow<Boolean> = _isRefreshing.asStateFlow()

    private val fusedLocationClient = LocationServices.getFusedLocationProviderClient(application)

    init {
        viewModelScope.launch {
            preferences.filterNotNull().collect { prefs ->
                if (prefs.useDeviceLocation) fetchDeviceLocation(prefs.weatherSource)
                else if (prefs.savedLat != null && prefs.savedLon != null)
                    fetchWeather(prefs.savedLat, prefs.savedLon, prefs.weatherSource)
            }
        }
    }

    fun refresh() {
        viewModelScope.launch {
            val prefs = preferences.value ?: return@launch
            _isRefreshing.value = true
            if (prefs.useDeviceLocation) fetchDeviceLocation(prefs.weatherSource)
            else if (prefs.savedLat != null && prefs.savedLon != null)
                fetchWeather(prefs.savedLat, prefs.savedLon, prefs.weatherSource)
            _isRefreshing.value = false
        }
    }

    @SuppressLint("MissingPermission")
    fun fetchDeviceLocation(source: WeatherSource) {
        viewModelScope.launch {
            _weatherState.value = WeatherUiState.Loading
            val cts = CancellationTokenSource()
            fusedLocationClient.getCurrentLocation(Priority.PRIORITY_BALANCED_POWER_ACCURACY, cts.token)
                .addOnSuccessListener { loc ->
                    if (loc != null) viewModelScope.launch { fetchWeather(loc.latitude, loc.longitude, source) }
                    else _weatherState.value = WeatherUiState.Error("Unable to get device location. Try searching for a city.")
                }
                .addOnFailureListener { _weatherState.value = WeatherUiState.Error("Location error: ${it.message}") }
        }
    }

    fun fetchWeather(lat: Double, lon: Double, source: WeatherSource) {
        viewModelScope.launch {
            _weatherState.value = WeatherUiState.Loading
            repository.getWeather(lat, lon, source).fold(
                onSuccess = { _weatherState.value = WeatherUiState.Success(it) },
                onFailure = { _weatherState.value = WeatherUiState.Error(it.message ?: "Unknown error") }
            )
        }
    }

    fun searchLocations(query: String) {
        if (query.length < 2) { _searchResults.value = emptyList(); return }
        viewModelScope.launch {
            val source = preferences.value?.weatherSource ?: WeatherSource.OPEN_METEO
            repository.searchLocation(query, source).onSuccess { _searchResults.value = it }
        }
    }

    fun clearSearch() { _searchResults.value = emptyList() }

    fun selectLocation(result: LocationResult) {
        viewModelScope.launch { prefsRepository.setSavedLocation(result.lat, result.lon, result.name); clearSearch() }
    }

    fun setWeatherSource(source: WeatherSource) { viewModelScope.launch { prefsRepository.setWeatherSource(source) } }
    fun setTemperatureUnit(unit: TemperatureUnit) { viewModelScope.launch { prefsRepository.setTemperatureUnit(unit) } }
    fun setWindUnit(unit: WindUnit) { viewModelScope.launch { prefsRepository.setWindUnit(unit) } }
    fun setOwmApiKey(key: String) { viewModelScope.launch { prefsRepository.setOwmApiKey(key) } }
    fun setWeatherApiKey(key: String) { viewModelScope.launch { prefsRepository.setWeatherApiKey(key) } }
    fun setUseDeviceLocation() { viewModelScope.launch { prefsRepository.setUseDeviceLocation(true) } }
}
