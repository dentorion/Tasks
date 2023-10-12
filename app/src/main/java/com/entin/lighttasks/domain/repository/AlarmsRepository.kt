package com.entin.lighttasks.domain.repository

import com.entin.lighttasks.data.db.entity.AlarmItemEntity
import kotlinx.coroutines.flow.Flow

/**
 * Interface of AlarmsRepository
 */

interface AlarmsRepository {

    /**
     * Get alarm by id
     */
    fun getAllAlarmById(alarmId: Int): Flow<List<AlarmItemEntity>>

    /**
     * Get alarm by task_id
     */
    fun getAlarmByTaskId(taskId: Int): Flow<AlarmItemEntity>

    /**
     * Get all alarms
     */
    fun getAllAlarms(): Flow<List<AlarmItemEntity>>

    /**
     * Get all actual alarms
     */
    fun getActualAlarms(): Flow<List<AlarmItemEntity>>

    /**
     * Add / update alarm
     */
    suspend fun addAlarm(alarmItemEntity: AlarmItemEntity): Long

    /**
     * Delete alarm
     */
    suspend fun deleteAlarm(alarmItemEntity: AlarmItemEntity): Int

    /**
     * Delete alarm by id
     */
    suspend fun deleteAlarmById(alarmId: Int)

    /**
     * Delete alarm by task_id
     */
    suspend fun deleteAlarmByTaskId(taskId: Int)
}
