package com.entin.lighttasks.data.db.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.entin.lighttasks.data.db.entity.AlarmItemEntity
import kotlinx.coroutines.flow.Flow
import java.util.Date

@Dao
interface AlarmsDao {

    /**
     * Get alarm by id
     */
    @Query("SELECT * FROM alarms WHERE alarm_id = :alarmId LIMIT 1")
    fun getAllAlarmById(alarmId: Int): Flow<List<AlarmItemEntity>>

    /**
     * Get alarm by task_id
     */
    @Query("SELECT * FROM alarms WHERE task_id = :taskId LIMIT 1")
    fun getAllAlarmByTaskId(taskId: Int): Flow<AlarmItemEntity?>

    /**
     * Get all alarms
     */
    @Query("SELECT * FROM alarms ORDER BY alarm_id ASC")
    fun getAllAlarms(): Flow<List<AlarmItemEntity>>

    /**
     * Get all actual alarms
     */
    @Query("SELECT * FROM alarms WHERE alarm_time >= :now ORDER BY alarm_id ASC")
    fun getActualAlarms(now: Long = Date().time): Flow<List<AlarmItemEntity>>

    /**
     * Add / update alarm implementation
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun addNewAlarm(alarmItemEntity: AlarmItemEntity): Long

    /**
     * Add / update alarm
     */
    @Transaction
    suspend fun addAlarm(alarmItemEntity: AlarmItemEntity): Long {
        deleteAlarmByTaskId(alarmItemEntity.taskId)
        return addNewAlarm(alarmItemEntity)
    }

    /**
     * Delete alarm
     */
    @Delete
    suspend fun deleteAlarm(alarmItemEntity: AlarmItemEntity): Int

    /**
     * Delete alarm by id
     */
    @Query("DELETE FROM alarms WHERE alarm_id = :alarmId")
    suspend fun deleteAlarmById(alarmId: Int)

    /**
     * Delete alarm by task_id
     */
    @Query("DELETE FROM alarms WHERE task_id = :taskId")
    suspend fun deleteAlarmByTaskId(taskId: Int)
}
