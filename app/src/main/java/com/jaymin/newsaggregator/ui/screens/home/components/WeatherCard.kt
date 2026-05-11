package com.jaymin.newsaggregator.ui.screens.home.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import com.jaymin.newsaggregator.core.domain.model.Weather
import com.jaymin.newsaggregator.ui.theme.AppTheme
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@Composable
fun WeatherCard(
    weather: Weather,
    isExpanded: Boolean = true,
    onToggle: () -> Unit = {}
) {
    val timeFormatter = DateTimeFormatter.ofPattern("h:mm a")
    val sunsetTime = Instant.ofEpochSecond(weather.sunset)
        .atZone(ZoneId.systemDefault())
        .format(timeFormatter)

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = AppTheme.spacing.medium),
        shape = AppTheme.shapes.large,
        tonalElevation = 8.dp,
        color = MaterialTheme.colorScheme.primary,
        onClick = onToggle
    ) {
        Column(
            modifier = Modifier
                .background(AppTheme.gradients.primary)
                .padding(AppTheme.spacing.large)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "${weather.cityName}, ${weather.country}",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color.White.copy(alpha = 0.9f)
                    )
                    
                    Row(verticalAlignment = Alignment.Bottom) {
                        Text(
                            text = weather.temperatureCelsius,
                            style = MaterialTheme.typography.displayLarge.copy(
                                fontSize = if (isExpanded) 64.sp else 32.sp,
                                lineHeight = if (isExpanded) 64.sp else 32.sp
                            ),
                            fontWeight = FontWeight.Black,
                            color = Color.White
                        )
                        if (isExpanded) {
                            Spacer(modifier = Modifier.width(AppTheme.spacing.small))
                            Column(modifier = Modifier.padding(bottom = 8.dp)) {
                                Text(
                                    text = "H: ${"%.0f°".format(weather.tempMax)} L: ${"%.0f°".format(weather.tempMin)}",
                                    style = MaterialTheme.typography.labelMedium,
                                    color = Color.White.copy(alpha = 0.8f)
                                )
                                Text(
                                    text = "Feels like ${weather.feelsLikeCelsius}",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = Color.White.copy(alpha = 0.8f)
                                )
                            }
                        }
                    }
                    
                    if (isExpanded) {
                        Text(
                            text = weather.description.replaceFirstChar { it.uppercase() },
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold,
                            color = Color.White
                        )
                    }
                }

                AsyncImage(
                    model = weather.iconUrl,
                    contentDescription = weather.description,
                    modifier = Modifier.size(if (isExpanded) 100.dp else 48.dp)
                )
            }

            if (isExpanded) {
                Spacer(modifier = Modifier.height(AppTheme.spacing.large))

                // Stats Grid
                Column(verticalArrangement = Arrangement.spacedBy(AppTheme.spacing.small)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(AppTheme.spacing.small)
                    ) {
                        WeatherStatItem(
                            icon = Icons.Rounded.WaterDrop,
                            label = "Humidity",
                            value = "${weather.humidity}%",
                            modifier = Modifier.weight(1f)
                        )
                        WeatherStatItem(
                            icon = Icons.Rounded.Air,
                            label = "Wind",
                            value = "${weather.windSpeed} m/s",
                            modifier = Modifier.weight(1f)
                        )
                        WeatherStatItem(
                            icon = Icons.Rounded.Compress,
                            label = "Pressure",
                            value = "${weather.pressure} hPa",
                            modifier = Modifier.weight(1f)
                        )
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(AppTheme.spacing.small)
                    ) {
                        WeatherStatItem(
                            icon = Icons.Rounded.Visibility,
                            label = "Visibility",
                            value = "${weather.visibility / 1000} km",
                            modifier = Modifier.weight(1f)
                        )
                        WeatherStatItem(
                            icon = Icons.Rounded.Cloud,
                            label = "Clouds",
                            value = "${weather.cloudiness}%",
                            modifier = Modifier.weight(1f)
                        )
                        WeatherStatItem(
                            icon = Icons.Rounded.WbTwilight,
                            label = "Sunset",
                            value = sunsetTime,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun WeatherStatItem(
    icon: ImageVector,
    label: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        color = Color.White.copy(alpha = 0.15f),
        shape = AppTheme.shapes.medium
    ) {
        Column(
            modifier = Modifier.padding(AppTheme.spacing.small),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = Color.White.copy(alpha = 0.8f),
                modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = value,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                color = Color.White.copy(alpha = 0.6f),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}
