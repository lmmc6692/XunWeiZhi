package com.lmc.xunweizhi.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

private val LightColors = lightColorScheme(
    primary = Terracotta,
    onPrimary = SoftWhite,
    primaryContainer = ColorTokens.PrimaryContainer,
    onPrimaryContainer = TerracottaDark,
    secondary = Moss,
    onSecondary = SoftWhite,
    secondaryContainer = ColorTokens.SecondaryContainer,
    onSecondaryContainer = ColorTokens.OnSecondaryContainer,
    tertiary = Gold,
    onTertiary = SoftWhite,
    background = WarmWhite,
    onBackground = Ink,
    surface = SoftWhite,
    onSurface = Ink,
    surfaceVariant = Sand,
    onSurfaceVariant = Taupe,
    outline = ColorTokens.Outline,
    outlineVariant = ColorTokens.OutlineVariant,
)

private val DarkColors = darkColorScheme(
    primary = ColorTokens.DarkPrimary,
    onPrimary = ColorTokens.DarkOnPrimary,
    primaryContainer = ColorTokens.DarkPrimaryContainer,
    onPrimaryContainer = ColorTokens.DarkOnPrimaryContainer,
    secondary = ColorTokens.DarkSecondary,
    onSecondary = Night,
    background = Night,
    onBackground = ColorTokens.DarkText,
    surface = NightSurface,
    onSurface = ColorTokens.DarkText,
    surfaceVariant = NightSand,
    onSurfaceVariant = ColorTokens.DarkMuted,
    outline = ColorTokens.DarkOutline,
)

private object ColorTokens {
    val PrimaryContainer = androidx.compose.ui.graphics.Color(0xFFF6D9CD)
    val SecondaryContainer = androidx.compose.ui.graphics.Color(0xFFD9E7DD)
    val OnSecondaryContainer = androidx.compose.ui.graphics.Color(0xFF193225)
    val Outline = androidx.compose.ui.graphics.Color(0xFF8A7C72)
    val OutlineVariant = androidx.compose.ui.graphics.Color(0xFFD9CEC2)
    val DarkPrimary = androidx.compose.ui.graphics.Color(0xFFFFB59D)
    val DarkOnPrimary = androidx.compose.ui.graphics.Color(0xFF5A1809)
    val DarkPrimaryContainer = androidx.compose.ui.graphics.Color(0xFF7A2D19)
    val DarkOnPrimaryContainer = androidx.compose.ui.graphics.Color(0xFFFFDBCF)
    val DarkSecondary = androidx.compose.ui.graphics.Color(0xFFB6CCBC)
    val DarkText = androidx.compose.ui.graphics.Color(0xFFF1E8DF)
    val DarkMuted = androidx.compose.ui.graphics.Color(0xFFCBBCAF)
    val DarkOutline = androidx.compose.ui.graphics.Color(0xFF9A8A7D)
}

@Composable
fun XunWeiTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit,
) {
    val colors = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }

        darkTheme -> DarkColors
        else -> LightColors
    }

    MaterialTheme(
        colorScheme = colors,
        typography = XunWeiTypography,
        content = content,
    )
}
