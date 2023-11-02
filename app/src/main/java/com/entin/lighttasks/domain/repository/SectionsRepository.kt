package com.entin.lighttasks.domain.repository

import com.entin.lighttasks.data.db.entity.SectionEntity
import com.entin.lighttasks.domain.entity.Section
import kotlinx.coroutines.flow.Flow

/**
 * Interface of Repository
 */

interface SectionsRepository {

    /**
     * Get all sections
     */
    fun getAllSections(): Flow<List<Section>>

    /** Get max position of section table */
    fun getMaxPosition(): Flow<Int>

    /**
     * Create section
     */
    suspend fun createSection(sectionEntity: SectionEntity): Long

    /**
     * Edit section
     */
    suspend fun updateSection(sectionEntity: SectionEntity): Int

    /**
     * Update list of sections
     */
    suspend fun updateSections(sectionEntities: List<SectionEntity>)

    /**
     * Delete section
     */
    suspend fun deleteSectionById(sectionId: Int): Int

    /** Get section by id */
    fun getSectionById(sectionId: Int): Flow<Section>
}
