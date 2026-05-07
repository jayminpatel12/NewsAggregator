package com.jaymin.newsaggregator.core.data.mapper

import com.jaymin.newsaggregator.core.data.local.entity.ArticleEntity
import com.jaymin.newsaggregator.core.data.local.entity.WeatherEntity
import com.jaymin.newsaggregator.core.data.remote.dto.ArticleDto
import com.jaymin.newsaggregator.core.data.remote.dto.WeatherResponseDto
import com.jaymin.newsaggregator.core.domain.model.Article
import com.jaymin.newsaggregator.core.domain.model.Weather
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

// Articles

fun ArticleDto.toEntity(category: String): ArticleEntity {
    val encodedUrl = URLEncoder.encode(url, StandardCharsets.UTF_8.toString())
    return ArticleEntity(
        id = encodedUrl.take(200), // Use URL as unique ID
        title = title ?: "Untitled",
        description = description,
        content = content,
        url = url,
        imageUrl = urlToImage,
        publishedAt = publishedAt,
        source = source.name,
        author = author,
        category = category
    )
}

fun ArticleEntity.toDomain(): Article = Article(
    id = id,
    title = title,
    description = description,
    content = content,
    url = url,
    imageUrl = imageUrl,
    publishedAt = publishedAt,
    source = source,
    author = author,
    category = category,
    aiSummary = aiSummary,
    isBookmarked = isBookmarked
)

fun Article.toEntity(): ArticleEntity = ArticleEntity(
    id = id,
    title = title,
    description = description,
    content = content,
    url = url,
    imageUrl = imageUrl,
    publishedAt = publishedAt,
    source = source,
    author = author,
    category = category,
    aiSummary = aiSummary,
    isBookmarked = isBookmarked
)

fun ArticleDto.toDomain(category: String = "general"): Article {
    val encodedUrl = URLEncoder.encode(url, StandardCharsets.UTF_8.toString())
    return Article(
        id = encodedUrl.take(200),
        title = title ?: "Untitled",
        description = description,
        content = content,
        url = url,
        imageUrl = urlToImage,
        publishedAt = publishedAt,
        source = source.name,
        author = author,
        category = category
    )
}

// Weather

fun WeatherResponseDto.toDomain(): Weather = Weather(
    cityName = name,
    country = sys.country,
    temperature = main.temp,
    feelsLike = main.feelsLike,
    tempMin = main.tempMin,
    tempMax = main.tempMax,
    humidity = main.humidity,
    pressure = main.pressure,
    windSpeed = wind.speed,
    description = weather.firstOrNull()?.description ?: "Unknown",
    icon = weather.firstOrNull()?.icon ?: "01d",
    visibility = visibility,
    cloudiness = clouds.all,
    sunrise = sys.sunrise,
    sunset = sys.sunset,
    latitude = coord.lat,
    longitude = coord.lon
)

fun WeatherResponseDto.toEntity(): WeatherEntity = WeatherEntity(
    cityName = name,
    country = sys.country,
    temperature = main.temp,
    feelsLike = main.feelsLike,
    tempMin = main.tempMin,
    tempMax = main.tempMax,
    humidity = main.humidity,
    pressure = main.pressure,
    windSpeed = wind.speed,
    description = weather.firstOrNull()?.description ?: "Unknown",
    icon = weather.firstOrNull()?.icon ?: "01d",
    visibility = visibility,
    cloudiness = clouds.all,
    sunrise = sys.sunrise,
    sunset = sys.sunset,
    latitude = coord.lat,
    longitude = coord.lon
)

fun WeatherEntity.toDomain(): Weather = Weather(
    cityName = cityName,
    country = country,
    temperature = temperature,
    feelsLike = feelsLike,
    tempMin = tempMin,
    tempMax = tempMax,
    humidity = humidity,
    pressure = pressure,
    windSpeed = windSpeed,
    description = description,
    icon = icon,
    visibility = visibility,
    cloudiness = cloudiness,
    sunrise = sunrise,
    sunset = sunset,
    latitude = latitude,
    longitude = longitude
)
