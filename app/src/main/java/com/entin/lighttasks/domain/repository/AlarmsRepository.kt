package com.entin.lighttasks.domain.repository

import com.entin.lighttasks.domain.entity.AlarmItem
import kotlinx.coroutines.flow.Flow
import java.util.Date

/**
 * Interface of AlarmsRepository
 */

interface AlarmsRepository {

    /**
     * Get alarm by id
     */
    fun getAllAlarmById(alarmId: Int): Flow<List<AlarmItem>>

    /**
     * Get alarm by task_id
     */
    fun getAllAlarmByTaskId(taskId: Int): Flow<List<AlarmItem>>

    /**
     * Get all alarms
     */
    fun getAllAlarms(): Flow<List<AlarmItem>>

    /**
     * Get all actual alarms
     */
    fun getActualAlarms(): Flow<List<AlarmItem>>

    /**
     * Add / update alarm
     */
    suspend fun addAlarm(alarmItem: AlarmItem): Long

    /**
     * Delete alarm
     */
    suspend fun deleteAlarm(alarmItem: AlarmItem): Int

    /**
     * Delete alarm by id
     */
    suspend fun deleteAlarmById(alarmId: Int)

    /**
     * Delete alarm by task_id
     */
    suspend fun deleteAlarmByTaskId(taskId: Int)
}
