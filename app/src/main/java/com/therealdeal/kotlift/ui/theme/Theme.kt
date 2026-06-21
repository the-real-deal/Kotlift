package com.therealdeal.kotlift.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat


private val DarkColorScheme = darkColorScheme(
    primary = AppGreen,
    onPrimary = White,
    secondary = AppGreenLight,
    tertiary = InfoBlue,
    background = BackgroundDark,
    surface = SurfaceDark,
    surfaceContainer = Color(0xFF242426),
    onBackground = TextPrimary,
    onSurface = TextPrimary,
    error = ErrorRed
)

private val LightColorScheme = lightColorScheme(
    primary = AppGreenDark,
    onPrimary = White,
    secondary = AppGreen,
    tertiary = InfoBlue,
    background = White,
    surface = Color(0xFFF2F2F7),
    surfaceContainer = Color(0xFFE4E4EC),
    onBackground = Black,
    onSurface = Black,
    error = ErrorRed
)

@Composable
fun KotliftTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            val windowInsetsController = WindowCompat.getInsetsController(window, view)
            window.statusBarColor = colorScheme.background.toArgb()
            windowInsetsController.isAppearanceLightStatusBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}