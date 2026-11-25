package com.example.ui.theme

import androidx.compose.material3.ColorScheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.ui.graphics.Color
import com.example.data.model.AccentPreset

// Semantic Subject Colors (used consistently across badges & charts)
val PhysicsColor = Color(0xFF0284C7)
val ChemistryColor = Color(0xFF059669)
val MathsColor = Color(0xFFD97706)

val StageTheoryColor = Color(0xFF6366F1)
val StagePracticeColor = Color(0xFF0EA5E9)
val StageRevisionColor = Color(0xFF10B981)
val PriorityAmber = Color(0xFFF59E0B)

fun buildColorSchemeForPreset(
    preset: AccentPreset,
    darkTheme: Boolean
): ColorScheme {
    return when (preset) {
        AccentPreset.INDIGO -> if (darkTheme) {
            darkColorScheme(
                primary = Color(0xFFB8C3FF),
                onPrimary = Color(0xFF1B2769),
                primaryContainer = Color(0xFF333F82),
                onPrimaryContainer = Color(0xFFDDE1FF),
                secondary = Color(0xFF8CD3EE),
                onSecondary = Color(0xFF003545),
                secondaryContainer = Color(0xFF004D63),
                onSecondaryContainer = Color(0xFFBFE9FF),
                tertiary = Color(0xFFF2BC6D),
                onTertiary = Color(0xFF442B00),
                tertiaryContainer = Color(0xFF614000),
                onTertiaryContainer = Color(0xFFFFDDB0)
            )
        } else {
            lightColorScheme(
                primary = Color(0xFF4B579B),
                onPrimary = Color.White,
                primaryContainer = Color(0xFFDDE1FF),
                onPrimaryContainer = Color(0xFF021156),
                secondary = Color(0xFF106680),
                onSecondary = Color.White,
                secondaryContainer = Color(0xFFBFE9FF),
                onSecondaryContainer = Color(0xFF001F29),
                tertiary = Color(0xFF7D570D),
                onTertiary = Color.White,
                tertiaryContainer = Color(0xFFFFDDB0),
                onTertiaryContainer = Color(0xFF281800)
            )
        }

        AccentPreset.COBALT -> if (darkTheme) {
            darkColorScheme(
                primary = Color(0xFF8ACEFF),
                onPrimary = Color(0xFF00344E),
                primaryContainer = Color(0xFF004B70),
                onPrimaryContainer = Color(0xFFC9E6FF),
                secondary = Color(0xFFB6C9D8),
                onSecondary = Color(0xFF21323E),
                secondaryContainer = Color(0xFF374956),
                onSecondaryContainer = Color(0xFFD2E5F4),
                tertiary = Color(0xFFCDC0E9),
                onTertiary = Color(0xFF342B4B),
                tertiaryContainer = Color(0xFF4B4163),
                onTertiaryContainer = Color(0xFFE9DDFF)
            )
        } else {
            lightColorScheme(
                primary = Color(0xFF006494),
                onPrimary = Color.White,
                primaryContainer = Color(0xFFC9E6FF),
                onPrimaryContainer = Color(0xFF001E30),
                secondary = Color(0xFF4F616E),
                onSecondary = Color.White,
                secondaryContainer = Color(0xFFD2E5F4),
                onSecondaryContainer = Color(0xFF0B1D29),
                tertiary = Color(0xFF63597C),
                onTertiary = Color.White,
                tertiaryContainer = Color(0xFFE9DDFF),
                onTertiaryContainer = Color(0xFF1F1635)
            )
        }

        AccentPreset.EMERALD -> if (darkTheme) {
            darkColorScheme(
                primary = Color(0xFF6CDAA9),
                onPrimary = Color(0xFF003824),
                primaryContainer = Color(0xFF005236),
                onPrimaryContainer = Color(0xFF89F7C4),
                secondary = Color(0xFFB2CCC0),
                onSecondary = Color(0xFF1E352B),
                secondaryContainer = Color(0xFF344B41),
                onSecondaryContainer = Color(0xFFCEE8DC),
                tertiary = Color(0xFFA9CCE3),
                onTertiary = Color(0xFF0E3447),
                tertiaryContainer = Color(0xFF294B5E),
                onTertiaryContainer = Color(0xFFC5E8FF)
            )
        } else {
            lightColorScheme(
                primary = Color(0xFF006C49),
                onPrimary = Color.White,
                primaryContainer = Color(0xFF89F7C4),
                onPrimaryContainer = Color(0xFF002113),
                secondary = Color(0xFF4C6358),
                onSecondary = Color.White,
                secondaryContainer = Color(0xFFCEE8DC),
                onSecondaryContainer = Color(0xFF092017),
                tertiary = Color(0xFF416377),
                onTertiary = Color.White,
                tertiaryContainer = Color(0xFFC5E8FF),
                onTertiaryContainer = Color(0xFF001E2C)
            )
        }

        AccentPreset.AMBER -> if (darkTheme) {
            darkColorScheme(
                primary = Color(0xFFFFBA49),
                onPrimary = Color(0xFF452B00),
                primaryContainer = Color(0xFF633F00),
                onPrimaryContainer = Color(0xFFFFDDB0),
                secondary = Color(0xFFDBC3A1),
                onSecondary = Color(0xFF3D2E16),
                secondaryContainer = Color(0xFF55442A),
                onSecondaryContainer = Color(0xFFF8DFBC),
                tertiary = Color(0xFFB5CE9C),
                onTertiary = Color(0xFF223611),
                tertiaryContainer = Color(0xFF384D25),
                onTertiaryContainer = Color(0xFFD0EAB7)
            )
        } else {
            lightColorScheme(
                primary = Color(0xFF835500),
                onPrimary = Color.White,
                primaryContainer = Color(0xFFFFDDB0),
                onPrimaryContainer = Color(0xFF291800),
                secondary = Color(0xFF6E5C40),
                onSecondary = Color.White,
                secondaryContainer = Color(0xFFF8DFBC),
                onSecondaryContainer = Color(0xFF261904),
                tertiary = Color(0xFF4F653B),
                onTertiary = Color.White,
                tertiaryContainer = Color(0xFFD0EAB7),
                onTertiaryContainer = Color(0xFF0E2001)
            )
        }

        AccentPreset.CORAL -> if (darkTheme) {
            darkColorScheme(
                primary = Color(0xFFFFB59A),
                onPrimary = Color(0xFF571E05),
                primaryContainer = Color(0xFF763317),
                onPrimaryContainer = Color(0xFFFFDBCE),
                secondary = Color(0xFFE7BDB0),
                onSecondary = Color(0xFF442A21),
                secondaryContainer = Color(0xFF5D4036),
                onSecondaryContainer = Color(0xFFFFDBCE),
                tertiary = Color(0xFFD5C78E),
                onTertiary = Color(0xFF393006),
                tertiaryContainer = Color(0xFF51461A),
                onTertiaryContainer = Color(0xFFF2E3A8)
            )
        } else {
            lightColorScheme(
                primary = Color(0xFF96492A),
                onPrimary = Color.White,
                primaryContainer = Color(0xFFFFDBCE),
                onPrimaryContainer = Color(0xFF370D00),
                secondary = Color(0xFF77574D),
                onSecondary = Color.White,
                secondaryContainer = Color(0xFFFFDBCE),
                onSecondaryContainer = Color(0xFF2C150E),
                tertiary = Color(0xFF695E2F),
                onTertiary = Color.White,
                tertiaryContainer = Color(0xFFF2E3A8),
                onTertiaryContainer = Color(0xFF221B00)
            )
        }

        AccentPreset.VIOLET -> if (darkTheme) {
            darkColorScheme(
                primary = Color(0xFFD0BCFF),
                onPrimary = Color(0xFF3A1D72),
                primaryContainer = Color(0xFF52358A),
                onPrimaryContainer = Color(0xFFEADDFF),
                secondary = Color(0xFFCBC2DB),
                onSecondary = Color(0xFF332D41),
                secondaryContainer = Color(0xFF4A4458),
                onSecondaryContainer = Color(0xFFE8DEF8),
                tertiary = Color(0xFFEFB8C8),
                onTertiary = Color(0xFF492532),
                tertiaryContainer = Color(0xFF633B48),
                onTertiaryContainer = Color(0xFFFFD8E4)
            )
        } else {
            lightColorScheme(
                primary = Color(0xFF6A4DA4),
                onPrimary = Color.White,
                primaryContainer = Color(0xFFEADDFF),
                onPrimaryContainer = Color(0xFF23035A),
                secondary = Color(0xFF625B71),
                onSecondary = Color.White,
                secondaryContainer = Color(0xFFE8DEF8),
                onSecondaryContainer = Color(0xFF1E192B),
                tertiary = Color(0xFF7E5260),
                onTertiary = Color.White,
                tertiaryContainer = Color(0xFFFFD8E4),
                onTertiaryContainer = Color(0xFF31101D)
            )
        }

        AccentPreset.TEAL -> if (darkTheme) {
            darkColorScheme(
                primary = Color(0xFF5BD9CA),
                onPrimary = Color(0xFF003732),
                primaryContainer = Color(0xFF005049),
                onPrimaryContainer = Color(0xFF7AF6E6),
                secondary = Color(0xFFB0CCC7),
                onSecondary = Color(0xFF1C3531),
                secondaryContainer = Color(0xFF324B47),
                onSecondaryContainer = Color(0xFFCCE8E3),
                tertiary = Color(0xFFB2C8E8),
                onTertiary = Color(0xFF1C324B),
                tertiaryContainer = Color(0xFF334863),
                onTertiaryContainer = Color(0xFFD2E4FF)
            )
        } else {
            lightColorScheme(
                primary = Color(0xFF006A61),
                onPrimary = Color.White,
                primaryContainer = Color(0xFF7AF6E6),
                onPrimaryContainer = Color(0xFF00201D),
                secondary = Color(0xFF4A635F),
                onSecondary = Color.White,
                secondaryContainer = Color(0xFFCCE8E3),
                onSecondaryContainer = Color(0xFF05201C),
                tertiary = Color(0xFF4A607B),
                onTertiary = Color.White,
                tertiaryContainer = Color(0xFFD2E4FF),
                onTertiaryContainer = Color(0xFF031D35)
            )
        }

        AccentPreset.CRIMSON -> if (darkTheme) {
            darkColorScheme(
                primary = Color(0xFFFFB2BC),
                onPrimary = Color(0xFF670022),
                primaryContainer = Color(0xFF900C34),
                onPrimaryContainer = Color(0xFFFFD9DE),
                secondary = Color(0xFFE5BDC1),
                onSecondary = Color(0xFF43292D),
                secondaryContainer = Color(0xFF5C3F43),
                onSecondaryContainer = Color(0xFFFFD9DE),
                tertiary = Color(0xFFEBBF90),
                onTertiary = Color(0xFF452B08),
                tertiaryContainer = Color(0xFF5E411C),
                onTertiaryContainer = Color(0xFFFFDDBA)
            )
        } else {
            lightColorScheme(
                primary = Color(0xFFB61E4A),
                onPrimary = Color.White,
                primaryContainer = Color(0xFFFFD9DE),
                onPrimaryContainer = Color(0xFF3F0013),
                secondary = Color(0xFF75565B),
                onSecondary = Color.White,
                secondaryContainer = Color(0xFFFFD9DE),
                onSecondaryContainer = Color(0xFF2B1519),
                tertiary = Color(0xFF795831),
                onTertiary = Color.White,
                tertiaryContainer = Color(0xFFFFDDBA),
                onTertiaryContainer = Color(0xFF2B1700)
            )
        }
    }
}
