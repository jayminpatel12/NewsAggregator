package com.jaymin.newsaggregator.ui.screens.weather

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.expandVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.AsyncImage
import com.jaymin.newsaggregator.core.common.util.Resource
import com.jaymin.newsaggregator.core.domain.model.Weather
import com.jaymin.newsaggregator.ui.theme.AppTheme
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WeatherScreen(
    viewModel: WeatherViewModel = hiltViewModel()
) {
    val weatherState by viewModel.weatherState.collectAsStateWithLifecycle()
    
    val (backgroundBrush, contentColor) = when (val state = weatherState) {
        is Resource.Success -> getWeatherVisuals(state.data.description)
        else -> Pair(
            Brush.verticalGradient(listOf(MaterialTheme.colorScheme.primary, MaterialTheme.colorScheme.surface)),
            Color.White
        )
    }

    Scaffold(
        containerColor = Color.Transparent,
        modifier = Modifier.background(backgroundBrush),
        topBar = {
            CenterAlignedTopAppBar(
                title = { 
                    Text(
                        "Weather Forecast", 
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = contentColor
                    ) 
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = Color.Transparent
                ),
                actions = {
                    IconButton(onClick = { /* TODO: Search Dialog */ }) {
                        Icon(Icons.Rounded.Search, contentDescription = "Search", tint = contentColor)
                    }
                }
            )
        }
    ) { paddingValues ->
        Box(modifier = Modifier.padding(paddingValues).fillMaxSize()) {
            when (val state = weatherState) {
                is Resource.Loading -> {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center),
                        color = contentColor
                    )
                }
                is Resource.Error -> {
                    Text(
                        text = state.message,
                        modifier = Modifier.align(Alignment.Center).padding(16.dp),
                        color = MaterialTheme.colorScheme.error,
                        textAlign = TextAlign.Center
                    )
                }
                is Resource.Success -> {
                    WeatherPageContent(weather = state.data, contentColor = contentColor)
                }
            }
        }
    }
}

@Composable
private fun getWeatherVisuals(description: String): Pair<Brush, Color> {
    val desc = description.lowercase()
    return when {
        desc.contains("rain") || desc.contains("drizzle") -> Pair(
            Brush.verticalGradient(listOf(Color(0xFF4B6175), Color(0xFF202931))),
            Color.White
        )
        desc.contains("snow") -> Pair(
            Brush.verticalGradient(listOf(Color(0xFFE0EAFC), Color(0xFFCFDEF3))),
            Color(0xFF2C3E50)
        )
        desc.contains("cloud") -> Pair(
            Brush.verticalGradient(listOf(Color(0xFF757F9A), Color(0xFFD7DDE8))),
            Color.White
        )
        desc.contains("clear") || desc.contains("sun") -> Pair(
            Brush.verticalGradient(listOf(Color(0xFF2980B9), Color(0xFF6DD5FA), Color(0xFFFFFFFF))),
            Color.White
        )
        else -> Pair(
            Brush.verticalGradient(listOf(MaterialTheme.colorScheme.primary, MaterialTheme.colorScheme.surface)),
            Color.White
        )
    }
}

@Composable
private fun WeatherMoodVisuals(description: String) {
    val desc = description.lowercase()
    when {
        desc.contains("rain") -> {
            repeat(10) {
                Icon(
                    Icons.Rounded.Grain,
                    contentDescription = null,
                    modifier = Modifier
                        .size(48.dp)
                        .offset(
                            x = ((-150)..150).random().dp,
                            y = ((-150)..150).random().dp
                        ),
                    tint = Color.White.copy(alpha = 0.2f)
                )
            }
        }
        desc.contains("snow") -> {
            repeat(10) {
                Icon(
                    Icons.Rounded.AcUnit,
                    contentDescription = null,
                    modifier = Modifier
                        .size(32.dp)
                        .offset(
                            x = ((-150)..150).random().dp,
                            y = ((-150)..150).random().dp
                        ),
                    tint = Color.White.copy(alpha = 0.3f)
                )
            }
        }
        desc.contains("clear") || desc.contains("sun") -> {
            Surface(
                modifier = Modifier
                    .size(250.dp)
                    .offset(x = 100.dp, y = (-100).dp),
                color = Color.Yellow.copy(alpha = 0.15f),
                shape = CircleShape
            ) {}
        }
    }
}

