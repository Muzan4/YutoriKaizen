package com.example.yutorikaizen.data.local.dao

import androidx.room.*
import com.example.yutorikaizen.data.local.entity.SurgeEventEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface SurgeEventDao {
    @Insert
    suspend fun insert(event: SurgeEventEntity)

    @Query("SELECT * FROM surge_events ORDER BY timestamp DESC LIMIT 50")
    fun observeRecent(): Flow<List<SurgeEventEntity>>

    @Query("SELECT COUNT(*) FROM surge_events WHERE outcome = 'RESISTED'")
    suspend fun countResisted(): Int

    @Query("SELECT COUNT(*) FROM surge_events WHERE outcome = 'YIELDED'")
    suspend fun countYielded(): Int

    @Query("SELECT COUNT(*) FROM surge_events WHERE timestamp >= :sinceMs")
    suspend fun countSince(sinceMs: Long): Int

    @Query("SELECT * FROM surge_events WHERE timestamp >= :sinceMs ORDER BY timestamp ASC")
    suspend fun getSince(sinceMs: Long): List<SurgeEventEntity>

    @Query("DELETE FROM surge_events")
    suspend fun deleteAll()
}
