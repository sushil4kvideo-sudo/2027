package com.example.data.model

import java.time.LocalDate

enum class ThemeMode(val label: String) {
    SYSTEM("System"),
    LIGHT("Light"),
    DARK("Dark")
}

enum class CardDensity(val label: String) {
    COMPACT("Compact"),
    COMFORTABLE("Comfortable")
}

enum class ChapterLayout(val label: String) {
    HORIZONTAL_CARDS("Horizontal"),
    VERTICAL_LIST("Vertical")
}

enum class AccentPreset(val label: String, val previewHex: Long) {
    INDIGO("Indigo", 0xFF4F46E5),
    COBALT("Cobalt", 0xFF0284C7),
    EMERALD("Emerald", 0xFF059669),
    AMBER("Amber", 0xFFD97706),
    CORAL("Coral", 0xFFEA580C),
    VIOLET("Violet", 0xFF7C3AED),
    TEAL("Teal", 0xFF0D9488),
    CRIMSON("Rose", 0xFFE11D48)
}

data class UserSettings(
    val themeMode: ThemeMode = ThemeMode.SYSTEM,
    val amoledBlack: Boolean = false,
    val dynamicColor: Boolean = true,
    val accentPreset: AccentPreset = AccentPreset.INDIGO,
    val cardCornerRadiusDp: Float = 20f,
    val fontScale: Float = 1.0f,
    val cardDensity: CardDensity = CardDensity.COMFORTABLE,
    val chapterLayout: ChapterLayout = ChapterLayout.HORIZONTAL_CARDS,
    val examDateEpochDay: Long = DEFAULT_EXAM_DATE.toEpochDay(),
    val bufferDays: Int = DEFAULT_BUFFER_DAYS
) {
    val examDate: LocalDate
        get() = LocalDate.ofEpochDay(examDateEpochDay)

    companion object {
        val DEFAULT_EXAM_DATE: LocalDate = LocalDate.of(2027, 1, 20)
        const val DEFAULT_BUFFER_DAYS: Int = 30
    }
}
