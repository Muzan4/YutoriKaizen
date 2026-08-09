package com.example.yutorikaizen.di

import com.example.yutorikaizen.data.repository.FocusSessionRepositoryImpl
import com.example.yutorikaizen.data.repository.TaskRepositoryImpl
import com.example.yutorikaizen.data.repository.TriggerLogRepositoryImpl
import com.example.yutorikaizen.domain.repository.FocusSessionRepository
import com.example.yutorikaizen.domain.repository.TaskRepository
import com.example.yutorikaizen.domain.repository.TriggerLogRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Tells Hilt how to satisfy repository interface injection requests.
 *
 * WHY @Binds instead of @Provides?
 * @Binds is used when you're just linking an interface to its implementation —
 * there's no construction logic. It's more efficient than @Provides because
 * Hilt doesn't need to generate a wrapper method body.
 */
@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindFocusSessionRepository(
        impl: FocusSessionRepositoryImpl
    ): FocusSessionRepository

    @Binds
    @Singleton
    abstract fun bindTriggerLogRepository(
        impl: TriggerLogRepositoryImpl
    ): TriggerLogRepository

    @Binds
    @Singleton
    abstract fun bindTaskRepository(
        impl: TaskRepositoryImpl
    ): TaskRepository
}
