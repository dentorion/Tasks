package com.entin.lighttasks.data.repository

import com.entin.lighttasks.data.db.AlarmsDao
import com.entin.lighttasks.domain.entity.AlarmItem
import com.entin.lighttasks.domain.entity.CalendarDatesConstraints
import com.entin.lighttasks.domain.entity.IconTask
import com.entin.lighttasks.domain.entity.OrderSort
import com.entin.lighttasks.domain.entity.Task
import com.entin.lighttasks.domain.repository.AlarmsRepository
import com.entin.lighttasks.domain.repository.TasksRepository
import com.entin.lighttasks.presentation.util.LAST_HOUR
import com.entin.lighttasks.presentation.util.LAST_MINUTE
import com.entin.lighttasks.presentation.util.LAST_SECOND
import com.entin.lighttasks.presentation.util.ONE
import com.entin.lighttasks.presentation.util.ZERO
import com.entin.lighttasks.presentation.util.getTimeMls
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Repository Implementation of [TasksRepository] interface
 */

@Singleton
class AlarmsRepositoryImpl @Inject constructor(
    private val alarmsDao: AlarmsDao
) : AlarmsRepository {

    override fun getAllAlarmById(alarmId: Int): Flow<List<AlarmItem>> =
        alarmsDao.getAllAlarmById(alarmId)

    override fun getAllAlarmByTaskId(taskId: Int): Flow<List<AlarmItem>> =
        alarmsDao.getAllAlarmByTaskId(taskId)

    override fun getAllAlarms(): Flow<List<AlarmItem>> =
        alarmsDao.getAllAlarms()

    override fun getActualAlarms(): Flow<List<AlarmItem>> =
        alarmsDao.getActualAlarms()

    override suspend fun addAlarm(alarmItem: AlarmItem): Long =
        alarmsDao.addAlarm(alarmItem)

    override suspend fun deleteAlarm(alarmItem: AlarmItem): Int =
        alarmsDao.deleteAlarm(alarmItem)

    override suspend fun deleteAlarmById(alarmId: Int) =
        alarmsDao.deleteAlarmById(alarmId)

    override suspend fun deleteAlarmByTaskId(taskId: Int) =
        alarmsDao.deleteAlarmByTaskId(taskId)
}
