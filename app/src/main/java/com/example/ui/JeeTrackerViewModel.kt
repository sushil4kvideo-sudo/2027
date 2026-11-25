package com.example.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.data.model.AccentPreset
import com.example.data.model.CardDensity
import com.example.data.model.ChapterLayout
import com.example.data.model.ChapterWithProgress
import com.example.data.model.StudyStage
import com.example.data.model.SubjectProgressSummary
import com.example.data.model.SyllabusData
import com.example.data.model.ThemeMode
import com.example.data.model.UserSettings
import com.example.data.repository.JeeTrackerRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.Duration
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.temporal.ChronoUnit

enum class PaceStatus {
    COMPLETED,
    ON_TRACK,
    BEHIND
}

data class SubjectPaceMetric(
    val subjectId: String,
    val subjectName: String,
    val totalChapters: Int,
    val completedChapters: Int,
    val remainingChapters: Int,
    val completedLast7Days: Int,
    val requiredPerDay: Double,
    val actualPerDay: Double,
    val status: PaceStatus
)

data class CountdownState(
    val daysRemaining: Long,
    val hoursRemaining: Long,
    val minutesRemaining: Long,
    val secondsRemaining: Long,
    val examPassed: Boolean,
    val examDate: LocalDate,
    val targetFinishDate: LocalDate,
    val bufferDays: Int,
    val daysUntilTargetFinish: Long
)

data class TimeCounterUiState(
    val countdown: CountdownState,
    val overallPace: SubjectPaceMetric,
    val subjectPaces: List<SubjectPaceMetric>
)

