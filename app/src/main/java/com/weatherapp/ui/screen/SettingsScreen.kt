package com.weatherapp.ui.screen

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.weatherapp.data.model.TemperatureUnit
import com.weatherapp.data.model.WindUnit
import com.weatherapp.data.provider.WeatherSource
import com.weatherapp.ui.viewmodel.WeatherViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(onBack: () -> Unit, viewModel: WeatherViewModel = hiltViewModel()) {
    val prefs by viewModel.preferences.collectAsState()
    Scaffold(
        topBar = { TopAppBar(title = { Text("Settings") }, navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.Default.ArrowBack, "Back") } }) }
    ) { padding ->
        LazyColumn(Modifier.fillMaxSize().padding(padding), contentPadding = PaddingValues(bottom = 32.dp)) {
            item {
                SettingsSectionHeader("Weather Data Source")
                WeatherSource.values().forEach { source ->
                    SourceCard(source = source, isSelected = prefs?.weatherSource == source, onClick = { viewModel.setWeatherSource(source) })
                }
            }
            item {
                SettingsSectionHeader("API Keys")
                Text("Open-Meteo requires no key. For other sources, enter your key below.",
                    style = MaterialTheme.typography.bodySmall, modifier = Modifier.padding(horizontal = 16.dp),
                    color = MaterialTheme.colorScheme.onSurfaceVariant)
                Spacer(Modifier.height(8.dp))
                ApiKeyField("OpenWeatherMap API Key", prefs?.owmApiKey ?: "", "Get free key at openweathermap.org/api") { viewModel.setOwmApiKey(it) }
                ApiKeyField("WeatherAPI.com API Key", prefs?.weatherApiKey ?: "", "Get free key at weatherapi.com") { viewModel.setWeatherApiKey(it) }
            }
            item {
                SettingsSectionHeader("Units")
                SegmentedPreference("Temperature", listOf("°C" to TemperatureUnit.CELSIUS, "°F" to TemperatureUnit.FAHRENHEIT), prefs?.temperatureUnit ?: TemperatureUnit.CELSIUS) { viewModel.setTemperatureUnit(it) }
                SegmentedPreference("Wind Speed", listOf("km/h" to WindUnit.KPH, "mph" to WindUnit.MPH, "m/s" to WindUnit.MS), prefs?.windUnit ?: WindUnit.KPH) { viewModel.setWindUnit(it) }
            }
            item {
                SettingsSectionHeader("About Sources")
                WeatherSource.values().forEach { source ->
                    ListItem(
                        headlineContent = { Text(source.displayName, fontWeight = FontWeight.Medium) },
                        supportingContent = { Column { Text(source.description); if (source.freeTier.isNotEmpty()) Text("Free tier: ${source.freeTier}") } },
                        leadingContent = { Icon(if (source.requiresApiKey) Icons.Default.Key else Icons.Default.Lock, null,
                            tint = if (source.requiresApiKey) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.primary) }
                    )
                    HorizontalDivider(Modifier.padding(horizontal = 16.dp))
                }
            }
        }
    }
}

@Composable
fun SourceCard(source: WeatherSource, isSelected: Boolean, onClick: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 6.dp).clickable(onClick = onClick),
        colors = CardDefaults.cardColors(containerColor = if (isSelected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surface),
        border = if (isSelected) CardDefaults.outlinedCardBorder() else null
    ) {
        Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Column(Modifier.weight(1f)) {
                Text(source.displayName, style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold))
                Text(source.description, style = MaterialTheme.typography.bodySmall)
                if (!source.requiresApiKey) {
                    Spacer(Modifier.height(4.dp))
                    AssistChip(onClick = {}, label = { Text("No API key needed", style = MaterialTheme.typography.labelSmall) }, leadingIcon = { Icon(Icons.Default.Check, null, Modifier.size(14.dp)) })
                }
            }
            if (isSelected) Icon(Icons.Default.CheckCircle, "Selected", tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(24.dp))
        }
    }
}

@Composable
fun ApiKeyField(label: String, value: String, hint: String, onSave: (String) -> Unit) {
    var text by remember(value) { mutableStateOf(value) }
    var visible by remember { mutableStateOf(false) }
    var saved by remember { mutableStateOf(false) }
    Column(Modifier.padding(horizontal = 16.dp, vertical = 8.dp)) {
        OutlinedTextField(
            value = text, onValueChange = { text = it; saved = false },
            label = { Text(label) }, placeholder = { Text(hint, style = MaterialTheme.typography.labelSmall) },
            singleLine = true, modifier = Modifier.fillMaxWidth(),
            visualTransformation = if (visible) VisualTransformation.None else PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            trailingIcon = {
                Row {
                    IconButton(onClick = { visible = !visible }) { Icon(if (visible) Icons.Default.VisibilityOff else Icons.Default.Visibility, null) }
                    IconButton(onClick = { onSave(text); saved = true }, enabled = text != value || !saved) {
                        Icon(if (saved) Icons.Default.Check else Icons.Default.Save, "Save", tint = if (saved) MaterialTheme.colorScheme.primary else LocalContentColor.current)
                    }
                }
            }
        )
    }
}

@Composable
fun <T> SegmentedPreference(title: String, options: List<Pair<String, T>>, selected: T, onSelect: (T) -> Unit) {
    Column(Modifier.padding(horizontal = 16.dp, vertical = 8.dp)) {
        Text(title, style = MaterialTheme.typography.labelLarge)
        Spacer(Modifier.height(6.dp))
        SingleChoiceSegmentedButtonRow(Modifier.fillMaxWidth()) {
            options.forEachIndexed { idx, (label, value) ->
                SegmentedButton(shape = SegmentedButtonDefaults.itemShape(idx, options.size), onClick = { onSelect(value) }, selected = selected == value, label = { Text(label) })
            }
        }
    }
}

@Composable
fun SettingsSectionHeader(title: String) {
    Text(title, modifier = Modifier.padding(start = 16.dp, end = 16.dp, top = 24.dp, bottom = 8.dp),
        style = MaterialTheme.typography.titleMedium.copy(color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold))
}
