package com.iptvplayer.data

import com.google.gson.Gson
import com.google.gson.annotations.SerializedName
import java.net.URL

data class XtreamAuth(
    val user_info: UserInfo,
    val server_info: ServerInfo,
    val available_channels: List<XtreamChannel>
)

data class UserInfo(
    @SerializedName("username") val username: String,
    @SerializedName("password") val password: String,
    @SerializedName("auth") val auth: Int
)

data class ServerInfo(
    @SerializedName("url") val url: String
)

data class XtreamChannel(
    @SerializedName("num") val num: Int,
    @SerializedName("name") val name: String,
    @SerializedName("stream_type") val streamType: String,
    @SerializedName("stream_id") val streamId: Int,
    @SerializedName("stream_icon") val logo: String?,
    @SerializedName("epg_channel_id") val epgId: String?
) 

object XtreamApi {

    suspend fun loginAndLoad(baseUrl: String, username: String, password: String): List<Channel> {
        return try {
            val authUrl = "$baseUrl/player_api.php?username=$username&password=$password"
            val json = URL(authUrl).readText()
            val auth = Gson().fromJson(json, XtreamAuth::class.java)

            val liveUrl = "$baseUrl/player_api.php?username=$username&password=$password&type=live"
            val liveJson = URL(liveUrl).readText()
            // Parse live channels (simplified)
            parseLiveChannels(liveJson)
        } catch (e: Exception) {
            emptyList()
        }
    }

    private fun parseLiveChannels(json: String): List<Channel> {
        // Full parsing would use Gson properly - this is simplified
        return emptyList() // TODO: Implement full Gson parsing for channels
    }
}