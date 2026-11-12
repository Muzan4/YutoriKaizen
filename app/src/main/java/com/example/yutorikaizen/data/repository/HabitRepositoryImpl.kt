package com.example.yutorikaizen.data.repository

import com.example.yutorikaizen.data.local.dao.HabitDao
import com.example.yutorikaizen.data.local.entity.HabitEntity
import com.example.yutorikaizen.data.local.entity.HabitLogEntity
import com.example.yutorikaizen.domain.model.Habit
import com.example.yutorikaizen.domain.model.HabitFrequency
import com.example.yutorikaizen.domain.model.HabitLog
import com.example.yutorikaizen.domain.repository.HabitRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class HabitRepositoryImpl @Inject constructor(
    private val dao: HabitDao
) : HabitRepository {

    override fun observeActiveHabits(): Flow<List<Habit>> =
        dao.observeActiveHabits().map { it.map(HabitEntity::toDomain) }

    override fun observeAllHabits(): Flow<List<Habit>> =
        dao.observeAllHabits().map { it.map(HabitEntity::toDomain) }

    override suspend fun getById(id: Long): Habit? =
        dao.getById(id)?.toDomain()

    override suspend fun insert(habit: Habit): Long =
        dao.insert(habit.toEntity())

    override suspend fun update(habit: Habit) = dao.update(habit.toEntity())

    override suspend fun delete(habit: Habit) = dao.delete(habit.toEntity())

    override suspend fun deleteById(id: Long) = dao.deleteById(id)

    override fun observeLogsForDay(epochDay: Long): Flow<List<HabitLog>> =
        dao.observeLogsForDay(epochDay).map { it.map(HabitLogEntity::toDomain) }

    override suspend fun getLogForHabitOnDay(habitId: Long, epochDay: Long): HabitLog? =
        dao.getLogForHabitOnDay(habitId, epochDay)?.toDomain()

    override suspend fun getRecentLogs(habitId: Long, limit: Int): List<HabitLog> =
        dao.getRecentLogs(habitId, limit).map(HabitLogEntity::toDomain)

    override suspend fun getLogsForRange(startDay: Long, endDay: Long): List<HabitLog> =
        dao.getLogsForRange(startDay, endDay).map(HabitLogEntity::toDomain)

    override suspend fun upsertLog(log: HabitLog) {
        val existing = dao.getLogForHabitOnDay(log.habitId, log.epochDay)
        if (existing != null) {
            dao.updateLog(log.toEntity().copy(id = existing.id))
        } else {
            dao.insertLog(log.toEntity())
        }
    }

    override suspend fun getTotalCompletedDays(habitId: Long): Int =
        dao.getTotalCompletedDays(habitId)
}

private fun HabitEntity.toDomain() = Habit(
    id = id, name = name, description = description, emoji = emoji,
    colorHex = colorHex, isGoodHabit = isGoodHabit,
    frequency = try { HabitFrequency.valueOf(frequency) } catch (e: Exception) { HabitFrequency.DAILY },
    targetCount = targetCount, unit = unit,
    createdAt = createdAt, isActive = isActive, sortOrder = sortOrder
)

private fun Habit.toEntity() = HabitEntity(
    id = id, name = name, description = description, emoji = emoji,
    colorHex = colorHex, isGoodHabit = isGoodHabit,
    frequency = frequency.name, targetCount = targetCount, unit = unit,
    createdAt = createdAt, isActive = isActive, sortOrder = sortOrder
)

private fun HabitLogEntity.toDomain() = HabitLog(
    id = id, habitId = habitId, epochDay = epochDay,
    completedCount = completedCount, note = note, loggedAt = loggedAt
)

private fun HabitLog.toEntity() = HabitLogEntity(
    id = id, habitId = habitId, epochDay = epochDay,
    completedCount = completedCount, note = note, loggedAt = loggedAt
)
