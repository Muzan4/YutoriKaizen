package com.example.yutorikaizen.data.repository

import com.example.yutorikaizen.data.local.dao.UserProfileDao
import com.example.yutorikaizen.data.local.entity.UserProfileEntity
import com.example.yutorikaizen.domain.model.DifficultyLevel
import com.example.yutorikaizen.domain.model.UserProfile
import com.example.yutorikaizen.domain.repository.UserProfileRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.json.JSONArray
import javax.inject.Inject

class UserProfileRepositoryImpl @Inject constructor(
    private val dao: UserProfileDao
) : UserProfileRepository {

    override fun observeProfile(): Flow<UserProfile?> =
        dao.observeProfile().map { it?.toDomain() }

    override suspend fun getProfile(): UserProfile? =
        dao.getProfile()?.toDomain()

    override suspend fun saveProfile(profile: UserProfile) {
        dao.upsert(profile.toEntity())
    }

    override suspend fun resetJourney(startDateMs: Long) {
        val current = dao.getProfile()
        if (current != null) {
            dao.upsert(current.copy(startDate = startDateMs))
        }
    }

    private fun UserProfileEntity.toDomain() = UserProfile(
        startDate         = startDate,
        difficultyLevel   = DifficultyLevel.valueOf(difficultyLevel),
        selectedTriggers  = parseJsonArray(selectedTriggers),
        selectedProgramId = selectedProgramId,
        onboardingComplete = onboardingComplete
    )

    private fun UserProfile.toEntity() = UserProfileEntity(
        id                = 0,
        startDate         = startDate,
        difficultyLevel   = difficultyLevel.name,
        selectedTriggers  = buildJsonArray(selectedTriggers),
        selectedProgramId = selectedProgramId,
        onboardingComplete = onboardingComplete
    )

    private fun parseJsonArray(json: String): List<String> = try {
        val arr = JSONArray(json)
        (0 until arr.length()).map { arr.getString(it) }
    } catch (_: Exception) { emptyList() }

    private fun buildJsonArray(list: List<String>): String =
        JSONArray(list).toString()
}
