package com.example.ui.screens

import android.os.Build
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.CalendarMonth
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.DeleteForever
import androidx.compose.material.icons.rounded.Info
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Slider
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.data.model.AccentPreset
import com.example.data.model.CardDensity
import com.example.data.model.ChapterLayout
import com.example.data.model.SyllabusData
import com.example.data.model.ThemeMode
import com.example.data.model.UserSettings
import com.example.ui.theme.JetBrainsMonoFontFamily
import com.example.ui.theme.LocalAppAppearance
import java.time.Instant
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.util.Locale
import kotlin.math.roundToInt

private val SETTINGS_DATE_FMT = DateTimeFormatter.ofPattern("MMMM dd, yyyy", Locale.getDefault())

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    settings: UserSettings,
    syllabusData: SyllabusData,
    onThemeModeChange: (ThemeMode) -> Unit,
    onAmoledBlackChange: (Boolean) -> Unit,
    onDynamicColorChange: (Boolean) -> Unit,
    onAccentPresetChange: (AccentPreset) -> Unit,
    onCornerRadiusChange: (Float) -> Unit,
    onFontScaleChange: (Float) -> Unit,
    onCardDensityChange: (CardDensity) -> Unit,
    onChapterLayoutChange: (ChapterLayout) -> Unit,
    onExamDateEpochDayChange: (Long) -> Unit,
    onBufferDaysChange: (Int) -> Unit,
    onResetProgress: () -> Unit,
    modifier: Modifier = Modifier
) {
    val appearance = LocalAppAppearance.current
    var showDatePicker by remember { mutableStateOf(false) }
    var showResetDialog by remember { mutableStateOf(false) }

    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.TopCenter
    ) {
        LazyColumn(
            modifier = Modifier
                .widthIn(max = 640.dp)
                .fillMaxSize(),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Appearance Section
            item(key = "appearance_section") {
                SettingsGroupCard(title = "Theme & Colors") {
                    Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                        Text(
                            text = "Theme Mode",
                            style = MaterialTheme.typography.titleSmall,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        SingleChoiceSegmentedButtonRow(modifier = Modifier.fillMaxWidth()) {
                            ThemeMode.entries.forEachIndexed { index, mode ->
                                SegmentedButton(
                                    selected = settings.themeMode == mode,
                                    onClick = { onThemeModeChange(mode) },
                                    shape = SegmentedButtonDefaults.itemShape(
                                        index = index,
                                        count = ThemeMode.entries.size
                                    ),
                                    modifier = Modifier.testTag("theme_mode_${mode.name.lowercase()}")
                                ) {
                                    Text(mode.label)
                                }
                            }
                        }

                        SettingsSwitchRow(
                            title = "AMOLED Pure Black",
                            subtitle = "Use true #000000 surfaces in dark theme",
                            checked = settings.amoledBlack,
                            testTag = "switch_amoled",
                            onCheckedChange = onAmoledBlackChange
                        )

                        SettingsSwitchRow(
                            title = "Dynamic Color (Material You)",
                            subtitle = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                                "Match your wallpaper palette automatically"
                            } else {
                                "Requires Android 12+; using preset accent"
                            },
                            checked = settings.dynamicColor,
                            testTag = "switch_dynamic_color",
                            onCheckedChange = onDynamicColorChange
                        )

                        AnimatedVisibility(visible = !settings.dynamicColor) {
                            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                                Text(
                                    text = "Accent Color Presets",
                                    style = MaterialTheme.typography.titleSmall,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                val rows = AccentPreset.entries.chunked(4)
                                rows.forEach { rowPresets ->
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                                    ) {
                                        rowPresets.forEach { preset ->
                                            val isSelected = settings.accentPreset == preset
                                            Column(
                                                modifier = Modifier
                                                    .weight(1f)
                                                    .clip(RoundedCornerShape(12.dp))
                                                    .clickable { onAccentPresetChange(preset) }
                                                    .padding(vertical = 6.dp)
                                                    .testTag("accent_${preset.name.lowercase()}"),
                                                horizontalAlignment = Alignment.CenterHorizontally,
                                                verticalArrangement = Arrangement.spacedBy(6.dp)
                                            ) {
                                                Box(
                                                    modifier = Modifier
                                                        .size(40.dp)
                                                        .clip(CircleShape)
                                                        .background(Color(preset.previewHex))
                                                        .then(
                                                            if (isSelected) {
                                                                Modifier.border(
                                                                    3.dp,
                                                                    MaterialTheme.colorScheme.onSurface,
                                                                    CircleShape
                                                                )
                                                            } else Modifier
                                                        ),
                                                    contentAlignment = Alignment.Center
                                                ) {
                                                    if (isSelected) {
                                                        Icon(
                                                            imageVector = Icons.Rounded.Check,
                                                            contentDescription = "${preset.label} selected",
                                                            tint = Color.White,
                                                            modifier = Modifier.size(20.dp)
                                                        )
                                                    }
                                                }
                                                Text(
                                                    text = preset.label,
                                                    style = MaterialTheme.typography.labelSmall,
                                                    color = if (isSelected) {
                                                        MaterialTheme.colorScheme.primary
                                                    } else {
                                                        MaterialTheme.colorScheme.onSurfaceVariant
                                                    }
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // Layout & Typography Section
            item(key = "layout_section") {
                SettingsGroupCard(title = "Layout & Typography") {
                    Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = "Card Corner Radius",
                                    style = MaterialTheme.typography.titleSmall
                                )
                                Text(
                                    text = "${settings.cardCornerRadiusDp.roundToInt()} dp",
                                    style = MaterialTheme.typography.labelLarge.copy(
                                        fontFamily = JetBrainsMonoFontFamily
                                    ),
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }
                            Slider(
                                value = settings.cardCornerRadiusDp,
                                onValueChange = onCornerRadiusChange,
                                valueRange = 8f..28f,
                                modifier = Modifier.testTag("slider_corner_radius")
                            )
                        }

                        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = "Font Size Scale",
                                    style = MaterialTheme.typography.titleSmall
                                )
                                Text(
                                    text = String.format(Locale.US, "%.2fx", settings.fontScale),
                                    style = MaterialTheme.typography.labelLarge.copy(
                                        fontFamily = JetBrainsMonoFontFamily
                                    ),
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }
                            Slider(
                                value = settings.fontScale,
                                onValueChange = onFontScaleChange,
                                valueRange = 0.85f..1.25f,
                                steps = 7,
                                modifier = Modifier.testTag("slider_font_scale")
                            )
                        }

                        Text(
                            text = "Card Density",
                            style = MaterialTheme.typography.titleSmall
                        )
                        SingleChoiceSegmentedButtonRow(modifier = Modifier.fillMaxWidth()) {
                            CardDensity.entries.forEachIndexed { index, density ->
                                SegmentedButton(
                                    selected = settings.cardDensity == density,
                                    onClick = { onCardDensityChange(density) },
                                    shape = SegmentedButtonDefaults.itemShape(
                                        index = index,
                                        count = CardDensity.entries.size
                                    ),
                                    modifier = Modifier.testTag("density_${density.name.lowercase()}")
                                ) {
                                    Text(density.label)
                                }
                            }
                        }

                        Text(
                            text = "Chapter Layout",
                            style = MaterialTheme.typography.titleSmall
                        )
                        SingleChoiceSegmentedButtonRow(modifier = Modifier.fillMaxWidth()) {
                            ChapterLayout.entries.forEachIndexed { index, layout ->
                                SegmentedButton(
                                    selected = settings.chapterLayout == layout,
                                    onClick = { onChapterLayoutChange(layout) },
                                    shape = SegmentedButtonDefaults.itemShape(
                                        index = index,
                                        count = ChapterLayout.entries.size
                                    ),
                                    modifier = Modifier.testTag("layout_${layout.name.lowercase()}")
                                ) {
                                    Text(layout.label)
                                }
                            }
                        }
                    }
                }
            }

            // Exam Schedule & Reset Section
            item(key = "exam_section") {
                SettingsGroupCard(title = "Exam Schedule & Data") {
                    Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                        Surface(
                            onClick = { showDatePicker = true },
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("edit_exam_date_button"),
                            shape = RoundedCornerShape((appearance.cardCornerRadius * 0.75f).coerceAtLeast(10.dp)),
                            color = MaterialTheme.colorScheme.surfaceContainer
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(14.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Rounded.CalendarMonth,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary
                                )
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = "JEE Main 2027 Exam Date",
                                        style = MaterialTheme.typography.titleSmall
                                    )
                                    Text(
                                        text = settings.examDate.format(SETTINGS_DATE_FMT),
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                }
                                Text(
                                    text = "Edit",
                                    style = MaterialTheme.typography.labelLarge,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }
                        }

                        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = "Final Revision Buffer",
                                    style = MaterialTheme.typography.titleSmall
                                )
                                Text(
                                    text = "${settings.bufferDays} days",
                                    style = MaterialTheme.typography.labelLarge.copy(
                                        fontFamily = JetBrainsMonoFontFamily
                                    ),
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }
                            Slider(
                                value = settings.bufferDays.toFloat(),
                                onValueChange = { onBufferDaysChange(it.roundToInt()) },
                                valueRange = 0f..90f,
                                modifier = Modifier.testTag("slider_buffer_days")
                            )
                            Text(
                                text = "Target syllabus finish: ${
                                    settings.examDate.minusDays(settings.bufferDays.toLong())
                                        .format(SETTINGS_DATE_FMT)
                                }",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        Surface(
                            color = MaterialTheme.colorScheme.surfaceContainer,
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp),
                                horizontalArrangement = Arrangement.spacedBy(10.dp),
                                verticalAlignment = Alignment.Top
                            ) {
                                Icon(
                                    imageVector = Icons.Rounded.Info,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.size(18.dp)
                                )
                                Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                                    Text(
                                        text = "${syllabusData.examName} (${syllabusData.sourceName})",
                                        style = MaterialTheme.typography.labelMedium,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                    Text(
                                        text = "Fetched ${syllabusData.fetchedDate} • ${syllabusData.sourceUrl}",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }

                        OutlinedButton(
                            onClick = { showResetDialog = true },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(48.dp)
                                .testTag("reset_progress_button"),
                            colors = ButtonDefaults.outlinedButtonColors(
                                contentColor = MaterialTheme.colorScheme.error
                            ),
                            border = BorderStroke(1.dp, MaterialTheme.colorScheme.error.copy(alpha = 0.6f))
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.DeleteForever,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.size(8.dp))
                            Text("Reset All Progress")
                        }
                    }
                }
            }
        }
    }

    if (showDatePicker) {
        val initialMillis = settings.examDate
            .atStartOfDay(ZoneOffset.UTC)
            .toInstant()
            .toEpochMilli()
        val datePickerState = rememberDatePickerState(initialSelectedDateMillis = initialMillis)

        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        datePickerState.selectedDateMillis?.let { millis ->
                            val pickedDate = Instant.ofEpochMilli(millis)
                                .atZone(ZoneOffset.UTC)
                                .toLocalDate()
                            onExamDateEpochDayChange(pickedDate.toEpochDay())
                        }
                        showDatePicker = false
                    }
                ) {
                    Text("Save")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) {
                    Text("Cancel")
                }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }

    if (showResetDialog) {
        AlertDialog(
            onDismissRequest = { showResetDialog = false },
            title = { Text("Reset all progress?") },
            text = {
                Text("This clears all Theory, Practice, and Revision checkmarks across Physics, Chemistry, and Maths. Your theme and exam settings will be kept.")
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        onResetProgress()
                        showResetDialog = false
                    },
                    colors = ButtonDefaults.textButtonColors(
                        contentColor = MaterialTheme.colorScheme.error
                    ),
                    modifier = Modifier.testTag("confirm_reset_button")
                ) {
                    Text("Reset")
                }
            },
            dismissButton = {
                TextButton(onClick = { showResetDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
private fun SettingsGroupCard(
    title: String,
    content: @Composable () -> Unit
) {
    val appearance = LocalAppAppearance.current
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
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary
            )
            content()
        }
    }
}

@Composable
private fun SettingsSwitchRow(
    title: String,
    subtitle: String,
    checked: Boolean,
    testTag: String,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .clickable { onCheckedChange(!checked) }
            .padding(vertical = 4.dp)
            .testTag(testTag),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleSmall,
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
            onCheckedChange = onCheckedChange
        )
    }
}
