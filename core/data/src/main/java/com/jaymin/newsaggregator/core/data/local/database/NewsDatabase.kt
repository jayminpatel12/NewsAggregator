package com.jaymin.newsaggregator.core.data.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.jaymin.newsaggregator.core.data.local.dao.ArticleDao
import com.jaymin.newsaggregator.core.data.local.dao.WeatherDao
import com.jaymin.newsaggregator.core.data.local.entity.ArticleEntity
import com.jaymin.newsaggregator.core.data.local.entity.WeatherEntity

@Database(
    entities = [ArticleEntity::class, WeatherEntity::class],
    version = 1,
    exportSchema = false
)
abstract class NewsDatabase : RoomDatabase() {
    abstract fun articleDao(): ArticleDao
    abstract fun weatherDao(): WeatherDao
}
