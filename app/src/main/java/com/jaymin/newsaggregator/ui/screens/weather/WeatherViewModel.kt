package com.jaymin.newsaggregator.ui.screens.weather

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jaymin.newsaggregator.core.common.util.Resource
import com.jaymin.newsaggregator.core.domain.model.Weather
import com.jaymin.newsaggregator.core.domain.usecase.GetWeatherByCityUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class WeatherViewModel @Inject constructor(
    private val getWeatherByCity: GetWeatherByCityUseCase
) : ViewModel() {

    private val _weatherState = MutableStateFlow<Resource<Weather>>(Resource.Loading)
    val weatherState: StateFlow<Resource<Weather>> = _weatherState.asStateFlow()

    init {
        searchCity("Hamilton")
    }

    fun searchCity(city: String) {
        viewModelScope.launch {
            getWeatherByCity(city).collect { _weatherState.value = it }
        }
    }
}
