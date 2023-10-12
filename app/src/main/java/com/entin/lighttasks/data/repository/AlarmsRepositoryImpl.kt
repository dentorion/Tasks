package com.entin.lighttasks.data.repository

import com.entin.lighttasks.data.db.dao.AlarmsDao
import com.entin.lighttasks.data.db.entity.AlarmItemEntity
import com.entin.lighttasks.domain.repository.AlarmsRepository
import com.entin.lighttasks.domain.repository.TasksRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Repository Implementation of [TasksRepository] interface
 */

@Singleton
class AlarmsRepositoryImpl @Inject constructor(
    private val alarmsDao: AlarmsDao
) : AlarmsRepository {

    override fun getAllAlarmById(alarmId: Int): Flow<List<AlarmItemEntity>> =
        alarmsDao.getAllAlarmById(alarmId)

    override fun getAlarmByTaskId(taskId: Int): Flow<AlarmItemEntity> =
        alarmsDao.getAllAlarmByTaskId(taskId)

    override fun getAllAlarms(): Flow<List<AlarmItemEntity>> =
        alarmsDao.getAllAlarms()

    override fun getActualAlarms(): Flow<List<AlarmItemEntity>> =
        alarmsDao.getActualAlarms()

    override suspend fun addAlarm(alarmItemEntity: AlarmItemEntity): Long =
        alarmsDao.addAlarm(alarmItemEntity)

    override suspend fun deleteAlarm(alarmItemEntity: AlarmItemEntity): Int =
        alarmsDao.deleteAlarm(alarmItemEntity)

    override suspend fun deleteAlarmById(alarmId: Int) =
        alarmsDao.deleteAlarmById(alarmId)

    override suspend fun deleteAlarmByTaskId(taskId: Int) =
        alarmsDao.deleteAlarmByTaskId(taskId)
}
