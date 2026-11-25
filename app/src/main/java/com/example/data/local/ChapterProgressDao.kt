package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface ChapterProgressDao {
    @Query("SELECT * FROM chapter_progress")
    fun observeAllProgress(): Flow<List<ChapterProgressEntity>>

    @Query("SELECT * FROM chapter_progress WHERE chapterId = :chapterId LIMIT 1")
    suspend fun getProgressById(chapterId: String): ChapterProgressEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertProgress(entity: ChapterProgressEntity)

    @Query("DELETE FROM chapter_progress")
    suspend fun resetAllProgress()
}
