package com.example.ui.components

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AutoAwesome
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.EditNote
import androidx.compose.material.icons.rounded.KeyboardArrowDown
import androidx.compose.material.icons.rounded.KeyboardArrowUp
import androidx.compose.material.icons.rounded.MenuBook
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.data.model.ChapterWithProgress
import com.example.data.model.StudyStage
import com.example.ui.theme.JetBrainsMonoFontFamily
import com.example.ui.theme.LocalAppAppearance
import com.example.ui.theme.StagePracticeColor
import com.example.ui.theme.StageRevisionColor
import com.example.ui.theme.StageTheoryColor

/**
 * Morphing Card-Open Overlay using SharedTransitionScope.sharedBounds for seamless
 * card-to-inspector container transform animation.
 */
@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun ChapterInspectorOverlay(
    chapter: ChapterWithProgress,
    sharedKey: String?,
    sharedTransitionScope: SharedTransitionScope?,
    animatedVisibilityScope: AnimatedVisibilityScope?,
    onToggleStage: (StudyStage) -> Unit,
    onDismiss: () -> Unit
) {
    BackHandler(onBack = onDismiss)

    val visual = subjectVisualFor(chapter.subjectId)
    val appearance = LocalAppAppearance.current
    var topicsExpanded by remember(chapter.spec.id) { mutableStateOf(false) }

    val progressFraction by animateFloatAsState(
        targetValue = chapter.stagesCompletedCount / 3f,
        animationSpec = spring(dampingRatio = 0.78f, stiffness = Spring.StiffnessLow),
        label = "inspector_progress"
    )

    val sharedCardModifier = if (
        sharedKey != null &&
        sharedTransitionScope != null &&
        animatedVisibilityScope != null
    ) {
        with(sharedTransitionScope) {
            Modifier.sharedBounds(
                sharedContentState = rememberSharedContentState(key = sharedKey),
                animatedVisibilityScope = animatedVisibilityScope,
                resizeMode = SharedTransitionScope.ResizeMode.ScaleToBounds()
            )
        }
    } else Modifier

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.46f))
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onDismiss
            )
            .statusBarsPadding()
            .navigationBarsPadding()
            .padding(horizontal = 16.dp, vertical = 20.dp),
        contentAlignment = Alignment.BottomCenter
    ) {
        Surface(
            modifier = sharedCardModifier
                .widthIn(max = 560.dp)
                .fillMaxWidth()
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null,
                    onClick = {}
                )
                .animateContentSize(
                    animationSpec = spring(
                        dampingRatio = 0.82f,
                        stiffness = Spring.StiffnessMediumLow
                    )
                ),
            shape = RoundedCornerShape((appearance.cardCornerRadius + 6.dp).coerceAtMost(30.dp)),
            color = MaterialTheme.colorScheme.surfaceContainerLow,
            tonalElevation = 6.dp,
            shadowElevation = 12.dp,
            border = BorderStroke(
                width = 1.dp,
                color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)
            )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 20.dp, vertical = 18.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // Header row with unit, importance, and close button
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.weight(1f)
                    ) {
                        Surface(
                            color = visual.accent.copy(alpha = 0.14f),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text(
                                text = "${chapter.subjectName} • Unit ${chapter.spec.unitNumber}",
                                style = MaterialTheme.typography.labelMedium.copy(
                                    fontFamily = JetBrainsMonoFontFamily,
                                    fontWeight = FontWeight.Bold
                                ),
                                color = visual.accent,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                            )
                        }
                        if (chapter.isImportant) {
                            ImportantChapterBadge(
                                tier = chapter.spec.importanceTier,
                                weightageTag = chapter.spec.weightageTag,
                                compact = false
                            )
                        }
                    }

                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier
                            .size(40.dp)
                            .testTag("close_inspector_button")
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.Close,
                            contentDescription = "Close chapter details",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = chapter.spec.name,
                        style = MaterialTheme.typography.headlineSmall,
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.weight(1f)
                    )

                    AnimatedVisibility(
                        visible = chapter.isComplete,
                        enter = scaleIn(spring(dampingRatio = 0.6f)) + fadeIn(),
                        exit = scaleOut() + fadeOut()
                    ) {
                        Surface(
                            color = StageRevisionColor.copy(alpha = 0.16f),
                            shape = RoundedCornerShape(20.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Rounded.CheckCircle,
                                    contentDescription = "Chapter completed",
                                    tint = StageRevisionColor,
                                    modifier = Modifier.size(16.dp)
                                )
                                Text(
                                    text = "Complete",
                                    style = MaterialTheme.typography.labelMedium,
                                    color = StageRevisionColor
                                )
                            }
                        }
                    }
                }

                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text(
                        text = if (chapter.isComplete) {
                            "All 3 milestones complete — counted toward syllabus finish"
                        } else {
                            "${chapter.stagesCompletedCount}/3 milestones done (all 3 required to complete)"
                        },
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    LinearProgressIndicator(
                        progress = { progressFraction },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(8.dp)
                            .clip(RoundedCornerShape(4.dp)),
                        color = if (chapter.isComplete) StageRevisionColor else visual.accent,
                        trackColor = MaterialTheme.colorScheme.surfaceVariant,
                        strokeCap = StrokeCap.Round
                    )
                }

                // 3 Stage Toggles
                StageToggleRow(
                    title = "Theory",
                    subtitle = "NCERT concepts, derivations & class notes",
                    icon = Icons.Rounded.MenuBook,
                    checked = chapter.theoryDone,
                    accentColor = StageTheoryColor,
                    cornerRadius = appearance.cardCornerRadius,
                    testTag = "toggle_theory",
                    onToggle = { onToggleStage(StudyStage.THEORY) }
                )

                StageToggleRow(
                    title = "Practice",
                    subtitle = "Solved examples, DPPs & JEE Main PYQs",
                    icon = Icons.Rounded.EditNote,
                    checked = chapter.practiceDone,
                    accentColor = StagePracticeColor,
                    cornerRadius = appearance.cardCornerRadius,
                    testTag = "toggle_practice",
                    onToggle = { onToggleStage(StudyStage.PRACTICE) }
                )

                StageToggleRow(
                    title = "Revision",
                    subtitle = "Formula recall, error log & timed review",
                    icon = Icons.Rounded.AutoAwesome,
                    checked = chapter.revisionDone,
                    accentColor = StageRevisionColor,
                    cornerRadius = appearance.cardCornerRadius,
                    testTag = "toggle_revision",
                    onToggle = { onToggleStage(StudyStage.REVISION) }
                )

                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))

                // Official NTA Topics accordion
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Official NTA Topics (${chapter.spec.topics.size})",
                        style = MaterialTheme.typography.titleSmall,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    TextButton(
                        onClick = { topicsExpanded = !topicsExpanded },
                        modifier = Modifier.testTag("toggle_topics_button")
                    ) {
                        Text(if (topicsExpanded) "Hide" else "View")
                        Icon(
                            imageVector = if (topicsExpanded) Icons.Rounded.KeyboardArrowUp else Icons.Rounded.KeyboardArrowDown,
                            contentDescription = null
                        )
                    }
                }

                AnimatedVisibility(
                    visible = topicsExpanded,
                    enter = expandVertically(tween(260, easing = MotionTokens.EmphasizedDecelerate)) + fadeIn(),
                    exit = shrinkVertically(tween(200)) + fadeOut()
                ) {
                    Column(
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.padding(bottom = 4.dp)
                    ) {
                        chapter.spec.topics.forEach { topic ->
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(10.dp),
                                verticalAlignment = Alignment.Top
                            ) {
                                Box(
                                    modifier = Modifier
                                        .padding(top = 7.dp)
                                        .size(6.dp)
                                        .clip(CircleShape)
                                        .background(visual.accent)
                                )
                                Text(
                                    text = topic,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun StageToggleRow(
    title: String,
    subtitle: String,
    icon: ImageVector,
    checked: Boolean,
    accentColor: Color,
    cornerRadius: Dp,
    testTag: String,
    onToggle: () -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    val bgColor by animateColorAsState(
        targetValue = if (checked) accentColor.copy(alpha = 0.14f) else MaterialTheme.colorScheme.surfaceContainer,
        animationSpec = tween(220),
        label = "toggle_row_bg"
    )
    val borderColor by animateColorAsState(
        targetValue = if (checked) accentColor.copy(alpha = 0.55f) else MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.45f),
        animationSpec = tween(220),
        label = "toggle_row_border"
    )
    val iconScale by animateFloatAsState(
        targetValue = if (checked) 1.06f else 1f,
        animationSpec = spring(dampingRatio = 0.5f, stiffness = Spring.StiffnessMedium),
        label = "toggle_icon_scale"
    )

    Surface(
        onClick = onToggle,
        interactionSource = interactionSource,
        modifier = Modifier
            .pressScale(interactionSource, pressedScale = 0.98f)
            .fillMaxWidth()
            .testTag(testTag),
        shape = RoundedCornerShape(cornerRadius),
        color = bgColor,
        border = BorderStroke(1.dp, borderColor)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(42.dp)
                    .graphicsLayer {
                        scaleX = iconScale
                        scaleY = iconScale
                    }
                    .clip(RoundedCornerShape(12.dp))
                    .background(if (checked) accentColor else MaterialTheme.colorScheme.surfaceVariant),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = if (checked) Icons.Rounded.Check else icon,
                    contentDescription = title,
                    tint = if (checked) Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(22.dp)
                )
            }

            Column(modifier = Modifier.weight(1f)) {
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

            Switch(
                checked = checked,
                onCheckedChange = { onToggle() }
            )
        }
    }
}
