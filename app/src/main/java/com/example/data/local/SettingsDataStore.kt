package com.example.data.local

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.data.model.AccentPreset
import com.example.data.model.CardDensity
import com.example.data.model.ChapterLayout
import com.example.data.model.ThemeMode
import com.example.data.model.UserSettings
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "jee_tracker_settings")

class SettingsDataStore(private val context: Context) {

    private object Keys {
        val THEME_MODE = stringPreferencesKey("theme_mode")
        val AMOLED_BLACK = booleanPreferencesKey("amoled_black")
        val DYNAMIC_COLOR = booleanPreferencesKey("dynamic_color")
        val ACCENT_PRESET = stringPreferencesKey("accent_preset")
        val CARD_CORNER_RADIUS = floatPreferencesKey("card_corner_radius")
        val FONT_SCALE = floatPreferencesKey("font_scale")
        val CARD_DENSITY = stringPreferencesKey("card_density")
        val CHAPTER_LAYOUT = stringPreferencesKey("chapter_layout")
        val EXAM_DATE_EPOCH_DAY = longPreferencesKey("exam_date_epoch_day")
        val BUFFER_DAYS = intPreferencesKey("buffer_days")
    }

    val settingsFlow: Flow<UserSettings> = context.dataStore.data.map { prefs ->
        UserSettings(
            themeMode = prefs[Keys.THEME_MODE]?.let { runCatching { ThemeMode.valueOf(it) }.getOrNull() }
                ?: ThemeMode.SYSTEM,
            amoledBlack = prefs[Keys.AMOLED_BLACK] ?: false,
            dynamicColor = prefs[Keys.DYNAMIC_COLOR] ?: true,
            accentPreset = prefs[Keys.ACCENT_PRESET]?.let { runCatching { AccentPreset.valueOf(it) }.getOrNull() }
                ?: AccentPreset.INDIGO,
            cardCornerRadiusDp = (prefs[Keys.CARD_CORNER_RADIUS] ?: 20f).coerceIn(8f, 28f),
            fontScale = (prefs[Keys.FONT_SCALE] ?: 1.0f).coerceIn(0.85f, 1.25f),
            cardDensity = prefs[Keys.CARD_DENSITY]?.let { runCatching { CardDensity.valueOf(it) }.getOrNull() }
                ?: CardDensity.COMFORTABLE,
            chapterLayout = prefs[Keys.CHAPTER_LAYOUT]?.let { runCatching { ChapterLayout.valueOf(it) }.getOrNull() }
                ?: ChapterLayout.HORIZONTAL_CARDS,
            examDateEpochDay = prefs[Keys.EXAM_DATE_EPOCH_DAY] ?: UserSettings.DEFAULT_EXAM_DATE.toEpochDay(),
            bufferDays = (prefs[Keys.BUFFER_DAYS] ?: UserSettings.DEFAULT_BUFFER_DAYS).coerceIn(0, 120)
        )
    }

    suspend fun setThemeMode(mode: ThemeMode) {
        context.dataStore.edit { it[Keys.THEME_MODE] = mode.name }
    }

    suspend fun setAmoledBlack(enabled: Boolean) {
        context.dataStore.edit { it[Keys.AMOLED_BLACK] = enabled }
    }

    suspend fun setDynamicColor(enabled: Boolean) {
        context.dataStore.edit { it[Keys.DYNAMIC_COLOR] = enabled }
    }

    suspend fun setAccentPreset(preset: AccentPreset) {
        context.dataStore.edit { it[Keys.ACCENT_PRESET] = preset.name }
    }

    suspend fun setCardCornerRadius(radiusDp: Float) {
        context.dataStore.edit { it[Keys.CARD_CORNER_RADIUS] = radiusDp.coerceIn(8f, 28f) }
    }

    suspend fun setFontScale(scale: Float) {
        context.dataStore.edit { it[Keys.FONT_SCALE] = scale.coerceIn(0.85f, 1.25f) }
    }

    suspend fun setCardDensity(density: CardDensity) {
        context.dataStore.edit { it[Keys.CARD_DENSITY] = density.name }
    }

    suspend fun setChapterLayout(layout: ChapterLayout) {
        context.dataStore.edit { it[Keys.CHAPTER_LAYOUT] = layout.name }
    }

    suspend fun setExamDateEpochDay(epochDay: Long) {
        context.dataStore.edit { it[Keys.EXAM_DATE_EPOCH_DAY] = epochDay }
    }

    suspend fun setBufferDays(days: Int) {
        context.dataStore.edit { it[Keys.BUFFER_DAYS] = days.coerceIn(0, 120) }
    }
}
