package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Bolt
import androidx.compose.material.icons.rounded.Bookmark
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.Functions
import androidx.compose.material.icons.rounded.Science
import androidx.compose.material.icons.rounded.Star
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.data.model.CardDensity
import com.example.data.model.ChapterWithProgress
import com.example.data.model.ImportanceTier
import com.example.ui.theme.ChemistryColor
import com.example.ui.theme.JetBrainsMonoFontFamily
import com.example.ui.theme.LocalAppAppearance
import com.example.ui.theme.MathsColor
import com.example.ui.theme.PhysicsColor
import com.example.ui.theme.PriorityAmber
import com.example.ui.theme.StagePracticeColor
import com.example.ui.theme.StageRevisionColor
import com.example.ui.theme.StageTheoryColor
import kotlin.math.roundToInt

data class SubjectVisual(
    val icon: ImageVector,
    val accent: Color,
    val tagline: String
)

fun subjectVisualFor(subjectId: String): SubjectVisual = when (subjectId) {
    "physics" -> SubjectVisual(
        icon = Icons.Rounded.Bolt,
        accent = PhysicsColor,
        tagline = "Mechanics, Electrodynamics & Modern"
    )
    "chemistry" -> SubjectVisual(
        icon = Icons.Rounded.Science,
        accent = ChemistryColor,
        tagline = "Physical, Organic & Inorganic"
    )
    else -> SubjectVisual(
        icon = Icons.Rounded.Functions,
        accent = MathsColor,
        tagline = "Algebra, Calculus & Geometry"
    )
}

@Composable
fun ImportantChapterBadge(
    tier: ImportanceTier?,
    weightageTag: String?,
    compact: Boolean = false
) {
    val (bgColor, fgColor, icon, defaultLabel) = when (tier) {
        ImportanceTier.ONE_SHOT -> Quadruple(
            Color(0xFF0EA5E9).copy(alpha = 0.14f),
            Color(0xFF0284C7),
            Icons.Rounded.Bolt,
            "One Shot"
        )
        ImportanceTier.KEY_CHAPTER -> Quadruple(
            PriorityAmber.copy(alpha = 0.14f),
            PriorityAmber,
            Icons.Rounded.Bookmark,
            "Must Do"
        )
        else -> Quadruple(
            PriorityAmber.copy(alpha = 0.16f),
            PriorityAmber,
            Icons.Rounded.Star,
            "Important"
        )
    }
    val text = buildString {
        if (tier == ImportanceTier.HIGH_WEIGHTAGE) append("IMP")
        else if (tier == ImportanceTier.KEY_CHAPTER) append("IMP")
        else append("ONE SHOT")
        if (!weightageTag.isNullOrBlank() && weightageTag != "One Shot") {
            append(" • ")
            append(weightageTag)
        }
    }

    Surface(
        color = bgColor,
        shape = RoundedCornerShape(8.dp),
        border = BorderStroke(0.75.dp, fgColor.copy(alpha = 0.4f))
    ) {
        Row(
            modifier = Modifier.padding(
                horizontal = if (compact) 6.dp else 8.dp,
                vertical = if (compact) 3.dp else 4.dp
            ),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(3.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = defaultLabel,
                tint = fgColor,
                modifier = Modifier.size(if (compact) 11.dp else 13.dp)
            )
            Text(
                text = text,
                style = MaterialTheme.typography.labelSmall.copy(
                    fontFamily = JetBrainsMonoFontFamily,
                    fontWeight = FontWeight.Bold
                ),
                color = fgColor,
                maxLines = 1
            )
        }
    }
}

private data class Quadruple<A, B, C, D>(val first: A, val second: B, val third: C, val fourth: D)

@Composable
fun AnimatedProgressRing(
    progress: Float,
    accentColor: Color,
    modifier: Modifier = Modifier,
    size: Dp = 68.dp,
    strokeWidth: Dp = 7.dp
) {
    val animatedProgress by animateFloatAsState(
        targetValue = progress.coerceIn(0f, 1f),
        animationSpec = spring(
            dampingRatio = 0.8f,
            stiffness = Spring.StiffnessLow
        ),
        label = "ring_progress"
    )
    val trackColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.65f)
    val pct = (animatedProgress * 100f).roundToInt()

    Box(
        modifier = modifier
            .size(size)
            .semantics { contentDescription = "$pct percent complete" },
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.size(size)) {
            val strokePx = strokeWidth.toPx()
            val inset = strokePx / 2f
            val arcSize = Size(this.size.width - strokePx, this.size.height - strokePx)
            drawArc(
                color = trackColor,
                startAngle = -90f,
                sweepAngle = 360f,
                useCenter = false,
                topLeft = Offset(inset, inset),
                size = arcSize,
                style = Stroke(width = strokePx, cap = StrokeCap.Round)
            )
            if (animatedProgress > 0f) {
                drawArc(
                    color = accentColor,
                    startAngle = -90f,
                    sweepAngle = 360f * animatedProgress,
                    useCenter = false,
                    topLeft = Offset(inset, inset),
                    size = arcSize,
                    style = Stroke(width = strokePx, cap = StrokeCap.Round)
                )
            }
        }
        AnimatedCounterText(
            text = "$pct%",
            style = MaterialTheme.typography.labelLarge.copy(
                fontFamily = JetBrainsMonoFontFamily,
                fontWeight = FontWeight.Bold
            ),
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}

