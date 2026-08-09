package com.example.yutorikaizen.data.local.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.yutorikaizen.data.local.dao.FocusSessionDao
import com.example.yutorikaizen.data.local.dao.TaskDao
import com.example.yutorikaizen.data.local.dao.TriggerLogDao
import com.example.yutorikaizen.data.local.entity.FocusSessionEntity
import com.example.yutorikaizen.data.local.entity.TaskEntity
import com.example.yutorikaizen.data.local.entity.TriggerLogEntity

/**
 * The single Room database for Yutori Kaizen.
 *
 * exportSchema = false — no migration JSON files generated during development.
 * Increment `version` (and add a Migration strategy) whenever you add or rename
 * columns/tables in a future update.
 */
@Database(
    entities = [
        FocusSessionEntity::class,
        TriggerLogEntity::class,
        TaskEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun focusSessionDao(): FocusSessionDao
    abstract fun triggerLogDao(): TriggerLogDao
    abstract fun taskDao(): TaskDao

    companion object {
        const val DATABASE_NAME = "yutori_kaizen.db"
    }
}
