package com.jaymin.newsaggregator.core.domain.model

/**
 * Domain model for weather data.
 * Clean representation without API-specific fields.
 */
data class Weather(
    val cityName: String,
    val country: String,
    val temperature: Double,
    val feelsLike: Double,
    val tempMin: Double,
    val tempMax: Double,
    val humidity: Int,
    val pressure: Int,
    val windSpeed: Double,
    val description: String,
    val icon: String,
    val visibility: Int,
    val cloudiness: Int,
    val sunrise: Long,
    val sunset: Long,
    val latitude: Double,
    val longitude: Double
) {
    val iconUrl: String
        get() = "https://openweathermap.org/img/wn/${icon}@2x.png"

    val temperatureCelsius: String
        get() = "%.0f°C".format(temperature)

    val feelsLikeCelsius: String
        get() = "%.0f°C".format(feelsLike)
}
