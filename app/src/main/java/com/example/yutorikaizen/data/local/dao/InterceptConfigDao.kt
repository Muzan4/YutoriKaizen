package com.example.yutorikaizen.data.local.dao

import androidx.room.*
import com.example.yutorikaizen.data.local.entity.InterceptConfigEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface InterceptConfigDao {

    /** Observe all configs — UI list reacts automatically. */
    @Query("SELECT * FROM intercept_configs ORDER BY appLabel ASC")
    fun observeAll(): Flow<List<InterceptConfigEntity>>

    /** One-shot read for the AccessibilityService hot path (non-suspending). */
    @Query("SELECT * FROM intercept_configs WHERE packageName = :pkg AND enabled = 1 LIMIT 1")
    fun getEnabledSync(pkg: String): InterceptConfigEntity?

    /** Upsert — insert or replace if package key already exists. */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(config: InterceptConfigEntity)

    @Delete
    suspend fun delete(config: InterceptConfigEntity)

    @Query("UPDATE intercept_configs SET enabled = :enabled WHERE packageName = :pkg")
    suspend fun setEnabled(pkg: String, enabled: Boolean)
}
