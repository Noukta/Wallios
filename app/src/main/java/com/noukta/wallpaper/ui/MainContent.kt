package com.noukta.wallpaper.ui

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.noukta.wallpaper.MainViewModel
import com.noukta.wallpaper.ext.review
import com.noukta.wallpaper.settings.ReviewChoice
import com.noukta.wallpaper.settings.ReviewTime
import com.noukta.wallpaper.ui.components.AppTopBar
import com.noukta.wallpaper.ui.components.ExitDialog
import com.noukta.wallpaper.ui.components.ReviewDialog
import com.noukta.wallpaper.ui.nav.BottomNavigationBar
import com.noukta.wallpaper.ui.nav.Screen
import com.noukta.wallpaper.ui.screens.FavoritesScreen
import com.noukta.wallpaper.ui.screens.HomeScreen
import com.noukta.wallpaper.ui.screens.PreviewScreen
import com.noukta.wallpaper.ui.screens.SearchScreen
import com.noukta.wallpaper.ui.screens.SplashScreen
import com.noukta.wallpaper.util.PrefHelper

@Composable
fun MainContent(vm: MainViewModel) {
    val context = LocalContext.current
    val uiState by vm.uiState.collectAsState()
    val navController = rememberNavController()
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentScreen by remember {
        derivedStateOf {
            Screen.items.find {
                it.route == backStackEntry?.destination?.route
            } ?: Screen.Splash
        }

    }

    LaunchedEffect(Unit) {
        if (uiState.wallpapers.isEmpty()) vm.fetchWallpapers()
    }

    Scaffold(topBar = {
        AppTopBar(
            screen = currentScreen,
            query = vm.searchTag,
            updateQuery = {
                vm.searchTag = it
            },
            searchByTag = {
                vm.searchByText(it)
                navController.navigate(Screen.Search.route) {
                    launchSingleTop = true
                    popUpTo(Screen.Home.route)
                }
            },
            onLogoClick = {
                navController.navigateUp()
            },
            modifier = Modifier.padding(horizontal = 16.dp)
        )
    }, bottomBar = {
        BottomNavigationBar(screen = currentScreen, onClick = {
            if (currentScreen == Screen.Home) {
                navController.navigate(Screen.Favorites.route)
                vm.updateWallpaperIdx(0, 0)
                vm.fetchFavorites()
            } else navController.navigateUp()
        })
    }) {
        NavHost(
            navController = navController,
            startDestination = Screen.Splash.route,
            modifier = Modifier
                .padding(it)
                .fillMaxSize()
        ) {
            composable(Screen.Splash.route) {
                SplashScreen {
                    navController.popBackStack()
                    navController.navigate(route = Screen.Home.route)
                }
            }
            composable(Screen.Home.route) {
                HomeScreen(wallpapers = uiState.wallpapers,
                    onLikeClick = { wallpaper, liked ->
                        vm.likeWallpaper(wallpaper, liked)
                    },
                    onShuffle = { vm.shuffleWallpapers() },
                    onWallpaperPreview = { wallpaperIdx ->
                        vm.updateWallpaperIdx(wallpaperIdx, uiState.wallpapers.lastIndex)
                        navController.navigate(Screen.Preview.route)
                    },
                    isFavorite = { wallpaperId ->
                        vm.isFavorite(wallpaperId)
                    })
            }
            composable(Screen.Favorites.route) {
                FavoritesScreen(wallpapers = uiState.favorites, onLikeClick = { wallpaper, liked ->
                    vm.likeWallpaper(wallpaper, liked)
                }, onWallpaperPreview = { wallpaperIdx ->
                    vm.updateWallpaperIdx(wallpaperIdx, uiState.favorites.lastIndex)
                    navController.navigate(Screen.Preview.route)
                }, isFavorite = { wallpaperId ->
                    vm.isFavorite(wallpaperId)
                })
            }
            composable(Screen.Search.route) {
                SearchScreen(
                    wallpapers = uiState.searchResult,
                    onLikeClick = { wallpaper, liked ->
                        vm.likeWallpaper(wallpaper, liked)
                    },
                    onWallpaperPreview = { wallpaperIdx ->
                        vm.updateWallpaperIdx(wallpaperIdx, uiState.searchResult.lastIndex)
                        navController.navigate(Screen.Preview.route)
                    },
                    isFavorite = { wallpaperId ->
                        vm.isFavorite(wallpaperId)
                    }
                )
            }
            composable(Screen.Preview.route) {
                val prev = navController.previousBackStackEntry?.destination?.route
                PreviewScreen(
                    wallpapers = when (prev) {
                        Screen.Home.route -> uiState.wallpapers
                        Screen.Favorites.route -> uiState.favorites
                        else -> uiState.searchResult
                    },
                    initialWallpaper = vm.wallpaperIdx,
                    onLikeClick = { wallpaper, liked ->
                        vm.likeWallpaper(wallpaper, liked)
                    },
                    onTagClick = { category ->
                        vm.searchByText(category.name)
                        navController.navigate(Screen.Search.route) {
                            launchSingleTop = true
                            popUpTo(Screen.Home.route)
                        }
                    },
                    isFavorite = { wallpaperId ->
                        vm.isFavorite(wallpaperId)
                    }
                )
            }
        }
    }

    LaunchedEffect(currentScreen){
        val reviewStatus = PrefHelper.getReviewStatus()
        val timeSpent = PrefHelper.getTimeSpent()
        val timeBeforeReview: Long = when(reviewStatus){
            ReviewChoice.REMIND -> ReviewTime.MIN_INTERVAL
            else -> ReviewTime.MAX_INTERVAL
        }
        if (timeSpent >= timeBeforeReview) {
            if (currentScreen == Screen.Preview) {
                vm.setShowReview(true)
            }
        }
    }

    ReviewDialog(vm.showReview){ reviewStatus ->
        vm.setShowReview(false)
        PrefHelper.setTimeSpent(0L)
        PrefHelper.setReviewStatus(reviewStatus)
        if(reviewStatus == ReviewChoice.RATE) {
            review(context)
        }
    }
    ExitDialog(vm.showExit) {
        vm.toggleExitDialog()
    }
}