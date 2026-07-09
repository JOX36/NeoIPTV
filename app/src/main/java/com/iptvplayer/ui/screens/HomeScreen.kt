package com.iptvplayer.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.iptvplayer.data.Channel
import com.iptvplayer.data.M3UParser

@Composable
fun HomeScreen(navController: NavController) {
    val channels = remember { mutableStateListOf<Channel>() }

    // Load channels from M3U
    LaunchedEffect(Unit) {
        val loadedChannels = M3UParser.parseM3U("https://iptv-org.github.io/iptv/countries/usa.m3u") // Ejemplo público - cámbialo por tu lista
        channels.addAll(loadedChannels)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("NeoIPTV", style = MaterialTheme.typography.titleLarge) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent
                )
            )
        },
        containerColor = Color(0xFF0A0A1F)
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(
                    Brush.verticalGradient(
                        listOf(Color(0xFF0A0A1F), Color(0xFF1A1A2E))
                    )
                )
        ) {
            items(channels) { channel ->
                ChannelItem(channel) {
                    navController.navigate("player/${channel.url}")
                }
            }
        }
    }
}

@Composable
fun ChannelItem(channel: Channel, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1A1A2E))
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Thumbnail
            Box(modifier = Modifier.size(80.dp).background(Color.DarkGray))
            Column {
                Text(channel.name, style = MaterialTheme.typography.titleMedium)
                Text(channel.category ?: "", style = MaterialTheme.typography.bodyMedium)
            }
        }
    }
}