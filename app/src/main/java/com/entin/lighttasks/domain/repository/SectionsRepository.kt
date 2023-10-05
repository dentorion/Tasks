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

    /**
     * Create section
     */
    suspend fun createSection(section: Section): Long

    /**
     * Edit section
     */
    suspend fun updateSection(section: Section): Int

    /**
     * Delete section
     */
    suspend fun deleteSection(section: Section): Int
}
