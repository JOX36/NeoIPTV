package com.iptvplayer.data

data class Channel(
    val id: String,
    val name: String,
    val url: String,
    val logo: String? = null,
    val category: String? = null,
    val isFavorite: Boolean = false
)