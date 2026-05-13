package com.onlytherecipe

import android.graphics.BitmapFactory
import android.widget.ImageView
import java.net.HttpURLConnection
import java.net.URL

// Minimal async image loader for the Recipe Detail hero. No caching, no
// memory management beyond the OS — fine because the hero is loaded at most
// once per screen view. If we ever need a list of remote images, swap to
// Glide or Coil rather than extend this.
object ImageLoader {

    fun loadInto(target: ImageView, url: String, onMissing: () -> Unit = {}) {
        if (url.isBlank() || !url.startsWith("http")) {
            target.setImageDrawable(null)
            onMissing()
            return
        }
        val viewRef = target  // captured for the worker thread

        Thread {
            try {
                val conn = (URL(url).openConnection() as HttpURLConnection).apply {
                    connectTimeout = 5_000
                    readTimeout    = 10_000
                    instanceFollowRedirects = true
                    setRequestProperty("User-Agent", "OnlyTheRecipe/1.0")
                }
                if (conn.responseCode != 200) {
                    conn.disconnect()
                    viewRef.post { onMissing() }
                    return@Thread
                }
                val bitmap = BitmapFactory.decodeStream(conn.inputStream)
                conn.disconnect()
                if (bitmap == null) {
                    viewRef.post { onMissing() }
                    return@Thread
                }
                viewRef.post { viewRef.setImageBitmap(bitmap) }
            } catch (_: Exception) {
                viewRef.post { onMissing() }
            }
        }.start()
    }
}
