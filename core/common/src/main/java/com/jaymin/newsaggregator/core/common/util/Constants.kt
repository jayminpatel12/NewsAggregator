package com.jaymin.newsaggregator.core.common.util

import com.jaymin.newsaggregator.core.common.BuildConfig

object Constants {
    // NewsAPI - free tier: https://newsapi.org/register
    const val NEWS_API_KEY = BuildConfig.NEWS_API_KEY
    const val NEWS_BASE_URL = "https://newsapi.org/v2/"

    // OpenWeatherMap - free tier: https://openweathermap.org/api
    const val WEATHER_API_KEY = BuildConfig.WEATHER_API_KEY
    const val WEATHER_BASE_URL = "https://api.openweathermap.org/"
    const val WEATHER_ICON_URL = "https://openweathermap.org/img/wn/%s@2x.png"

    // Gemini AI - free tier: https://aistudio.google.com/apikey
    const val GEMINI_API_KEY = BuildConfig.GEMINI_API_KEY

    // GraphQL endpoint for news (using a proxy that wraps NewsAPI in GraphQL)
    // You can self-host this with Apollo Server or use the REST fallback
    const val GRAPHQL_ENDPOINT = "http://10.0.2.2:4000/graphql"

    // Database
    const val DATABASE_NAME = "news_aggregator_db"

    // Paging
    const val PAGE_SIZE = 20
    const val INITIAL_LOAD_SIZE = 40
}
