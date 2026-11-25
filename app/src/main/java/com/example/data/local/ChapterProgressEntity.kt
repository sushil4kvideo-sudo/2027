package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "chapter_progress")
data class ChapterProgressEntity(
    @PrimaryKey val chapterId: String,
    val subjectId: String,
    val branchId: String,
    val theoryDone: Boolean = false,
    val practiceDone: Boolean = false,
    val revisionDone: Boolean = false,
    val completedAtMillis: Long? = null,
    val updatedAtMillis: Long = System.currentTimeMillis()
)
