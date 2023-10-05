package com.entin.lighttasks.presentation.screens.section

import com.entin.lighttasks.domain.entity.Section

/**
 * List of available operations for Section Preferences
 */

sealed class SectionsEventContract {
    data class ShowAllSections(val sections: List<Section> = listOf()): SectionsEventContract()
}
