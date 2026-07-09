package com.iptvplayer.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.navigation.NavController

@Composable
fun FavoritesScreen(navController: NavController) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("⭐ Tus Favoritos", style = MaterialTheme.typography.titleLarge, color = Color(0xFF39FF14))
        Text("Los canales guardados aparecerán aquí", style = MaterialTheme.typography.bodyLarge)
    }
}
