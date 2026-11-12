package com.example.yutorikaizen.data.local

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import androidx.datastore.preferences.core.booleanPreferencesKey
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.runBlocking
import javax.inject.Inject
import javax.inject.Singleton

private val Context.skinDataStore: DataStore<Preferences> by preferencesDataStore(name = "skin_prefs")

/**
 * Persists the user's chosen frog skin ID using DataStore Preferences.
 *
 * WHY DataStore over SharedPreferences?
 * DataStore is coroutine-safe and doesn't block the main thread. It's already
 * a declared dependency in the project.
 */
@Singleton
class SkinPreferences @Inject constructor(
    @ApplicationContext private val context: Context
) {

    companion object {
        private val SKIN_KEY = stringPreferencesKey("selected_skin_id")
        private val RIBI_INTRO_KEY = booleanPreferencesKey("has_seen_ribi_intro")
        private val USAGE_TRACKING_KEY = booleanPreferencesKey("usage_tracking_enabled")
        private val NOTIFICATIONS_ENABLED_KEY = booleanPreferencesKey("notifications_enabled")
        private val CURRENT_JOURNEY_DAY_KEY = intPreferencesKey("current_journey_day")
        const val DEFAULT_SKIN = "green"
    }

    /** Emits the user's current journey day (default 1). */
    val currentJourneyDay: Flow<Int> = context.skinDataStore.data
        .map { prefs -> prefs[CURRENT_JOURNEY_DAY_KEY] ?: 1 }

    suspend fun setCurrentJourneyDay(day: Int) {
        context.skinDataStore.edit { prefs ->
            prefs[CURRENT_JOURNEY_DAY_KEY] = day.coerceAtLeast(1)
        }
    }

    /** Emits the currently selected skin ID (defaults to "green"). */
    val selectedSkinId: Flow<String> = context.skinDataStore.data
        .map { prefs -> prefs[SKIN_KEY] ?: DEFAULT_SKIN }

    /** Emits whether the user has seen the Ribi intro dialog. */
    val hasSeenRibiIntro: Flow<Boolean> = context.skinDataStore.data
        .map { prefs -> prefs[RIBI_INTRO_KEY] ?: false }

    /** Emits whether usage tracking is enabled. */
    val usageTrackingEnabled: Flow<Boolean> = context.skinDataStore.data
        .map { prefs -> prefs[USAGE_TRACKING_KEY] ?: false }

    /** Emits whether notifications are enabled. */
    val notificationsEnabled: Flow<Boolean> = context.skinDataStore.data
        .map { prefs -> prefs[NOTIFICATIONS_ENABLED_KEY] ?: false }

    suspend fun markRibiIntroSeen() {
        context.skinDataStore.edit { prefs ->
            prefs[RIBI_INTRO_KEY] = true
        }
    }

    suspend fun setUsageTrackingEnabled(enabled: Boolean) {
        context.skinDataStore.edit { prefs ->
            prefs[USAGE_TRACKING_KEY] = enabled
        }
    }

    suspend fun setNotificationsEnabled(enabled: Boolean) {
        context.skinDataStore.edit { prefs ->
            prefs[NOTIFICATIONS_ENABLED_KEY] = enabled
        }
    }

    suspend fun clearAll() {
        context.skinDataStore.edit { prefs ->
            prefs.clear()
            prefs[CURRENT_JOURNEY_DAY_KEY] = 1
        }
        writeSkinSignal(DEFAULT_SKIN)
    }

    /** Persists a new skin selection AND writes the signal file for Godot's live polling. */
    suspend fun setSkin(skinId: String) {
        // 1. Persist to DataStore so getCommandLine() picks it up on next cold start
        context.skinDataStore.edit { prefs ->
            prefs[SKIN_KEY] = skinId
        }
        // 2. Write the signal file that Godot polls every 1 second via _check_skin().
        //    Godot 3.4 maps user:// to getExternalFilesDir(null) on Android,
        //    so we write there first, then fall back to internal filesDir.
        writeSkinSignal(skinId)
    }

    fun writeSkinSignal(skinId: String) {
        try {
            // Primary: external files dir = Godot's user:// on Android 3.4
            val externalDir = context.getExternalFilesDir(null)
            if (externalDir != null) {
                java.io.File(externalDir, "skin_signal").writeText(skinId)
            }
        } catch (e: Exception) { /* ignore */ }
        try {
            // Fallback: internal files dir
            java.io.File(context.filesDir, "skin_signal").writeText(skinId)
        } catch (e: Exception) { /* ignore */ }
    }

    /**
     * Blocking read used exclusively in [MainActivity.getCommandLine] which executes
     * before the first Compose composition. Fires once at startup — intentionally
     * synchronous and takes <1 ms on a warm DataStore.
     */
    fun getSelectedSkinIdBlocking(): String {
        return try {
            runBlocking { selectedSkinId.first() }
        } catch (e: Exception) {
            DEFAULT_SKIN
        }
    }

    suspend fun saveDailyGoalData(epochDay: Long, key: String, value: String) {
        val prefKey = stringPreferencesKey("goal_${epochDay}_${key}")
        context.skinDataStore.edit { prefs ->
            prefs[prefKey] = value
        }
    }

    suspend fun getDailyGoalData(epochDay: Long, key: String): String? {
        val prefKey = stringPreferencesKey("goal_${epochDay}_${key}")
        return try {
            context.skinDataStore.data.map { it[prefKey] }.first()
        } catch (e: Exception) {
            null
        }
    }
}
