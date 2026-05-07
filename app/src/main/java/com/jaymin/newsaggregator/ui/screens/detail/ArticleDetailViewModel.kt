package com.jaymin.newsaggregator.ui.screens.detail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jaymin.newsaggregator.core.ai.service.GeminiAiService
import com.jaymin.newsaggregator.core.common.util.Resource
import com.jaymin.newsaggregator.core.domain.model.Article
import com.jaymin.newsaggregator.core.domain.usecase.GetArticleByIdUseCase
import com.jaymin.newsaggregator.core.domain.usecase.ToggleBookmarkUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ArticleDetailUiState(
    val article: Article? = null,
    val isBookmarked: Boolean = false,
    val summary: String = "",
    val hasSummary: Boolean = false,
    val isSummaryLoading: Boolean = false,
    val isLoading: Boolean = true,
    val error: String? = null
)

@HiltViewModel
class ArticleDetailViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val getArticleById: GetArticleByIdUseCase,
    private val geminiAiService: GeminiAiService,
    private val toggleBookmark: ToggleBookmarkUseCase
) : ViewModel() {

    private val articleId: String = savedStateHandle["articleId"] ?: ""

    private val _uiState = MutableStateFlow(ArticleDetailUiState())
    val uiState: StateFlow<ArticleDetailUiState> = _uiState.asStateFlow()

    init {
        loadArticle()
    }

    private fun loadArticle() {
        if (articleId.isBlank()) {
            _uiState.update { it.copy(isLoading = false, error = "Invalid Article ID") }
            return
        }

        viewModelScope.launch {
            val article = getArticleById(articleId)
            if (article != null) {
                _uiState.update {
                    it.copy(
                        article = article,
                        isBookmarked = article.isBookmarked,
                        summary = article.aiSummary ?: "",
                        hasSummary = article.aiSummary != null,
                        isLoading = false
                    )
                }
            } else {
                _uiState.update { it.copy(isLoading = false, error = "Article not found") }
            }
        }
    }

    fun generateSummary() {
        val article = _uiState.value.article ?: return
        viewModelScope.launch {
            geminiAiService.summarizeArticle(article).collect { result ->
                when (result) {
                    is Resource.Loading -> {
                        _uiState.update { it.copy(isSummaryLoading = true) }
                    }
                    is Resource.Error -> {
                        _uiState.update {
                            it.copy(isSummaryLoading = false, summary = "Could not generate summary.")
                        }
                    }
                    is Resource.Success -> {
                        _uiState.update {
                            it.copy(
                                isSummaryLoading = false,
                                summary = result.data,
                                hasSummary = true
                            )
                        }
                    }
                }
            }
        }
    }

    fun toggleBookmark() {
        val article = _uiState.value.article ?: return
        viewModelScope.launch {
            toggleBookmark.invoke(article)
            _uiState.update { it.copy(isBookmarked = !it.isBookmarked) }
        }
    }
}
