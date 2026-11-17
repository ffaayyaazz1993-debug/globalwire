package com.globalwire.news.ui

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import okhttp3.OkHttpClient
import okhttp3.Request
import java.util.concurrent.TimeUnit

object Remote {
    private val client = OkHttpClient.Builder()
        .connectTimeout(20, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .build()

    private val json = Json { ignoreUnknownKeys = true }

    private fun get(path: String): String =
        client.newCall(Request.Builder()
            .url("https://y-squre.com$path")
            .header("User-Agent", "YSQ-News-Android")
            .build()).execute().use { r ->
            if (!r.isSuccessful) error("HTTP ${r.code} for $path")
            r.body?.string() ?: error("empty body for $path")
        }

    suspend fun feed(): Feed = withContext(Dispatchers.IO) {
        json.decodeFromString(get("/assets/data/feed/global.json"))
    }

    suspend fun taxonomy(): Taxonomy = withContext(Dispatchers.IO) {
        json.decodeFromString(get("/assets/data/taxonomy.json"))
    }
}
