# NewsAggregator

An Android app built with Kotlin and Jetpack Compose that combines news and weather. It uses Gemini to provide summaries and local briefings.

## Key Features
- **Local Briefing** — Combines current weather and local news for a quick morning update.
- **Top Headlines** — Browse news by category (Business, Tech, etc.) using Paging 3.
- **Search** — Find news on any topic globally.
- **Live Weather** — Integrated weather data from OpenWeatherMap.
- **Smart Summaries** — Generate quick summaries for any article.
- **Offline Support** — Bookmarks and recent articles are cached in Room for offline reading.
- **Location Aware** — Automatically detects your city via GPS.

## Tech Stack
- **Architecture**: Multi-module Clean Architecture + MVVM
- **UI**: Jetpack Compose with Material 3
- **Network**: Retrofit & Apollo GraphQL
- **Database**: Room
- **DI**: Hilt
- **AI**: Google Generative AI (Gemini)

## Getting Started

1. Get your API keys:
   - [NewsAPI](https://newsapi.org/)
   - [OpenWeatherMap](https://openweathermap.org/)
   - [Gemini](https://aistudio.google.com/)

2. Add them to `core/common/src/main/java/com/jaymin/newsaggregator/core/common/util/Constants.kt`.

3. Build and run in Android Studio.


<!-- PORTFOLIO
title: News Aggregator
subtitle: News and Weather with AI Summary
description: 
tags: Kotlin, Jetpack Compose, Hilt, Room, Retrofit
icon: 📱
order: 1
color: blue
-->
```

## License
MIT
