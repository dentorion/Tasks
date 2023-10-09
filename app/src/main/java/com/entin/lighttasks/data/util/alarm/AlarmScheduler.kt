package com.entin.lighttasks.data.util.alarm

import com.entin.lighttasks.domain.entity.AlarmItem

interface AlarmScheduler {

    fun schedule(item: AlarmItem)

    fun cancel(item: AlarmItem)
}