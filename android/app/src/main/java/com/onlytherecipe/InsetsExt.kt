package com.onlytherecipe

import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

// On Android 15+ (targetSdk 35) edge-to-edge is enforced — the activity draws
// behind the status and nav bars. setDecorFitsSystemWindows(true) is silently
// ignored. Instead we listen for window insets and apply them as padding to
// android.R.id.content (the FrameLayout that wraps the activity's layout).
fun AppCompatActivity.applySystemBarInsets() {
    val rootView = findViewById<View>(android.R.id.content)
    ViewCompat.setOnApplyWindowInsetsListener(rootView) { v, insets ->
        val bars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
        v.setPadding(bars.left, bars.top, bars.right, bars.bottom)
        WindowInsetsCompat.CONSUMED
    }
}
