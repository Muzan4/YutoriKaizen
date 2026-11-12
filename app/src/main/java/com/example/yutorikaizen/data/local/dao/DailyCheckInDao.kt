package com.example.yutorikaizen.data.local.dao

import androidx.room.*
import com.example.yutorikaizen.data.local.entity.DailyCheckInEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface DailyCheckInDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(entity: DailyCheckInEntity)

    @Query("SELECT * FROM daily_check_ins WHERE epochDay = :day LIMIT 1")
    suspend fun getForDay(day: Long): DailyCheckInEntity?

    @Query("SELECT * FROM daily_check_ins ORDER BY epochDay DESC LIMIT 14")
    fun observeLast14Days(): Flow<List<DailyCheckInEntity>>

    @Query("SELECT * FROM daily_check_ins ORDER BY epochDay DESC")
    suspend fun getAll(): List<DailyCheckInEntity>

    @Query("SELECT COUNT(*) FROM daily_check_ins WHERE nciScore >= 0.6")
    suspend fun countGoodDays(): Int

    @Query("DELETE FROM daily_check_ins")
    suspend fun deleteAll()
}
