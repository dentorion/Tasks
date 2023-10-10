package com.entin.lighttasks.presentation.di

import android.content.Context
import com.entin.lighttasks.data.util.alarm.AlarmScheduler
import com.entin.lighttasks.data.util.alarm.AndroidAlarmScheduler
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AndroidAlarmSchedulerModule {

    @Provides
    @Singleton
    fun provideAndroidAlarmScheduler(@ApplicationContext context: Context): AlarmScheduler {
        return AndroidAlarmScheduler(context)
    }
}