@Composable
fun WeatherPageContent(weather: Weather, contentColor: Color) {
    val timeFormatter = DateTimeFormatter.ofPattern("h:mm a")
    val sunriseTime = Instant.ofEpochSecond(weather.sunrise)
        .atZone(ZoneId.systemDefault())
        .format(timeFormatter)
    val sunsetTime = Instant.ofEpochSecond(weather.sunset)
        .atZone(ZoneId.systemDefault())
        .format(timeFormatter)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = AppTheme.spacing.medium),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(AppTheme.spacing.medium))

        // Main Header
        Text(
            text = weather.cityName,
            style = MaterialTheme.typography.displaySmall,
            fontWeight = FontWeight.Bold,
            color = contentColor
        )
        Text(
            text = weather.country,
            style = MaterialTheme.typography.titleMedium,
            color = contentColor.copy(alpha = 0.7f)
        )

        Spacer(modifier = Modifier.height(AppTheme.spacing.large))

        // Big Temp & Icon
        Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxWidth()) {
            // Background visual mood
            WeatherMoodVisuals(weather.description)

            AsyncImage(
                model = weather.iconUrl,
                contentDescription = null,
                modifier = Modifier.size(200.dp).offset(y = (-20).dp)
            )
            Text(
                text = "%.0f°".format(weather.temperature),
                style = MaterialTheme.typography.displayLarge.copy(
                    fontSize = 100.sp,
                    lineHeight = 100.sp,
                    fontWeight = FontWeight.Black
                ),
                color = contentColor
            )
        }

        Text(
            text = weather.description.replaceFirstChar { it.uppercase() },
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Medium,
            color = contentColor
        )
        
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(AppTheme.spacing.medium)
        ) {
            Text(
                text = "H: ${"%.0f°".format(weather.tempMax)}",
                style = MaterialTheme.typography.titleMedium,
                color = contentColor.copy(alpha = 0.9f)
            )
            Text(
                text = "L: ${"%.0f°".format(weather.tempMin)}",
                style = MaterialTheme.typography.titleMedium,
                color = contentColor.copy(alpha = 0.9f)
            )
        }

        Spacer(modifier = Modifier.height(AppTheme.spacing.extraLarge))

        // Detailed Stats Grid
        Text(
            text = "Weather Details",
            modifier = Modifier.fillMaxWidth(),
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = contentColor
        )
        
        Spacer(modifier = Modifier.height(AppTheme.spacing.small))

        Column(verticalArrangement = Arrangement.spacedBy(AppTheme.spacing.small)) {
            Row(horizontalArrangement = Arrangement.spacedBy(AppTheme.spacing.small)) {
                WeatherDetailTile(
                    icon = Icons.Rounded.Thermostat,
                    label = "Feels Like",
                    value = weather.feelsLikeCelsius,
                    modifier = Modifier.weight(1f),
                    contentColor = contentColor
                )
                WeatherDetailTile(
                    icon = Icons.Rounded.WaterDrop,
                    label = "Humidity",
                    value = "${weather.humidity}%",
                    modifier = Modifier.weight(1f),
                    contentColor = contentColor
                )
            }
            Row(horizontalArrangement = Arrangement.spacedBy(AppTheme.spacing.small)) {
                WeatherDetailTile(
                    icon = Icons.Rounded.Air,
                    label = "Wind Speed",
                    value = "${weather.windSpeed} m/s",
                    modifier = Modifier.weight(1f),
                    contentColor = contentColor
                )
                WeatherDetailTile(
                    icon = Icons.Rounded.Compress,
                    label = "Pressure",
                    value = "${weather.pressure} hPa",
                    modifier = Modifier.weight(1f),
                    contentColor = contentColor
                )
            }
            Row(horizontalArrangement = Arrangement.spacedBy(AppTheme.spacing.small)) {
                WeatherDetailTile(
                    icon = Icons.Rounded.Visibility,
                    label = "Visibility",
                    value = "${weather.visibility / 1000} km",
                    modifier = Modifier.weight(1f),
                    contentColor = contentColor
                )
                WeatherDetailTile(
                    icon = Icons.Rounded.Cloud,
                    label = "Cloudiness",
                    value = "${weather.cloudiness}%",
                    modifier = Modifier.weight(1f),
                    contentColor = contentColor
                )
            }
            Row(horizontalArrangement = Arrangement.spacedBy(AppTheme.spacing.small)) {
                WeatherDetailTile(
                    icon = Icons.Rounded.WbSunny,
                    label = "Sunrise",
                    value = sunriseTime,
                    modifier = Modifier.weight(1f),
                    contentColor = contentColor
                )
                WeatherDetailTile(
                    icon = Icons.Rounded.WbTwilight,
                    label = "Sunset",
                    value = sunsetTime,
                    modifier = Modifier.weight(1f),
                    contentColor = contentColor
                )
            }
        }
        
        Spacer(modifier = Modifier.height(AppTheme.spacing.extraLarge))
    }
}

@Composable
fun WeatherDetailTile(
    icon: ImageVector,
    label: String,
    value: String,
    modifier: Modifier = Modifier,
    contentColor: Color
) {
    Surface(
        modifier = modifier,
        color = contentColor.copy(alpha = 0.1f),
        shape = AppTheme.shapes.medium,
        border = androidx.compose.foundation.BorderStroke(
            0.5.dp, 
            contentColor.copy(alpha = 0.1f)
        )
    ) {
        Column(
            modifier = Modifier.padding(AppTheme.spacing.medium),
            horizontalAlignment = Alignment.Start
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = contentColor.copy(alpha = 0.6f),
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(AppTheme.spacing.extraSmall))
                Text(
                    text = label.uppercase(),
                    style = MaterialTheme.typography.labelSmall,
                    color = contentColor.copy(alpha = 0.6f),
                    fontWeight = FontWeight.Bold
                )
            }
            Spacer(modifier = Modifier.height(AppTheme.spacing.small))
            Text(
                text = value,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = contentColor
            )
        }
    }
}
