package com.iptvplayer.data

import java.util.Date

data class EpgProgram(
    val channelId: String,
    val title: String,
    val description: String?,
    val startTime: Date,
    val endTime: Date
)