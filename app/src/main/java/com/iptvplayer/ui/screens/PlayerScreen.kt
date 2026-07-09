package com.iptvplayer.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.navigation.NavController
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView
import com.iptvplayer.data.EpgProgram
import java.text.SimpleDateFormat
import java.util.Date

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlayerScreen(url: String, navController: NavController) {
    val context = LocalContext.current
    val exoPlayer = remember {
        ExoPlayer.Builder(context).build().apply {
            setMediaItem(MediaItem.fromUri(url))
            prepare()
            playWhenReady = true
        }
    }

    DisposableEffect(Unit) {
        onDispose {
            exoPlayer.release()
        }
    }

    Box(modifier = Modifier.fillMaxSize().background(Color.Black)) {
        AndroidView(
            factory = { ctx ->
                PlayerView(ctx).apply {
                    player = exoPlayer
                    useController = true
                }
            },
            modifier = Modifier.fillMaxSize()
        )

        Column(modifier = Modifier.fillMaxSize()) {
            TopAppBar(
                title = { Text("Reproduciendo") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Text("←")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Black.copy(alpha = 0.7f))
            )

            EpgPanel()
        }
    }
}

@Composable
fun EpgPanel() {
    val currentProgram = remember {
        EpgProgram(
            channelId = "1",
            title = "Programa Actual",
            description = "Descripción del contenido en vivo...",
            startTime = Date(),
            endTime = Date(System.currentTimeMillis() + 3600000)
        )
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1A1A2E).copy(alpha = 0.95f))
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text("📺 EPG - Ahora", style = MaterialTheme.typography.titleSmall, color = Color(0xFF00F0FF))
            Text(currentProgram.title, style = MaterialTheme.typography.titleMedium)
            Text(
                "${SimpleDateFormat("HH:mm").format(currentProgram.startTime)} - ${SimpleDateFormat("HH:mm").format(currentProgram.endTime)}",
                style = MaterialTheme.typography.bodySmall
            )
            currentProgram.description?.let { Text(it, style = MaterialTheme.typography.bodyMedium) }
        }
    }
}
