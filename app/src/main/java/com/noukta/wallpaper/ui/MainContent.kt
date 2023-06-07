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
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.noukta.wallpaper.MainViewModel
import com.noukta.wallpaper.ui.components.AppTopBar
import com.noukta.wallpaper.ui.components.ExitDialog
import com.noukta.wallpaper.ui.nav.BottomNavigationBar
import com.noukta.wallpaper.ui.nav.Screen
import com.noukta.wallpaper.ui.screens.HomeScreen
import com.noukta.wallpaper.ui.screens.SplashScreen

@Composable
fun MainContent(vm: MainViewModel) {
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
        if (uiState.wallpapers.isEmpty())
            vm.fetchWallpapers()
//        if (uiState.favorites.isEmpty())
//            vm.fetchFavorites()
    }

    Scaffold(
        topBar = { AppTopBar(currentScreen) },
        bottomBar = {
            BottomNavigationBar(
                screen = currentScreen,
                onClick = {
                    if (currentScreen == Screen.Home)
                        navController.navigate(Screen.Favorites.route)
                    else
                        navController.navigateUp()
                }
            )
        }
    ) {
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
                HomeScreen(
                    wallpapers = uiState.wallpapers,
                    firstVisibleIdx = vm.homeFirstVisibleIdx,
                    firstVisibleOffset = vm.homeFirstVisibleOffset,
                    onLikeClick = { wallpaper, liked ->
                        vm.likeWallpaper(wallpaper, liked)
                    },
                    onShuffle = { vm.shuffleWallpapers() },
                    onWallpaperPreview = { wallpaperIdx, firstVisibleIdx, firstVisibleOffset ->
                        vm.persistHomeScreen(firstVisibleIdx, firstVisibleOffset)
                        vm.updateWallpaperIdx(wallpaperIdx)
                        navController.navigate(Screen.Preview.route)
                    }
                )
            }
            composable(Screen.Favorites.route) {
                // TODO: FavoritesScreen
            }
            composable(Screen.Preview.route) {
                // TODO: PreviewScreen
            }
        }
    }

    ExitDialog(vm.showExit) {
        vm.showExit = false
    }
}