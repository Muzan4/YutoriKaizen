package com.example.yutorikaizen.service

import android.content.Context
import android.content.SharedPreferences

/**
 * Persistent settings for the floating cat overlay.
 * Stores: auto-hide enabled, last Y position, corner (left/right).
 */
object CatOverlayPreferences {
    private const val PREFS_NAME = "cat_overlay_prefs"
    private const val KEY_AUTO_HIDE = "cat_auto_hide"
    private const val KEY_LAST_Y = "cat_last_y"
    private const val KEY_LAST_X_SIDE = "cat_last_x_side" // "left" or "right"

    private fun prefs(context: Context): SharedPreferences =
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    fun isAutoHideEnabled(context: Context): Boolean =
        prefs(context).getBoolean(KEY_AUTO_HIDE, true)

    fun setAutoHideEnabled(context: Context, enabled: Boolean) {
        prefs(context).edit().putBoolean(KEY_AUTO_HIDE, enabled).apply()
    }

    fun getLastY(context: Context): Int =
        prefs(context).getInt(KEY_LAST_Y, 400)

    fun setLastY(context: Context, y: Int) {
        prefs(context).edit().putInt(KEY_LAST_Y, y).apply()
    }

    fun getLastXSide(context: Context): String =
        prefs(context).getString(KEY_LAST_X_SIDE, "right") ?: "right"

    fun setLastXSide(context: Context, side: String) {
        prefs(context).edit().putString(KEY_LAST_X_SIDE, side).apply()
    }

    fun registerListener(
        context: Context,
        listener: SharedPreferences.OnSharedPreferenceChangeListener
    ) {
        prefs(context).registerOnSharedPreferenceChangeListener(listener)
    }

    fun unregisterListener(
        context: Context,
        listener: SharedPreferences.OnSharedPreferenceChangeListener
    ) {
        prefs(context).unregisterOnSharedPreferenceChangeListener(listener)
    }
}
