package com.entin.lighttasks.domain.repository

import com.entin.lighttasks.data.db.entity.SecurityEntity
import kotlinx.coroutines.flow.Flow

/**
 * Interface of SecurityRepository
 */

interface SecurityRepository {

    /**
     * Get security item by security item id
     */
    fun getSecurityItemById(id: Int): Flow<SecurityEntity>

    /**
     * Get security item by task_id
     */
    fun getSecurityItemByTaskId(taskId: Int): Flow<SecurityEntity?>

    /**
     * Get security item by section_id
     */
    fun getSecurityItemBySectionId(sectionId: Int): Flow<SecurityEntity?>

    /**
     * Add security item for task or section
     */
    suspend fun addSecurityItem(securityEntity: SecurityEntity): Long

    /**
     * Update password by security item id
     */
    suspend fun updatePasswordBySecurityItemId(id: Int, password: String)

    /**
     * Update security item by task id
     */
    suspend fun updateSecurityItemByTaskId(taskId: Int, password: String)

    /**
     * Update security item by section id
     */
    suspend fun updateSecurityItemBySectionId(sectionId: Int, password: String)

    /**
     * Delete password by security item id
     */
    suspend fun deleteSecurityItemById(id: Int)

    /**
     * Delete password by security section id
     */
    suspend fun deleteSecurityItemBySectionId(sectionId: Int)

    /**
     * Delete password by security task id
     */
    suspend fun deleteSecurityItemByTaskId(taskId: Int)
}
