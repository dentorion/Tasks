package com.example.tasksexample.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.tasksexample.domain.entity.Task

@Database(entities = [Task::class], version = 1, exportSchema = false)
abstract class AppDataBase: RoomDatabase() {

    abstract fun getTaskDAO(): TaskDao
}