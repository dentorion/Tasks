package com.entin.lighttasks.presentation.di

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import javax.inject.Singleton

@Singleton
class DbMigration {

    /**
     * Migration From 1 to 2 version
     */
    val migrationFrom1To2 = object : Migration(1, 2) {
        override fun migrate(database: SupportSQLiteDatabase) {
            database.execSQL(
                "CREATE TABLE IF NOT EXISTS tasks_temp (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL," +
                    "task_title TEXT NOT NULL," +
                    "task_message TEXT NOT NULL," +
                    "task_finished INTEGER NOT NULL," +
                    "task_important INTEGER NOT NULL," +
                    "date_created INTEGER NOT NULL," +
                    "task_group INTEGER NOT NULL," +
                    "task_position INTEGER NOT NULL)",
            )

            database.execSQL(
                "INSERT INTO tasks_temp (id, task_title, task_message, task_finished, task_important, date_created, task_group, task_position) " +
                    "SELECT id, task_title, task_message, task_finished, task_important, date_created, task_group, task_position FROM tasks",
            )

            database.execSQL("DROP TABLE IF EXISTS tasks")

            database.execSQL("ALTER TABLE tasks_temp RENAME TO tasks")
        }
    }
}
