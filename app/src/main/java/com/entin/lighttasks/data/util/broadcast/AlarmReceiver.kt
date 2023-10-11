package com.entin.lighttasks.data.util.broadcast

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.entin.lighttasks.data.util.alarm.AlarmScheduler
import com.entin.lighttasks.presentation.util.INTENT_MESSAGE
import com.entin.lighttasks.presentation.util.core.NotificationService
import javax.inject.Inject

class AlarmReceiver: BroadcastReceiver() {

    override fun onReceive(context: Context?, intent: Intent?) {
        val message = intent?.getStringExtra(INTENT_MESSAGE) ?: return
        Log.e("ALARM_MY", "ALARM: $message")

        context?.let {
            val notificationService: NotificationService = NotificationService(it)
            notificationService.showNotification()
        }
    }
}
