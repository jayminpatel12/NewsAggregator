package com.jaymin.newsaggregator.ui.screens.home

import android.annotation.SuppressLint
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.location.FusedLocationProviderClient
import com.jaymin.newsaggregator.core.ai.service.GeminiAiService
import com.jaymin.newsaggregator.core.common.util.Resource
import com.jaymin.newsaggregator.core.domain.model.Article
import com.jaymin.newsaggregator.core.domain.model.Weather
import com.jaymin.newsaggregator.core.domain.usecase.GetNewsByLocationUseCase
import com.jaymin.newsaggregator.core.domain.usecase.GetWeatherByCityUseCase
import com.jaymin.newsaggregator.core.domain.usecase.GetWeatherByLocationUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class HomeUiState(
    val weather: Weather? = null,
    val localNews: List<Article> = emptyList(),
    val briefing: String? = null,
    val isLoading: Boolean = true,
    val isBriefingLoading: Boolean = false,
    val isBriefingError: Boolean = false,
    val error: String? = null,
    val cityName: String = "Loading...",
    val lastSearchQuery: String = "Hamilton",
    val suggestions: List<String> = emptyList(),
    val categories: List<String> = listOf("All", "Tech", "Science", "Sports", "Business", "Health"),
    val selectedCategory: String = "All",
    val isWeatherExpanded: Boolean = true
)

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val getWeatherByCity: GetWeatherByCityUseCase,
    private val getWeatherByLocation: GetWeatherByLocationUseCase,
    private val getNewsByLocation: GetNewsByLocationUseCase,
    private val geminiAiService: GeminiAiService,
    private val fusedLocationClient: FusedLocationProviderClient
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    private val popularCities = listOf(
        "London", "New York", "Tokyo", "Paris", "Berlin", 
        "Sydney", "Mumbai", "Toronto", "Dubai", "Singapore",
        "Hamilton", "Auckland", "Wellington", "Christchurch"
    )

    init {
        refresh()
    }

    fun onSearchQueryChanged(query: String) {
        val suggestions = if (query.isBlank()) {
            emptyList()
        } else {
            popularCities.filter { it.contains(query, ignoreCase = true) }
        }
        _uiState.update { it.copy(suggestions = suggestions) }
    }

    fun clearSuggestions() {
        _uiState.update { it.copy(suggestions = emptyList()) }
    }

    fun onCategorySelected(category: String) {
        _uiState.update { it.copy(selectedCategory = category) }
        // Fetch news for the selected category
        val city = _uiState.value.weather?.cityName ?: _uiState.value.lastSearchQuery
        val country = _uiState.value.weather?.country ?: "us"
        
        if (category == "All") {
            fetchLocalNews(city, country)
        } else {
            fetchCategoryNews(category, country)
        }
    }

    private fun fetchCategoryNews(category: String, country: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            // Using category as the query for local news
            getNewsByLocation(query = category, country = country).collect { result ->
                when (result) {
                    is Resource.Loading -> {}
                    is Resource.Error -> _uiState.update { it.copy(isLoading = false, error = result.message) }
                    is Resource.Success -> {
                        _uiState.update { it.copy(localNews = result.data, isLoading = false) }
                    }
                }
            }
        }
    }

    fun toggleWeatherExpanded() {
        _uiState.update { it.copy(isWeatherExpanded = !it.isWeatherExpanded) }
    }

    fun refresh() {
        searchCity(_uiState.value.lastSearchQuery)
    }

    @SuppressLint("MissingPermission")
    fun loadFromGps() {
        fusedLocationClient.lastLocation.addOnSuccessListener { location ->
            location?.let { loadByCoordinates(it.latitude, it.longitude) }
        }.addOnFailureListener {
            _uiState.update { s -> s.copy(error = "Location unavailable") }
        }
    }

    fun searchCity(city: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null, briefing = null, lastSearchQuery = city) }
            getWeatherByCity(city).collect { result ->
                when (result) {
                    is Resource.Loading -> {}
                    is Resource.Error -> _uiState.update { it.copy(error = result.message, isLoading = false) }
                    is Resource.Success -> onWeatherLoaded(result.data)
                }
            }
        }
    }

    private fun loadByCoordinates(lat: Double, lon: Double) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null, briefing = null) }
            getWeatherByLocation(lat, lon).collect { result ->
                when (result) {
                    is Resource.Loading -> {}
                    is Resource.Error -> _uiState.update { it.copy(error = result.message, isLoading = false) }
                    is Resource.Success -> onWeatherLoaded(result.data)
                }
            }
        }
    }

    private fun onWeatherLoaded(weather: Weather) {
        _uiState.update { it.copy(weather = weather, cityName = weather.cityName, isLoading = false) }
        fetchLocalNews(weather.cityName, weather.country)
    }

    private fun fetchLocalNews(city: String, country: String) {
        viewModelScope.launch {
            getNewsByLocation(city, country).collect { result ->
                when (result) {
                    is Resource.Loading -> {}
                    is Resource.Error -> generateBriefing(_uiState.value.weather!!, emptyList())
                    is Resource.Success -> {
                        _uiState.update { it.copy(localNews = result.data) }
                        generateBriefing(_uiState.value.weather!!, result.data)
                    }
                }
            }
        }
    }

    private fun generateBriefing(weather: Weather, articles: List<Article>) {
        viewModelScope.launch {
            _uiState.update { it.copy(isBriefingLoading = true, isBriefingError = false) }
            geminiAiService.generateLocationOverview(weather, articles).collect { result ->
                when (result) {
                    is Resource.Loading -> {}
                    is Resource.Error -> _uiState.update { 
                        it.copy(isBriefingLoading = false, briefing = result.message, isBriefingError = true) 
                    }
                    is Resource.Success -> _uiState.update { 
                        it.copy(briefing = result.data, isBriefingLoading = false, isBriefingError = false) 
                    }
                }
            }
        }
    }

    fun refreshOverview() {
        _uiState.value.weather?.let { generateBriefing(it, _uiState.value.localNews) }
    }
}
