package com.iptvplayer.ui.navigation

import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState

sealed class BottomNavItem(val route: String, val title: String, val icon: String) {
    object Home : BottomNavItem("home", "Inicio", "🏠")
    object Search : BottomNavItem("search", "Buscar", "🔍")
    object Playlists : BottomNavItem("playlists", "Listas", "📋")
    object Favorites : BottomNavItem("favorites", "Favoritos", "⭐")
}

@Composable
fun BottomNavBar(navController: NavController) {
    val items = listOf(
        BottomNavItem.Home,
        BottomNavItem.Search,
        BottomNavItem.Playlists,
        BottomNavItem.Favorites
    )

    NavigationBar(
        containerColor = Color(0xFF0A0A1F).copy(alpha = 0.95f)
    ) {
        val navBackStackEntry = navController.currentBackStackEntryAsState()
        val currentRoute = navBackStackEntry.value?.destination?.route

        items.forEach { item ->
            NavigationBarItem(
                icon = { Text(item.icon, style = MaterialTheme.typography.headlineMedium) },
                label = { Text(item.title) },
                selected = currentRoute == item.route,
                onClick = {
                    navController.navigate(item.route) {
                        popUpTo(navController.graph.startDestinationId)
                        launchSingleTop = true
                    }
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = Color(0xFF00F0FF),
                    selectedTextColor = Color(0xFF00F0FF)
                )
            )
        }
    }
}