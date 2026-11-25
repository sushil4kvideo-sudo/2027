package com.example.data.model

enum class StudyStage {
    THEORY,
    PRACTICE,
    REVISION
}

enum class ImportanceTier {
    HIGH_WEIGHTAGE,
    ONE_SHOT,
    KEY_CHAPTER
}

data class SyllabusData(
    val examName: String,
    val syllabusYear: Int,
    val sourceName: String,
    val sourceUrl: String,
    val bulletinUrl: String,
    val fetchedDate: String,
    val session1ExamStartDate: String,
    val session1ExamDatesNote: String,
    val subjects: List<SubjectSpec>
)

data class SubjectSpec(
    val id: String,
    val name: String,
    val branches: List<BranchSpec>
)

data class BranchSpec(
    val id: String,
    val name: String,
    val chapters: List<ChapterSpec>
)

data class ChapterSpec(
    val id: String,
    val unitNumber: Int,
    val name: String,
    val topics: List<String>,
    val isImportant: Boolean = false,
    val importanceTier: ImportanceTier? = null,
    val weightageTag: String? = null
)

data class ChapterWithProgress(
    val spec: ChapterSpec,
    val subjectId: String,
    val subjectName: String,
    val branchId: String,
    val branchName: String,
    val theoryDone: Boolean,
    val practiceDone: Boolean,
    val revisionDone: Boolean,
    val completedAtMillis: Long?,
    val updatedAtMillis: Long
) {
    val isComplete: Boolean
        get() = theoryDone && practiceDone && revisionDone

    val isRevisionPriority: Boolean
        get() = theoryDone && practiceDone && !revisionDone

    val stagesCompletedCount: Int
        get() = listOf(theoryDone, practiceDone, revisionDone).count { it }

    val isImportant: Boolean
        get() = spec.isImportant
}

data class SubjectProgressSummary(
    val subjectId: String,
    val subjectName: String,
    val totalChapters: Int,
    val completedChapters: Int,
    val theoryDoneCount: Int,
    val practiceDoneCount: Int,
    val revisionDoneCount: Int,
    val importantTotalCount: Int = 0,
    val importantCompletedCount: Int = 0
) {
    val progressFraction: Float
        get() = if (totalChapters == 0) 0f else completedChapters.toFloat() / totalChapters.toFloat()

    val revisionFraction: Float
        get() = if (totalChapters == 0) 0f else revisionDoneCount.toFloat() / totalChapters.toFloat()

    val remainingChapters: Int
        get() = (totalChapters - completedChapters).coerceAtLeast(0)
}
