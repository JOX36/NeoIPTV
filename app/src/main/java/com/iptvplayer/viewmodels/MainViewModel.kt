package com.iptvplayer.viewmodels

import android.app.Application
import android.content.Context
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.iptvplayer.data.Channel
import com.iptvplayer.data.M3UParser
import com.iptvplayer.data.XtreamApi
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    application: Application
) : AndroidViewModel(application) {

    private val prefs = application.getSharedPreferences("neoiptv", Context.MODE_PRIVATE)

    private val _channels = MutableStateFlow<List<Channel>>(emptyList())
    val channels = _channels.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    private val _isLoggedIn = MutableStateFlow(false)
    val isLoggedIn = _isLoggedIn.asStateFlow()

    private val _serverName = MutableStateFlow("")
    val serverName = _serverName.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error = _error.asStateFlow()

    init {
        val savedUrl = prefs.getString("xtream_url", null)
        val savedUser = prefs.getString("xtream_user", null)
        val savedPass = prefs.getString("xtream_pass", null)

        if (savedUrl != null && savedUser != null && savedPass != null) {
            Log.d("MainVM", "Auto-login with saved credentials")
            loginXtream(savedUrl, savedUser, savedPass)
        }
    }

    fun loginXtream(baseUrl: String, username: String, password: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null

            Log.d("MainVM", "Logging in to $baseUrl")

            val loginResult = XtreamApi.login(baseUrl, username, password)
            loginResult.fold(
                onSuccess = { auth ->
                    Log.d("MainVM", "Login success, loading channels...")
                    prefs.edit()
                        .putString("xtream_url", baseUrl)
                        .putString("xtream_user", username)
                        .putString("xtream_pass", password)
                        .apply()

                    _isLoggedIn.value = true
                    _serverName.value = auth.serverInfo?.url ?: baseUrl

                    // Load channels
                    val channelsResult = XtreamApi.getLiveChannels(baseUrl, username, password)
                    channelsResult.fold(
                        onSuccess = { channels ->
                            Log.d("MainVM", "Loaded ${channels.size} channels")
                            _channels.value = channels
                            _isLoading.value = false
                        },
                        onFailure = { e ->
                            Log.e("MainVM", "Error loading channels", e)
                            _error.value = e.message
                            _isLoading.value = false
                        }
                    )
                },
                onFailure = { e ->
                    Log.e("MainVM", "Login failed", e)
                    _error.value = e.message
                    _isLoading.value = false
                }
            )
        }
    }

    fun loadM3U(url: String) {
        viewModelScope.launch {
            _isLoading.value = true
            val loaded = M3UParser.parseM3U(url)
            _channels.value = loaded
            _isLoading.value = false
        }
    }

    fun logout() {
        prefs.edit().clear().apply()
        _isLoggedIn.value = false
        _channels.value = emptyList()
        _serverName.value = ""
        _error.value = null
    }

    fun clearError() {
        _error.value = null
    }
}
