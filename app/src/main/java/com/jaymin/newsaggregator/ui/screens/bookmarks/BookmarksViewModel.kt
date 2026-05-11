package com.jaymin.newsaggregator.ui.screens.bookmarks

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jaymin.newsaggregator.core.domain.model.Article
import com.jaymin.newsaggregator.core.domain.usecase.GetBookmarkedArticlesUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

data class BookmarksUiState(
    val bookmarkedArticles: List<Article> = emptyList(),
    val isLoading: Boolean = true,
    val groupedArticles: Map<String, List<Article>> = emptyMap()
)

@HiltViewModel
class BookmarksViewModel @Inject constructor(
    private val getBookmarkedArticles: GetBookmarkedArticlesUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(BookmarksUiState())
    val uiState: StateFlow<BookmarksUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            getBookmarkedArticles().collectLatest { articles ->
                _uiState.value = BookmarksUiState(
                    bookmarkedArticles = articles,
                    isLoading = false,
                    groupedArticles = articles.groupBy { it.category }
                )
            }
        }
    }
}
