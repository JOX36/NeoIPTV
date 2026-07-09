package com.iptvplayer.data

import android.util.Log
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
    @SerializedName("exp_date") val expDate: String?,
    @SerializedName("message") val message: String?
)

data class ServerInfo(
    @SerializedName("url") val url: String?,
    @SerializedName("port") val port: String?,
    @SerializedName("https_port") val httpsPort: String?
)

data class XtreamChannel(
    @SerializedName("num") val num: Int = 0,
    @SerializedName("name") val name: String = "",
    @SerializedName("stream_type") val streamType: String = "",
    @SerializedName("stream_id") val streamId: Int = 0,
    @SerializedName("stream_icon") val streamIcon: String? = null,
    @SerializedName("epg_channel_id") val epgChannelId: String? = null,
    @SerializedName("category_name") val categoryName: String? = null,
    @SerializedName("category_id") val categoryId: String? = null,
    @SerializedName("direct_source") val directSource: String? = null
)

data class XtreamCategory(
    @SerializedName("category_id") val categoryId: String = "",
    @SerializedName("category_name") val categoryName: String = ""
)

object XtreamApi {

    private const val TAG = "XtreamApi"

    suspend fun login(
        baseUrl: String,
        username: String,
        password: String
    ): Result<XtreamAuth> = withContext(Dispatchers.IO) {
        try {
            val cleanUrl = baseUrl.trimEnd('/')
            val apiUrl = "$cleanUrl/player_api.php?username=$username&password=$password"
            Log.d(TAG, "Login URL: $apiUrl")

            val json = fetchUrl(URL(apiUrl))
            Log.d(TAG, "Login response: ${json.take(500)}")

            val auth = Gson().fromJson(json, XtreamAuth::class.java)

            when {
                auth.userInfo == null -> {
                    Result.failure(Exception("Respuesta inválida del servidor"))
                }
                auth.userInfo.auth == 0 -> {
                    val msg = auth.userInfo.message ?: "Credenciales inválidas"
                    Result.failure(Exception(msg))
                }
                auth.userInfo.status?.lowercase() != "active" -> {
                    Result.failure(Exception("Cuenta ${auth.userInfo.status ?: "inactiva"}"))
                }
                else -> {
                    Result.success(auth)
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Login error", e)
            Result.failure(Exception("Error de conexión: ${e.message}"))
        }
    }

    suspend fun getLiveChannels(
        baseUrl: String,
        username: String,
        password: String
    ): Result<List<Channel>> = withContext(Dispatchers.IO) {
        try {
            val cleanUrl = baseUrl.trimEnd('/')
            val apiUrl = "$cleanUrl/player_api.php?username=$username&password=$password&type=get_live_streams"
            Log.d(TAG, "Channels URL: $apiUrl")

            val json = fetchUrl(URL(apiUrl))
            Log.d(TAG, "Channels response length: ${json.length}")

            val channels = Gson().fromJson(json, Array<XtreamChannel>::class.java)
            Log.d(TAG, "Parsed ${channels?.size ?: 0} channels")

            if (channels == null || channels.isEmpty()) {
                Result.failure(Exception("No se encontraron canales en el servidor"))
            } else {
                val result = channels.map { ch ->
                    val streamUrl = if (!ch.directSource.isNullOrBlank()) {
                        ch.directSource
                    } else {
                        "$cleanUrl/live/$username/$password/${ch.streamId}.m3u8"
                    }
                    Channel(
                        id = "xtream_${ch.streamId}",
                        name = ch.name.ifBlank { "Canal ${ch.streamId}" },
                        url = streamUrl,
                        logo = ch.streamIcon,
                        category = ch.categoryName ?: "Sin categoría"
                    )
                }
                Result.success(result)
            }
        } catch (e: Exception) {
            Log.e(TAG, "getLiveChannels error", e)
            Result.failure(Exception("Error cargando canales: ${e.message}"))
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
            Log.e(TAG, "getCategories error", e)
            emptyList()
        }
    }

    private fun fetchUrl(url: URL): String {
        val conn = url.openConnection() as HttpURLConnection
        conn.connectTimeout = 20000
        conn.readTimeout = 20000
        conn.setRequestProperty("User-Agent", "NeoIPTV/1.0")
        conn.instanceFollowRedirects = true

        val responseCode = conn.responseCode
        Log.d(TAG, "HTTP $responseCode for ${url}")

        if (responseCode != 200) {
            val errorBody = conn.errorStream?.bufferedReader()?.readText() ?: "Sin detalle"
            throw Exception("HTTP $responseCode: $errorBody")
        }

        return conn.inputStream.bufferedReader().readText()
    }
}
