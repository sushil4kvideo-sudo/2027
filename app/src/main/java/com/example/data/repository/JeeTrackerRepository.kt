package com.example.data.repository

import com.example.data.local.ChapterProgressDao
import com.example.data.local.ChapterProgressEntity
import com.example.data.local.SettingsDataStore
import com.example.data.local.SyllabusAssetLoader
import com.example.data.model.AccentPreset
import com.example.data.model.CardDensity
import com.example.data.model.ChapterLayout
import com.example.data.model.ChapterWithProgress
import com.example.data.model.StudyStage
import com.example.data.model.SyllabusData
import com.example.data.model.ThemeMode
import com.example.data.model.UserSettings
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class JeeTrackerRepository(
    syllabusAssetLoader: SyllabusAssetLoader,
    private val progressDao: ChapterProgressDao,
    private val settingsDataStore: SettingsDataStore
) {
    val syllabusData: SyllabusData = syllabusAssetLoader.loadSyllabus()

    val settingsFlow: Flow<UserSettings> = settingsDataStore.settingsFlow

    val chaptersFlow: Flow<List<ChapterWithProgress>> = progressDao.observeAllProgress().map { entities ->
        val progressById = entities.associateBy { it.chapterId }
        buildList {
            for (subject in syllabusData.subjects) {
                for (branch in subject.branches) {
                    for (chapter in branch.chapters) {
                        val entity = progressById[chapter.id]
                        add(
                            ChapterWithProgress(
                                spec = chapter,
                                subjectId = subject.id,
                                subjectName = subject.name,
                                branchId = branch.id,
                                branchName = branch.name,
                                theoryDone = entity?.theoryDone ?: false,
                                practiceDone = entity?.practiceDone ?: false,
                                revisionDone = entity?.revisionDone ?: false,
                                completedAtMillis = entity?.completedAtMillis,
                                updatedAtMillis = entity?.updatedAtMillis ?: 0L
                            )
                        )
                    }
                }
            }
        }
    }

    suspend fun toggleStage(chapterId: String, subjectId: String, branchId: String, stage: StudyStage) {
        val current = progressDao.getProgressById(chapterId)
        val theory = current?.theoryDone ?: false
        val practice = current?.practiceDone ?: false
        val revision = current?.revisionDone ?: false

        val newTheory = if (stage == StudyStage.THEORY) !theory else theory
        val newPractice = if (stage == StudyStage.PRACTICE) !practice else practice
        val newRevision = if (stage == StudyStage.REVISION) !revision else revision

        saveProgressState(
            chapterId = chapterId,
            subjectId = subjectId,
            branchId = branchId,
            theory = newTheory,
            practice = newPractice,
            revision = newRevision,
            previousCompletedAt = current?.completedAtMillis
        )
    }

    suspend fun setRevisionDone(chapterId: String, subjectId: String, branchId: String, done: Boolean) {
        val current = progressDao.getProgressById(chapterId)
        val theory = current?.theoryDone ?: false
        val practice = current?.practiceDone ?: false
        saveProgressState(
            chapterId = chapterId,
            subjectId = subjectId,
            branchId = branchId,
            theory = theory,
            practice = practice,
            revision = done,
            previousCompletedAt = current?.completedAtMillis
        )
    }

    private suspend fun saveProgressState(
        chapterId: String,
        subjectId: String,
        branchId: String,
        theory: Boolean,
        practice: Boolean,
        revision: Boolean,
        previousCompletedAt: Long?
    ) {
        val now = System.currentTimeMillis()
        val isNowComplete = theory && practice && revision
        val completedAt = if (isNowComplete) previousCompletedAt ?: now else null

        progressDao.upsertProgress(
            ChapterProgressEntity(
                chapterId = chapterId,
                subjectId = subjectId,
                branchId = branchId,
                theoryDone = theory,
                practiceDone = practice,
                revisionDone = revision,
                completedAtMillis = completedAt,
                updatedAtMillis = now
            )
        )
    }

    suspend fun resetAllProgress() {
        progressDao.resetAllProgress()
    }

    suspend fun setThemeMode(mode: ThemeMode) = settingsDataStore.setThemeMode(mode)
    suspend fun setAmoledBlack(enabled: Boolean) = settingsDataStore.setAmoledBlack(enabled)
    suspend fun setDynamicColor(enabled: Boolean) = settingsDataStore.setDynamicColor(enabled)
    suspend fun setAccentPreset(preset: AccentPreset) = settingsDataStore.setAccentPreset(preset)
    suspend fun setCardCornerRadius(radiusDp: Float) = settingsDataStore.setCardCornerRadius(radiusDp)
    suspend fun setFontScale(scale: Float) = settingsDataStore.setFontScale(scale)
    suspend fun setCardDensity(density: CardDensity) = settingsDataStore.setCardDensity(density)
    suspend fun setChapterLayout(layout: ChapterLayout) = settingsDataStore.setChapterLayout(layout)
    suspend fun setExamDateEpochDay(epochDay: Long) = settingsDataStore.setExamDateEpochDay(epochDay)
    suspend fun setBufferDays(days: Int) = settingsDataStore.setBufferDays(days)
}
