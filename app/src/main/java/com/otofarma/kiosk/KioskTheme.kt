package com.otofarma.kiosk

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight

internal val KioskInk = Color(0xFF13241C)
internal val KioskMuted = Color(0xFF5C6F66)
internal val KioskMist = Color(0xFFF3F7F4)
internal val KioskGreen = Color(0xFF0E7A52)
internal val KioskGreenDeep = Color(0xFF084734)
internal val KioskGreenSoft = Color(0xFFE7F5EE)
internal val KioskYellow = Color(0xFFF0C400)
internal val KioskYellowInk = Color(0xFF1C1915)
internal val KioskWhite = Color(0xFFFFFFFF)
internal val KioskLine = Color(0xFFE3EEE7)
internal val KioskShadow = Color(0x17084734)
internal val KioskBarShadow = Color(0x21084734)

private val Inter = FontFamily(
    Font(R.font.inter_regular, FontWeight.Normal),
    Font(R.font.inter_medium, FontWeight.Medium),
    Font(R.font.inter_semibold, FontWeight.SemiBold),
)

private val KioskColors = lightColorScheme(
    primary = KioskGreen,
    onPrimary = KioskWhite,
    primaryContainer = KioskGreenSoft,
    onPrimaryContainer = KioskGreenDeep,
    background = KioskMist,
    onBackground = KioskInk,
    surface = KioskWhite,
    onSurface = KioskInk,
)

private fun TextStyle.inter(): TextStyle = copy(fontFamily = Inter)

private val KioskTypography = Typography().run {
    copy(
        displayLarge = displayLarge.inter(),
        displayMedium = displayMedium.inter(),
        displaySmall = displaySmall.inter(),
        headlineLarge = headlineLarge.inter(),
        headlineMedium = headlineMedium.inter(),
        headlineSmall = headlineSmall.inter(),
        titleLarge = titleLarge.inter(),
        titleMedium = titleMedium.inter(),
        titleSmall = titleSmall.inter(),
        bodyLarge = bodyLarge.inter(),
        bodyMedium = bodyMedium.inter(),
        bodySmall = bodySmall.inter(),
        labelLarge = labelLarge.inter(),
        labelMedium = labelMedium.inter(),
        labelSmall = labelSmall.inter(),
    )
}

@Composable
internal fun KioskTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = KioskColors,
        typography = KioskTypography,
        content = content,
    )
}
