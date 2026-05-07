package com.jaymin.newsaggregator.core.ai.service

import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.content
import com.jaymin.newsaggregator.core.common.util.Constants
import com.jaymin.newsaggregator.core.common.util.Resource
import com.jaymin.newsaggregator.core.domain.model.Article
import com.jaymin.newsaggregator.core.domain.model.Weather
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Gemini integration for generating:
 * 1. Article summaries
 * 2. Location-based news overviews
 */
@Singleton
class GeminiAiService @Inject constructor() {

    private val model by lazy {
        GenerativeModel(
            modelName = "gemini-1.5-flash",
            apiKey = Constants.GEMINI_API_KEY
        )
    }

    /**
     * Generate a concise summary for a news article.
     */
    fun summarizeArticle(article: Article): Flow<Resource<String>> = flow {
        emit(Resource.Loading)
        try {
            val prompt = buildString {
                append("Summarize this news article in 2-3 concise sentences. ")
                append("Focus on the key facts and implications.\n\n")
                append("Title: ${article.title}\n")
                article.description?.let { append("Description: $it\n") }
                article.content?.let { append("Content: $it\n") }
                append("Source: ${article.source}")
            }

            val response = model.generateContent(content { text(prompt) })
            val summary = response.text ?: "Unable to generate summary."
            emit(Resource.Success(summary))
        } catch (e: Exception) {
            emit(Resource.Error("Summary failed: ${e.message}"))
        }
    }

    /**
     * Generate a location-wise overview combining weather data and local news.
     */
    fun generateLocationOverview(
        weather: Weather,
        articles: List<Article>
    ): Flow<Resource<String>> = flow {
        emit(Resource.Loading)
        try {
            val newsSection = articles.take(5).joinToString("\n") { "- ${it.title}" }

            val prompt = buildString {
                append("You are a helpful local news & weather assistant. ")
                append("Based on the following data, provide a brief, friendly overview ")
                append("of what's happening in this location. Include weather advice ")
                append("and highlight the most important local news. Keep it under 150 words.\n\n")
                append("=== WEATHER ===\n")
                append("Location: ${weather.cityName}, ${weather.country}\n")
                append("Temperature: ${weather.temperatureCelsius} (Feels like ${weather.feelsLikeCelsius})\n")
                append("Conditions: ${weather.description}\n")
                append("Humidity: ${weather.humidity}%\n")
                append("Wind: ${weather.windSpeed} m/s\n\n")
                append("=== TOP LOCAL NEWS ===\n")
                if (articles.isEmpty()) {
                    append("No local news available at the moment.\n")
                } else {
                    append(newsSection)
                }
            }

            val response = model.generateContent(content { text(prompt) })
            val insight = response.text ?: "Unable to generate overview."
            emit(Resource.Success(insight))
        } catch (e: Exception) {
            emit(Resource.Error("Overview failed: ${e.message}"))
        }
    }
}
