package com.example.yutorikaizen.data.repository

import com.example.yutorikaizen.data.local.dao.InterceptConfigDao
import com.example.yutorikaizen.data.local.entity.InterceptConfigEntity
import com.example.yutorikaizen.domain.repository.InterceptConfigRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class InterceptConfigRepositoryImpl @Inject constructor(
    private val dao: InterceptConfigDao
) : InterceptConfigRepository {

    override fun observeAll(): Flow<List<InterceptConfigEntity>> = dao.observeAll()

    override suspend fun upsert(config: InterceptConfigEntity) = dao.upsert(config)

    override suspend fun delete(config: InterceptConfigEntity) = dao.delete(config)

    override suspend fun setEnabled(packageName: String, enabled: Boolean) =
        dao.setEnabled(packageName, enabled)
}
