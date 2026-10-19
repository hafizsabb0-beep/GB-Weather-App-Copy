package com.weatherapp.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.weatherapp.data.model.*
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun SkyGradientBackground(isDay: Boolean, modifier: Modifier = Modifier, content: @Composable BoxScope.() -> Unit) {
    val gradient = if (isDay)
        Brush.verticalGradient(listOf(Color(0xFF1565C0), Color(0xFF42A5F5), Color(0xFF81D4FA)))
    else
        Brush.verticalGradient(listOf(Color(0xFF0D1B2A), Color(0xFF1A237E), Color(0xFF283593)))
    Box(modifier.background(gradient), content = content)
}

@Composable
fun CurrentTempDisplay(current: CurrentWeather, unit: TemperatureUnit, modifier: Modifier = Modifier) {
    Column(modifier, horizontalAlignment = Alignment.CenterHorizontally) {
        Text(current.condition.icon.emoji, fontSize = 72.sp)
        Text(
            formatTemp(current.tempC, unit),
            style = MaterialTheme.typography.displayLarge.copy(fontWeight = FontWeight.Thin, fontSize = 80.sp, color = Color.White)
        )
        Text(current.condition.text, style = MaterialTheme.typography.titleLarge.copy(color = Color.White.copy(alpha = 0.9f)))
        Spacer(Modifier.height(4.dp))
        Text("Feels like ${formatTemp(current.feelsLikeC, unit)}", style = MaterialTheme.typography.bodyMedium.copy(color = Color.White.copy(alpha = 0.75f)))
    }
}

@Composable
fun WeatherDetailGrid(current: CurrentWeather, windUnit: WindUnit, modifier: Modifier = Modifier) {
    val items = listOf(
        "💧" to "Humidity" to "${current.humidity}%",
        "💨" to "Wind" to "${formatWind(current.windKph, windUnit)} ${current.windDirection}",
        "🌡️" to "Pressure" to "${current.pressureMb.toInt()} mb",
        "👁️" to "Visibility" to "${current.visibilityKm.toInt()} km",
        "☀️" to "UV Index" to current.uvIndex.toInt().toString(),
        "💦" to "Dew Point" to formatTemp(current.dewPointC, TemperatureUnit.CELSIUS)
    )
    Column(modifier) {
        items.chunked(3).forEach { row ->
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                row.forEach { (labelPair, value) ->
                    val (emoji, label) = labelPair
                    DetailChip(emoji, label, value, Modifier.weight(1f).padding(4.dp))
                }
            }
        }
    }
}

@Composable
fun DetailChip(emoji: String, label: String, value: String, modifier: Modifier = Modifier) {
    Card(modifier = modifier, colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.15f)), shape = RoundedCornerShape(12.dp)) {
        Column(Modifier.padding(10.dp).fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
            Text(emoji, fontSize = 20.sp)
            Text(value, style = MaterialTheme.typography.titleSmall.copy(color = Color.White, fontWeight = FontWeight.SemiBold), textAlign = TextAlign.Center)
            Text(label, style = MaterialTheme.typography.labelSmall.copy(color = Color.White.copy(alpha = 0.7f)), textAlign = TextAlign.Center)
        }
    }
}

@Composable
fun HourlyCard(hour: HourlyWeather, unit: TemperatureUnit, modifier: Modifier = Modifier) {
    val time = SimpleDateFormat("ha", Locale.getDefault()).format(Date(hour.timeEpoch * 1000))
    Card(modifier = modifier.width(72.dp), colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.15f)), shape = RoundedCornerShape(16.dp)) {
        Column(Modifier.padding(8.dp), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text(time, style = MaterialTheme.typography.labelSmall.copy(color = Color.White.copy(alpha = 0.8f)))
            Text(hour.condition.icon.emoji, fontSize = 22.sp)
            Text(formatTemp(hour.tempC, unit), style = MaterialTheme.typography.bodyMedium.copy(color = Color.White, fontWeight = FontWeight.Medium))
            if (hour.precipProbability > 10) Text("${hour.precipProbability}%", style = MaterialTheme.typography.labelSmall.copy(color = Color(0xFF81D4FA)))
        }
    }
}

@Composable
fun DailyRow(day: DailyWeather, unit: TemperatureUnit, modifier: Modifier = Modifier) {
    val dayName = SimpleDateFormat("EEE", Locale.getDefault()).format(Date(day.dateEpoch * 1000))
    Row(modifier.fillMaxWidth().padding(vertical = 6.dp), verticalAlignment = Alignment.CenterVertically) {
        Text(dayName, modifier = Modifier.width(48.dp), style = MaterialTheme.typography.bodyMedium.copy(color = Color.White))
        Text(day.condition.icon.emoji, fontSize = 20.sp, modifier = Modifier.padding(horizontal = 8.dp))
        if (day.precipProbability > 0)
            Text("${day.precipProbability}%", style = MaterialTheme.typography.labelSmall.copy(color = Color(0xFF81D4FA)), modifier = Modifier.width(36.dp))
        else Spacer(Modifier.width(36.dp))
        Spacer(Modifier.weight(1f))
        Text(formatTemp(day.minTempC, unit), style = MaterialTheme.typography.bodyMedium.copy(color = Color.White.copy(alpha = 0.6f)), modifier = Modifier.padding(end = 8.dp))
        Text(formatTemp(day.maxTempC, unit), style = MaterialTheme.typography.bodyMedium.copy(color = Color.White, fontWeight = FontWeight.SemiBold))
    }
}

@Composable
fun AlertBanner(alert: WeatherAlert) {
    val color = when (alert.severity) {
        AlertSeverity.EXTREME -> Color(0xFFB71C1C)
        AlertSeverity.SEVERE -> Color(0xFFE53935)
        AlertSeverity.MODERATE -> Color(0xFFF57C00)
        AlertSeverity.MINOR -> Color(0xFFF9A825)
    }
    Card(Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 4.dp), colors = CardDefaults.cardColors(containerColor = color.copy(alpha = 0.85f)), shape = RoundedCornerShape(12.dp)) {
        Row(Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
            Text("⚠️", fontSize = 20.sp, modifier = Modifier.padding(end = 8.dp))
            Column {
                Text(alert.headline, style = MaterialTheme.typography.bodyMedium.copy(color = Color.White, fontWeight = FontWeight.Bold))
                if (alert.expires.isNotEmpty()) Text("Until ${alert.expires}", style = MaterialTheme.typography.labelSmall.copy(color = Color.White.copy(alpha = 0.8f)))
            }
        }
    }
}

@Composable
fun SourceBadge(sourceName: String) {
    Card(colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.2f)), shape = RoundedCornerShape(8.dp)) {
        Text("via $sourceName", modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp), style = MaterialTheme.typography.labelSmall.copy(color = Color.White.copy(alpha = 0.8f)))
    }
}

fun formatTemp(tempC: Double, unit: TemperatureUnit): String = when (unit) {
    TemperatureUnit.CELSIUS -> "${tempC.toInt()}°C"
    TemperatureUnit.FAHRENHEIT -> "${tempC.toFahrenheit().toInt()}°F"
}

fun formatWind(kph: Double, unit: WindUnit): String = when (unit) {
    WindUnit.KPH -> "${kph.toInt()} km/h"
    WindUnit.MPH -> "${kph.kphToMph().toInt()} mph"
    WindUnit.MS -> "${kph.kphToMs().toInt()} m/s"
}
