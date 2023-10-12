package com.entin.lighttasks.data.util.alarm

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import com.entin.lighttasks.R
import com.entin.lighttasks.data.util.broadcast.AlarmReceiver
import com.entin.lighttasks.domain.entity.AlarmItem
import com.entin.lighttasks.presentation.util.INTENT_MESSAGE
import com.entin.lighttasks.presentation.util.SPACE

class AndroidAlarmScheduler(
    private val context: Context
) : AlarmScheduler {

    private val alarmManager = context.getSystemService(AlarmManager::class.java)

    override fun schedule(item: AlarmItem) {
        val message = context.resources.getString(R.string.alarm_message) + SPACE + item.message
        alarmManager.setExactAndAllowWhileIdle(
            AlarmManager.RTC_WAKEUP,
            item.time,
            PendingIntent.getBroadcast(
                context,
                item.taskId,
                Intent(context, AlarmReceiver::class.java).apply {
                    putExtra(INTENT_MESSAGE, message)
                },
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
        )
    }

    override fun cancel(item: AlarmItem) {
        alarmManager.cancel(
            PendingIntent.getBroadcast(
                context,
                item.taskId,
                Intent(context, AlarmReceiver::class.java),
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
        )
    }
}
