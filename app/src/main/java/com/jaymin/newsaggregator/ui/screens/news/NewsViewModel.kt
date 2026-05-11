package com.jaymin.newsaggregator.ui.screens.news

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.jaymin.newsaggregator.core.domain.model.Article
import com.jaymin.newsaggregator.core.domain.usecase.GetTopHeadlinesUseCase
import com.jaymin.newsaggregator.core.domain.usecase.SearchNewsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import javax.inject.Inject

@HiltViewModel
class NewsViewModel @Inject constructor(
    private val getTopHeadlinesUseCase: GetTopHeadlinesUseCase,
    private val searchNewsUseCase: SearchNewsUseCase
) : ViewModel() {

    private val _currentCategory = MutableStateFlow("general")
    private val _searchQuery = MutableStateFlow<String?>(null)

    @OptIn(ExperimentalCoroutinesApi::class)
    val articles: Flow<PagingData<Article>> = combine(
        _currentCategory,
        _searchQuery
    ) { category, query ->
        category to query
    }.flatMapLatest { (category, query) ->
        if (query.isNullOrBlank()) {
            getTopHeadlinesUseCase(category = category)
        } else {
            searchNewsUseCase(query)
        }
    }.cachedIn(viewModelScope)

    fun loadCategory(category: String) {
        _searchQuery.value = null
        _currentCategory.value = category
    }

    fun searchNews(query: String) {
        _searchQuery.value = query
    }
}
