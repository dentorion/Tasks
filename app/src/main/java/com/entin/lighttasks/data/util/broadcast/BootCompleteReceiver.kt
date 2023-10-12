package com.entin.lighttasks.data.util.broadcast

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.entin.lighttasks.data.util.alarm.AlarmScheduler
import com.entin.lighttasks.domain.repository.AlarmsRepository
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class BootCompleteReceiver : BroadcastReceiver() {

    @Inject
    lateinit var alarmsRepository: AlarmsRepository

    @Inject
    lateinit var alarmScheduler: AlarmScheduler

    private val scope = CoroutineScope(Dispatchers.IO)

    override fun onReceive(context: Context?, intent: Intent?) {
        if (intent?.action == Intent.ACTION_BOOT_COMPLETED) {
            scope.launch {
                alarmsRepository.getActualAlarms().first { listAlarms ->
                    listAlarms.forEach { alarm ->
                        alarmScheduler.schedule(alarm)
                    }
                    true
                }
            }
        }
    }
}
