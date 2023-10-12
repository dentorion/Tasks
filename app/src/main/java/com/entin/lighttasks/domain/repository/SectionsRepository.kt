package com.entin.lighttasks.domain.repository

import com.entin.lighttasks.data.db.entity.SectionEntity
import kotlinx.coroutines.flow.Flow

/**
 * Interface of Repository
 */

interface SectionsRepository {

    /**
     * Get all sections
     */
    fun getAllSections(): Flow<List<SectionEntity>>

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
    suspend fun deleteSection(sectionEntity: SectionEntity): Int

    /** Get section by id */
    suspend fun getSectionById(sectionId: Int): SectionEntity
}
