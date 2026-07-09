package com.iptvplayer.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "channel")
data class Channel(
    @PrimaryKey val id: String,
    val name: String,
    val url: String,
    val logo: String? = null,
    val category: String? = null,
    val isFavorite: Boolean = false
)
