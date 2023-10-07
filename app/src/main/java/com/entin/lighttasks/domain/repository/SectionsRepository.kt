package com.entin.lighttasks.domain.repository

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
    suspend fun createSection(section: Section): Long

    /**
     * Edit section
     */
    suspend fun updateSection(section: Section): Int

    /**
     * Update list of sections
     */
    suspend fun updateSections(sections: List<Section>)

    /**
     * Delete section
     */
    suspend fun deleteSection(section: Section): Int

    /** Get section by id */
    suspend fun getSectionById(sectionId: Int): Section
}
