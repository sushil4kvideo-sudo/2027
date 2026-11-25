package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.Event
import androidx.compose.material.icons.rounded.Speed
import androidx.compose.material.icons.rounded.TrendingDown
import androidx.compose.material.icons.rounded.TrendingUp
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.data.model.CardDensity
import com.example.ui.CountdownState
import com.example.ui.PaceStatus
import com.example.ui.SubjectPaceMetric
import com.example.ui.TimeCounterUiState
import com.example.ui.components.AnimatedCounterText
import com.example.ui.components.StaggeredEntrance
import com.example.ui.components.subjectVisualFor
import com.example.ui.theme.JetBrainsMonoFontFamily
import com.example.ui.theme.LocalAppAppearance
import com.example.ui.theme.PriorityAmber
import com.example.ui.theme.StageRevisionColor
import java.time.format.DateTimeFormatter
import java.util.Locale

private val DATE_FMT = DateTimeFormatter.ofPattern("MMM dd, yyyy", Locale.getDefault())

@Composable
fun TimeCounterScreen(
    uiState: TimeCounterUiState,
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
                .widthIn(max = 680.dp)
                .fillMaxSize(),
            contentPadding = PaddingValues(
                horizontal = 16.dp,
                vertical = if (isCompact) 10.dp else 16.dp
            ),
            verticalArrangement = Arrangement.spacedBy(appearance.listSpacing)
        ) {
            item(key = "countdown_card") {
                StaggeredEntrance(index = 0) {
                    LiveCountdownCard(countdown = uiState.countdown)
                }
            }

            item(key = "required_pace_hero") {
                StaggeredEntrance(index = 1) {
                    RequiredPaceHeroCard(
                        countdown = uiState.countdown,
                        overallPace = uiState.overallPace
                    )
                }
            }

            item(key = "actual_vs_required") {
                StaggeredEntrance(index = 2) {
                    ActualVsRequiredCard(overallPace = uiState.overallPace)
                }
            }

            item(key = "subject_paces_header") {
                StaggeredEntrance(index = 3) {
                    Text(
                        text = "Subject Pace Breakdown",
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.onBackground,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }
            }

            itemsIndexed(uiState.subjectPaces, key = { _, metric -> metric.subjectId }) { idx, metric ->
                StaggeredEntrance(index = idx + 4) {
                    SubjectPaceCard(metric = metric)
                }
            }
        }
    }
}

@Composable
private fun LiveCountdownCard(countdown: CountdownState) {
    val appearance = LocalAppAppearance.current
    val gradient = Brush.linearGradient(
        colors = listOf(
            MaterialTheme.colorScheme.primaryContainer,
            MaterialTheme.colorScheme.tertiaryContainer.copy(alpha = 0.85f)
        )
    )

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("live_countdown_card"),
        shape = RoundedCornerShape(appearance.cardCornerRadius),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.25f))
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(gradient)
                .padding(appearance.cardPadding)
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.Event,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onPrimaryContainer,
                            modifier = Modifier.size(18.dp)
                        )
                        Text(
                            text = "JEE MAIN SESSION 1",
                            style = MaterialTheme.typography.labelMedium.copy(
                                fontFamily = JetBrainsMonoFontFamily,
                                fontWeight = FontWeight.Bold
                            ),
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }
                    Surface(
                        color = MaterialTheme.colorScheme.surface.copy(alpha = 0.72f),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text(
                            text = countdown.examDate.format(DATE_FMT),
                            style = MaterialTheme.typography.labelMedium.copy(
                                fontWeight = FontWeight.SemiBold
                            ),
                            color = MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                        )
                    }
                }

                if (countdown.examPassed) {
                    Text(
                        text = "Exam date reached",
                        style = MaterialTheme.typography.headlineMedium,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                } else {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        CountdownDigitBox(
                            value = countdown.daysRemaining.toString(),
                            unit = "DAYS",
                            modifier = Modifier.weight(1.25f)
                        )
                        CountdownDigitBox(
                            value = countdown.hoursRemaining.toString().padStart(2, '0'),
                            unit = "HOURS",
                            modifier = Modifier.weight(1f)
                        )
                        CountdownDigitBox(
                            value = countdown.minutesRemaining.toString().padStart(2, '0'),
                            unit = "MINS",
                            modifier = Modifier.weight(1f)
                        )
                        CountdownDigitBox(
                            value = countdown.secondsRemaining.toString().padStart(2, '0'),
                            unit = "SECS",
                            modifier = Modifier.weight(1f)
                        )
                    }
                }

                Text(
                    text = "Target syllabus finish: ${countdown.targetFinishDate.format(DATE_FMT)} (${countdown.bufferDays}d revision buffer)",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.85f)
                )
            }
        }
    }
}

