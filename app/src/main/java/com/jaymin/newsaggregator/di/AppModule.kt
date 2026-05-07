package com.jaymin.newsaggregator.di

import android.content.Context
import androidx.room.Room
import com.jaymin.newsaggregator.core.ai.service.GeminiAiService
import com.jaymin.newsaggregator.core.common.util.Constants
import com.jaymin.newsaggregator.core.data.local.dao.ArticleDao
import com.jaymin.newsaggregator.core.data.local.dao.WeatherDao
import com.jaymin.newsaggregator.core.data.local.database.NewsDatabase
import com.jaymin.newsaggregator.core.data.remote.api.NewsApiService
import com.jaymin.newsaggregator.core.data.remote.api.WeatherApiService
import com.jaymin.newsaggregator.core.data.repository.NewsRepositoryImpl
import com.jaymin.newsaggregator.core.data.repository.WeatherRepositoryImpl
import com.jaymin.newsaggregator.core.domain.repository.NewsRepository
import com.jaymin.newsaggregator.core.domain.repository.WeatherRepository
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Named
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    // Network

    @Provides
    @Singleton
    fun provideOkHttpClient(): OkHttpClient = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .addInterceptor(
            HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BODY
            }
        )
        .build()

    @Provides
    @Singleton
    fun provideJson(): Json = Json {
        ignoreUnknownKeys = true
        coerceInputValues = true
        isLenient = true
    }

    @Provides
    @Singleton
    @Named("news")
    fun provideNewsRetrofit(client: OkHttpClient, json: Json): Retrofit = Retrofit.Builder()
        .baseUrl(Constants.NEWS_BASE_URL)
        .client(client)
        .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
        .build()

    @Provides
    @Singleton
    @Named("weather")
    fun provideWeatherRetrofit(client: OkHttpClient, json: Json): Retrofit = Retrofit.Builder()
        .baseUrl(Constants.WEATHER_BASE_URL)
        .client(client)
        .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
        .build()

    @Provides
    @Singleton
    fun provideNewsApiService(@Named("news") retrofit: Retrofit): NewsApiService =
        retrofit.create(NewsApiService::class.java)

    @Provides
    @Singleton
    fun provideWeatherApiService(@Named("weather") retrofit: Retrofit): WeatherApiService =
        retrofit.create(WeatherApiService::class.java)

    // Database

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): NewsDatabase =
        Room.databaseBuilder(
            context,
            NewsDatabase::class.java,
            Constants.DATABASE_NAME
        )
        .fallbackToDestructiveMigration()
        .build()

    @Provides
    fun provideArticleDao(database: NewsDatabase): ArticleDao = database.articleDao()

    @Provides
    fun provideWeatherDao(database: NewsDatabase): WeatherDao = database.weatherDao()

    // Repositories

    @Provides
    @Singleton
    fun provideNewsRepository(
        newsApiService: NewsApiService,
        articleDao: ArticleDao
    ): NewsRepository = NewsRepositoryImpl(newsApiService, articleDao)

    @Provides
    @Singleton
    fun provideWeatherRepository(
        weatherApiService: WeatherApiService,
        weatherDao: WeatherDao
    ): WeatherRepository = WeatherRepositoryImpl(weatherApiService, weatherDao)

    // Location

    @Provides
    @Singleton
    fun provideFusedLocationClient(@ApplicationContext context: Context): FusedLocationProviderClient =
        LocationServices.getFusedLocationProviderClient(context)

    // AI Integration

    @Provides
    @Singleton
    fun provideGeminiAiService(): GeminiAiService = GeminiAiService()
}
