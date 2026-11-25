package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
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
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowForward
import androidx.compose.material.icons.rounded.AutoStories
import androidx.compose.material.icons.rounded.Star
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.data.model.CardDensity
import com.example.data.model.SubjectProgressSummary
import com.example.ui.components.AnimatedCounterText
import com.example.ui.components.AnimatedProgressRing
import com.example.ui.components.StaggeredEntrance
import com.example.ui.components.pressScale
import com.example.ui.components.subjectVisualFor
import com.example.ui.theme.JetBrainsMonoFontFamily
import com.example.ui.theme.LocalAppAppearance
import com.example.ui.theme.PriorityAmber
import com.example.ui.theme.StagePracticeColor
import com.example.ui.theme.StageRevisionColor
import com.example.ui.theme.StageTheoryColor

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun SyllabusScreen(
    overallSummary: SubjectProgressSummary,
    subjectSummaries: List<SubjectProgressSummary>,
    syllabusYear: Int,
    sharedTransitionScope: SharedTransitionScope,
    animatedVisibilityScope: AnimatedVisibilityScope,
    onSubjectClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val appearance = LocalAppAppearance.current
    val isCompact = appearance.cardDensity == CardDensity.COMPACT

    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.TopCenter
    ) {
        LazyColumn(
            modifier = Modifier
                .widthIn(max = 640.dp)
                .fillMaxSize(),
            contentPadding = PaddingValues(
                horizontal = 16.dp,
                vertical = if (isCompact) 10.dp else 16.dp
            ),
            verticalArrangement = Arrangement.spacedBy(appearance.listSpacing)
        ) {
            item(key = "hero_banner") {
                StaggeredEntrance(index = 0) {
                    OverallHeroCard(
                        overallSummary = overallSummary,
                        syllabusYear = syllabusYear
                    )
                }
            }

            item(key = "subjects_header") {
                StaggeredEntrance(index = 1) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 4.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Subjects",
                            style = MaterialTheme.typography.titleLarge,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                        Text(
                            text = "${overallSummary.importantCompletedCount}/${overallSummary.importantTotalCount} high-yield done",
                            style = MaterialTheme.typography.labelMedium.copy(
                                fontFamily = JetBrainsMonoFontFamily
                            ),
                            color = PriorityAmber
                        )
                    }
                }
            }

            itemsIndexed(subjectSummaries, key = { _, item -> item.subjectId }) { index, summary ->
                StaggeredEntrance(index = index + 2) {
                    SubjectLargeCard(
                        summary = summary,
                        sharedTransitionScope = sharedTransitionScope,
                        animatedVisibilityScope = animatedVisibilityScope,
                        onClick = { onSubjectClick(summary.subjectId) }
                    )
                }
            }
        }
    }
}

@Composable
private fun OverallHeroCard(
    overallSummary: SubjectProgressSummary,
    syllabusYear: Int
) {
    val appearance = LocalAppAppearance.current
    val isCompact = appearance.cardDensity == CardDensity.COMPACT
    val animatedProgress by animateFloatAsState(
        targetValue = overallSummary.progressFraction,
        animationSpec = spring(dampingRatio = 0.8f, stiffness = Spring.StiffnessLow),
        label = "overall_progress"
    )

    val gradientBrush = Brush.linearGradient(
        colors = listOf(
            MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.85f),
            MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.65f)
        )
    )

    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(appearance.cardCornerRadius),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.2f))
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(gradientBrush)
                .padding(appearance.cardPadding)
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(if (isCompact) 10.dp else 14.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.AutoStories,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onPrimaryContainer,
                                modifier = Modifier.size(18.dp)
                            )
                            Text(
                                text = "NTA JEE MAIN $syllabusYear",
                                style = MaterialTheme.typography.labelMedium.copy(
                                    fontFamily = JetBrainsMonoFontFamily,
                                    fontWeight = FontWeight.Bold
                                ),
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        AnimatedCounterText(
                            text = "${overallSummary.completedChapters}/${overallSummary.totalChapters} Chapters Complete",
                            style = MaterialTheme.typography.headlineSmall,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }

                    AnimatedProgressRing(
                        progress = overallSummary.progressFraction,
                        accentColor = MaterialTheme.colorScheme.primary,
                        size = if (isCompact) 58.dp else 66.dp
                    )
                }

                LinearProgressIndicator(
                    progress = { animatedProgress },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp)
                        .clip(RoundedCornerShape(4.dp)),
                    color = MaterialTheme.colorScheme.primary,
                    trackColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.45f),
                    strokeCap = StrokeCap.Round
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    HeroStatChip(
                        label = "Theory",
                        value = "${overallSummary.theoryDoneCount}/${overallSummary.totalChapters}",
                        color = StageTheoryColor,
                        modifier = Modifier.weight(1f)
                    )
                    HeroStatChip(
                        label = "Practice",
                        value = "${overallSummary.practiceDoneCount}/${overallSummary.totalChapters}",
                        color = StagePracticeColor,
                        modifier = Modifier.weight(1f)
                    )
                    HeroStatChip(
                        label = "Revision",
                        value = "${overallSummary.revisionDoneCount}/${overallSummary.totalChapters}",
                        color = StageRevisionColor,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
    }
}

