package com.entin.lighttasks.data.util.alarm

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.util.Log
import com.entin.lighttasks.data.util.broadcast.AlarmReceiver
import com.entin.lighttasks.domain.entity.AlarmItem
import com.entin.lighttasks.presentation.util.INTENT_MESSAGE
import java.time.ZoneId

class AndroidAlarmScheduler(
    private val context: Context
) : AlarmScheduler {

    private val alarmManager = context.getSystemService(AlarmManager::class.java)

    override fun schedule(item: AlarmItem) {
        Log.e("ALARM_MY", "AndroidAlarmScheduler(). schedule: ${item.time} / ${item.time.atZone(ZoneId.systemDefault()).toEpochSecond() * ONE_SEC_MLS}")
        alarmManager.setExactAndAllowWhileIdle(
            AlarmManager.RTC_WAKEUP,
            item.time.atZone(ZoneId.systemDefault()).toEpochSecond() * ONE_SEC_MLS,
            PendingIntent.getBroadcast(
                context,
                item.taskId,
                Intent(context, AlarmReceiver::class.java).apply {
                    putExtra(INTENT_MESSAGE, item.message)
                },
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
        )
    }

    override fun cancel(item: AlarmItem) {
        Log.e("ALARM_MY", "AndroidAlarmScheduler(). cancel: ${item.time} / ${item.time.atZone(ZoneId.systemDefault()).toEpochSecond() * ONE_SEC_MLS}")
        alarmManager.setExactAndAllowWhileIdle(
            AlarmManager.RTC_WAKEUP,
            item.time.atZone(ZoneId.systemDefault()).toEpochSecond() * ONE_SEC_MLS,
            PendingIntent.getBroadcast(
                context,
                item.taskId,
                Intent(context, AlarmReceiver::class.java),
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
        )
    }

    companion object {
        private const val ONE_SEC_MLS = 1000
    }
}
