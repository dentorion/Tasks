package com.entin.lighttasks.data.repository

import com.entin.lighttasks.data.db.dao.SecurityDao
import com.entin.lighttasks.data.db.entity.SecurityEntity
import com.entin.lighttasks.domain.repository.SecurityRepository
import com.entin.lighttasks.domain.repository.TasksRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Repository Implementation of [TasksRepository] interface
 */

@Singleton
class SecurityRepositoryImpl @Inject constructor(
    private val securityDao: SecurityDao
) : SecurityRepository {

    override fun getSecurityItemById(id: Int): Flow<SecurityEntity> =
        securityDao.getSecurityItemById(id)

    override fun getSecurityItemByTaskId(taskId: Int): Flow<SecurityEntity> =
        securityDao.getSecurityItemByTaskId(taskId)

    override fun getSecurityItemBySectionId(sectionId: Int): Flow<SecurityEntity> =
        securityDao.getSecurityItemBySectionId(sectionId)

    override suspend fun addSecurityItem(securityEntity: SecurityEntity): Long =
        securityDao.addSecurityItem(securityEntity)

    override suspend fun updatePasswordBySecurityItemId(id: Int, password: String) =
        securityDao.updatePasswordBySecurityItemId(id, password)

    override suspend fun deleteSecurityItemById(id: Int) =
        securityDao.deleteSecurityItemById(id)

    override suspend fun deleteSecurityItemBySectionId(sectionId: Int) =
        securityDao.deleteSecurityItemBySectionId(sectionId)

    override suspend fun deleteSecurityItemByTaskId(taskId: Int) =
        securityDao.deleteSecurityItemByTaskId(taskId)
}
