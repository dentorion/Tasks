package com.entin.lighttasks.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.entin.lighttasks.domain.entity.AlarmItem
import com.entin.lighttasks.domain.entity.IconTask
import com.entin.lighttasks.domain.entity.Section
import com.entin.lighttasks.domain.entity.Task

@Database(
    entities = [
        Task::class,
        IconTask::class,
        Section::class,
        AlarmItem::class,
    ],
    version = 6,
    exportSchema = false,
)
abstract class DataBase : RoomDatabase() {

    abstract fun getTaskDAO(): TaskDao

    abstract fun getTaskGroupsDAO(): TaskGroupsDao

    abstract fun getSectionDAO(): SectionsDao

    abstract fun getAlarmsDAO(): AlarmsDao

    companion object {
        const val DATABASE_NAME: String = "notes-data-base"
    }
}
