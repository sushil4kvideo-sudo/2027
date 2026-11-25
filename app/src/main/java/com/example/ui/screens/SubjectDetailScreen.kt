package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
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
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Star
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.PrimaryTabRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.data.model.ChapterLayout
import com.example.data.model.ChapterWithProgress
import com.example.data.model.StudyStage
import com.example.ui.components.AnimatedCounterText
import com.example.ui.components.AnimatedProgressRing
import com.example.ui.components.ChapterHorizontalCard
import com.example.ui.components.ChapterInspectorOverlay
import com.example.ui.components.ChapterVerticalRow
import com.example.ui.components.MotionTokens
import com.example.ui.components.subjectVisualFor
import com.example.ui.theme.JetBrainsMonoFontFamily
import com.example.ui.theme.LocalAppAppearance
import com.example.ui.theme.PriorityAmber
import kotlinx.coroutines.launch
import kotlin.math.absoluteValue

@OptIn(ExperimentalMaterial3Api::class, ExperimentalSharedTransitionApi::class)
@Composable
fun SubjectDetailScreen(
    subjectId: String,
    allChapters: List<ChapterWithProgress>,
    sharedTransitionScope: SharedTransitionScope,
    animatedVisibilityScope: AnimatedVisibilityScope,
    onToggleStage: (ChapterWithProgress, StudyStage) -> Unit,
    modifier: Modifier = Modifier
) {
    val subjectChapters = remember(allChapters, subjectId) {
        allChapters.filter { it.subjectId == subjectId }
    }
    val visual = subjectVisualFor(subjectId)
    val appearance = LocalAppAppearance.current
    var selectedChapterId by remember { mutableStateOf<String?>(null) }
    var importantOnly by remember { mutableStateOf(false) }

    val selectedChapter = remember(subjectChapters, selectedChapterId) {
        selectedChapterId?.let { id -> subjectChapters.find { it.spec.id == id } }
    }

    val displayedChapters = remember(subjectChapters, importantOnly) {
        if (importantOnly) subjectChapters.filter { it.isImportant } else subjectChapters
    }

    val branches = remember(subjectId) {
        if (subjectId == "chemistry") {
            listOf("physical" to "Physical", "organic" to "Organic", "inorganic" to "Inorganic")
        } else {
            listOf("all" to "All Units")
        }
    }

    val completedCount = subjectChapters.count { it.isComplete }
    val totalCount = subjectChapters.size
    val importantCount = subjectChapters.count { it.isImportant }
    val subjectFraction = if (totalCount == 0) 0f else completedCount.toFloat() / totalCount.toFloat()

    Box(modifier = modifier.fillMaxSize()) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.TopCenter
        ) {
            Column(
                modifier = Modifier
                    .widthIn(max = 720.dp)
                    .fillMaxSize()
            ) {
                SubjectDetailHeader(
                    subjectId = subjectId,
                    subjectName = subjectChapters.firstOrNull()?.subjectName
                        ?: subjectId.replaceFirstChar { it.uppercase() },
                    completedCount = completedCount,
                    totalCount = totalCount,
                    theoryCount = subjectChapters.count { it.theoryDone },
                    practiceCount = subjectChapters.count { it.practiceDone },
                    revisionCount = subjectChapters.count { it.revisionDone },
                    fraction = subjectFraction,
                    accentColor = visual.accent,
                    sharedTransitionScope = sharedTransitionScope,
                    animatedVisibilityScope = animatedVisibilityScope
                )

                // All vs Important filter chips
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 4.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    FilterChip(
                        selected = !importantOnly,
                        onClick = { importantOnly = false },
                        modifier = Modifier.testTag("subject_filter_all"),
                        label = { Text("All Units ($totalCount)") }
                    )
                    FilterChip(
                        selected = importantOnly,
                        onClick = { importantOnly = true },
                        modifier = Modifier.testTag("subject_filter_important"),
                        label = { Text("Important ($importantCount)") },
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

                if (subjectId == "chemistry") {
                    val pagerState = rememberPagerState(pageCount = { branches.size })
                    val scope = rememberCoroutineScope()

                    PrimaryTabRow(
                        selectedTabIndex = pagerState.currentPage,
                        containerColor = MaterialTheme.colorScheme.surface
                    ) {
                        branches.forEachIndexed { index, (branchId, branchName) ->
                            val branchList = displayedChapters.filter { it.branchId == branchId }
                            val branchDone = branchList.count { it.isComplete }
                            Tab(
                                selected = pagerState.currentPage == index,
                                onClick = {
                                    scope.launch { pagerState.animateScrollToPage(index) }
                                },
                                modifier = Modifier.testTag("chemistry_tab_$branchId"),
                                text = {
                                    Text(
                                        text = "$branchName ($branchDone/${branchList.size})",
                                        maxLines = 1
                                    )
                                }
                            )
                        }
                    }

                    HorizontalPager(
                        state = pagerState,
                        modifier = Modifier.weight(1f)
                    ) { page ->
                        val pageOffset = (
                            (pagerState.currentPage - page) + pagerState.currentPageOffsetFraction
                            ).absoluteValue.coerceIn(0f, 1f)
                        val branchId = branches[page].first
                        val branchChapters = displayedChapters.filter { it.branchId == branchId }

                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .graphicsLayer {
                                    val scale = 1f - (0.04f * pageOffset)
                                    scaleX = scale
                                    scaleY = scale
                                    alpha = 1f - (0.28f * pageOffset)
                                }
                        ) {
                            ChapterCollectionContent(
                                chapters = branchChapters,
                                subjectAccent = visual.accent,
                                chapterLayout = appearance.chapterLayout,
                                onChapterClick = { selectedChapterId = it.spec.id }
                            )
                        }
                    }
                } else {
                    ChapterCollectionContent(
                        chapters = displayedChapters,
                        subjectAccent = visual.accent,
                        chapterLayout = appearance.chapterLayout,
                        onChapterClick = { selectedChapterId = it.spec.id },
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }

        // Clean spring-animated card open inspector overlay
        AnimatedVisibility(
            visible = selectedChapter != null,
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
            if (selectedChapter != null) {
                ChapterInspectorOverlay(
                    chapter = selectedChapter,
                    sharedKey = null,
                    sharedTransitionScope = null,
                    animatedVisibilityScope = null,
                    onToggleStage = { stage -> onToggleStage(selectedChapter, stage) },
                    onDismiss = { selectedChapterId = null }
                )
            }
        }
    }
}

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
private fun SubjectDetailHeader(
    subjectId: String,
    subjectName: String,
    completedCount: Int,
    totalCount: Int,
    theoryCount: Int,
    practiceCount: Int,
    revisionCount: Int,
    fraction: Float,
    accentColor: androidx.compose.ui.graphics.Color,
    sharedTransitionScope: SharedTransitionScope,
    animatedVisibilityScope: AnimatedVisibilityScope
) {
    val appearance = LocalAppAppearance.current
    val animatedProgress by animateFloatAsState(
        targetValue = fraction,
        animationSpec = spring(dampingRatio = 0.8f, stiffness = Spring.StiffnessLow),
        label = "detail_header_progress"
    )

    with(sharedTransitionScope) {
        Surface(
            modifier = Modifier
                .sharedBounds(
                    sharedContentState = rememberSharedContentState(key = "subject_card_$subjectId"),
                    animatedVisibilityScope = animatedVisibilityScope,
                    resizeMode = SharedTransitionScope.ResizeMode.ScaleToBounds()
                )
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            shape = RoundedCornerShape(appearance.cardCornerRadius),
            color = MaterialTheme.colorScheme.surfaceContainerLow
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
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "$subjectName Progress",
                            style = MaterialTheme.typography.titleLarge,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        AnimatedCounterText(
                            text = "$completedCount of $totalCount chapters complete",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "Theory $theoryCount/$totalCount  •  Practice $practiceCount/$totalCount  •  Rev $revisionCount/$totalCount",
                            style = MaterialTheme.typography.labelMedium.copy(
                                fontFamily = JetBrainsMonoFontFamily
                            ),
                            color = accentColor
                        )
                    }

                    AnimatedProgressRing(
                        progress = fraction,
                        accentColor = accentColor,
                        size = 62.dp
                    )
                }

                LinearProgressIndicator(
                    progress = { animatedProgress },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(6.dp)
                        .clip(RoundedCornerShape(3.dp)),
                    color = accentColor,
                    trackColor = MaterialTheme.colorScheme.surfaceVariant,
                    strokeCap = StrokeCap.Round
                )
            }
        }
    }
}

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
private fun ChapterCollectionContent(
    chapters: List<ChapterWithProgress>,
    subjectAccent: androidx.compose.ui.graphics.Color,
    chapterLayout: ChapterLayout,
    onChapterClick: (ChapterWithProgress) -> Unit,
    modifier: Modifier = Modifier
) {
    val appearance = LocalAppAppearance.current

    if (chapterLayout == ChapterLayout.HORIZONTAL_CARDS) {
        LazyColumn(
            modifier = modifier.fillMaxSize(),
            contentPadding = PaddingValues(vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            item(key = "horizontal_hint") {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Chapters (${chapters.size})",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "Swipe & tap card to open",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            item(key = "horizontal_deck") {
                LazyRow(
                    contentPadding = PaddingValues(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.testTag("chapters_horizontal_row")
                ) {
                    items(chapters, key = { it.spec.id }) { chapter ->
                        ChapterHorizontalCard(
                            chapter = chapter,
                            subjectAccent = subjectAccent,
                            sharedKey = "deck_${chapter.spec.id}",
                            sharedTransitionScope = null,
                            animatedVisibilityScope = null,
                            onClick = { onChapterClick(chapter) },
                            modifier = Modifier.animateItem(
                                fadeInSpec = tween(220),
                                fadeOutSpec = tween(180),
                                placementSpec = spring(stiffness = Spring.StiffnessMediumLow)
                            )
                        )
                    }
                }
            }

            item(key = "all_units_label") {
                Text(
                    text = "Quick Index",
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 2.dp)
                )
            }

            items(chapters, key = { "index_${it.spec.id}" }) { chapter ->
                ChapterVerticalRow(
                    chapter = chapter,
                    subjectAccent = subjectAccent,
                    sharedKey = "index_${chapter.spec.id}",
                    sharedTransitionScope = null,
                    animatedVisibilityScope = null,
                    onClick = { onChapterClick(chapter) },
                    modifier = Modifier
                        .padding(horizontal = 16.dp)
                        .animateItem(
                            fadeInSpec = tween(220),
                            fadeOutSpec = tween(180),
                            placementSpec = spring(stiffness = Spring.StiffnessMediumLow)
                        )
                )
            }
        }
    } else {
        LazyColumn(
            modifier = modifier
                .fillMaxSize()
                .testTag("chapters_vertical_list"),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(appearance.listSpacing)
        ) {
            item(key = "vertical_hint") {
                Text(
                    text = "All Chapters (${chapters.size})",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }

            items(chapters, key = { it.spec.id }) { chapter ->
                ChapterVerticalRow(
                    chapter = chapter,
                    subjectAccent = subjectAccent,
                    sharedKey = "vert_${chapter.spec.id}",
                    sharedTransitionScope = null,
                    animatedVisibilityScope = null,
                    onClick = { onChapterClick(chapter) },
                    modifier = Modifier.animateItem(
                        fadeInSpec = tween(220),
                        fadeOutSpec = tween(180),
                        placementSpec = spring(stiffness = Spring.StiffnessMediumLow)
                    )
                )
            }
        }
    }
}
