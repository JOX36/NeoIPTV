package com.iptvplayer

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.iptvplayer.ui.navigation.BottomNavBar
import com.iptvplayer.ui.screens.FavoritesScreen
import com.iptvplayer.ui.screens.HomeScreen
import com.iptvplayer.ui.screens.PlayerScreen
import com.iptvplayer.ui.screens.PlaylistsScreen
import com.iptvplayer.ui.screens.SearchScreen
import com.iptvplayer.ui.screens.XtreamLoginScreen
import com.iptvplayer.viewmodels.MainViewModel

@Composable
fun NeoIPTVApp() {
    val navController = rememberNavController()
    val viewModel: MainViewModel = hiltViewModel()

    Scaffold(
        bottomBar = { BottomNavBar(navController) }
    ) { paddingValues ->
        NavHost(
            navController = navController,
            startDestination = "home",
            modifier = Modifier.padding(paddingValues)
        ) {
            composable("home") { HomeScreen(navController, viewModel) }
            composable("search") { SearchScreen(navController) }
            composable("playlists") { PlaylistsScreen(navController) }
            composable("favorites") { FavoritesScreen(navController) }
            composable("login") { XtreamLoginScreen(navController, viewModel) }
            composable("player/{url}") { backStackEntry ->
                val url = backStackEntry.arguments?.getString("url") ?: ""
                PlayerScreen(url = java.net.URLDecoder.decode(url, "UTF-8"), navController)
            }
        }
    }
}
