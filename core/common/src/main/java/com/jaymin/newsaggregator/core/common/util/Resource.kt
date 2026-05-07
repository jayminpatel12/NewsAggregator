package com.jaymin.newsaggregator.core.common.util

/**
 * A generic wrapper class for handling data states across the app.
 * Follows the Resource pattern for clean StateFlow-driven UI updates.
 */
sealed class Resource<out T> {
    data class Success<T>(val data: T) : Resource<T>()
    data class Error(val message: String, val throwable: Throwable? = null) : Resource<Nothing>()
    data object Loading : Resource<Nothing>()
}
