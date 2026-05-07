package com.jaymin.newsaggregator.core.domain.repository

import com.jaymin.newsaggregator.core.common.util.Resource
import com.jaymin.newsaggregator.core.domain.model.Weather
import kotlinx.coroutines.flow.Flow

/**
 * Weather repository contract.
 */
interface WeatherRepository {

    fun getWeatherByCity(city: String): Flow<Resource<Weather>>

    fun getWeatherByCoordinates(lat: Double, lon: Double): Flow<Resource<Weather>>
}
