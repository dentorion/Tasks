package com.entin.lighttasks.presentation.screens.section

import com.entin.lighttasks.data.db.entity.SectionEntity

/**
 * List of available operations for Section Preferences
 */

sealed class SectionsEventContract {
    data class ShowAllSections(val sectionEntities: List<SectionEntity> = listOf()): SectionsEventContract()
}
