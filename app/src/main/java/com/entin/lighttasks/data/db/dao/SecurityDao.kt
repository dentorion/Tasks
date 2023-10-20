package com.entin.lighttasks.data.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.entin.lighttasks.data.db.entity.SecurityEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface SecurityDao {

    /**
     * Get security item by security item id
     */
    @Query("SELECT * FROM security WHERE id = :id LIMIT 1")
    fun getSecurityItemById(id: Int): Flow<SecurityEntity>

    /**
     * Get security item by task_id
     */
    @Query("SELECT * FROM security WHERE task_id = :taskId LIMIT 1")
    fun getSecurityItemByTaskId(taskId: Int): Flow<SecurityEntity?>

    /**
     * Get security item by section_id
     */
    @Query("SELECT * FROM security WHERE section_id = :sectionId LIMIT 1")
    fun getSecurityItemBySectionId(sectionId: Int): Flow<SecurityEntity>

    /**
     * Add security item for task or section
     */
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun addSecurityItem(securityEntity: SecurityEntity): Long

    /**
     * Update password by security item id
     */
    @Query("UPDATE security SET password = :password WHERE id = :id")
    suspend fun updatePasswordBySecurityItemId(id: Int, password: String)

    /**
     * Update password by task id
     */
    @Query("UPDATE security SET password = :password WHERE task_id = :taskId")
    suspend fun updateSecurityItemByTaskId(taskId: Int, password: String)

    /**
     * Delete password by security item id
     */
    @Query("DELETE FROM security WHERE id = :id")
    suspend fun deleteSecurityItemById(id: Int)

    /**
     * Delete password by security section id
     */
    @Query("DELETE FROM security WHERE section_id = :sectionId")
    suspend fun deleteSecurityItemBySectionId(sectionId: Int)

    /**
     * Delete password by security task id
     */
    @Query("DELETE FROM security WHERE task_id = :taskId")
    suspend fun deleteSecurityItemByTaskId(taskId: Int)

}
