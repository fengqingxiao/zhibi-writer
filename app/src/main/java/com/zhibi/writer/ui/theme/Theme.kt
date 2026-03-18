package com.zhibi.writer.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

// 执笔写作配色 - 温暖的写作风格

// 浅色主题
private val LightPrimary = Color(0xFF6B4E3D) // 温暖的棕色
private val LightOnPrimary = Color(0xFFFFFFFF)
private val LightPrimaryContainer = Color(0xFFFFE4C4)
private val LightOnPrimaryContainer = Color(0xFF2A1508)
private val LightSecondary = Color(0xFF755C48)
private val LightOnSecondary = Color(0xFFFFFFFF)
private val LightSecondaryContainer = Color(0xFFFFE4C4)
private val LightOnSecondaryContainer = Color(0xFF2A1508)
private val LightTertiary = Color(0xFF8B6914)
private val LightOnTertiary = Color(0xFFFFFFFF)
private val LightTertiaryContainer = Color(0xFFFFE087)
private val LightOnTertiaryContainer = Color(0xFF2A2000)
private val LightBackground = Color(0xFFFFFBF8)
private val LightOnBackground = Color(0xFF1E1B18)
private val LightSurface = Color(0xFFFFFBF8)
private val LightOnSurface = Color(0xFF1E1B18)
private val LightSurfaceVariant = Color(0xFFF3DFD4)
private val LightOnSurfaceVariant = Color(0xFF51443A)
private val LightOutline = Color(0xFF837469)
private val LightOutlineVariant = Color(0xFFD6C3B8)
private val LightInverseSurface = Color(0xFF33302D)
private val LightInverseOnSurface = Color(0xFFF7F0EA)
private val LightInversePrimary = Color(0xFFDAB8A1)
private val LightSurfaceTint = Color(0xFF6B4E3D)

// 深色主题
private val DarkPrimary = Color(0xFFDAB8A1)
private val DarkOnPrimary = Color(0xFF3A291D)
private val DarkPrimaryContainer = Color(0xFF523828)
private val DarkOnPrimaryContainer = Color(0xFFFFE4C4)
private val DarkSecondary = Color(0xFFDAB8A1)
private val DarkOnSecondary = Color(0xFF3A291D)
private val DarkSecondaryContainer = Color(0xFF523828)
private val DarkOnSecondaryContainer = Color(0xFFFFE4C4)
private val DarkTertiary = Color(0xFFFFE087)
private val DarkOnTertiary = Color(0xFF463800)
private val DarkTertiaryContainer = Color(0xFF654D00)
private val DarkOnTertiaryContainer = Color(0xFFFFE087)
private val DarkBackground = Color(0xFF1A1815)
private val DarkOnBackground = Color(0xFFE8E2DC)
private val DarkSurface = Color(0xFF1A1815)
private val DarkOnSurface = Color(0xFFE8E2DC)
private val DarkSurfaceVariant = Color(0xFF51443A)
private val DarkOnSurfaceVariant = Color(0xFFD6C3B8)
private val DarkOutline = Color(0xFF9F8D82)
private val DarkOutlineVariant = Color(0xFF51443A)
private val DarkInverseSurface = Color(0xFFE8E2DC)
private val DarkInverseOnSurface = Color(0xFF33302D)
private val DarkInversePrimary = Color(0xFF6B4E3D)
private val DarkSurfaceTint = Color(0xFFDAB8A1)

// 护眼模式（米黄色背景）
private val EyeCareBackground = Color(0xFFF5E6D3)
private val EyeCareSurface = Color(0xFFF5E6D3)
private val EyeCareOnBackground = Color(0xFF2A2015)
private val EyeCareOnSurface = Color(0xFF2A2015)

private val LightColorScheme = lightColorScheme(
    primary = LightPrimary,
    onPrimary = LightOnPrimary,
    primaryContainer = LightPrimaryContainer,
    onPrimaryContainer = LightOnPrimaryContainer,
    secondary = LightSecondary,
    onSecondary = LightOnSecondary,
    secondaryContainer = LightSecondaryContainer,
    onSecondaryContainer = LightOnSecondaryContainer,
    tertiary = LightTertiary,
    onTertiary = LightOnTertiary,
    tertiaryContainer = LightTertiaryContainer,
    onTertiaryContainer = LightOnTertiaryContainer,
    background = LightBackground,
    onBackground = LightOnBackground,
    surface = LightSurface,
    onSurface = LightOnSurface,
    surfaceVariant = LightSurfaceVariant,
    onSurfaceVariant = LightOnSurfaceVariant,
    outline = LightOutline,
    outlineVariant = LightOutlineVariant,
    inverseSurface = LightInverseSurface,
    inverseOnSurface = LightInverseOnSurface,
    inversePrimary = LightInversePrimary,
    surfaceTint = LightSurfaceTint,
)

private val DarkColorScheme = darkColorScheme(
    primary = DarkPrimary,
    onPrimary = DarkOnPrimary,
    primaryContainer = DarkPrimaryContainer,
    onPrimaryContainer = DarkOnPrimaryContainer,
    secondary = DarkSecondary,
    onSecondary = DarkOnSecondary,
    secondaryContainer = DarkSecondaryContainer,
    onSecondaryContainer = DarkOnSecondaryContainer,
    tertiary = DarkTertiary,
    onTertiary = DarkOnTertiary,
    tertiaryContainer = DarkTertiaryContainer,
    onTertiaryContainer = DarkOnTertiaryContainer,
    background = DarkBackground,
    onBackground = DarkOnBackground,
    surface = DarkSurface,
    onSurface = DarkOnSurface,
    surfaceVariant = DarkSurfaceVariant,
    onSurfaceVariant = DarkOnSurfaceVariant,
    outline = DarkOutline,
    outlineVariant = DarkOutlineVariant,
    inverseSurface = DarkInverseSurface,
    inverseOnSurface = DarkInverseOnSurface,
    inversePrimary = DarkInversePrimary,
    surfaceTint = DarkSurfaceTint,
)

/**
 * 执笔写作主题
 */
@Composable
fun ZhibiWriterTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // 动态颜色（Android 12+）
    dynamicColor: Boolean = false,
    // 护眼模式
    eyeCareMode: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        eyeCareMode -> LightColorScheme.copy(
            background = EyeCareBackground,
            surface = EyeCareSurface,
            onBackground = EyeCareOnBackground,
            onSurface = EyeCareOnSurface,
        )
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.background.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
