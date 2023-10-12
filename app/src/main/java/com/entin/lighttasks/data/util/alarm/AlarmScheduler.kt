package com.entin.lighttasks.data.util.alarm

import com.entin.lighttasks.data.db.entity.AlarmItemEntity

interface AlarmScheduler {

    fun schedule(item: AlarmItemEntity)

    fun cancel(item: AlarmItemEntity)
}