@Composable
private fun HeroStatChip(
    label: String,
    value: String,
    color: Color,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        color = MaterialTheme.colorScheme.surface.copy(alpha = 0.72f),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            AnimatedCounterText(
                text = value,
                style = MaterialTheme.typography.titleSmall.copy(
                    fontFamily = JetBrainsMonoFontFamily,
                    fontWeight = FontWeight.Bold
                ),
                color = color
            )
        }
    }
}

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
private fun SubjectLargeCard(
    summary: SubjectProgressSummary,
    sharedTransitionScope: SharedTransitionScope,
    animatedVisibilityScope: AnimatedVisibilityScope,
    onClick: () -> Unit
) {
    val appearance = LocalAppAppearance.current
    val isCompact = appearance.cardDensity == CardDensity.COMPACT
    val visual = subjectVisualFor(summary.subjectId)
    val interactionSource = remember { MutableInteractionSource() }
    val animatedProgress by animateFloatAsState(
        targetValue = summary.progressFraction,
        animationSpec = spring(dampingRatio = 0.8f, stiffness = Spring.StiffnessLow),
        label = "subject_bar_${summary.subjectId}"
    )

    with(sharedTransitionScope) {
        Card(
            onClick = onClick,
            interactionSource = interactionSource,
            modifier = Modifier
                .sharedBounds(
                    sharedContentState = rememberSharedContentState(key = "subject_card_${summary.subjectId}"),
                    animatedVisibilityScope = animatedVisibilityScope,
                    resizeMode = SharedTransitionScope.ResizeMode.ScaleToBounds()
                )
                .pressScale(interactionSource)
                .fillMaxWidth()
                .testTag("subject_card_${summary.subjectId}"),
            shape = RoundedCornerShape(appearance.cardCornerRadius),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceContainerLow
            ),
            border = BorderStroke(
                width = 1.dp,
                color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.55f)
            )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(appearance.cardPadding),
                verticalArrangement = Arrangement.spacedBy(if (isCompact) 10.dp else 14.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Surface(
                        color = visual.accent.copy(alpha = 0.14f),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Box(
                            modifier = Modifier.size(if (isCompact) 48.dp else 54.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = visual.icon,
                                contentDescription = summary.subjectName,
                                tint = visual.accent,
                                modifier = Modifier.size(28.dp)
                            )
                        }
                    }

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = summary.subjectName,
                            style = MaterialTheme.typography.headlineSmall,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        AnimatedCounterText(
                            text = "${summary.completedChapters}/${summary.totalChapters} chapters done",
                            style = MaterialTheme.typography.bodyMedium.copy(
                                fontWeight = FontWeight.Medium
                            ),
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        if (summary.importantTotalCount > 0) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp),
                                modifier = Modifier.padding(top = 2.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Rounded.Star,
                                    contentDescription = "Important chapters",
                                    tint = PriorityAmber,
                                    modifier = Modifier.size(14.dp)
                                )
                                Text(
                                    text = "${summary.importantCompletedCount}/${summary.importantTotalCount} Important",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontFamily = JetBrainsMonoFontFamily,
                                        fontWeight = FontWeight.SemiBold
                                    ),
                                    color = PriorityAmber
                                )
                            }
                        }
                    }

                    AnimatedProgressRing(
                        progress = summary.progressFraction,
                        accentColor = visual.accent,
                        size = if (isCompact) 58.dp else 66.dp
                    )
                }

                LinearProgressIndicator(
                    progress = { animatedProgress },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(7.dp)
                        .clip(RoundedCornerShape(4.dp)),
                    color = visual.accent,
                    trackColor = MaterialTheme.colorScheme.surfaceVariant,
                    strokeCap = StrokeCap.Round
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "T: ${summary.theoryDoneCount}  •  P: ${summary.practiceDoneCount}  •  R: ${summary.revisionDoneCount}",
                        style = MaterialTheme.typography.labelMedium.copy(
                            fontFamily = JetBrainsMonoFontFamily
                        ),
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text(
                            text = "Open",
                            style = MaterialTheme.typography.labelLarge,
                            color = visual.accent
                        )
                        Icon(
                            imageVector = Icons.AutoMirrored.Rounded.ArrowForward,
                            contentDescription = null,
                            tint = visual.accent,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }
        }
    }
}
