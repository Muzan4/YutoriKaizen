package com.example.yutorikaizen.domain.usecase.focus

import com.example.yutorikaizen.domain.model.FocusSession
import com.example.yutorikaizen.domain.repository.FocusSessionRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/** Returns a Flow of all past focus sessions, newest first. */
class GetSessionHistoryUseCase @Inject constructor(
    private val repository: FocusSessionRepository
) {
    operator fun invoke(): Flow<List<FocusSession>> = repository.observeAllSessions()
}
