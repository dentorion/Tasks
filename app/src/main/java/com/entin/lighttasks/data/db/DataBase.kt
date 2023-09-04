package com.entin.lighttasks.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.entin.lighttasks.domain.entity.Task
import com.entin.lighttasks.domain.entity.TaskGroup

@Database(
    entities = [
        Task::class,
        TaskGroup::class,
    ],
    version = 2,
    exportSchema = false,
)
abstract class DataBase : RoomDatabase() {

    abstract fun getTaskDAO(): TaskDao

    abstract fun getTaskGroupsDAO(): TaskGroupsDao

    companion object {
        const val DATABASE_NAME: String = "notes-data-base"
    }
}
