package com.example.yutorikaizen.di

import android.content.Context
import androidx.room.Room
import com.example.yutorikaizen.data.local.dao.*
import com.example.yutorikaizen.data.local.db.AppDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): AppDatabase =
        Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            AppDatabase.DATABASE_NAME
        )
            .addMigrations(
                AppDatabase.MIGRATION_1_2,
                AppDatabase.MIGRATION_2_3,
                AppDatabase.MIGRATION_3_4,
                AppDatabase.MIGRATION_4_5,
                AppDatabase.MIGRATION_5_6
            )
            .fallbackToDestructiveMigrationOnDowngrade()
            .build()

    @Provides fun provideFocusSessionDao(db: AppDatabase): FocusSessionDao = db.focusSessionDao()
    @Provides fun provideTriggerLogDao(db: AppDatabase): TriggerLogDao     = db.triggerLogDao()
    @Provides fun provideTaskDao(db: AppDatabase): TaskDao                  = db.taskDao()
    @Provides fun provideUserProfileDao(db: AppDatabase): UserProfileDao    = db.userProfileDao()
    @Provides fun provideSurgeEventDao(db: AppDatabase): SurgeEventDao      = db.surgeEventDao()
    @Provides fun provideDailyCheckInDao(db: AppDatabase): DailyCheckInDao  = db.dailyCheckInDao()
    @Provides fun provideAppUsageDao(db: AppDatabase): AppUsageDao          = db.appUsageDao()
    @Provides fun provideInterceptConfigDao(db: AppDatabase): InterceptConfigDao = db.interceptConfigDao()
    @Provides fun provideExpenseDao(db: AppDatabase): ExpenseDao = db.expenseDao()
    @Provides fun provideHabitDao(db: AppDatabase): HabitDao = db.habitDao()
}
