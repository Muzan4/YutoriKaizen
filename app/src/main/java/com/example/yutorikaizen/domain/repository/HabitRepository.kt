package com.example.yutorikaizen.domain.repository

import com.example.yutorikaizen.domain.model.Habit
import com.example.yutorikaizen.domain.model.HabitLog
import kotlinx.coroutines.flow.Flow

interface HabitRepository {
    fun observeActiveHabits(): Flow<List<Habit>>
    fun observeAllHabits(): Flow<List<Habit>>
    suspend fun getById(id: Long): Habit?
    suspend fun insert(habit: Habit): Long
    suspend fun update(habit: Habit)
    suspend fun delete(habit: Habit)
    suspend fun deleteById(id: Long)
    fun observeLogsForDay(epochDay: Long): Flow<List<HabitLog>>
    suspend fun getLogForHabitOnDay(habitId: Long, epochDay: Long): HabitLog?
    suspend fun getRecentLogs(habitId: Long, limit: Int = 30): List<HabitLog>
    suspend fun getLogsForRange(startDay: Long, endDay: Long): List<HabitLog>
    suspend fun upsertLog(log: HabitLog)
    suspend fun getTotalCompletedDays(habitId: Long): Int
}