@Composable
private fun CountdownDigitBox(
    value: String,
    unit: String,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        color = MaterialTheme.colorScheme.surface.copy(alpha = 0.78f),
        shape = RoundedCornerShape(14.dp)
    ) {
        Column(
            modifier = Modifier.padding(vertical = 10.dp, horizontal = 6.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            AnimatedCounterText(
                text = value,
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontFamily = JetBrainsMonoFontFamily,
                    fontWeight = FontWeight.Bold
                ),
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = unit,
                style = MaterialTheme.typography.labelSmall.copy(
                    fontFamily = JetBrainsMonoFontFamily
                ),
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun RequiredPaceHeroCard(
    countdown: CountdownState,
    overallPace: SubjectPaceMetric
) {
    val appearance = LocalAppAppearance.current
    val requiredText = String.format(Locale.US, "%.2f", overallPace.requiredPerDay)
    val daysLeftDisplay = countdown.daysUntilTargetFinish.coerceAtLeast(0)

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("required_pace_card"),
        shape = RoundedCornerShape(appearance.cardCornerRadius),
        color = MaterialTheme.colorScheme.surfaceContainerLow,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.55f))
    ) {
        Column(
            modifier = Modifier.padding(appearance.cardPadding),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    imageVector = Icons.Rounded.Speed,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = "Target Daily Pace",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }

            if (overallPace.remainingChapters == 0) {
                Text(
                    text = "Syllabus complete! All ${overallPace.totalChapters} chapters are done.",
                    style = MaterialTheme.typography.headlineSmall,
                    color = StageRevisionColor
                )
            } else {
                Text(
                    text = "To finish the syllabus you need $requiredText chapters/day",
                    style = MaterialTheme.typography.headlineSmall,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = if (countdown.daysUntilTargetFinish > 0) {
                        "${overallPace.remainingChapters} remaining chapters ÷ $daysLeftDisplay days left before buffer"
                    } else {
                        "${overallPace.remainingChapters} remaining chapters (buffer period started — finish immediately)"
                    },
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
private fun ActualVsRequiredCard(overallPace: SubjectPaceMetric) {
    val appearance = LocalAppAppearance.current
    val actualText = String.format(Locale.US, "%.2f", overallPace.actualPerDay)
    val requiredText = String.format(Locale.US, "%.2f", overallPace.requiredPerDay)

    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(appearance.cardCornerRadius),
        color = MaterialTheme.colorScheme.surfaceContainerLow,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.55f))
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
                Text(
                    text = "Last 7 Days Pace",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
                PaceStatusBadge(status = overallPace.status)
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Surface(
                    modifier = Modifier.weight(1f),
                    color = MaterialTheme.colorScheme.surfaceContainer,
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(12.dp),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text(
                            text = "Actual Pace",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        AnimatedCounterText(
                            text = "$actualText ch/day",
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontFamily = JetBrainsMonoFontFamily,
                                fontWeight = FontWeight.Bold
                            ),
                            color = MaterialTheme.colorScheme.primary
                        )
                        Text(
                            text = "${overallPace.completedLast7Days} chapters in 7d",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Surface(
                    modifier = Modifier.weight(1f),
                    color = MaterialTheme.colorScheme.surfaceContainer,
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(12.dp),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text(
                            text = "Required Pace",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        AnimatedCounterText(
                            text = "$requiredText ch/day",
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontFamily = JetBrainsMonoFontFamily,
                                fontWeight = FontWeight.Bold
                            ),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "${overallPace.remainingChapters} chapters left",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun SubjectPaceCard(metric: SubjectPaceMetric) {
    val appearance = LocalAppAppearance.current
    val visual = subjectVisualFor(metric.subjectId)
    val requiredText = String.format(Locale.US, "%.2f", metric.requiredPerDay)
    val actualText = String.format(Locale.US, "%.2f", metric.actualPerDay)

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("pace_card_${metric.subjectId}"),
        shape = RoundedCornerShape(appearance.cardCornerRadius),
        color = MaterialTheme.colorScheme.surfaceContainerLow,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(appearance.cardPadding),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Surface(
                color = visual.accent.copy(alpha = 0.14f),
                shape = RoundedCornerShape(14.dp)
            ) {
                Box(
                    modifier = Modifier.size(46.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = visual.icon,
                        contentDescription = metric.subjectName,
                        tint = visual.accent,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = metric.subjectName,
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    PaceStatusBadge(status = metric.status)
                }

                Text(
                    text = "Need $requiredText ch/day  •  Actual $actualText ch/day",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontFamily = JetBrainsMonoFontFamily
                    ),
                    color = MaterialTheme.colorScheme.onSurface
                )

                Text(
                    text = "${metric.remainingChapters} of ${metric.totalChapters} remaining  •  ${metric.completedLast7Days} done last 7d",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
private fun PaceStatusBadge(status: PaceStatus) {
    val (label, color, icon) = when (status) {
        PaceStatus.COMPLETED -> Triple("Completed", StageRevisionColor, Icons.Rounded.CheckCircle)
        PaceStatus.ON_TRACK -> Triple("On track", StageRevisionColor, Icons.Rounded.TrendingUp)
        PaceStatus.BEHIND -> Triple("Behind", PriorityAmber, Icons.Rounded.TrendingDown)
    }

    Surface(
        color = color.copy(alpha = 0.16f),
        shape = RoundedCornerShape(20.dp)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = color,
                modifier = Modifier.size(15.dp)
            )
            Text(
                text = label,
                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                color = color
            )
        }
    }
}
