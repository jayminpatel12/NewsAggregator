package com.jaymin.newsaggregator.core.domain.repository

import androidx.paging.PagingData
import com.jaymin.newsaggregator.core.common.util.Resource
import com.jaymin.newsaggregator.core.domain.model.Article
import kotlinx.coroutines.flow.Flow

/**
 * News repository contract — the domain layer defines WHAT it needs,
 * the data layer decides HOW to get it.
 */
interface NewsRepository {

    fun getTopHeadlines(
        category: String = "general",
        country: String = "us"
    ): Flow<PagingData<Article>>

    fun searchNews(query: String): Flow<PagingData<Article>>

    fun getNewsByLocation(
        query: String,
        country: String
    ): Flow<Resource<List<Article>>>

    fun getBookmarkedArticles(): Flow<List<Article>>

    suspend fun bookmarkArticle(article: Article)

    suspend fun removeBookmark(articleId: String)

    suspend fun isBookmarked(articleId: String): Boolean

    suspend fun getArticleById(id: String): Article?
}
