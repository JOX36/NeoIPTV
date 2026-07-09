package com.iptvplayer.data

data class Playlist(
    val id: String,
    val name: String,
    val url: String,           // M3U or Xtream API
    val type: PlaylistType,    // M3U or XTREAM
    val username: String? = null,
    val password: String? = null,
    val isActive: Boolean = true
)

enum class PlaylistType {
    M3U, XTREAM
}