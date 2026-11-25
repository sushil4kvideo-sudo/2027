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
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AutoStories
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
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.data.model.AccentPreset
import com.example.data.model.CardDensity
import com.example.data.model.SubjectProgressSummary
import com.example.ui.components.AnimatedCounterText
import com.example.ui.components.AnimatedProgressRing
import com.example.ui.components.StaggeredEntrance
import com.example.ui.components.pressScale
import com.example.ui.theme.LocalAppAppearance
import com.example.ui.theme.StagePracticeColor
import com.example.ui.theme.StageRevisionColor
import com.example.ui.theme.StageTheoryColor

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun SyllabusScreen(
    overallSummary: SubjectProgressSummary,
    subjectSummaries: List<SubjectProgressSummary>,
    syllabusYear: Int,
    accentPreset: AccentPreset,
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
                    Text(
                        text = "Subjects",
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 4.dp),
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                }
            }

            itemsIndexed(subjectSummaries, key = { _, item -> item.subjectId }) { index, summary ->
                StaggeredEntrance(index = index + 2) {
                    SubjectLargeCard(
                        summary = summary,
                        accentPreset = accentPreset,
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
            MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.9f),
            MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.72f)
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
                        Text(
                            text = "NTA JEE MAIN $syllabusYear",
                            style = MaterialTheme.typography.labelMedium.copy(
                                fontWeight = FontWeight.Bold
                            ),
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                        Spacer(modifier = Modifier.height(5.dp))
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
                        .height(7.dp)
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
                    fontWeight = FontWeight.Bold
                ),
                color = color
            )
        }
    }
}

private data class SubjectCardStyle(
    val gradientColors: List<Color>,
    val foreground: Color,
    val progress: Color,
    val progressTrack: Color,
    val border: Color
)

@Composable
private fun subjectCardStyle(
    subjectId: String,
    accentPreset: AccentPreset
): SubjectCardStyle {
    val presetHsv = FloatArray(3)
    android.graphics.Color.colorToHSV(accentPreset.previewHex.toInt(), presetHsv)

    val subjectHue = when (subjectId) {
        "physics" -> 205f
        "chemistry" -> 153f
        "maths" -> 34f
        else -> 250f
    }
    val hueOffset = presetHsv[0] - 242f
    val hue = ((subjectHue + hueOffset + 360f) % 360f)
    val nextHue = (hue + 16f) % 360f
    val darkSurface = MaterialTheme.colorScheme.background.luminance() < 0.5f

    return if (darkSurface) {
        SubjectCardStyle(
            gradientColors = listOf(
                Color.hsv(hue, 0.72f, 0.43f),
                Color.hsv(nextHue, 0.78f, 0.32f)
            ),
            foreground = Color(0xFFF8FAFC),
            progress = Color.hsv(hue, 0.22f, 1f),
            progressTrack = Color.White.copy(alpha = 0.22f),
            border = Color.hsv(hue, 0.42f, 0.72f).copy(alpha = 0.42f)
        )
    } else {
        SubjectCardStyle(
            gradientColors = listOf(
                Color.hsv(hue, 0.34f, 0.98f),
                Color.hsv(nextHue, 0.46f, 0.88f)
            ),
            foreground = Color.hsv(hue, 0.78f, 0.24f),
            progress = Color.hsv(hue, 0.78f, 0.58f),
            progressTrack = Color.White.copy(alpha = 0.72f),
            border = Color.hsv(hue, 0.48f, 0.72f).copy(alpha = 0.5f)
        )
    }
}

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
private fun SubjectLargeCard(
    summary: SubjectProgressSummary,
    accentPreset: AccentPreset,
    sharedTransitionScope: SharedTransitionScope,
    animatedVisibilityScope: AnimatedVisibilityScope,
    onClick: () -> Unit
) {
    val appearance = LocalAppAppearance.current
    val isCompact = appearance.cardDensity == CardDensity.COMPACT
    val interactionSource = remember { MutableInteractionSource() }
    val style = subjectCardStyle(summary.subjectId, accentPreset)
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
            colors = CardDefaults.cardColors(containerColor = style.gradientColors.first()),
            border = BorderStroke(1.dp, style.border)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Brush.linearGradient(style.gradientColors))
                    .padding(
                        horizontal = appearance.cardPadding + 4.dp,
                        vertical = if (isCompact) 17.dp else 21.dp
                    ),
                verticalArrangement = Arrangement.spacedBy(if (isCompact) 9.dp else 12.dp)
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                    Text(
                        text = summary.subjectName,
                        style = MaterialTheme.typography.headlineMedium.copy(
                            fontWeight = FontWeight.SemiBold
                        ),
                        color = style.foreground
                    )
                    AnimatedCounterText(
                        text = "${summary.completedChapters} / ${summary.totalChapters} chapters",
                        style = MaterialTheme.typography.bodyLarge,
                        color = style.foreground.copy(alpha = 0.9f)
                    )
                }

                LinearProgressIndicator(
                    progress = { animatedProgress },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(5.dp)
                        .clip(RoundedCornerShape(4.dp)),
                    color = style.progress,
                    trackColor = style.progressTrack,
                    strokeCap = StrokeCap.Round
                )
            }
        }
    }
}
