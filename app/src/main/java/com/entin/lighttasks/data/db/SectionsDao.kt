package com.entin.lighttasks.data.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.entin.lighttasks.domain.entity.Section
import kotlinx.coroutines.flow.Flow

@Dao
interface SectionsDao {

    /** Find a maximum value of position among the list of tasks */
    @Query("SELECT MAX(position) FROM sections")
    fun getLastId(): Flow<Int?>

    /**
     * Get all sections
     */
    @Query("SELECT * FROM sections ORDER BY position ASC")
    fun getAllSections(): Flow<List<Section>>

    /**
     * Create section
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun createSection(section: Section): Long

    /**
     * Edit section
     */
    @Update
    suspend fun updateSection(section: Section): Int

    /**
     * Update list of sections
     */
    @Update
    suspend fun updateSections(sections: List<Section>)

    /**
     * Delete section
     */
    @Delete
    suspend fun deleteSection(section: Section): Int

    /** Get section by id */
    @Query("SELECT * FROM sections WHERE id = :sectionId LIMIT 1")
    suspend fun getSectionById(sectionId: Int): Section
}
