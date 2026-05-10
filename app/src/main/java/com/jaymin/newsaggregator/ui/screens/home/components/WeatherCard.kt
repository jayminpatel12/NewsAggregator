package com.jaymin.newsaggregator.ui.screens.home.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.jaymin.newsaggregator.core.domain.model.Weather
import com.jaymin.newsaggregator.ui.theme.AppTheme

@Composable
fun WeatherCard(weather: Weather) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = AppTheme.spacing.medium),
        shape = AppTheme.shapes.large,
        tonalElevation = 8.dp,
        color = MaterialTheme.colorScheme.primary
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
                    Text(
                        text = weather.temperatureCelsius,
                        style = MaterialTheme.typography.displayLarge,
                        fontWeight = FontWeight.Black,
                        color = Color.White
                    )
                    Text(
                        text = "Feels like ${weather.feelsLikeCelsius}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.White.copy(alpha = 0.8f)
                    )
                    Text(
                        text = weather.description.replaceFirstChar { it.uppercase() },
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Medium,
                        color = Color.White
                    )
                }

                AsyncImage(
                    model = weather.iconUrl,
                    contentDescription = weather.description,
                    modifier = Modifier.size(100.dp)
                )
            }

            Spacer(modifier = Modifier.height(AppTheme.spacing.large))

            Surface(
                color = Color.White.copy(alpha = 0.15f),
                shape = AppTheme.shapes.medium
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(AppTheme.spacing.medium),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    WeatherStat("Humidity", "${weather.humidity}%")
                    WeatherStat("Wind", "${weather.windSpeed} m/s")
                    WeatherStat("Pressure", "${weather.pressure} hPa")
                }
            }
        }
    }
}

@Composable
private fun WeatherStat(label: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = value, 
            style = MaterialTheme.typography.bodyLarge, 
            fontWeight = FontWeight.Bold,
            color = Color.White
        )
        Text(
            text = label, 
            style = MaterialTheme.typography.labelSmall,
            color = Color.White.copy(alpha = 0.7f)
        )
    }
}
