package com.jaymin.newsaggregator.core.domain.usecase

import com.jaymin.newsaggregator.core.domain.model.Article
import com.jaymin.newsaggregator.core.domain.repository.NewsRepository
import javax.inject.Inject

class GetArticleByIdUseCase @Inject constructor(
    private val repository: NewsRepository
) {
    suspend operator fun invoke(id: String): Article? = repository.getArticleById(id)
}
