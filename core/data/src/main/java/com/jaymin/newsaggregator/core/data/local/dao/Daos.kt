package com.jaymin.newsaggregator.core.data.local.dao

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.jaymin.newsaggregator.core.data.local.entity.ArticleEntity
import com.jaymin.newsaggregator.core.data.local.entity.WeatherEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ArticleDao {

    @Query("SELECT * FROM articles WHERE category = :category ORDER BY publishedAt DESC")
    fun getArticlesByCategory(category: String): PagingSource<Int, ArticleEntity>

    @Query("SELECT * FROM articles WHERE isBookmarked = 1 ORDER BY publishedAt DESC")
    fun getBookmarkedArticles(): Flow<List<ArticleEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertArticles(articles: List<ArticleEntity>)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertArticlesIgnore(articles: List<ArticleEntity>)

    @Query("UPDATE articles SET isBookmarked = 1, aiSummary = :aiSummary WHERE id = :id")
    suspend fun bookmarkArticle(id: String, aiSummary: String? = null)

    @Query("UPDATE articles SET isBookmarked = 0 WHERE id = :id")
    suspend fun removeBookmark(id: String)

    @Query("SELECT EXISTS(SELECT 1 FROM articles WHERE id = :id AND isBookmarked = 1)")
    suspend fun isBookmarked(id: String): Boolean

    @Query("UPDATE articles SET aiSummary = :summary WHERE id = :id")
    suspend fun updateAiSummary(id: String, summary: String)

    @Query("DELETE FROM articles WHERE isBookmarked = 0 AND cachedAt < :threshold")
    suspend fun clearOldCache(threshold: Long)

    @Query("DELETE FROM articles WHERE category = :category AND isBookmarked = 0")
    suspend fun clearCategoryCache(category: String)

    @Query("SELECT * FROM articles WHERE id = :id LIMIT 1")
    suspend fun getArticleById(id: String): ArticleEntity?
}

@Dao
interface WeatherDao {

    @Query("SELECT * FROM weather_cache WHERE cityName = :city LIMIT 1")
    suspend fun getWeatherByCity(city: String): WeatherEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWeather(weather: WeatherEntity)

    @Query("DELETE FROM weather_cache WHERE cachedAt < :threshold")
    suspend fun clearOldCache(threshold: Long)
}
