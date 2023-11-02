package com.entin.lighttasks.presentation.screens.section

import com.entin.lighttasks.domain.entity.Section

/**
 * List of available operations for Section Preferences
 */

sealed class SectionsEventContract {
    data class ShowAllSections(val sectionEntities: List<Section> = listOf()): SectionsEventContract()
    data class CheckPassword(val sectionId: Int): SectionsEventContract()
    data class CheckPasswordDeletion(val sectionId: Int): SectionsEventContract()
}
