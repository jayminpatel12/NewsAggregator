package com.jaymin.newsaggregator.core.data.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.jaymin.newsaggregator.core.common.util.Constants
import com.jaymin.newsaggregator.core.common.util.Resource
import com.jaymin.newsaggregator.core.data.local.dao.ArticleDao
import com.jaymin.newsaggregator.core.data.mapper.toDomain
import com.jaymin.newsaggregator.core.data.mapper.toEntity
import com.jaymin.newsaggregator.core.data.remote.api.NewsApiService
import com.jaymin.newsaggregator.core.data.remote.api.NewsPagingSource
import com.jaymin.newsaggregator.core.domain.model.Article
import com.jaymin.newsaggregator.core.domain.repository.NewsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NewsRepositoryImpl @Inject constructor(
    private val newsApiService: NewsApiService,
    private val articleDao: ArticleDao
) : NewsRepository {

    override fun getTopHeadlines(
        category: String,
        country: String
    ): Flow<PagingData<Article>> = Pager(
        config = PagingConfig(
            pageSize = Constants.PAGE_SIZE,
            initialLoadSize = Constants.INITIAL_LOAD_SIZE,
            enablePlaceholders = false
        ),
        pagingSourceFactory = {
            NewsPagingSource(
                newsApiService = newsApiService,
                articleDao = articleDao,
                category = category,
                country = country
            )
        }
    ).flow

    override fun searchNews(query: String): Flow<PagingData<Article>> = Pager(
        config = PagingConfig(
            pageSize = Constants.PAGE_SIZE,
            initialLoadSize = Constants.INITIAL_LOAD_SIZE,
            enablePlaceholders = false
        ),
        pagingSourceFactory = {
            NewsPagingSource(
                newsApiService = newsApiService,
                articleDao = articleDao,
                query = query
            )
        }
    ).flow

    override fun getNewsByLocation(
        query: String,
        country: String
    ): Flow<Resource<List<Article>>> = flow {
        emit(Resource.Loading)
        try {
            val response = newsApiService.searchNews(
                query = query,
                pageSize = 10,
                apiKey = Constants.NEWS_API_KEY
            )
            val articles = response.articles
                .filter { it.title != null && it.title != "[Removed]" }
                .map { it.toDomain() }

            // Cache articles locally so they can be viewed in detail screen
            articleDao.insertArticlesIgnore(articles.map { it.toEntity() })

            emit(Resource.Success(articles))
        } catch (e: Exception) {
            emit(Resource.Error(e.message ?: "Failed to fetch location news"))
        }
    }

    override fun getBookmarkedArticles(): Flow<List<Article>> =
        flow {
            articleDao.getBookmarkedArticles().collect { entities ->
                emit(entities.map { it.toDomain() })
            }
        }

    override suspend fun bookmarkArticle(article: Article) {
        articleDao.insertArticles(listOf(article.toEntity().copy(isBookmarked = true)))
    }

    override suspend fun removeBookmark(articleId: String) {
        articleDao.removeBookmark(articleId)
    }

    override suspend fun isBookmarked(articleId: String): Boolean =
        articleDao.isBookmarked(articleId)

    override suspend fun getArticleById(id: String): Article? =
        articleDao.getArticleById(id)?.toDomain()
}
