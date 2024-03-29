package com.entin.lighttasks.presentation.util.core

import android.app.NotificationManager
import android.app.PendingIntent
import android.app.PendingIntent.FLAG_IMMUTABLE
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import com.entin.lighttasks.R
import com.entin.lighttasks.presentation.activity.MainActivity
import com.entin.lighttasks.presentation.util.SPACE
import javax.inject.Inject

class NotificationService @Inject constructor(
    private val context: Context
) {

    private val notificationManager =
        context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    fun showNotification(message: String) {
        val activityIntent = Intent(context, MainActivity::class.java)
        val activityPendingIntent = PendingIntent.getActivity(
            context,
            NOTIFICATION_REQUEST_CODE,
            activityIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or FLAG_IMMUTABLE
        )
        val notification = NotificationCompat.Builder(context, NOTIFICATION_CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_alarm_bell)
            .setContentTitle(context.getString(R.string.task_alarm))
            .setContentText(message)
            .setContentIntent(activityPendingIntent)
            .setPriority(NotificationManager.IMPORTANCE_HIGH)
            .setCategory(NotificationCompat.CATEGORY_ALARM)
            .build()
        notificationManager.notify(NOTIFICATION_REQUEST_CODE, notification)
    }

    companion object {
        const val NOTIFICATION_CHANNEL_ID = "light_tasks_alarm_channel"
        const val NOTIFICATION_CHANNEL_NAME = "Light tasks channel"
        const val NOTIFICATION_REQUEST_CODE = 72000
    }
}
