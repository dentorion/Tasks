package com.entin.lighttasks.data.repository

import com.entin.lighttasks.data.db.dao.SectionsDao
import com.entin.lighttasks.data.db.entity.SectionEntity
import com.entin.lighttasks.domain.entity.Section
import com.entin.lighttasks.domain.repository.SectionsRepository
import com.entin.lighttasks.domain.repository.TasksRepository
import com.entin.lighttasks.presentation.util.ONE
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Repository Implementation of [TasksRepository] interface
 */

@Singleton
class SectionsRepositoryImpl @Inject constructor(
    private val sectionsDao: SectionsDao
) : SectionsRepository {

    /**
     * Get maximum position of sections table
     */
    override fun getMaxPosition(): Flow<Int> =
        sectionsDao.getLastId().map { max -> max?.let { it + ONE } ?: ONE }

    /** Get all sections */
    override fun getAllSections(): Flow<List<Section>> =
        sectionsDao.getAllSections()

    /**
     * Create section
     */
    override suspend fun createSection(sectionEntity: SectionEntity): Long =
        sectionsDao.createSection(sectionEntity.copy(position = getMaxPosition().first()))

    /**
     * Edit section
     */
    override suspend fun updateSection(sectionEntity: SectionEntity): Int =
        sectionsDao.updateSection(sectionEntity)

    /**
     * Update list of sections
     */
    override suspend fun updateSections(sectionEntities: List<SectionEntity>) =
        sectionsDao.updateSections(sectionEntities)

    /**
     * Delete section
     */
    override suspend fun deleteSectionById(sectionId: Int): Int =
        sectionsDao.deleteSection(sectionId)

    /** Get section by id */
    override fun getSectionById(sectionId: Int): Flow<String> =
        sectionsDao.getSectionById(sectionId)
}
