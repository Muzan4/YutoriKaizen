package com.example.yutorikaizen.data.local.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.yutorikaizen.data.local.dao.*
import com.example.yutorikaizen.data.local.entity.*
import com.example.yutorikaizen.data.local.entity.HabitEntity
import com.example.yutorikaizen.data.local.entity.HabitLogEntity

/**
 * The single Room database for Yutori Kaizen.
 * Version 2 adds: user_profile, surge_events, daily_check_ins tables,
 * and the actionTaken column on trigger_logs.
 */
@Database(
    entities = [
        FocusSessionEntity::class,
        TriggerLogEntity::class,
        TaskEntity::class,
        UserProfileEntity::class,
        SurgeEventEntity::class,
        DailyCheckInEntity::class,
        AppUsageEntity::class,
        InterceptConfigEntity::class,
        ExpenseEntity::class,
        HabitEntity::class,
        HabitLogEntity::class
    ],
    version = 6,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun focusSessionDao(): FocusSessionDao
    abstract fun triggerLogDao(): TriggerLogDao
    abstract fun taskDao(): TaskDao
    abstract fun userProfileDao(): UserProfileDao
    abstract fun surgeEventDao(): SurgeEventDao
    abstract fun dailyCheckInDao(): DailyCheckInDao
    abstract fun appUsageDao(): AppUsageDao
    abstract fun interceptConfigDao(): InterceptConfigDao
    abstract fun expenseDao(): ExpenseDao
    abstract fun habitDao(): HabitDao

    companion object {
        const val DATABASE_NAME = "yutori_kaizen.db"

        /** Migration v1 → v2: add new tables + actionTaken column. */
        val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(db: SupportSQLiteDatabase) {
                // Add actionTaken to existing trigger_logs table
                db.execSQL(
                    "ALTER TABLE trigger_logs ADD COLUMN actionTaken TEXT NOT NULL DEFAULT 'YIELDED'"
                )
                // New: user_profile table
                db.execSQL(
                    """CREATE TABLE IF NOT EXISTS user_profile (
                        id INTEGER NOT NULL PRIMARY KEY,
                        startDate INTEGER NOT NULL,
                        difficultyLevel TEXT NOT NULL,
                        selectedTriggers TEXT NOT NULL,
                        selectedProgramId TEXT NOT NULL,
                        onboardingComplete INTEGER NOT NULL DEFAULT 0
                    )"""
                )
                // New: surge_events table
                db.execSQL(
                    """CREATE TABLE IF NOT EXISTS surge_events (
                        id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        timestamp INTEGER NOT NULL,
                        durationSeconds INTEGER NOT NULL,
                        outcome TEXT NOT NULL,
                        methodUsed TEXT NOT NULL,
                        emotionLogged TEXT
                    )"""
                )
                // New: daily_check_ins table
                db.execSQL(
                    """CREATE TABLE IF NOT EXISTS daily_check_ins (
                        id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        epochDay INTEGER NOT NULL,
                        phaseNumber INTEGER NOT NULL,
                        completedHabitIds TEXT NOT NULL,
                        focusMinutes INTEGER NOT NULL,
                        nciScore REAL NOT NULL
                    )"""
                )
            }
        }

        /** Migration v2 → v3: add app_usage_events table. */
        val MIGRATION_2_3 = object : Migration(2, 3) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL(
                    """CREATE TABLE IF NOT EXISTS app_usage_events (
                        id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        packageName TEXT NOT NULL,
                        appLabel TEXT NOT NULL,
                        epochDay INTEGER NOT NULL,
                        totalForegroundMs INTEGER NOT NULL,
                        launchCount INTEGER NOT NULL
                    )"""
                )
                db.execSQL(
                    "CREATE UNIQUE INDEX IF NOT EXISTS `index_app_usage_events_packageName_epochDay` ON `app_usage_events` (`packageName`, `epochDay`)"
                )
            }
        }

        /** Migration v3 → v4: add intercept_configs table. */
        val MIGRATION_3_4 = object : Migration(3, 4) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL(
                    """CREATE TABLE IF NOT EXISTS intercept_configs (
                        packageName TEXT NOT NULL PRIMARY KEY,
                        appLabel TEXT NOT NULL,
                        prompt TEXT NOT NULL,
                        enabled INTEGER NOT NULL DEFAULT 1,
                        delaySeconds INTEGER NOT NULL DEFAULT 5
                    )"""
                )
            }
        }

        /** Migration v4 → v5: add expenses table. */
        val MIGRATION_4_5 = object : Migration(4, 5) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL(
                    """CREATE TABLE IF NOT EXISTS expenses (
                        id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        amount REAL NOT NULL,
                        category TEXT NOT NULL,
                        note TEXT,
                        timestamp INTEGER NOT NULL,
                        epochMonth INTEGER NOT NULL
                    )"""
                )
            }
        }

        /** Migration v5 → v6: add habits and habit_logs tables. */
        val MIGRATION_5_6 = object : Migration(5, 6) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("""CREATE TABLE IF NOT EXISTS habits (
                    id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                    name TEXT NOT NULL,
                    description TEXT NOT NULL,
                    emoji TEXT NOT NULL,
                    colorHex TEXT NOT NULL DEFAULT '#7CB47A',
                    isGoodHabit INTEGER NOT NULL DEFAULT 1,
                    frequency TEXT NOT NULL DEFAULT 'DAILY',
                    targetCount INTEGER NOT NULL DEFAULT 1,
                    unit TEXT NOT NULL DEFAULT '',
                    createdAt INTEGER NOT NULL,
                    isActive INTEGER NOT NULL DEFAULT 1,
                    sortOrder INTEGER NOT NULL DEFAULT 0
                )""")
                db.execSQL("""CREATE TABLE IF NOT EXISTS habit_logs (
                    id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                    habitId INTEGER NOT NULL,
                    epochDay INTEGER NOT NULL,
                    completedCount INTEGER NOT NULL DEFAULT 0,
                    note TEXT NOT NULL DEFAULT '',
                    loggedAt INTEGER NOT NULL
                )""")
                db.execSQL("CREATE UNIQUE INDEX IF NOT EXISTS `index_habit_logs_habitId_epochDay` ON `habit_logs` (`habitId`, `epochDay`)")
            }
        }
    }
}
