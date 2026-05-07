package com.jaymin.newsaggregator.core.data.repository

import com.jaymin.newsaggregator.core.common.util.Constants
import com.jaymin.newsaggregator.core.common.util.Resource
import com.jaymin.newsaggregator.core.data.local.dao.WeatherDao
import com.jaymin.newsaggregator.core.data.mapper.toDomain
import com.jaymin.newsaggregator.core.data.mapper.toEntity
import com.jaymin.newsaggregator.core.data.remote.api.WeatherApiService
import com.jaymin.newsaggregator.core.domain.model.Weather
import com.jaymin.newsaggregator.core.domain.repository.WeatherRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class WeatherRepositoryImpl @Inject constructor(
    private val weatherApiService: WeatherApiService,
    private val weatherDao: WeatherDao
) : WeatherRepository {

    companion object {
        private const val CACHE_DURATION = 30 * 60 * 1000L // 30 minutes
    }

    override fun getWeatherByCity(city: String): Flow<Resource<Weather>> = flow {
        emit(Resource.Loading)

        // Check cache first
        val cached = weatherDao.getWeatherByCity(city)
        if (cached != null && System.currentTimeMillis() - cached.cachedAt < CACHE_DURATION) {
            emit(Resource.Success(cached.toDomain()))
            return@flow
        }

        try {
            val response = weatherApiService.getWeatherByCity(
                city = city,
                apiKey = Constants.WEATHER_API_KEY
            )
            // Cache it
            weatherDao.insertWeather(response.toEntity())
            emit(Resource.Success(response.toDomain()))
        } catch (e: Exception) {
            // Fallback to cache if available
            if (cached != null) {
                emit(Resource.Success(cached.toDomain()))
            } else {
                emit(Resource.Error(e.message ?: "Failed to fetch weather"))
            }
        }
    }

    override fun getWeatherByCoordinates(lat: Double, lon: Double): Flow<Resource<Weather>> = flow {
        emit(Resource.Loading)
        try {
            val response = weatherApiService.getWeatherByCoordinates(
                latitude = lat,
                longitude = lon,
                apiKey = Constants.WEATHER_API_KEY
            )
            weatherDao.insertWeather(response.toEntity())
            emit(Resource.Success(response.toDomain()))
        } catch (e: Exception) {
            emit(Resource.Error(e.message ?: "Failed to fetch weather"))
        }
    }
}
