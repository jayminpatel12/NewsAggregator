package com.jaymin.newsaggregator.ui.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.Cloud
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Newspaper
import androidx.compose.material.icons.outlined.Bookmark
import androidx.compose.material.icons.outlined.Cloud
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Newspaper
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.jaymin.newsaggregator.ui.screens.detail.ArticleDetailScreen
import com.jaymin.newsaggregator.ui.screens.home.HomeScreen
import com.jaymin.newsaggregator.ui.screens.news.NewsScreen
import com.jaymin.newsaggregator.ui.screens.weather.WeatherScreen

import java.net.URLEncoder
import java.nio.charset.StandardCharsets

sealed class Screen(
    val route: String,
    val title: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector
) {
    data object Home : Screen("home", "Home", Icons.Filled.Home, Icons.Outlined.Home)
    data object News : Screen("news", "News", Icons.Filled.Newspaper, Icons.Outlined.Newspaper)
    data object Weather : Screen("weather", "Weather", Icons.Filled.Cloud, Icons.Outlined.Cloud)
    data object Bookmarks : Screen("bookmarks", "Saved", Icons.Filled.Bookmark, Icons.Outlined.Bookmark)
    data object ArticleDetail : Screen("article/{articleId}", "Detail", Icons.Filled.Newspaper, Icons.Outlined.Newspaper)
}

val bottomNavItems = listOf(Screen.Home, Screen.News, Screen.Weather, Screen.Bookmarks)

@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    Scaffold(
        bottomBar = { BottomNavBar(navController) }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Home.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(Screen.Home.route) {
                HomeScreen(
                    onArticleClick = { articleId ->
                        val encodedId = URLEncoder.encode(articleId, StandardCharsets.UTF_8.toString())
                        navController.navigate("article/$encodedId")
                    }
                )
            }
            composable(Screen.News.route) {
                NewsScreen(
                    onArticleClick = { articleId ->
                        val encodedId = URLEncoder.encode(articleId, StandardCharsets.UTF_8.toString())
                        navController.navigate("article/$encodedId")
                    }
                )
            }
            composable(Screen.Weather.route) {
                WeatherScreen()
            }
            composable(Screen.Bookmarks.route) {
                NewsScreen(
                    showBookmarksOnly = true,
                    onArticleClick = { articleId ->
                        val encodedId = URLEncoder.encode(articleId, StandardCharsets.UTF_8.toString())
                        navController.navigate("article/$encodedId")
                    }
                )
            }
            composable(Screen.ArticleDetail.route) { backStackEntry ->
                val articleId = backStackEntry.arguments?.getString("articleId") ?: ""
                ArticleDetailScreen(
                    articleId = articleId,
                    onBackClick = { navController.popBackStack() }
                )
            }
        }
    }
}

@Composable
fun BottomNavBar(navController: NavHostController) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    // Hide bottom bar on detail screen
    if (currentDestination?.route?.startsWith("article/") == true) return

    NavigationBar {
        bottomNavItems.forEach { screen ->
            val selected = currentDestination?.hierarchy?.any { it.route == screen.route } == true
            NavigationBarItem(
                icon = {
                    Icon(
                        imageVector = if (selected) screen.selectedIcon else screen.unselectedIcon,
                        contentDescription = screen.title
                    )
                },
                label = { Text(screen.title) },
                selected = selected,
                onClick = {
                    navController.navigate(screen.route) {
                        popUpTo(navController.graph.findStartDestination().id) {
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            )
        }
    }
}
