package com.onlytherecipe

import android.content.Context

object AppPrefs {
    private const val PREFS_NAME = "app_prefs"
    private const val KEY_USE_METRIC        = "use_metric"
    private const val KEY_DEFAULT_SERVINGS  = "default_servings"
    private const val KEY_SHOW_AUTHOR_NOTES = "show_author_notes"

    // 0 = render quantities as written, no scaling.
    const val SERVINGS_AS_WRITTEN = 0

    private fun prefs(context: Context) =
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    fun useMetric(context: Context): Boolean =
        prefs(context).getBoolean(KEY_USE_METRIC, false)

    fun setUseMetric(context: Context, value: Boolean) {
        prefs(context).edit().putBoolean(KEY_USE_METRIC, value).apply()
    }

    fun defaultServings(context: Context): Int =
        prefs(context).getInt(KEY_DEFAULT_SERVINGS, SERVINGS_AS_WRITTEN)

    fun setDefaultServings(context: Context, value: Int) {
        prefs(context).edit().putInt(KEY_DEFAULT_SERVINGS, value).apply()
    }

    fun showAuthorNotes(context: Context): Boolean =
        prefs(context).getBoolean(KEY_SHOW_AUTHOR_NOTES, true)

    fun setShowAuthorNotes(context: Context, value: Boolean) {
        prefs(context).edit().putBoolean(KEY_SHOW_AUTHOR_NOTES, value).apply()
    }
}
