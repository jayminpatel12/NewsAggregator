package com.jaymin.newsaggregator.core.domain.usecase

import androidx.paging.PagingData
import com.jaymin.newsaggregator.core.common.util.Resource
import com.jaymin.newsaggregator.core.domain.model.Article
import com.jaymin.newsaggregator.core.domain.repository.NewsRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetTopHeadlinesUseCase @Inject constructor(
    private val repository: NewsRepository
) {
    operator fun invoke(
        category: String = "general",
        country: String = "us"
    ): Flow<PagingData<Article>> = repository.getTopHeadlines(category, country)
}

class SearchNewsUseCase @Inject constructor(
    private val repository: NewsRepository
) {
    operator fun invoke(query: String): Flow<PagingData<Article>> =
        repository.searchNews(query)
}

class GetNewsByLocationUseCase @Inject constructor(
    private val repository: NewsRepository
) {
    operator fun invoke(
        query: String,
        country: String
    ): Flow<Resource<List<Article>>> = repository.getNewsByLocation(query, country)
}

class GetBookmarkedArticlesUseCase @Inject constructor(
    private val repository: NewsRepository
) {
    operator fun invoke(): Flow<List<Article>> = repository.getBookmarkedArticles()
}

class ToggleBookmarkUseCase @Inject constructor(
    private val repository: NewsRepository
) {
    suspend operator fun invoke(article: Article) {
        if (repository.isBookmarked(article.id)) {
            repository.removeBookmark(article.id)
        } else {
            repository.bookmarkArticle(article)
        }
    }
}
