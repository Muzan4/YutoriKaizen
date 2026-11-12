package com.example.yutorikaizen.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Stores the user-configured "one sec" intercept rule for a specific app.
 *
 * Each row = one app the user wants to intercept.
 * [prompt] is the personal message the user wrote to themselves —
 * displayed full-screen before the app opens.
 * [enabled] lets the user temporarily pause a rule without deleting it.
 * [delaySeconds] is how long they must sit with the prompt before they can
 * proceed. Defaults to 5 seconds — short enough to feel fair, long enough
 * to break automaticity.
 */
@Entity(tableName = "intercept_configs")
data class InterceptConfigEntity(
    @PrimaryKey
    val packageName: String,        // e.g. "com.instagram.android"
    val appLabel: String,           // e.g. "Instagram"
    val prompt: String,             // e.g. "Do I actually need this right now?"
    val enabled: Boolean = true,
    val delaySeconds: Int = 5       // countdown before "Open anyway" unlocks
)
