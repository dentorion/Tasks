package com.entin.lighttasks.data.db.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.entin.lighttasks.data.db.entity.SectionEntity
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
    @Query("SELECT Sections.*, " +
            "CASE WHEN Security.password IS NOT NULL THEN 1 ELSE 0 END AS has_password " +
            "FROM sections " +
            "LEFT JOIN Security ON Sections.id = Security.section_id " +
            "ORDER BY position ASC")
    fun getAllSections(): Flow<List<Section>>

    /**
     * Create section
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun createSection(sectionEntity: SectionEntity): Long

    /**
     * Edit section
     */
    @Update
    suspend fun updateSection(sectionEntity: SectionEntity): Int

    /**
     * Update list of sections
     */
    @Update
    suspend fun updateSections(sectionEntities: List<SectionEntity>)

    /**
     * Delete section
     */
    @Query("DELETE FROM sections WHERE id = :sectionId")
    suspend fun deleteSection(sectionId: Int): Int

    /** Get section by id */
    @Query("SELECT Sections.*, " +
            "CASE WHEN Security.password IS NOT NULL THEN 1 ELSE 0 END AS has_password " +
            "FROM sections " +
            "LEFT JOIN Security ON Sections.id = Security.section_id " +
            "WHERE Sections.id = :sectionId")
    fun getSectionById(sectionId: Int): Flow<Section>
}
