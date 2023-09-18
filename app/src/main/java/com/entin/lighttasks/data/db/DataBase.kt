package com.entin.lighttasks.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.entin.lighttasks.domain.entity.Task
import com.entin.lighttasks.domain.entity.IconTask

@Database(
    entities = [
        Task::class,
        IconTask::class,
    ],
    version = 3,
    exportSchema = false,
)
abstract class DataBase : RoomDatabase() {

    abstract fun getTaskDAO(): TaskDao

    abstract fun getTaskGroupsDAO(): TaskGroupsDao

    companion object {
        const val DATABASE_NAME: String = "notes-data-base"
    }
}
