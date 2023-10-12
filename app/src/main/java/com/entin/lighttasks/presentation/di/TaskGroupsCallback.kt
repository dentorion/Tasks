package com.entin.lighttasks.presentation.di

import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.entin.lighttasks.R
import com.entin.lighttasks.data.db.dao.TaskIconsDao
import com.entin.lighttasks.data.db.entity.IconTaskEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import javax.inject.Provider

class TaskGroupsCallback(
    private val provider: Provider<TaskIconsDao>,
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
            IconTaskEntity(backgroundRes = R.drawable.empty_btn_radio, groupId = 0),
            IconTaskEntity(backgroundRes = R.drawable.work_btn_radio, groupId = 1),
            IconTaskEntity(backgroundRes = R.drawable.rest_btn_radio, groupId = 2),
            IconTaskEntity(backgroundRes = R.drawable.food_btn_radio, groupId = 3),
            IconTaskEntity(backgroundRes = R.drawable.home_btn_radio, groupId = 4),
            IconTaskEntity(backgroundRes = R.drawable.fish_btn_radio, groupId = 5),
            IconTaskEntity(backgroundRes = R.drawable.bird_btn_radio, groupId = 6),
            IconTaskEntity(backgroundRes = R.drawable.blueberries_btn_radio, groupId = 7),
            IconTaskEntity(backgroundRes = R.drawable.bicycle_btn_radio, groupId = 8),
            IconTaskEntity(backgroundRes = R.drawable.saw_btn_radio, groupId = 9),
            IconTaskEntity(backgroundRes = R.drawable.camera_btn_radio, groupId = 10),
            IconTaskEntity(backgroundRes = R.drawable.broom_btn_radio, groupId = 11),
            IconTaskEntity(backgroundRes = R.drawable.film_btn_radio, groupId = 12),
            IconTaskEntity(backgroundRes = R.drawable.collision_btn_radio, groupId = 13),
            IconTaskEntity(backgroundRes = R.drawable.coconut_btn_radio, groupId = 14),
            IconTaskEntity(backgroundRes = R.drawable.beer_btn_radio, groupId = 15),
            IconTaskEntity(backgroundRes = R.drawable.boy_btn_radio, groupId = 16),
            IconTaskEntity(backgroundRes = R.drawable.underwear_btn_radio, groupId = 17),
            IconTaskEntity(backgroundRes = R.drawable.balloon_btn_radio, groupId = 18),
            IconTaskEntity(backgroundRes = R.drawable.alien_btn_radio, groupId = 19),
            IconTaskEntity(backgroundRes = R.drawable.car_btn_radio, groupId = 20),
            IconTaskEntity(backgroundRes = R.drawable.amphora_btn_radio, groupId = 21),
            IconTaskEntity(backgroundRes = R.drawable.accordion_btn_radio, groupId = 22),
            IconTaskEntity(backgroundRes = R.drawable.airplane_btn_radio, groupId = 23),
            IconTaskEntity(backgroundRes = R.drawable.tree_btn_radio, groupId = 24),
            IconTaskEntity(backgroundRes = R.drawable.bandage_btn_radio, groupId = 25),
            IconTaskEntity(backgroundRes = R.drawable.deer_btn_radio, groupId = 26),
            IconTaskEntity(backgroundRes = R.drawable.knife_btn_radio, groupId = 27),
        )
        provider.get().insertData(radioButtonElements)
    }
}