@Composable
fun StagePillIndicator(
    label: String,
    done: Boolean,
    activeColor: Color,
    compact: Boolean = false
) {
    val bgColor by animateColorAsState(
        targetValue = if (done) activeColor.copy(alpha = 0.16f) else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f),
        animationSpec = tween(220),
        label = "stage_pill_bg"
    )
    val contentColor by animateColorAsState(
        targetValue = if (done) activeColor else MaterialTheme.colorScheme.onSurfaceVariant,
        animationSpec = tween(220),
        label = "stage_pill_fg"
    )
    val checkScale by animateFloatAsState(
        targetValue = if (done) 1f else 0.82f,
        animationSpec = spring(dampingRatio = 0.55f, stiffness = Spring.StiffnessMedium),
        label = "stage_check_scale"
    )

    Surface(
        color = bgColor,
        shape = RoundedCornerShape(10.dp),
        border = if (done) BorderStroke(1.dp, activeColor.copy(alpha = 0.45f)) else null
    ) {
        Row(
            modifier = Modifier.padding(
                horizontal = if (compact) 7.dp else 9.dp,
                vertical = if (compact) 4.dp else 5.dp
            ),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(if (compact) 14.dp else 16.dp)
                    .graphicsLayer {
                        scaleX = checkScale
                        scaleY = checkScale
                    }
                    .clip(CircleShape)
                    .background(if (done) activeColor else MaterialTheme.colorScheme.outline.copy(alpha = 0.25f)),
                contentAlignment = Alignment.Center
            ) {
                androidx.compose.animation.AnimatedVisibility(
                    visible = done,
                    enter = scaleIn(spring(dampingRatio = 0.5f)) + fadeIn(),
                    exit = scaleOut() + fadeOut()
                ) {
                    Icon(
                        imageVector = Icons.Rounded.Check,
                        contentDescription = "$label done",
                        tint = Color.White,
                        modifier = Modifier.size(if (compact) 10.dp else 11.dp)
                    )
                }
            }
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall.copy(
                    fontWeight = if (done) FontWeight.SemiBold else FontWeight.Medium
                ),
                color = contentColor,
                maxLines = 1
            )
        }
    }
}

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun ChapterHorizontalCard(
    chapter: ChapterWithProgress,
    subjectAccent: Color,
    sharedKey: String,
    sharedTransitionScope: SharedTransitionScope?,
    animatedVisibilityScope: AnimatedVisibilityScope?,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val appearance = LocalAppAppearance.current
    val isCompact = appearance.cardDensity == CardDensity.COMPACT
    val interactionSource = remember { MutableInteractionSource() }

    val containerColor by animateColorAsState(
        targetValue = when {
            chapter.isComplete -> MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.42f)
            chapter.isImportant -> MaterialTheme.colorScheme.surfaceContainerLow
            else -> MaterialTheme.colorScheme.surfaceContainerLow
        },
        animationSpec = tween(250),
        label = "chapter_card_bg"
    )
    val borderColor by animateColorAsState(
        targetValue = when {
            chapter.isComplete -> subjectAccent.copy(alpha = 0.6f)
            chapter.isImportant -> PriorityAmber.copy(alpha = 0.55f)
            else -> MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)
        },
        animationSpec = tween(250),
        label = "chapter_card_border"
    )

    val sharedMod = if (sharedTransitionScope != null && animatedVisibilityScope != null) {
        with(sharedTransitionScope) {
            Modifier.sharedBounds(
                sharedContentState = rememberSharedContentState(key = sharedKey),
                animatedVisibilityScope = animatedVisibilityScope,
                resizeMode = SharedTransitionScope.ResizeMode.ScaleToBounds()
            )
        }
    } else Modifier

    Card(
        onClick = onClick,
        interactionSource = interactionSource,
        modifier = modifier
            .then(sharedMod)
            .pressScale(interactionSource)
            .width(if (isCompact) 252.dp else 280.dp)
            .testTag("chapter_card_${chapter.spec.id}"),
        shape = RoundedCornerShape(appearance.cardCornerRadius),
        colors = CardDefaults.cardColors(containerColor = containerColor),
        border = BorderStroke(if (chapter.isComplete || chapter.isImportant) 1.4.dp else 1.dp, borderColor)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(appearance.cardPadding),
            verticalArrangement = Arrangement.spacedBy(if (isCompact) 10.dp else 12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        color = subjectAccent.copy(alpha = 0.14f),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            text = "U${chapter.spec.unitNumber.toString().padStart(2, '0')}",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontFamily = JetBrainsMonoFontFamily,
                                fontWeight = FontWeight.Bold
                            ),
                            color = subjectAccent,
                            modifier = Modifier.padding(horizontal = 7.dp, vertical = 4.dp)
                        )
                    }
                    if (chapter.isImportant) {
                        ImportantChapterBadge(
                            tier = chapter.spec.importanceTier,
                            weightageTag = chapter.spec.weightageTag,
                            compact = true
                        )
                    }
                }

                if (chapter.isComplete) {
                    Icon(
                        imageVector = Icons.Rounded.CheckCircle,
                        contentDescription = "Chapter complete",
                        tint = subjectAccent,
                        modifier = Modifier.size(20.dp)
                    )
                } else {
                    Text(
                        text = "${chapter.stagesCompletedCount}/3",
                        style = MaterialTheme.typography.labelMedium.copy(
                            fontFamily = JetBrainsMonoFontFamily
                        ),
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(
                    text = chapter.spec.name,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.height(if (isCompact) 42.dp else 46.dp)
                )
                Text(
                    text = "${chapter.spec.topics.size} official topics",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                StagePillIndicator(
                    label = "Theory",
                    done = chapter.theoryDone,
                    activeColor = StageTheoryColor,
                    compact = true
                )
                StagePillIndicator(
                    label = "Practice",
                    done = chapter.practiceDone,
                    activeColor = StagePracticeColor,
                    compact = true
                )
                StagePillIndicator(
                    label = "Rev",
                    done = chapter.revisionDone,
                    activeColor = StageRevisionColor,
                    compact = true
                )
            }
        }
    }
}

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun ChapterVerticalRow(
    chapter: ChapterWithProgress,
    subjectAccent: Color,
    sharedKey: String,
    sharedTransitionScope: SharedTransitionScope?,
    animatedVisibilityScope: AnimatedVisibilityScope?,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val appearance = LocalAppAppearance.current
    val isCompact = appearance.cardDensity == CardDensity.COMPACT
    val interactionSource = remember { MutableInteractionSource() }

    val containerColor by animateColorAsState(
        targetValue = if (chapter.isComplete) {
            MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.36f)
        } else {
            MaterialTheme.colorScheme.surfaceContainerLow
        },
        animationSpec = tween(250),
        label = "chapter_row_bg"
    )
    val borderColor by animateColorAsState(
        targetValue = when {
            chapter.isComplete -> subjectAccent.copy(alpha = 0.6f)
            chapter.isImportant -> PriorityAmber.copy(alpha = 0.55f)
            else -> MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)
        },
        animationSpec = tween(250),
        label = "chapter_row_border"
    )

    val sharedMod = if (sharedTransitionScope != null && animatedVisibilityScope != null) {
        with(sharedTransitionScope) {
            Modifier.sharedBounds(
                sharedContentState = rememberSharedContentState(key = sharedKey),
                animatedVisibilityScope = animatedVisibilityScope,
                resizeMode = SharedTransitionScope.ResizeMode.ScaleToBounds()
            )
        }
    } else Modifier

    Card(
        onClick = onClick,
        interactionSource = interactionSource,
        modifier = modifier
            .then(sharedMod)
            .pressScale(interactionSource)
            .fillMaxWidth()
            .testTag("chapter_card_${chapter.spec.id}"),
        shape = RoundedCornerShape(appearance.cardCornerRadius),
        colors = CardDefaults.cardColors(containerColor = containerColor),
        border = BorderStroke(if (chapter.isComplete || chapter.isImportant) 1.4.dp else 1.dp, borderColor)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(appearance.cardPadding),
            verticalArrangement = Arrangement.spacedBy(if (isCompact) 8.dp else 11.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Surface(
                    color = subjectAccent.copy(alpha = 0.14f),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text(
                        text = chapter.spec.unitNumber.toString().padStart(2, '0'),
                        style = MaterialTheme.typography.labelLarge.copy(
                            fontFamily = JetBrainsMonoFontFamily,
                            fontWeight = FontWeight.Bold
                        ),
                        color = subjectAccent,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                    )
                }

                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = chapter.spec.name,
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurface,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        if (chapter.isImportant) {
                            ImportantChapterBadge(
                                tier = chapter.spec.importanceTier,
                                weightageTag = chapter.spec.weightageTag,
                                compact = true
                            )
                        }
                        Text(
                            text = "${chapter.spec.topics.size} topics",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                if (chapter.isComplete) {
                    Icon(
                        imageVector = Icons.Rounded.CheckCircle,
                        contentDescription = "Complete",
                        tint = subjectAccent,
                        modifier = Modifier.size(22.dp)
                    )
                }
            }

            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                StagePillIndicator(
                    label = "Theory",
                    done = chapter.theoryDone,
                    activeColor = StageTheoryColor,
                    compact = isCompact
                )
                StagePillIndicator(
                    label = "Practice",
                    done = chapter.practiceDone,
                    activeColor = StagePracticeColor,
                    compact = isCompact
                )
                StagePillIndicator(
                    label = "Revision",
                    done = chapter.revisionDone,
                    activeColor = StageRevisionColor,
                    compact = isCompact
                )
            }
        }
    }
}
