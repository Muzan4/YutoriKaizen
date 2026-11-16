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
    private const val KEY_EXCLUDED_PACKAGES = "cat_excluded_packages"

    /**
     * Default list of popular UPI / payment apps where overlay should be hidden by default
     * to prevent touch obstruction and overlay security blocks (such as Google Pay).
     */
    val DEFAULT_EXCLUDED_PACKAGES: Set<String> = setOf(
        "com.google.android.apps.nbu.paisa.user", // Google Pay (India/Tez)
        "com.google.android.apps.walletnfcrel",  // Google Wallet
        "com.phonepe.app",                       // PhonePe
        "net.one97.paytm",                       // Paytm
        "in.org.npci.upiapp"                     // BHIM UPI
    )

    private fun prefs(context: Context): SharedPreferences =
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    fun getExcludedPackages(context: Context): Set<String> =
        prefs(context).getStringSet(KEY_EXCLUDED_PACKAGES, DEFAULT_EXCLUDED_PACKAGES) ?: DEFAULT_EXCLUDED_PACKAGES

    fun setExcludedPackages(context: Context, packages: Set<String>) {
        prefs(context).edit().putStringSet(KEY_EXCLUDED_PACKAGES, packages).apply()
    }

    fun isPackageExcluded(context: Context, packageName: String): Boolean =
        getExcludedPackages(context).contains(packageName)

    fun addExcludedPackage(context: Context, packageName: String) {
        val current = getExcludedPackages(context).toMutableSet()
        if (current.add(packageName)) {
            setExcludedPackages(context, current)
        }
    }

    fun removeExcludedPackage(context: Context, packageName: String) {
        val current = getExcludedPackages(context).toMutableSet()
        if (current.remove(packageName)) {
            setExcludedPackages(context, current)
        }
    }

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
