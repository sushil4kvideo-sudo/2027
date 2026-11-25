package com.example.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

// Use platform fonts to avoid packaging three custom TTF assets.
val SpaceGroteskFontFamily = FontFamily.SansSerif
val PlusJakartaSansFontFamily = FontFamily.SansSerif
val JetBrainsMonoFontFamily = FontFamily.Monospace

fun buildAppTypography(scale: Float = 1.0f): Typography {
    val s = scale.coerceIn(0.85f, 1.25f)
    return Typography(
        displayLarge = TextStyle(
            fontFamily = SpaceGroteskFontFamily,
            fontWeight = FontWeight.Bold,
            fontSize = (40f * s).sp,
            lineHeight = (46f * s).sp,
            letterSpacing = (-0.5).sp
        ),
        displayMedium = TextStyle(
            fontFamily = SpaceGroteskFontFamily,
            fontWeight = FontWeight.Bold,
            fontSize = (32f * s).sp,
            lineHeight = (38f * s).sp,
            letterSpacing = (-0.25).sp
        ),
        headlineLarge = TextStyle(
            fontFamily = SpaceGroteskFontFamily,
            fontWeight = FontWeight.Bold,
            fontSize = (28f * s).sp,
            lineHeight = (34f * s).sp
        ),
        headlineMedium = TextStyle(
            fontFamily = SpaceGroteskFontFamily,
            fontWeight = FontWeight.SemiBold,
            fontSize = (24f * s).sp,
            lineHeight = (30f * s).sp
        ),
        headlineSmall = TextStyle(
            fontFamily = SpaceGroteskFontFamily,
            fontWeight = FontWeight.SemiBold,
            fontSize = (20f * s).sp,
            lineHeight = (26f * s).sp
        ),
        titleLarge = TextStyle(
            fontFamily = SpaceGroteskFontFamily,
            fontWeight = FontWeight.SemiBold,
            fontSize = (20f * s).sp,
            lineHeight = (26f * s).sp
        ),
        titleMedium = TextStyle(
            fontFamily = PlusJakartaSansFontFamily,
            fontWeight = FontWeight.SemiBold,
            fontSize = (16f * s).sp,
            lineHeight = (22f * s).sp,
            letterSpacing = 0.1.sp
        ),
        titleSmall = TextStyle(
            fontFamily = PlusJakartaSansFontFamily,
            fontWeight = FontWeight.SemiBold,
            fontSize = (14f * s).sp,
            lineHeight = (20f * s).sp,
            letterSpacing = 0.1.sp
        ),
        bodyLarge = TextStyle(
            fontFamily = PlusJakartaSansFontFamily,
            fontWeight = FontWeight.Normal,
            fontSize = (16f * s).sp,
            lineHeight = (24f * s).sp,
            letterSpacing = 0.15.sp
        ),
        bodyMedium = TextStyle(
            fontFamily = PlusJakartaSansFontFamily,
            fontWeight = FontWeight.Normal,
            fontSize = (14f * s).sp,
            lineHeight = (20f * s).sp,
            letterSpacing = 0.15.sp
        ),
        bodySmall = TextStyle(
            fontFamily = PlusJakartaSansFontFamily,
            fontWeight = FontWeight.Normal,
            fontSize = (12f * s).sp,
            lineHeight = (16f * s).sp,
            letterSpacing = 0.2.sp
        ),
        labelLarge = TextStyle(
            fontFamily = PlusJakartaSansFontFamily,
            fontWeight = FontWeight.SemiBold,
            fontSize = (14f * s).sp,
            lineHeight = (20f * s).sp,
            letterSpacing = 0.1.sp
        ),
        labelMedium = TextStyle(
            fontFamily = PlusJakartaSansFontFamily,
            fontWeight = FontWeight.Medium,
            fontSize = (12f * s).sp,
            lineHeight = (16f * s).sp,
            letterSpacing = 0.3.sp
        ),
        labelSmall = TextStyle(
            fontFamily = PlusJakartaSansFontFamily,
            fontWeight = FontWeight.Medium,
            fontSize = (11f * s).sp,
            lineHeight = (14f * s).sp,
            letterSpacing = 0.3.sp
        )
    )
}
