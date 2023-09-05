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
            // Tasks

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

            // TaskGroups

            database.execSQL(
                "CREATE TABLE IF NOT EXISTS taskGroups (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL," +
                    "background_resource INTEGER NOT NULL," +
                    "group_id INTEGER NOT NULL)",
            )
            database.execSQL(
                "INSERT INTO taskGroups (background_resource, group_id) " +
                    "VALUES" +
                    "(R.drawable.empty_btn_radio, 0)," +
                    "(R.drawable.work_btn_radio, 1)," +
                    "(R.drawable.rest_btn_radio, 2)," +
                    "(R.drawable.food_btn_radio, 3)," +
                    "(R.drawable.home_btn_radio, 4)," +
                    "(R.drawable.fish_btn_radio, 5)," +
                    "(R.drawable.bird_btn_radio, 6)," +
                    "(R.drawable.blueberries_btn_radio, 7)," +
                    "(R.drawable.bicycle_btn_radio, 8)," +
                    "(R.drawable.saw_btn_radio, 9)," +
                    "(R.drawable.camera_btn_radio, 10)," +
                    "(R.drawable.broom_btn_radio, 11)," +
                    "(R.drawable.film_btn_radio, 12)," +
                    "(R.drawable.collision_btn_radio, 13)," +
                    "(R.drawable.coconut_btn_radio, 14)," +
                    "(R.drawable.beer_btn_radio, 15)," +
                    "(R.drawable.boy_btn_radio, 16)," +
                    "(R.drawable.underwear_btn_radio, 17)," +
                    "(R.drawable.balloon_btn_radio, 18)," +
                    "(R.drawable.alien_btn_radio, 19)," +
                    "(R.drawable.car_btn_radio, 20)," +
                    "(R.drawable.amphora_btn_radio, 21)," +
                    "(R.drawable.accordion_btn_radio, 22)," +
                    "(R.drawable.airplane_btn_radio, 23)," +
                    "(R.drawable.tree_btn_radio, 24)," +
                    "(R.drawable.bandage_btn_radio, 25)," +
                    "(R.drawable.deer_btn_radio, 26)," +
                    "(R.drawable.knife_btn_radio, 27);",
            )
        }
    }
}
