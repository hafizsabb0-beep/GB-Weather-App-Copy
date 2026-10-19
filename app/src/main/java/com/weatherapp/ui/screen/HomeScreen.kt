package com.weatherapp.ui.screen

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.material3.pulltorefresh.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.weatherapp.data.model.*
import com.weatherapp.ui.components.*
import com.weatherapp.ui.viewmodel.WeatherViewModel
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(onNavigateToSettings: () -> Unit, viewModel: WeatherViewModel = hiltViewModel()) {
    val weatherState by viewModel.weatherState.collectAsState()
    val prefs by viewModel.preferences.collectAsState()
    val searchResults by viewModel.searchResults.collectAsState()
    val isRefreshing by viewModel.isRefreshing.collectAsState()

    val unit = prefs?.temperatureUnit ?: TemperatureUnit.CELSIUS
    val windUnit = prefs?.windUnit ?: WindUnit.KPH
    var searchQuery by remember { mutableStateOf("") }
    var isSearching by remember { mutableStateOf(false) }
    val keyboardController = LocalSoftwareKeyboardController.current
    val isDay = (weatherState as? WeatherUiState.Success)?.data?.current?.isDay ?: true

    SkyGradientBackground(isDay = isDay, modifier = Modifier.fillMaxSize()) {
        PullToRefreshBox(isRefreshing = isRefreshing, onRefresh = { viewModel.refresh() }, modifier = Modifier.fillMaxSize()) {
            Column(Modifier.fillMaxSize().statusBarsPadding()) {
                Row(Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp), verticalAlignment = Alignment.CenterVertically) {
                    if (isSearching) {
                        OutlinedTextField(
                            value = searchQuery,
                            onValueChange = { searchQuery = it; viewModel.searchLocations(it) },
                            placeholder = { Text("Search city…", color = Color.White.copy(alpha = 0.6f)) },
                            singleLine = true,
                            modifier = Modifier.weight(1f),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = Color.White, unfocusedTextColor = Color.White,
                                focusedBorderColor = Color.White, unfocusedBorderColor = Color.White.copy(alpha = 0.5f), cursorColor = Color.White
                            ),
                            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                            keyboardActions = KeyboardActions(onSearch = { keyboardController?.hide() })
                        )
                        IconButton(onClick = { isSearching = false; searchQuery = ""; viewModel.clearSearch() }) {
                            Icon(Icons.Default.Close, null, tint = Color.White)
                        }
                    } else {
                        val locationName = when (val s = weatherState) {
                            is WeatherUiState.Success -> s.data.location.name.ifEmpty { "My Location" }
                            else -> "Weather"
                        }
                        Text(locationName, style = MaterialTheme.typography.titleLarge.copy(color = Color.White, fontWeight = FontWeight.SemiBold), modifier = Modifier.weight(1f))
                        IconButton(onClick = { isSearching = true }) { Icon(Icons.Default.Search, "Search", tint = Color.White) }
                        IconButton(onClick = onNavigateToSettings) { Icon(Icons.Default.Settings, "Settings", tint = Color.White) }
                    }
                }

                if (searchResults.isNotEmpty()) {
                    Card(Modifier.fillMaxWidth().padding(horizontal = 16.dp), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)) {
                        searchResults.forEach { result ->
                            ListItem(
                                headlineContent = { Text(result.name) },
                                supportingContent = { Text("${result.region}, ${result.country}") },
                                leadingContent = { Icon(Icons.Default.LocationOn, null) },
                                modifier = Modifier.fillMaxWidth().clickable { viewModel.selectLocation(result) }
                            )
                            HorizontalDivider()
                        }
                    }
                }

                when (val state = weatherState) {
                    is WeatherUiState.Loading -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { CircularProgressIndicator(color = Color.White) }
                    is WeatherUiState.Error -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.padding(32.dp)) {
                            Text("⚠️", fontSize = 48.sp); Spacer(Modifier.height(16.dp))
                            Text(state.message, color = Color.White, style = MaterialTheme.typography.bodyLarge); Spacer(Modifier.height(16.dp))
                            Button(onClick = { viewModel.refresh() }) { Text("Retry") }
                        }
                    }
                    is WeatherUiState.Success -> {
                        val data = state.data
                        LazyColumn(Modifier.fillMaxSize(), contentPadding = PaddingValues(bottom = 24.dp)) {
                            if (data.alerts.isNotEmpty()) items(data.alerts) { AlertBanner(it) }
                            item {
                                Column(Modifier.fillMaxWidth().padding(top = 16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text("Updated ${SimpleDateFormat("h:mm a", Locale.getDefault()).format(Date(data.updatedAt))}",
                                        style = MaterialTheme.typography.labelSmall.copy(color = Color.White.copy(alpha = 0.6f)))
                                    Spacer(Modifier.height(8.dp))
                                    CurrentTempDisplay(data.current, unit)
                                    Spacer(Modifier.height(4.dp))
                                    SourceBadge(data.source.displayName)
                                }
                            }
                            item { Spacer(Modifier.height(20.dp)); WeatherDetailGrid(data.current, windUnit, Modifier.padding(horizontal = 16.dp)) }
                            item {
                                Spacer(Modifier.height(20.dp))
                                Text("Hourly", style = MaterialTheme.typography.titleMedium.copy(color = Color.White, fontWeight = FontWeight.SemiBold), modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp))
                                LazyRow(contentPadding = PaddingValues(horizontal = 16.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                    items(data.hourly.take(24)) { HourlyCard(it, unit) }
                                }
                            }
                            item {
                                Spacer(Modifier.height(20.dp))
                                Card(Modifier.fillMaxWidth().padding(horizontal = 16.dp), colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.1f))) {
                                    Column(Modifier.padding(16.dp)) {
                                        Text("7-Day Forecast", style = MaterialTheme.typography.titleMedium.copy(color = Color.White, fontWeight = FontWeight.SemiBold))
                                        HorizontalDivider(Modifier.padding(vertical = 8.dp), color = Color.White.copy(alpha = 0.3f))
                                        data.daily.forEach { DailyRow(it, unit) }
                                    }
                                }
                            }
                            val firstDay = data.daily.firstOrNull()
                            if (firstDay != null && firstDay.sunrise.isNotEmpty()) {
                                item {
                                    Spacer(Modifier.height(12.dp))
                                    Row(Modifier.fillMaxWidth().padding(horizontal = 16.dp), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                                        SunCard("🌅", "Sunrise", firstDay.sunrise, Modifier.weight(1f))
                                        SunCard("🏗️", "Sunset", firstDay.sunset, Modifier.weight(1f))
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun SunCard(emoji: String, label: String, time: String, modifier: Modifier = Modifier) {
    Card(modifier = modifier, colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.1f))) {
        Row(Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.Center) {
            Text(emoji, fontSize = 24.sp, modifier = Modifier.padding(end = 8.dp))
            Column {
                Text(label, style = MaterialTheme.typography.labelSmall.copy(color = Color.White.copy(alpha = 0.7f)))
                Text(time, style = MaterialTheme.typography.bodyMedium.copy(color = Color.White, fontWeight = FontWeight.Medium))
            }
        }
    }
}
