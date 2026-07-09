package com.iptvplayer.data

import com.google.gson.Gson
import com.google.gson.annotations.SerializedName
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.net.HttpURLConnection
import java.net.URL

data class XtreamAuth(
    @SerializedName("user_info") val userInfo: UserInfo?,
    @SerializedName("server_info") val serverInfo: ServerInfo?
)

data class UserInfo(
    @SerializedName("username") val username: String?,
    @SerializedName("password") val password: String?,
    @SerializedName("auth") val auth: Int?,
    @SerializedName("status") val status: String?,
    @SerializedName("exp_date") val expDate: String?
)

data class ServerInfo(
    @SerializedName("url") val url: String?,
    @SerializedName("port") val port: String?
)

data class XtreamChannel(
    @SerializedName("num") val num: Int = 0,
    @SerializedName("name") val name: String = "",
    @SerializedName("stream_type") val streamType: String = "",
    @SerializedName("stream_id") val streamId: Int = 0,
    @SerializedName("stream_icon") val streamIcon: String? = null,
    @SerializedName("epg_channel_id") val epgChannelId: String? = null,
    @SerializedName("category_name") val categoryName: String? = null,
    @SerializedName("category_id") val categoryId: String? = null
)

data class XtreamCategory(
    @SerializedName("category_id") val categoryId: String = "",
    @SerializedName("category_name") val categoryName: String = ""
)

object XtreamApi {

    suspend fun login(
        baseUrl: String,
        username: String,
        password: String
): Result<XtreamAuth> = withContext(Dispatchers.IO) {
        try {
            val cleanUrl = baseUrl.trimEnd('/')
            val url = URL("$cleanUrl/player_api.php?username=$username&password=$password")
            val json = fetchUrl(url)
            val auth = Gson().fromJson(json, XtreamAuth::class.java)
            if (auth.userInfo != null) {
                Result.success(auth)
            } else {
                Result.failure(Exception("Credenciales inválidas"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getLiveChannels(
        baseUrl: String,
        username: String,
        password: String
    ): List<Channel> = withContext(Dispatchers.IO) {
        try {
            val cleanUrl = baseUrl.trimEnd('/')
            val url = URL("$cleanUrl/player_api.php?username=$username&password=$password&type=get_live_streams")
            val json = fetchUrl(url)
            val channels = Gson().fromJson(json, Array<XtreamChannel>::class.java) ?: emptyArray()

            channels.map { ch ->
                Channel(
                    id = "xtream_${ch.streamId}",
                    name = ch.name,
                    url = "$cleanUrl/live/$username/$password/${ch.streamId}.m3u8",
                    logo = ch.streamIcon,
                    category = ch.categoryName ?: "Sin categoría"
                )
            }
        } catch (e: Exception) {
            emptyList()
        }
    }

    suspend fun getCategories(
        baseUrl: String,
        username: String,
        password: String
    ): List<XtreamCategory> = withContext(Dispatchers.IO) {
        try {
            val cleanUrl = baseUrl.trimEnd('/')
            val url = URL("$cleanUrl/player_api.php?username=$username&password=$password&type=get_live_categories")
            val json = fetchUrl(url)
            Gson().fromJson(json, Array<XtreamCategory>::class.java)?.toList() ?: emptyList()
        } catch (e: Exception) {
            emptyList()
        }
    }

    private fun fetchUrl(url: URL): String {
        val conn = url.openConnection() as HttpURLConnection
        conn.connectTimeout = 15000
        conn.readTimeout = 15000
        conn.setRequestProperty("User-Agent", "NeoIPTV/1.0")
        return conn.inputStream.bufferedReader().readText()
    }
}
