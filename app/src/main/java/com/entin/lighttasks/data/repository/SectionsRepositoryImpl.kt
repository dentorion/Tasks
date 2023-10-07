package com.entin.lighttasks.data.repository

import com.entin.lighttasks.data.db.SectionsDao
import com.entin.lighttasks.domain.entity.Section
import com.entin.lighttasks.domain.repository.SectionsRepository
import com.entin.lighttasks.domain.repository.TasksRepository
import com.entin.lighttasks.presentation.util.ONE
import com.entin.lighttasks.presentation.util.ZERO
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
    override suspend fun createSection(section: Section): Long =
        sectionsDao.createSection(section.copy(position = getMaxPosition().first()))

    /**
     * Edit section
     */
    override suspend fun updateSection(section: Section): Int =
        sectionsDao.updateSection(section)

    /**
     * Update list of sections
     */
    override suspend fun updateSections(sections: List<Section>) =
        sectionsDao.updateSections(sections)

    /**
     * Delete section
     */
    override suspend fun deleteSection(section: Section): Int =
        sectionsDao.deleteSection(section)

    /** Get section by id */
    override suspend fun getSectionById(sectionId: Int): Section =
        sectionsDao.getSectionById(sectionId)
}
