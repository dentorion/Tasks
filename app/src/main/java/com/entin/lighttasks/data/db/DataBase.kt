package com.entin.lighttasks.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.entin.lighttasks.data.db.converter.ListUriToStringConverter
import com.entin.lighttasks.data.db.dao.AlarmsDao
import com.entin.lighttasks.data.db.dao.SectionsDao
import com.entin.lighttasks.data.db.dao.TaskDao
import com.entin.lighttasks.data.db.dao.TaskIconsDao
import com.entin.lighttasks.data.db.entity.AlarmItemEntity
import com.entin.lighttasks.data.db.entity.IconTaskEntity
import com.entin.lighttasks.data.db.entity.SectionEntity
import com.entin.lighttasks.data.db.entity.TaskEntity

@Database(
    entities = [
        TaskEntity::class,
        IconTaskEntity::class,
        SectionEntity::class,
        AlarmItemEntity::class,
    ],
    version = 8,
    exportSchema = false,
)
@TypeConverters(
    value = [
        ListUriToStringConverter::class,
    ]
)
abstract class DataBase : RoomDatabase() {

    abstract fun getTaskDAO(): TaskDao

    abstract fun getTaskGroupsDAO(): TaskIconsDao

    abstract fun getSectionDAO(): SectionsDao

    abstract fun getAlarmsDAO(): AlarmsDao

    companion object {
        const val DATABASE_NAME: String = "notes-data-base"
    }
}
