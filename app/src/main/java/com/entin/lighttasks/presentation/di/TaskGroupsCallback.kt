package com.entin.lighttasks.presentation.di

import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.entin.lighttasks.R
import com.entin.lighttasks.data.db.TaskGroupsDao
import com.entin.lighttasks.domain.entity.TaskGroup
import com.entin.lighttasks.presentation.util.FISH_HOME_ID
import com.entin.lighttasks.presentation.util.RADIO_EMPTY_ID
import com.entin.lighttasks.presentation.util.RADIO_FOOD_ID
import com.entin.lighttasks.presentation.util.RADIO_HOME_ID
import com.entin.lighttasks.presentation.util.RADIO_REST_ID
import com.entin.lighttasks.presentation.util.RADIO_WORK_ID
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
            TaskGroup(backgroundResource = R.drawable.empty_btn_radio, groupId = RADIO_EMPTY_ID),
            TaskGroup(backgroundResource = R.drawable.work_btn_radio, groupId = RADIO_WORK_ID),
            TaskGroup(backgroundResource = R.drawable.rest_btn_radio, groupId = RADIO_REST_ID),
            TaskGroup(backgroundResource = R.drawable.food_btn_radio, groupId = RADIO_FOOD_ID),
            TaskGroup(backgroundResource = R.drawable.home_btn_radio, groupId = RADIO_HOME_ID),
            TaskGroup(backgroundResource = R.drawable.fish_btn_radio, groupId = FISH_HOME_ID),
        )
        provider.get().insertData(radioButtonElements)
    }
}
