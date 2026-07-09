package com.iptvplayer.data

import java.net.URL

object M3UParser {

    suspend fun parseM3U(url: String): List<Channel> {
        return try {
            val content = URL(url).readText()
            parseContent(content)
        } catch (e: Exception) {
            emptyList()
        }
    }

    private fun parseContent(content: String): List<Channel> {
        val channels = mutableListOf<Channel>()
        val lines = content.lines()
        var currentName = ""
        var currentUrl = ""
        var currentLogo = ""

        lines.forEach { line ->
            when {
                line.startsWith("#EXTINF") -> {
                    currentName = extractName(line)
                    currentLogo = extractLogo(line)
                }
                line.isNotBlank() && !line.startsWith("#") -> {
                    currentUrl = line.trim()
                    if (currentName.isNotEmpty() && currentUrl.isNotEmpty()) {
                        channels.add(
                            Channel(
                                id = currentUrl.hashCode().toString(),
                                name = currentName,
                                url = currentUrl,
                                logo = currentLogo
                            )
                        )
                    }
                }
            }
        }
        return channels
    }

    private fun extractName(line: String): String {
        return line.substringAfter(",").trim()
    }

    private fun extractLogo(line: String): String {
        return if (line.contains("tvg-logo=")) {
            line.substringAfter("tvg-logo=\"").substringBefore("\"")
        } else ""
    }
}