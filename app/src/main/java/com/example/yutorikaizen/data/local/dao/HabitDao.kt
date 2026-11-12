package com.example.yutorikaizen.data.local.dao

import androidx.room.*
import com.example.yutorikaizen.data.local.entity.HabitEntity
import com.example.yutorikaizen.data.local.entity.HabitLogEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface HabitDao {
    @Query("SELECT * FROM habits WHERE isActive = 1 ORDER BY sortOrder ASC, createdAt ASC")
    fun observeActiveHabits(): Flow<List<HabitEntity>>

    @Query("SELECT * FROM habits ORDER BY sortOrder ASC, createdAt ASC")
    fun observeAllHabits(): Flow<List<HabitEntity>>

    @Query("SELECT * FROM habits WHERE id = :id")
    suspend fun getById(id: Long): HabitEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(habit: HabitEntity): Long

    @Update
    suspend fun update(habit: HabitEntity)

    @Delete
    suspend fun delete(habit: HabitEntity)

    @Query("DELETE FROM habits WHERE id = :id")
    suspend fun deleteById(id: Long)

    // Logs
    @Query("SELECT * FROM habit_logs WHERE epochDay = :epochDay")
    fun observeLogsForDay(epochDay: Long): Flow<List<HabitLogEntity>>

    @Query("SELECT * FROM habit_logs WHERE habitId = :habitId AND epochDay = :epochDay LIMIT 1")
    suspend fun getLogForHabitOnDay(habitId: Long, epochDay: Long): HabitLogEntity?

    @Query("SELECT * FROM habit_logs WHERE habitId = :habitId ORDER BY epochDay DESC LIMIT :limit")
    suspend fun getRecentLogs(habitId: Long, limit: Int = 30): List<HabitLogEntity>

    @Query("SELECT * FROM habit_logs WHERE epochDay BETWEEN :startDay AND :endDay")
    suspend fun getLogsForRange(startDay: Long, endDay: Long): List<HabitLogEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLog(log: HabitLogEntity): Long

    @Update
    suspend fun updateLog(log: HabitLogEntity)

    @Query("DELETE FROM habit_logs WHERE id = :id")
    suspend fun deleteLog(id: Long)

    @Query("SELECT COUNT(DISTINCT epochDay) FROM habit_logs WHERE habitId = :habitId AND completedCount >= 1")
    suspend fun getTotalCompletedDays(habitId: Long): Int
}
