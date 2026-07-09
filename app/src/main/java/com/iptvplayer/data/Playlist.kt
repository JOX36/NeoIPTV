package com.iptvplayer.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "playlist")
data class Playlist(
    @PrimaryKey val id: String,
    val name: String,
    val url: String,
    val type: String = "M3U",
    val username: String? = null,
    val password: String? = null,
    val isActive: Boolean = true
)
