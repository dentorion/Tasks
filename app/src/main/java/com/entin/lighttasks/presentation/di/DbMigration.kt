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
                "CREATE TABLE tasksNew (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
                    "task_title TEXT NOT NULL, " +
                    "task_message TEXT NOT NULL, " +
                    "task_finished INTEGER NOT NULL, " +
                    "task_important INTEGER NOT NULL, " +
                    "date_created INTEGER NOT NULL, " +
                    "task_group INTEGER NOT NULL, " +
                    "task_position INTEGER NOT NULL)",
            )
            database.execSQL(
                "INSERT INTO tasksNew (id, task_title, task_message, task_finished, task_important, date_created, task_group, task_position) " +
                    "SELECT id, task_title, task_message, task_finished, task_important, date_created, task_group, task_position FROM tasks",
            )
            database.execSQL("DROP TABLE tasks")
            database.execSQL("ALTER TABLE tasksNew RENAME TO tasks")

            // TaskGroups

            database.execSQL(
                "CREATE TABLE taskGroups (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
                    "background_resource INTEGER NOT NULL, " +
                    "group_id INTEGER NOT NULL)",
            )
            database.execSQL(
                "INSERT INTO taskGroups (background_resource, group_id) " +
                    "VALUES " +
                    "(R.drawable.empty_btn_radio, 0), " +
                    "(R.drawable.work_btn_radio, 1), " +
                    "(R.drawable.rest_btn_radio, 2), " +
                    "(R.drawable.food_btn_radio, 3), " +
                    "(R.drawable.home_btn_radio, 4), " +
                    "(R.drawable.fish_btn_radio, 5), " +
                    "(R.drawable.bird_btn_radio, 6), " +
                    "(R.drawable.blueberries_btn_radio, 7), " +
                    "(R.drawable.bicycle_btn_radio, 8), " +
                    "(R.drawable.saw_btn_radio, 9), " +
                    "(R.drawable.camera_btn_radio, 10), " +
                    "(R.drawable.broom_btn_radio, 11), " +
                    "(R.drawable.film_btn_radio, 12), " +
                    "(R.drawable.collision_btn_radio, 13), " +
                    "(R.drawable.coconut_btn_radio, 14), " +
                    "(R.drawable.beer_btn_radio, 15), " +
                    "(R.drawable.boy_btn_radio, 16), " +
                    "(R.drawable.underwear_btn_radio, 17), " +
                    "(R.drawable.balloon_btn_radio, 18), " +
                    "(R.drawable.alien_btn_radio, 19), " +
                    "(R.drawable.car_btn_radio, 20), " +
                    "(R.drawable.amphora_btn_radio, 21), " +
                    "(R.drawable.accordion_btn_radio, 22), " +
                    "(R.drawable.airplane_btn_radio, 23), " +
                    "(R.drawable.tree_btn_radio, 24), " +
                    "(R.drawable.bandage_btn_radio, 25), " +
                    "(R.drawable.deer_btn_radio, 26), " +
                    "(R.drawable.knife_btn_radio, 27)",
            )
        }
    }

    /**
     * Migration From 2 to 3 version
     */
    val migrationFrom2To3 = object : Migration(2, 3) {
        override fun migrate(database: SupportSQLiteDatabase) {
            // Tasks

            database.execSQL(
                "CREATE TABLE tasksNew (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
                    "title TEXT NOT NULL, " +
                    "message TEXT NOT NULL, " +
                    "is_finished INTEGER NOT NULL, " +
                    "is_important INTEGER NOT NULL, " +
                    "created_at INTEGER NOT NULL, " +
                    "task_group INTEGER NOT NULL, " +
                    "position INTEGER NOT NULL, " +
                    "expire_date_first INTEGER NOT NULL, " +
                    "expire_date_second INTEGER NOT NULL, " +
                    "is_task_expired INTEGER NOT NULL, " +
                    "is_event INTEGER NOT NULL, " +
                    "is_range INTEGER NOT NULL)",
            )

            database.execSQL(
                "INSERT INTO tasksNew (id, title, message, is_finished, is_important, created_at, task_group, position, expire_date_first, expire_date_second, is_task_expired, is_event, is_range) " +
                    "SELECT id, task_title, task_message, task_finished, task_important, date_created, task_group, task_position, 0, 0, 0, 0, 0 FROM tasks",
            )
            database.execSQL("DROP TABLE tasks")
            database.execSQL("ALTER TABLE tasksNew RENAME TO tasks")
        }
    }

    /**
     * Migration From 3 to 4 version
     */
    val migrationFrom3To4 = object : Migration(3, 4) {
        override fun migrate(database: SupportSQLiteDatabase) {
            // Tasks

            database.execSQL(
                "CREATE TABLE tasksNew (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
                    "title TEXT NOT NULL, " +
                    "message TEXT NOT NULL, " +
                    "is_finished INTEGER NOT NULL, " +
                    "is_important INTEGER NOT NULL, " +
                    "created_at INTEGER NOT NULL, " +
                    "edited_at INTEGER NOT NULL, " +
                    "task_group INTEGER NOT NULL, " +
                    "position INTEGER NOT NULL, " +
                    "expire_date_first INTEGER NOT NULL, " +
                    "expire_date_second INTEGER NOT NULL, " +
                    "is_task_expired INTEGER NOT NULL, " +
                    "is_event INTEGER NOT NULL, " +
                    "is_range INTEGER NOT NULL, " +
                    "attached_link TEXT NOT NULL)",
            )
            database.execSQL(
                "INSERT INTO tasksNew (id, title, message, is_finished, is_important, created_at, edited_at, task_group, position, expire_date_first, expire_date_second, is_task_expired, is_event, is_range, attached_link) " +
                    "SELECT id, title, message, is_finished, is_important, created_at, 0, task_group, position, expire_date_first, expire_date_second, is_task_expired, is_event, is_range, '' FROM tasks",
            )
            database.execSQL("DROP TABLE tasks")
            database.execSQL("ALTER TABLE tasksNew RENAME TO tasks")
        }
    }

    /**
     * Migration From 3 to 4 version
     */
    val migrationFrom4To5 = object : Migration(4, 5) {
        override fun migrate(database: SupportSQLiteDatabase) {
            // Tasks

            database.execSQL(
                "CREATE TABLE tasksNew (" +
                        "id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
                        "title TEXT NOT NULL, " +
                        "message TEXT NOT NULL, " +
                        "is_finished INTEGER NOT NULL, " +
                        "is_important INTEGER NOT NULL, " +
                        "created_at INTEGER NOT NULL, " +
                        "edited_at INTEGER NOT NULL, " +
                        "task_group INTEGER NOT NULL, " +
                        "position INTEGER NOT NULL, " +
                        "expire_date_first INTEGER NOT NULL, " +
                        "expire_date_second INTEGER NOT NULL, " +
                        "is_task_expired INTEGER NOT NULL, " +
                        "is_event INTEGER NOT NULL, " +
                        "is_range INTEGER NOT NULL, " +
                        "attached_link, TEXT NOT NULL, " +
                        "attached_photo TEXT NOT NULL, " +
                        "attached_voice TEXT NOT NULL)",
            )
            database.execSQL(
                "INSERT INTO tasksNew (id, title, message, is_finished, is_important, created_at, edited_at, task_group, position, expire_date_first, expire_date_second, is_task_expired, is_event, is_range, attached_link, attached_photo, attached_voice) " +
                        "SELECT id, title, message, is_finished, is_important, created_at, edited_at, task_group, position, expire_date_first, expire_date_second, is_task_expired, is_event, is_range, attached_link, '', '' FROM tasks",
            )
            database.execSQL("DROP TABLE tasks")
            database.execSQL("ALTER TABLE tasksNew RENAME TO tasks")
        }
    }
}