class JeeTrackerViewModel(
    private val repository: JeeTrackerRepository
) : ViewModel() {

    val syllabusData: SyllabusData = repository.syllabusData

    val settings: StateFlow<UserSettings> = repository.settingsFlow.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = UserSettings()
    )

    val chapters: StateFlow<List<ChapterWithProgress>> = repository.chaptersFlow.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    val subjectSummaries: StateFlow<List<SubjectProgressSummary>> = chapters.map { allChapters ->
        val subjectOrder = listOf("physics", "chemistry", "maths")
        subjectOrder.mapNotNull { subId ->
            val group = allChapters.filter { it.subjectId == subId }
            if (group.isEmpty()) return@mapNotNull null
            SubjectProgressSummary(
                subjectId = subId,
                subjectName = group.first().subjectName,
                totalChapters = group.size,
                completedChapters = group.count { it.isComplete },
                theoryDoneCount = group.count { it.theoryDone },
                practiceDoneCount = group.count { it.practiceDone },
                revisionDoneCount = group.count { it.revisionDone },
                importantTotalCount = group.count { it.isImportant },
                importantCompletedCount = group.count { it.isImportant && it.isComplete }
            )
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    val overallSummary: StateFlow<SubjectProgressSummary> = chapters.map { allChapters ->
        SubjectProgressSummary(
            subjectId = "all",
            subjectName = "Overall",
            totalChapters = allChapters.size,
            completedChapters = allChapters.count { it.isComplete },
            theoryDoneCount = allChapters.count { it.theoryDone },
            practiceDoneCount = allChapters.count { it.practiceDone },
            revisionDoneCount = allChapters.count { it.revisionDone },
            importantTotalCount = allChapters.count { it.isImportant },
            importantCompletedCount = allChapters.count { it.isImportant && it.isComplete }
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = SubjectProgressSummary("all", "Overall", 54, 0, 0, 0, 0, 0, 0)
    )

    private val tickerFlow = flow {
        while (true) {
            emit(System.currentTimeMillis())
            delay(1000L)
        }
    }

    val timeCounterState: StateFlow<TimeCounterUiState> = combine(
        chapters,
        settings,
        tickerFlow
    ) { allChapters, currentSettings, nowMillis ->
        val zone = ZoneId.systemDefault()
        val nowDateTime = LocalDateTime.now(zone)
        val today = nowDateTime.toLocalDate()
        val examDate = currentSettings.examDate
        val examStartDateTime = examDate.atStartOfDay()
        val duration = Duration.between(nowDateTime, examStartDateTime)
        val examPassed = duration.isNegative

        val safeDuration = if (examPassed) Duration.ZERO else duration
        val totalHours = safeDuration.toHours()
        val daysRem = totalHours / 24
        val hoursRem = totalHours % 24
        val minsRem = safeDuration.toMinutes() % 60
        val secsRem = safeDuration.seconds % 60

        val targetFinishDate = examDate.minusDays(currentSettings.bufferDays.toLong())
        val daysUntilTarget = ChronoUnit.DAYS.between(today, targetFinishDate)

        val countdown = CountdownState(
            daysRemaining = daysRem,
            hoursRemaining = hoursRem,
            minutesRemaining = minsRem,
            secondsRemaining = secsRem,
            examPassed = examPassed,
            examDate = examDate,
            targetFinishDate = targetFinishDate,
            bufferDays = currentSettings.bufferDays,
            daysUntilTargetFinish = daysUntilTarget
        )

        val sevenDaysAgoMillis = nowMillis - (7L * 24L * 60L * 60L * 1000L)

        fun computePace(id: String, name: String, list: List<ChapterWithProgress>): SubjectPaceMetric {
            val total = list.size
            val completed = list.count { it.isComplete }
            val remaining = (total - completed).coerceAtLeast(0)
            val completedLast7Days = list.count {
                it.completedAtMillis != null && it.completedAtMillis >= sevenDaysAgoMillis
            }
            val actualPerDay = completedLast7Days / 7.0
            val requiredPerDay = when {
                remaining == 0 -> 0.0
                daysUntilTarget <= 0 -> remaining.toDouble()
                else -> remaining.toDouble() / daysUntilTarget.toDouble()
            }
            val status = when {
                remaining == 0 -> PaceStatus.COMPLETED
                daysUntilTarget <= 0 -> PaceStatus.BEHIND
                actualPerDay >= requiredPerDay -> PaceStatus.ON_TRACK
                else -> PaceStatus.BEHIND
            }
            return SubjectPaceMetric(
                subjectId = id,
                subjectName = name,
                totalChapters = total,
                completedChapters = completed,
                remainingChapters = remaining,
                completedLast7Days = completedLast7Days,
                requiredPerDay = requiredPerDay,
                actualPerDay = actualPerDay,
                status = status
            )
        }

        val overallPace = computePace("all", "Overall Syllabus", allChapters)
        val subjectOrder = listOf("physics" to "Physics", "chemistry" to "Chemistry", "maths" to "Maths")
        val subjectPaces = subjectOrder.map { (id, name) ->
            computePace(id, name, allChapters.filter { it.subjectId == id })
        }

        TimeCounterUiState(
            countdown = countdown,
            overallPace = overallPace,
            subjectPaces = subjectPaces
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = TimeCounterUiState(
            countdown = CountdownState(
                daysRemaining = 0,
                hoursRemaining = 0,
                minutesRemaining = 0,
                secondsRemaining = 0,
                examPassed = false,
                examDate = UserSettings.DEFAULT_EXAM_DATE,
                targetFinishDate = UserSettings.DEFAULT_EXAM_DATE.minusDays(UserSettings.DEFAULT_BUFFER_DAYS.toLong()),
                bufferDays = UserSettings.DEFAULT_BUFFER_DAYS,
                daysUntilTargetFinish = 0
            ),
            overallPace = SubjectPaceMetric("all", "Overall Syllabus", 54, 0, 54, 0, 0.0, 0.0, PaceStatus.BEHIND),
            subjectPaces = emptyList()
        )
    )

    fun toggleChapterStage(chapter: ChapterWithProgress, stage: StudyStage) {
        viewModelScope.launch {
            repository.toggleStage(
                chapterId = chapter.spec.id,
                subjectId = chapter.subjectId,
                branchId = chapter.branchId,
                stage = stage
            )
        }
    }

    fun setRevisionDone(chapter: ChapterWithProgress, done: Boolean) {
        viewModelScope.launch {
            repository.setRevisionDone(
                chapterId = chapter.spec.id,
                subjectId = chapter.subjectId,
                branchId = chapter.branchId,
                done = done
            )
        }
    }

    fun resetAllProgress() {
        viewModelScope.launch {
            repository.resetAllProgress()
        }
    }

    fun setThemeMode(mode: ThemeMode) = viewModelScope.launch { repository.setThemeMode(mode) }
    fun setAmoledBlack(enabled: Boolean) = viewModelScope.launch { repository.setAmoledBlack(enabled) }
    fun setDynamicColor(enabled: Boolean) = viewModelScope.launch { repository.setDynamicColor(enabled) }
    fun setAccentPreset(preset: AccentPreset) = viewModelScope.launch { repository.setAccentPreset(preset) }
    fun setCardCornerRadius(radiusDp: Float) = viewModelScope.launch { repository.setCardCornerRadius(radiusDp) }
    fun setFontScale(scale: Float) = viewModelScope.launch { repository.setFontScale(scale) }
    fun setCardDensity(density: CardDensity) = viewModelScope.launch { repository.setCardDensity(density) }
    fun setChapterLayout(layout: ChapterLayout) = viewModelScope.launch { repository.setChapterLayout(layout) }
    fun setExamDateEpochDay(epochDay: Long) = viewModelScope.launch { repository.setExamDateEpochDay(epochDay) }
    fun setBufferDays(days: Int) = viewModelScope.launch { repository.setBufferDays(days) }

    class Factory(private val repository: JeeTrackerRepository) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return JeeTrackerViewModel(repository) as T
        }
    }
}
