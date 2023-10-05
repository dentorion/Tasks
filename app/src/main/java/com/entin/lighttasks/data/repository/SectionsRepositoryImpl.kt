package com.entin.lighttasks.data.repository

import com.entin.lighttasks.data.db.SectionsDao
import com.entin.lighttasks.domain.entity.Section
import com.entin.lighttasks.domain.repository.SectionsRepository
import com.entin.lighttasks.domain.repository.TasksRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Repository Implementation of [TasksRepository] interface
 */

@Singleton
class SectionsRepositoryImpl @Inject constructor(
    private val sectionsDao: SectionsDao
) : SectionsRepository {

    override fun getAllSections(): Flow<List<Section>> {
        return sectionsDao.getAllSections()
    }

    override suspend fun createSection(section: Section): Long =
        sectionsDao.createSection(section)

    override suspend fun updateSection(section: Section): Int =
        sectionsDao.updateSection(section)

    override suspend fun deleteSection(section: Section): Int =
        sectionsDao.deleteSection(section)
}
