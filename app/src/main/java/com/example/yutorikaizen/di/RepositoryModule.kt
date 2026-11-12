package com.example.yutorikaizen.di

import com.example.yutorikaizen.data.repository.*
import com.example.yutorikaizen.data.repository.InterceptConfigRepositoryImpl
import com.example.yutorikaizen.domain.repository.*
import com.example.yutorikaizen.domain.repository.InterceptConfigRepository
import com.example.yutorikaizen.data.repository.HabitRepositoryImpl
import com.example.yutorikaizen.domain.repository.HabitRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds @Singleton
    abstract fun bindFocusSessionRepository(impl: FocusSessionRepositoryImpl): FocusSessionRepository

    @Binds @Singleton
    abstract fun bindTriggerLogRepository(impl: TriggerLogRepositoryImpl): TriggerLogRepository

    @Binds @Singleton
    abstract fun bindTaskRepository(impl: TaskRepositoryImpl): TaskRepository

    @Binds @Singleton
    abstract fun bindUserProfileRepository(impl: UserProfileRepositoryImpl): UserProfileRepository

    @Binds @Singleton
    abstract fun bindSurgeEventRepository(impl: SurgeEventRepositoryImpl): SurgeEventRepository

    @Binds @Singleton
    abstract fun bindDailyCheckInRepository(impl: DailyCheckInRepositoryImpl): DailyCheckInRepository

    @Binds @Singleton
    abstract fun bindAppUsageRepository(impl: AppUsageRepositoryImpl): AppUsageRepository

    @Binds @Singleton
    abstract fun bindInterceptConfigRepository(impl: InterceptConfigRepositoryImpl): InterceptConfigRepository

    @Binds @Singleton
    abstract fun bindExpenseRepository(impl: ExpenseRepositoryImpl): ExpenseRepository

    @Binds @Singleton
    abstract fun bindHabitRepository(impl: HabitRepositoryImpl): HabitRepository
}
