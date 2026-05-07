package com.jaymin.newsaggregator.core.data.remote.dto

import kotlinx.serialization.InternalSerializationApi
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

// ==================== NEWS DTOs ====================

@OptIn(InternalSerializationApi::class)
@Serializable
data class NewsResponseDto(
    val status: String,
    val totalResults: Int,
    val articles: List<ArticleDto>
)

@OptIn(InternalSerializationApi::class)
@Serializable
data class ArticleDto(
    val title: String? = null,
    val description: String? = null,
    val content: String? = null,
    val url: String,
    val urlToImage: String? = null,
    val publishedAt: String,
    val source: SourceDto,
    val author: String? = null
)


@OptIn(InternalSerializationApi::class)
@Serializable
data class SourceDto(
    val id: String? = null,
    val name: String
)

// ==================== WEATHER DTOs ====================

@OptIn(InternalSerializationApi::class)
@Serializable
data class WeatherResponseDto(
    val coord: CoordDto,
    val weather: List<WeatherItemDto>,
    val main: MainDto,
    val visibility: Int,
    val wind: WindDto,
    val clouds: CloudsDto,
    val sys: SysDto,
    val name: String,
    val id: Int
)
@OptIn(InternalSerializationApi::class)

@Serializable
data class CoordDto(
    val lon: Double,
    val lat: Double
)

@OptIn(InternalSerializationApi::class)
@Serializable
data class WeatherItemDto(
    val id: Int,
    val main: String,
    val description: String,
    val icon: String
)

@OptIn(InternalSerializationApi::class)
@Serializable
data class MainDto(
    val temp: Double,
    @SerialName("feels_like") val feelsLike: Double,
    @SerialName("temp_min") val tempMin: Double,
    @SerialName("temp_max") val tempMax: Double,
    val pressure: Int,
    val humidity: Int
)

@OptIn(InternalSerializationApi::class)
@Serializable
data class WindDto(
    val speed: Double
)

@OptIn(InternalSerializationApi::class)
@Serializable
data class CloudsDto(
    val all: Int
)

@OptIn(InternalSerializationApi::class)
@Serializable
data class SysDto(
    val country: String,
    val sunrise: Long,
    val sunset: Long
)
