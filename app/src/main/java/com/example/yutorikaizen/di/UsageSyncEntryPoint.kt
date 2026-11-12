package com.example.yutorikaizen.di

import com.example.yutorikaizen.data.local.UsageTracker
import com.example.yutorikaizen.data.local.dao.AppUsageDao
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@EntryPoint
@InstallIn(SingletonComponent::class)
interface UsageSyncEntryPoint {
    fun usageTracker(): UsageTracker
    fun appUsageDao(): AppUsageDao
}
