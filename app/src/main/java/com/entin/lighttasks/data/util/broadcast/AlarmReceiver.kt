package com.entin.lighttasks.data.util.broadcast

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.entin.lighttasks.presentation.util.INTENT_MESSAGE
import com.entin.lighttasks.presentation.util.core.NotificationService

class AlarmReceiver: BroadcastReceiver() {

    override fun onReceive(context: Context?, intent: Intent?) {
        val message = intent?.getStringExtra(INTENT_MESSAGE) ?: return

        context?.let {
            val notificationService: NotificationService = NotificationService(it)
            notificationService.showNotification(message)
        }
    }
}
