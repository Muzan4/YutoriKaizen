package com.example.yutorikaizen.data.local.dao

import androidx.room.*
import com.example.yutorikaizen.data.local.entity.FocusSessionEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface FocusSessionDao {

    @Insert
    suspend fun insert(session: FocusSessionEntity): Long

    /** Live list of all sessions, newest first. Compose recomposes automatically when data changes. */
    @Query("SELECT * FROM focus_sessions ORDER BY startTime DESC")
    fun observeAll(): Flow<List<FocusSessionEntity>>

    /**
     * Count completed sessions for a given ISO date string ("YYYY-MM-DD").
     * SQLite's date() converts epoch millis ÷ 1000 (seconds) to a local date string.
     */
    @Query(
        "SELECT COUNT(*) FROM focus_sessions " +
        "WHERE completed = 1 " +
        "AND date(startTime/1000,'unixepoch','localtime') = :date"
    )
    suspend fun countCompletedForDate(date: String): Int
}
