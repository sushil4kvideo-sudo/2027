package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AutoAwesome
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.DoneAll
import androidx.compose.material.icons.rounded.PriorityHigh
import androidx.compose.material.icons.rounded.Star
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.data.model.CardDensity
import com.example.data.model.ChapterWithProgress
import com.example.data.model.StudyStage
import com.example.data.model.SubjectProgressSummary
import com.example.ui.components.AnimatedCounterText
import com.example.ui.components.ChapterInspectorOverlay
import com.example.ui.components.ImportantChapterBadge
import com.example.ui.components.MotionTokens
import com.example.ui.components.StaggeredEntrance
import com.example.ui.components.pressScale
import com.example.ui.components.subjectVisualFor
import com.example.ui.theme.JetBrainsMonoFontFamily
import com.example.ui.theme.LocalAppAppearance
import com.example.ui.theme.PriorityAmber
import com.example.ui.theme.StageRevisionColor

private enum class RevisionSubjectFilter(val id: String, val label: String) {
    ALL("all", "All"),
    PHYSICS("physics", "Physics"),
    CHEMISTRY("chemistry", "Chemistry"),
    MATHS("maths", "Maths")
}

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun RevisionScreen(
    chapters: List<ChapterWithProgress>,
    subjectSummaries: List<SubjectProgressSummary>,
    onMarkRevisionDone: (ChapterWithProgress, Boolean) -> Unit,
    onToggleStage: (ChapterWithProgress, StudyStage) -> Unit,
    modifier: Modifier = Modifier
) {
    val appearance = LocalAppAppearance.current
    val isCompact = appearance.cardDensity == CardDensity.COMPACT
    var selectedFilter by remember { mutableStateOf(RevisionSubjectFilter.ALL) }
    var importantOnly by remember { mutableStateOf(false) }
    var sheetChapterId by remember { mutableStateOf<String?>(null) }

    val filtered = remember(chapters, selectedFilter, importantOnly) {
        chapters.filter { ch ->
            (selectedFilter == RevisionSubjectFilter.ALL || ch.subjectId == selectedFilter.id) &&
                (!importantOnly || ch.isImportant)
        }
    }

    val priorityChapters = remember(filtered) {
        filtered.filter { it.isRevisionPriority }
    }
    val remainingUnrevised = remember(filtered) {
        filtered.filter { !it.revisionDone && !it.isRevisionPriority }
    }
    val revisedChapters = remember(filtered) {
        filtered.filter { it.revisionDone }
    }

    val sheetChapter = remember(chapters, sheetChapterId) {
        sheetChapterId?.let { id -> chapters.find { it.spec.id == id } }
    }

    Box(modifier = modifier.fillMaxSize()) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.TopCenter
        ) {
            LazyColumn(
                modifier = Modifier
                    .widthIn(max = 680.dp)
                    .fillMaxSize(),
                contentPadding = PaddingValues(
                    horizontal = 16.dp,
                    vertical = if (isCompact) 10.dp else 16.dp
                ),
                verticalArrangement = Arrangement.spacedBy(appearance.listSpacing)
            ) {
                item(key = "revision_subject_progress") {
                    StaggeredEntrance(index = 0) {
                        RevisionSubjectProgressCard(subjectSummaries = subjectSummaries)
                    }
                }

                item(key = "filter_chips") {
                    StaggeredEntrance(index = 1) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .horizontalScroll(rememberScrollState()),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            RevisionSubjectFilter.entries.forEach { filter ->
                                FilterChip(
                                    selected = selectedFilter == filter,
                                    onClick = { selectedFilter = filter },
                                    modifier = Modifier.testTag("filter_chip_${filter.id}"),
                                    label = { Text(filter.label) },
                                    leadingIcon = if (selectedFilter == filter) {
                                        {
                                            Icon(
                                                imageVector = Icons.Rounded.Check,
                                                contentDescription = null,
                                                modifier = Modifier.size(16.dp)
                                            )
                                        }
                                    } else null
                                )
                            }

                            FilterChip(
                                selected = importantOnly,
                                onClick = { importantOnly = !importantOnly },
                                modifier = Modifier.testTag("filter_chip_important"),
                                label = { Text("Important") },
                                leadingIcon = {
                                    Icon(
                                        imageVector = Icons.Rounded.Star,
                                        contentDescription = null,
                                        tint = if (importantOnly) PriorityAmber else MaterialTheme.colorScheme.onSurfaceVariant,
                                        modifier = Modifier.size(16.dp)
                                    )
                                },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = PriorityAmber.copy(alpha = 0.16f),
                                    selectedLabelColor = MaterialTheme.colorScheme.onSurface
                                )
                            )
                        }
                    }
                }

                if (priorityChapters.isEmpty() && remainingUnrevised.isEmpty()) {
                    item(key = "all_revised_empty") {
                        AllRevisedEmptyCard(subjectLabel = selectedFilter.label)
                    }
                }

                if (priorityChapters.isNotEmpty()) {
                    item(key = "priority_header") {
                        SectionHeaderRow(
                            title = "Ready for Revision (${priorityChapters.size})",
                            subtitle = "Theory & Practice complete",
                            badgeColor = PriorityAmber,
                            isPriority = true
                        )
                    }

                    items(priorityChapters, key = { "prio_${it.spec.id}" }) { chapter ->
                        RevisionChapterCard(
                            chapter = chapter,
                            isPriority = true,
                            onToggleRevision = { onMarkRevisionDone(chapter, !chapter.revisionDone) },
                            onOpenDetails = { sheetChapterId = chapter.spec.id },
                            modifier = Modifier.animateItem(
                                fadeInSpec = null,
                                fadeOutSpec = null,
                                placementSpec = spring(
                                    dampingRatio = 0.86f,
                                    stiffness = Spring.StiffnessMedium
                                )
                            )
                        )
                    }
                }

                if (remainingUnrevised.isNotEmpty()) {
                    item(key = "other_pending_header") {
                        SectionHeaderRow(
                            title = "Pending Revision (${remainingUnrevised.size})",
                            subtitle = "Complete Theory & Practice first or revise early",
                            badgeColor = MaterialTheme.colorScheme.secondary,
                            isPriority = false
                        )
                    }

                    items(remainingUnrevised, key = { "rem_${it.spec.id}" }) { chapter ->
                        RevisionChapterCard(
                            chapter = chapter,
                            isPriority = false,
                            onToggleRevision = { onMarkRevisionDone(chapter, !chapter.revisionDone) },
                            onOpenDetails = { sheetChapterId = chapter.spec.id },
                            modifier = Modifier.animateItem(
                                fadeInSpec = null,
                                fadeOutSpec = null,
                                placementSpec = spring(
                                    dampingRatio = 0.86f,
                                    stiffness = Spring.StiffnessMedium
                                )
                            )
                        )
                    }
                }

                if (revisedChapters.isNotEmpty()) {
                    item(key = "revised_header") {
                        SectionHeaderRow(
                            title = "Revised (${revisedChapters.size})",
                            subtitle = "Tap check to undo if re-studying",
                            badgeColor = StageRevisionColor,
                            isPriority = false
                        )
                    }

                    items(revisedChapters, key = { "done_${it.spec.id}" }) { chapter ->
                        RevisionChapterCard(
                            chapter = chapter,
                            isPriority = false,
                            onToggleRevision = { onMarkRevisionDone(chapter, !chapter.revisionDone) },
                            onOpenDetails = { sheetChapterId = chapter.spec.id },
                            modifier = Modifier.animateItem(
                                fadeInSpec = null,
                                fadeOutSpec = null,
                                placementSpec = spring(
                                    dampingRatio = 0.86f,
                                    stiffness = Spring.StiffnessMedium
                                )
                            )
                        )
                    }
                }
            }
        }

        AnimatedVisibility(
            visible = sheetChapter != null,
            enter = fadeIn(tween(220, easing = MotionTokens.EmphasizedDecelerate)) +
                scaleIn(
                    initialScale = 0.88f,
                    animationSpec = tween(300, easing = MotionTokens.EmphasizedDecelerate)
                ) +
                slideInVertically(
                    animationSpec = tween(300, easing = MotionTokens.EmphasizedDecelerate),
                    initialOffsetY = { it / 8 }
                ),
            exit = fadeOut(tween(180, easing = MotionTokens.EmphasizedAccelerate)) +
                scaleOut(
                    targetScale = 0.90f,
                    animationSpec = tween(180, easing = MotionTokens.EmphasizedAccelerate)
                ) +
                slideOutVertically(
                    animationSpec = tween(180, easing = MotionTokens.EmphasizedAccelerate),
                    targetOffsetY = { it / 10 }
                )
        ) {
            if (sheetChapter != null) {
                ChapterInspectorOverlay(
                    chapter = sheetChapter,
                    sharedKey = null,
                    sharedTransitionScope = null,
                    animatedVisibilityScope = null,
                    onToggleStage = { stage -> onToggleStage(sheetChapter, stage) },
                    onDismiss = { sheetChapterId = null }
                )
            }
        }
    }
}

