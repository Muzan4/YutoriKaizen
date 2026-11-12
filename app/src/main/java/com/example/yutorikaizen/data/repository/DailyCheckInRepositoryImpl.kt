package com.example.yutorikaizen.data.repository

import com.example.yutorikaizen.data.local.dao.DailyCheckInDao
import com.example.yutorikaizen.data.local.entity.DailyCheckInEntity
import com.example.yutorikaizen.domain.model.DailyCheckIn
import com.example.yutorikaizen.domain.repository.DailyCheckInRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.json.JSONArray
import java.time.LocalDate
import javax.inject.Inject

class DailyCheckInRepositoryImpl @Inject constructor(
    private val dao: DailyCheckInDao
) : DailyCheckInRepository {

    override suspend fun upsert(checkIn: DailyCheckIn) {
        dao.upsert(checkIn.toEntity())
    }

    override suspend fun getForToday(): DailyCheckIn? =
        dao.getForDay(LocalDate.now().toEpochDay())?.toDomain()

    override fun observeLast14Days(): Flow<List<DailyCheckIn>> =
        dao.observeLast14Days().map { list -> list.map { it.toDomain() } }

    override suspend fun getAll(): List<DailyCheckIn> =
        dao.getAll().map { it.toDomain() }

    override suspend fun deleteAll() {
        dao.deleteAll()
    }

    private fun DailyCheckInEntity.toDomain() = DailyCheckIn(
        id               = id,
        epochDay         = epochDay,
        phaseNumber      = phaseNumber,
        completedHabitIds = parseJsonArray(completedHabitIds),
        focusMinutes     = focusMinutes,
        nciScore         = nciScore
    )

    private fun DailyCheckIn.toEntity() = DailyCheckInEntity(
        id               = id,
        epochDay         = epochDay,
        phaseNumber      = phaseNumber,
        completedHabitIds = buildJsonArray(completedHabitIds),
        focusMinutes     = focusMinutes,
        nciScore         = nciScore
    )

    private fun parseJsonArray(json: String): List<String> = try {
        val arr = JSONArray(json)
        (0 until arr.length()).map { arr.getString(it) }
    } catch (_: Exception) { emptyList() }

    private fun buildJsonArray(list: List<String>): String =
        JSONArray(list).toString()
}
