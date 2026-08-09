package com.example.yutorikaizen.di

import android.content.Context
import androidx.room.Room
import com.example.yutorikaizen.data.local.dao.FocusSessionDao
import com.example.yutorikaizen.data.local.dao.TaskDao
import com.example.yutorikaizen.data.local.dao.TriggerLogDao
import com.example.yutorikaizen.data.local.db.AppDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Hilt module that creates and provides the Room database singleton.
 *
 * @Singleton — only one AppDatabase instance exists for the entire app lifetime.
 * @InstallIn(SingletonComponent) — this module is scoped to the application, not an activity.
 *
 * DAO @Provides methods don't need @Singleton because the DAOs themselves are stateless;
 * they simply delegate to the database (which IS a singleton).
 */
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
        ).build()

    @Provides
    fun provideFocusSessionDao(db: AppDatabase): FocusSessionDao = db.focusSessionDao()

    @Provides
    fun provideTriggerLogDao(db: AppDatabase): TriggerLogDao = db.triggerLogDao()

    @Provides
    fun provideTaskDao(db: AppDatabase): TaskDao = db.taskDao()
}
