package com.onlytherecipe

import android.content.Context
import android.os.Handler
import android.os.Looper
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL

object UpdateChecker {

    private const val API_URL =
        "https://api.github.com/repos/karljuderojas/only-the-recipe-chrome-extension/releases/latest"

    fun check(context: Context, onUpdateAvailable: (latestTag: String, apkUrl: String) -> Unit) {
        val currentVersion = try {
            @Suppress("DEPRECATION")
            context.packageManager.getPackageInfo(context.packageName, 0).versionName
        } catch (_: Exception) { return }
        if (currentVersion == null) return

        Thread {
            try {
                val conn = URL(API_URL).openConnection() as HttpURLConnection
                conn.setRequestProperty("Accept", "application/vnd.github+json")
                conn.connectTimeout = 5_000
                conn.readTimeout    = 5_000
                if (conn.responseCode != 200) { conn.disconnect(); return@Thread }
                val json = JSONObject(conn.inputStream.bufferedReader().readText())
                conn.disconnect()

                val tag = json.optString("tag_name").ifEmpty { return@Thread }
                val assets = json.optJSONArray("assets") ?: return@Thread
                val apkUrl = (0 until assets.length())
                    .map { assets.getJSONObject(it) }
                    .firstOrNull { it.optString("name").endsWith(".apk") }
                    ?.optString("browser_download_url")
                    ?.ifEmpty { null } ?: return@Thread

                if (isNewer(tag, currentVersion)) {
                    Handler(Looper.getMainLooper()).post { onUpdateAvailable(tag, apkUrl) }
                }
            } catch (_: Exception) {
                // network unavailable — silently ignore
            }
        }.start()
    }

    private fun isNewer(tag: String, current: String): Boolean {
        fun parts(v: String) = v.trimStart('v').split('.').map { it.toIntOrNull() ?: 0 }
        val latest    = parts(tag)
        val installed = parts(current)
        val len = maxOf(latest.size, installed.size)
        for (i in 0 until len) {
            val l = latest.getOrElse(i) { 0 }
            val c = installed.getOrElse(i) { 0 }
            if (l > c) return true
            if (l < c) return false
        }
        return false
    }
}
