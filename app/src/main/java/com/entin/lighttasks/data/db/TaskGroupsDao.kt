package com.entin.lighttasks.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.entin.lighttasks.domain.entity.TaskGroup

@Dao
interface TaskGroupsDao {

    /**
     * Insert pre-populate
     */
    @Insert
    suspend fun insertData(radioButtonElements: List<TaskGroup>)

    /**
     * Select all groups icons
     */

    @Query("SELECT * FROM taskGroups")
    suspend fun getTaskGroups(): List<TaskGroup>
}