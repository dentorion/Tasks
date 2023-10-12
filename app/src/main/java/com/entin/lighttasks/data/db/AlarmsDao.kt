package com.entin.lighttasks.data.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.entin.lighttasks.domain.entity.AlarmItem
import kotlinx.coroutines.flow.Flow
import java.util.Date

@Dao
interface AlarmsDao {

    /**
     * Get alarm by id
     */
    @Query("SELECT * FROM alarms WHERE id = :alarmId LIMIT 1")
    fun getAllAlarmById(alarmId: Int): Flow<List<AlarmItem>>

    /**
     * Get alarm by task_id
     */
    @Query("SELECT * FROM alarms WHERE task_id = :taskId LIMIT 1")
    fun getAllAlarmByTaskId(taskId: Int): Flow<List<AlarmItem>>

    /**
     * Get all alarms
     */
    @Query("SELECT * FROM alarms ORDER BY id ASC")
    fun getAllAlarms(): Flow<List<AlarmItem>>

    /**
     * Get all actual alarms
     */
    @Query("SELECT * FROM alarms WHERE time >= :now ORDER BY id ASC")
    fun getActualAlarms(now: Long = Date().time): Flow<List<AlarmItem>>

    /**
     * Add / update alarm implementation
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun addNewAlarm(alarmItem: AlarmItem): Long

    /**
     * Add / update alarm
     */
    @Transaction
    suspend fun addAlarm(alarmItem: AlarmItem): Long {
        deleteAlarmByTaskId(alarmItem.taskId)
        return addNewAlarm(alarmItem)
    }

    /**
     * Delete alarm
     */
    @Delete
    suspend fun deleteAlarm(alarmItem: AlarmItem): Int

    /**
     * Delete alarm by id
     */
    @Query("DELETE FROM alarms WHERE id = :alarmId")
    suspend fun deleteAlarmById(alarmId: Int)

    /**
     * Delete alarm by task_id
     */
    @Query("DELETE FROM alarms WHERE task_id = :taskId")
    suspend fun deleteAlarmByTaskId(taskId: Int)
}
