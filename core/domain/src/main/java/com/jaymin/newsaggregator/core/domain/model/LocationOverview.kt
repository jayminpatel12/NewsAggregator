package com.jaymin.newsaggregator.core.domain.model

/**
 * Briefing combining weather and local news for a specific location.
 */
data class LocationOverview(
    val cityName: String,
    val weather: Weather,
    val topHeadlines: List<Article>,
    val aiInsight: String
)
