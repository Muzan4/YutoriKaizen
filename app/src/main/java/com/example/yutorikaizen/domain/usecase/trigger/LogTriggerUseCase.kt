package com.example.yutorikaizen.domain.usecase.trigger

import com.example.yutorikaizen.domain.repository.TriggerLogRepository
import javax.inject.Inject

/** Logs a user's urge/trigger with an emotion tag and optional note. */
class LogTriggerUseCase @Inject constructor(
    private val repository: TriggerLogRepository
) {
    suspend operator fun invoke(emotion: String, note: String? = null) {
        repository.saveLog(
            timestamp = System.currentTimeMillis(),
            emotion   = emotion,
            note      = note
        )
    }
}
