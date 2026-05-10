package com.jaymin.newsaggregator.core.ai.service

import android.util.Log
import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.BlockThreshold
import com.google.ai.client.generativeai.type.HarmCategory
import com.google.ai.client.generativeai.type.SafetySetting
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
            apiKey = Constants.GEMINI_API_KEY,
            safetySettings = listOf(
                SafetySetting(HarmCategory.HARASSMENT, BlockThreshold.ONLY_HIGH),
                SafetySetting(HarmCategory.HATE_SPEECH, BlockThreshold.ONLY_HIGH),
                SafetySetting(HarmCategory.SEXUALLY_EXPLICIT, BlockThreshold.ONLY_HIGH),
                SafetySetting(HarmCategory.DANGEROUS_CONTENT, BlockThreshold.ONLY_HIGH)
            )
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
                append("Focus on the key facts and implications. ")
                append("If the content is insufficient, use the title and description to infer the summary.\n\n")
                append("Title: ${article.title}\n")
                article.description?.let { append("Description: $it\n") }
                article.content?.let { append("Content: $it\n") }
                append("Source: ${article.source}")
            }

            val response = model.generateContent(content { text(prompt) })
            val summary = response.text
            
            if (summary != null) {
                emit(Resource.Success(summary))
            } else {
                Log.e("GeminiAiService", "Empty response for article: ${article.title}")
                emit(Resource.Error("Unable to generate summary at this time."))
            }
        } catch (e: Exception) {
            Log.e("GeminiAiService", "Error summarizing article: ${article.title}", e)
            emit(Resource.Error("Something went wrong. Please try again later."))
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
            val insight = response.text
            
            if (insight != null) {
                emit(Resource.Success(insight))
            } else {
                Log.e("GeminiAiService", "Empty response for location overview: ${weather.cityName}")
                emit(Resource.Error("Unable to generate overview."))
            }
        } catch (e: Exception) {
            Log.e("GeminiAiService", "Error generating location overview", e)
            emit(Resource.Error("Something went wrong. Please try again later."))
        }
    }
}
