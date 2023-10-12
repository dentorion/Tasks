package com.entin.lighttasks.data.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.entin.lighttasks.data.db.entity.IconTaskEntity

@Dao
interface TaskIconsDao {

    /**
     * Insert pre-populate
     */
    @Insert
    suspend fun insertData(radioButtonElements: List<IconTaskEntity>)

    /**
     * Select all groups icons
     */

    @Query("SELECT * FROM taskGroups")
    suspend fun getTaskIcons(): List<IconTaskEntity>
}
