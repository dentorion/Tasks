package com.entin.lighttasks.presentation.application

import android.app.Application
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import com.entin.lighttasks.R
import com.entin.lighttasks.presentation.util.core.NotificationService.Companion.NOTIFICATION_CHANNEL_ID
import com.entin.lighttasks.presentation.util.core.NotificationService.Companion.NOTIFICATION_CHANNEL_NAME
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class App : Application() {

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
    }

    private fun createNotificationChannel() {
        val channel = NotificationChannel(
            NOTIFICATION_CHANNEL_ID,
            NOTIFICATION_CHANNEL_NAME,
            NotificationManager.IMPORTANCE_HIGH
        ).apply {
            description = this@App.getString(R.string.used_to_show_alarm_of_task)
            lockscreenVisibility = Notification.VISIBILITY_PUBLIC
            enableLights(true)
            enableVibration(true)
        }

        val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)
    }
}
