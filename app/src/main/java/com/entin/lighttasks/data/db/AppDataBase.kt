package com.entin.lighttasks.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.entin.lighttasks.domain.entity.Task

@Database(entities = [Task::class], version = 1, exportSchema = false)
abstract class AppDataBase : RoomDatabase() {

    abstract fun getTaskDAO(): TaskDao
}
