package com.iptvplayer.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface ChannelDao {
    @Query("SELECT * FROM channel")
    fun getAllChannels(): Flow<List<Channel>>

    @Insert
    suspend fun insertAll(channels: List<Channel>)
}