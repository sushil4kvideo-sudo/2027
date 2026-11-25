package com.example.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.core.view.WindowCompat
import com.example.data.model.CardDensity
import com.example.data.model.ChapterLayout
import com.example.data.model.ThemeMode
import com.example.data.model.UserSettings

data class AppAppearance(
    val cardCornerRadius: Dp = 20.dp,
    val cardDensity: CardDensity = CardDensity.COMFORTABLE,
    val chapterLayout: ChapterLayout = ChapterLayout.HORIZONTAL_CARDS,
    val isAmoledDark: Boolean = false
) {
    val cardPadding: Dp
        get() = if (cardDensity == CardDensity.COMPACT) 12.dp else 18.dp

    val listSpacing: Dp
        get() = if (cardDensity == CardDensity.COMPACT) 10.dp else 14.dp
}

val LocalAppAppearance = staticCompositionLocalOf { AppAppearance() }

private fun ColorScheme.withAmoledBlack(): ColorScheme = copy(
    background = Color.Black,
    surface = Color.Black,
    surfaceDim = Color.Black,
    surfaceBright = Color(0xFF141418),
    surfaceContainerLowest = Color.Black,
    surfaceContainerLow = Color(0xFF0A0A0E),
    surfaceContainer = Color(0xFF111116),
    surfaceContainerHigh = Color(0xFF18181F),
    surfaceContainerHighest = Color(0xFF202028)
)

@Composable
fun JeeTrackerTheme(
    settings: UserSettings = UserSettings(),
    content: @Composable () -> Unit
) {
    val systemDark = isSystemInDarkTheme()
    val darkTheme = when (settings.themeMode) {
        ThemeMode.SYSTEM -> systemDark
        ThemeMode.LIGHT -> false
        ThemeMode.DARK -> true
    }

    val context = LocalContext.current
    val baseColorScheme = when {
        settings.dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        else -> buildColorSchemeForPreset(settings.accentPreset, darkTheme)
    }

    val finalColorScheme = if (darkTheme && settings.amoledBlack) {
        baseColorScheme.withAmoledBlack()
    } else {
        baseColorScheme
    }

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as? Activity)?.window ?: return@SideEffect
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
            WindowCompat.getInsetsController(window, view).isAppearanceLightNavigationBars = !darkTheme
        }
    }

    val radius = settings.cardCornerRadiusDp.dp
    val shapes = Shapes(
        small = RoundedCornerShape((radius * 0.5f).coerceAtLeast(6.dp)),
        medium = RoundedCornerShape(radius),
        large = RoundedCornerShape((radius * 1.25f).coerceAtMost(32.dp))
    )

    val appearance = AppAppearance(
        cardCornerRadius = radius,
        cardDensity = settings.cardDensity,
        chapterLayout = settings.chapterLayout,
        isAmoledDark = darkTheme && settings.amoledBlack
    )

    CompositionLocalProvider(LocalAppAppearance provides appearance) {
        MaterialTheme(
            colorScheme = finalColorScheme,
            typography = buildAppTypography(settings.fontScale),
            shapes = shapes,
            content = content
        )
    }
}
