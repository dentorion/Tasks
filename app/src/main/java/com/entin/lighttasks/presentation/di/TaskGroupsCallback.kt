package com.entin.lighttasks.presentation.di

import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.entin.lighttasks.R
import com.entin.lighttasks.data.db.TaskGroupsDao
import com.entin.lighttasks.domain.entity.IconTask
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import javax.inject.Provider

class TaskGroupsCallback(
    private val provider: Provider<TaskGroupsDao>,
) : RoomDatabase.Callback() {

    private val applicationScope = CoroutineScope(SupervisorJob())

    override fun onCreate(db: SupportSQLiteDatabase) {
        super.onCreate(db)
        applicationScope.launch(Dispatchers.IO) {
            populateDatabase()
        }
    }

    private suspend fun populateDatabase() {
        val radioButtonElements = listOf(
            IconTask(backgroundRes = R.drawable.empty_btn_radio, groupId = 0),
            IconTask(backgroundRes = R.drawable.work_btn_radio, groupId = 1),
            IconTask(backgroundRes = R.drawable.rest_btn_radio, groupId = 2),
            IconTask(backgroundRes = R.drawable.food_btn_radio, groupId = 3),
            IconTask(backgroundRes = R.drawable.home_btn_radio, groupId = 4),
            IconTask(backgroundRes = R.drawable.fish_btn_radio, groupId = 5),
            IconTask(backgroundRes = R.drawable.bird_btn_radio, groupId = 6),
            IconTask(backgroundRes = R.drawable.blueberries_btn_radio, groupId = 7),
            IconTask(backgroundRes = R.drawable.bicycle_btn_radio, groupId = 8),
            IconTask(backgroundRes = R.drawable.saw_btn_radio, groupId = 9),
            IconTask(backgroundRes = R.drawable.camera_btn_radio, groupId = 10),
            IconTask(backgroundRes = R.drawable.broom_btn_radio, groupId = 11),
            IconTask(backgroundRes = R.drawable.film_btn_radio, groupId = 12),
            IconTask(backgroundRes = R.drawable.collision_btn_radio, groupId = 13),
            IconTask(backgroundRes = R.drawable.coconut_btn_radio, groupId = 14),
            IconTask(backgroundRes = R.drawable.beer_btn_radio, groupId = 15),
            IconTask(backgroundRes = R.drawable.boy_btn_radio, groupId = 16),
            IconTask(backgroundRes = R.drawable.underwear_btn_radio, groupId = 17),
            IconTask(backgroundRes = R.drawable.balloon_btn_radio, groupId = 18),
            IconTask(backgroundRes = R.drawable.alien_btn_radio, groupId = 19),
            IconTask(backgroundRes = R.drawable.car_btn_radio, groupId = 20),
            IconTask(backgroundRes = R.drawable.amphora_btn_radio, groupId = 21),
            IconTask(backgroundRes = R.drawable.accordion_btn_radio, groupId = 22),
            IconTask(backgroundRes = R.drawable.airplane_btn_radio, groupId = 23),
            IconTask(backgroundRes = R.drawable.tree_btn_radio, groupId = 24),
            IconTask(backgroundRes = R.drawable.bandage_btn_radio, groupId = 25),
            IconTask(backgroundRes = R.drawable.deer_btn_radio, groupId = 26),
            IconTask(backgroundRes = R.drawable.knife_btn_radio, groupId = 27),
        )
        provider.get().insertData(radioButtonElements)
    }
}
