package com.jaymin.newsaggregator.core.data.remote.api

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.jaymin.newsaggregator.core.common.util.Constants
import com.jaymin.newsaggregator.core.data.local.dao.ArticleDao
import com.jaymin.newsaggregator.core.data.mapper.toDomain
import com.jaymin.newsaggregator.core.data.mapper.toEntity
import com.jaymin.newsaggregator.core.domain.model.Article

/**
 * Paging source that loads news articles page by page from NewsAPI REST endpoint.
 * Supports both top-headlines and search modes.
 */
class NewsPagingSource(
    private val newsApiService: NewsApiService,
    private val articleDao: ArticleDao,
    private val category: String? = null,
    private val country: String? = null,
    private val query: String? = null
) : PagingSource<Int, Article>() {

    override fun getRefreshKey(state: PagingState<Int, Article>): Int? {
        return state.anchorPosition?.let { anchor ->
            state.closestPageToPosition(anchor)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(anchor)?.nextKey?.minus(1)
        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Article> {
        val page = params.key ?: 1
        return try {
            val response = if (query != null) {
                newsApiService.searchNews(
                    query = query,
                    page = page,
                    pageSize = params.loadSize,
                    apiKey = Constants.NEWS_API_KEY
                )
            } else {
                newsApiService.getTopHeadlines(
                    country = country ?: "us",
                    category = category ?: "general",
                    page = page,
                    pageSize = params.loadSize,
                    apiKey = Constants.NEWS_API_KEY
                )
            }

            val articles = response.articles
                .filter { it.title != null && it.title != "[Removed]" }
                .map { it.toDomain(category ?: "general") }

            // Cache articles in background so detail screen can find them
            articleDao.insertArticlesIgnore(articles.map { it.toEntity() })

            LoadResult.Page(
                data = articles,
                prevKey = if (page == 1) null else page - 1,
                nextKey = if (articles.isEmpty()) null else page + 1
            )
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }
}
