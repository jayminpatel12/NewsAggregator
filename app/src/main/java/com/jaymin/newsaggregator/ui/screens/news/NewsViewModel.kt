package com.jaymin.newsaggregator.ui.screens.news

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.jaymin.newsaggregator.core.domain.model.Article
import com.jaymin.newsaggregator.core.domain.usecase.GetTopHeadlinesUseCase
import com.jaymin.newsaggregator.core.domain.usecase.SearchNewsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NewsViewModel @Inject constructor(
    private val getTopHeadlines: GetTopHeadlinesUseCase,
    private val searchNews: SearchNewsUseCase
) : ViewModel() {

    private val _articles = MutableStateFlow<PagingData<Article>>(PagingData.empty())
    val articles: StateFlow<PagingData<Article>> = _articles.asStateFlow()

    init {
        loadCategory("general")
    }

    fun loadCategory(category: String) {
        viewModelScope.launch {
            getTopHeadlines(category = category)
                .cachedIn(viewModelScope)
                .collectLatest { _articles.value = it }
        }
    }

    fun searchNews(query: String) {
        viewModelScope.launch {
            searchNews.invoke(query)
                .cachedIn(viewModelScope)
                .collectLatest { _articles.value = it }
        }
    }
}
