package com.jaymin.newsaggregator.core.domain.usecase

import com.jaymin.newsaggregator.core.common.util.Resource
import com.jaymin.newsaggregator.core.domain.model.Weather
import com.jaymin.newsaggregator.core.domain.repository.WeatherRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetWeatherByCityUseCase @Inject constructor(
    private val repository: WeatherRepository
) {
    operator fun invoke(city: String): Flow<Resource<Weather>> =
        repository.getWeatherByCity(city)
}

class GetWeatherByLocationUseCase @Inject constructor(
    private val repository: WeatherRepository
) {
    operator fun invoke(lat: Double, lon: Double): Flow<Resource<Weather>> =
        repository.getWeatherByCoordinates(lat, lon)
}