@Composable
private fun RevisionSubjectProgressCard(
    subjectSummaries: List<SubjectProgressSummary>
) {
    val appearance = LocalAppAppearance.current
    val totalChapters = subjectSummaries.sumOf { it.totalChapters }
    val totalRevised = subjectSummaries.sumOf { it.revisionDoneCount }

    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(appearance.cardCornerRadius),
        color = MaterialTheme.colorScheme.surfaceContainerLow,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
    ) {
        Column(
            modifier = Modifier.padding(appearance.cardPadding),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Rounded.AutoAwesome,
                        contentDescription = null,
                        tint = StageRevisionColor
                    )
                    Text(
                        text = "Revision Progress",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
                AnimatedCounterText(
                    text = "$totalRevised/$totalChapters revised",
                    style = MaterialTheme.typography.labelLarge.copy(
                        fontFamily = JetBrainsMonoFontFamily
                    ),
                    color = StageRevisionColor
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                subjectSummaries.forEach { summary ->
                    val visual = subjectVisualFor(summary.subjectId)
                    val animFrac by animateFloatAsState(
                        targetValue = summary.revisionFraction,
                        animationSpec = spring(dampingRatio = 0.8f, stiffness = Spring.StiffnessLow),
                        label = "rev_bar_${summary.subjectId}"
                    )
                    Surface(
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp),
                        color = MaterialTheme.colorScheme.surfaceContainer
                    ) {
                        Column(
                            modifier = Modifier.padding(10.dp),
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Text(
                                text = summary.subjectName,
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            LinearProgressIndicator(
                                progress = { animFrac },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(6.dp)
                                    .clip(RoundedCornerShape(3.dp)),
                                color = visual.accent,
                                trackColor = MaterialTheme.colorScheme.surfaceVariant,
                                strokeCap = StrokeCap.Round
                            )
                            AnimatedCounterText(
                                text = "${summary.revisionDoneCount}/${summary.totalChapters}",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontFamily = JetBrainsMonoFontFamily,
                                    fontWeight = FontWeight.Bold
                                ),
                                color = visual.accent
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun SectionHeaderRow(
    title: String,
    subtitle: String,
    badgeColor: Color,
    isPriority: Boolean
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        if (isPriority) {
            Surface(
                color = badgeColor.copy(alpha = 0.16f),
                shape = CircleShape
            ) {
                Icon(
                    imageVector = Icons.Rounded.PriorityHigh,
                    contentDescription = "Top priority",
                    tint = badgeColor,
                    modifier = Modifier
                        .padding(4.dp)
                        .size(16.dp)
                )
            }
        }
        Column {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun RevisionChapterCard(
    chapter: ChapterWithProgress,
    isPriority: Boolean,
    onToggleRevision: () -> Unit,
    onOpenDetails: () -> Unit,
    modifier: Modifier = Modifier
) {
    val appearance = LocalAppAppearance.current
    val visual = subjectVisualFor(chapter.subjectId)
    val interactionSource = remember { MutableInteractionSource() }
    val bgColor by animateColorAsState(
        targetValue = when {
            chapter.revisionDone -> StageRevisionColor.copy(alpha = 0.08f)
            isPriority -> PriorityAmber.copy(alpha = 0.10f)
            else -> MaterialTheme.colorScheme.surfaceContainerLow
        },
        animationSpec = tween(250),
        label = "rev_card_bg"
    )
    val borderColor = when {
        chapter.revisionDone -> StageRevisionColor.copy(alpha = 0.45f)
        isPriority -> PriorityAmber.copy(alpha = 0.6f)
        chapter.isImportant -> PriorityAmber.copy(alpha = 0.42f)
        else -> MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)
    }
    val checkScale by animateFloatAsState(
        targetValue = if (chapter.revisionDone) 1.08f else 1f,
        animationSpec = spring(dampingRatio = 0.5f, stiffness = Spring.StiffnessMedium),
        label = "rev_check_scale"
    )

    Card(
        onClick = onOpenDetails,
        interactionSource = interactionSource,
        modifier = modifier
            .pressScale(interactionSource)
            .fillMaxWidth()
            .testTag("revision_card_${chapter.spec.id}"),
        shape = RoundedCornerShape(appearance.cardCornerRadius),
        colors = CardDefaults.cardColors(containerColor = bgColor),
        border = BorderStroke(if (isPriority) 1.5.dp else 1.dp, borderColor)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = appearance.cardPadding, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(5.dp)
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        color = visual.accent.copy(alpha = 0.14f),
                        shape = RoundedCornerShape(6.dp)
                    ) {
                        Text(
                            text = "${chapter.subjectName} • U${chapter.spec.unitNumber}",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontFamily = JetBrainsMonoFontFamily,
                                fontWeight = FontWeight.Bold
                            ),
                            color = visual.accent,
                            modifier = Modifier.padding(horizontal = 7.dp, vertical = 3.dp)
                        )
                    }
                    if (chapter.isImportant) {
                        ImportantChapterBadge(
                            tier = chapter.spec.importanceTier,
                            weightageTag = chapter.spec.weightageTag,
                            compact = true
                        )
                    }
                    if (isPriority) {
                        Surface(
                            color = PriorityAmber.copy(alpha = 0.18f),
                            shape = RoundedCornerShape(6.dp)
                        ) {
                            Text(
                                text = "PRIORITY",
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                color = PriorityAmber,
                                modifier = Modifier.padding(horizontal = 7.dp, vertical = 3.dp)
                            )
                        }
                    }
                }

                Text(
                    text = chapter.spec.name,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )

                Text(
                    text = buildString {
                        append("Theory: ${if (chapter.theoryDone) "Done" else "Pending"}")
                        append("  •  ")
                        append("Practice: ${if (chapter.practiceDone) "Done" else "Pending"}")
                    },
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            IconButton(
                onClick = onToggleRevision,
                modifier = Modifier
                    .size(48.dp)
                    .testTag("revision_toggle_${chapter.spec.id}")
            ) {
                Box(
                    modifier = Modifier
                        .size(34.dp)
                        .graphicsLayer {
                            scaleX = checkScale
                            scaleY = checkScale
                        }
                        .clip(CircleShape)
                        .background(
                            if (chapter.revisionDone) StageRevisionColor
                            else MaterialTheme.colorScheme.surfaceVariant
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Rounded.Check,
                        contentDescription = if (chapter.revisionDone) "Mark revision pending" else "Mark revision done",
                        tint = if (chapter.revisionDone) Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun AllRevisedEmptyCard(subjectLabel: String) {
    val appearance = LocalAppAppearance.current
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(appearance.cardCornerRadius),
        color = StageRevisionColor.copy(alpha = 0.10f),
        border = BorderStroke(1.dp, StageRevisionColor.copy(alpha = 0.4f))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Icon(
                imageVector = Icons.Rounded.DoneAll,
                contentDescription = null,
                tint = StageRevisionColor,
                modifier = Modifier.size(36.dp)
            )
            Text(
                text = "All $subjectLabel chapters revised!",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = "Great discipline. You can still review or toggle any chapter below.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
