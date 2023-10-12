package com.entin.lighttasks.presentation.di

import android.content.Context
import com.entin.lighttasks.data.util.alarm.AlarmScheduler
import com.entin.lighttasks.data.util.alarm.AndroidAlarmScheduler
import com.entin.lighttasks.presentation.util.core.NotificationService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AlarmSchedulerModule {

    @Provides
    @Singleton
    fun provideAndroidAlarmScheduler(@ApplicationContext context: Context): AlarmScheduler {
        return AndroidAlarmScheduler(context)
    }

    @Provides
    @Singleton
    fun provideNotificationService(@ApplicationContext context: Context): NotificationService {
        return NotificationService(context)
    }
}
