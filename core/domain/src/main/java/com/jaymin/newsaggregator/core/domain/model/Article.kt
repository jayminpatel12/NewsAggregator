package com.jaymin.newsaggregator.core.domain.model

/**
 * Domain model for a news article.
 */
data class Article(
    val id: String,
    val title: String,
    val description: String?,
    val content: String?,
    val url: String,
    val imageUrl: String?,
    val publishedAt: String,
    val source: String,
    val author: String?,
    val category: String = "general",
    val aiSummary: String? = null,
    val isBookmarked: Boolean = false
)
