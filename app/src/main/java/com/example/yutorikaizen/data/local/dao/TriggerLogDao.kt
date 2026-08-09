package com.example.yutorikaizen.data.local.dao

import androidx.room.*
import com.example.yutorikaizen.data.local.entity.TriggerLogEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface TriggerLogDao {

    @Insert
    suspend fun insert(log: TriggerLogEntity): Long

    @Query("SELECT * FROM trigger_logs ORDER BY timestamp DESC")
    fun observeAll(): Flow<List<TriggerLogEntity>>

    @Query("SELECT * FROM trigger_logs WHERE timestamp >= :fromEpoch ORDER BY timestamp DESC")
    fun observeSince(fromEpoch: Long): Flow<List<TriggerLogEntity>>
}
