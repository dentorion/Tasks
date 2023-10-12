package com.entin.lighttasks.data.db.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.entin.lighttasks.data.db.entity.SectionEntity
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
    fun getAllSections(): Flow<List<SectionEntity>>

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
    @Delete
    suspend fun deleteSection(sectionEntity: SectionEntity): Int

    /** Get section by id */
    @Query("SELECT * FROM sections WHERE id = :sectionId LIMIT 1")
    suspend fun getSectionById(sectionId: Int): SectionEntity
}
