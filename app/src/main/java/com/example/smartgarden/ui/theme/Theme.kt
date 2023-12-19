package com.example.smartgarden.ui.theme

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
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val DarkColorScheme = darkColorScheme(
    primary = Green,
    secondary = PurpleGrey80,
    tertiary = Pink80,
    background = Black,
    onBackground = White,

    onPrimary = Green20,

    onPrimaryContainer = WhiteText,

    primaryContainer = BlackOpac,
)

private val LightColorScheme = lightColorScheme(
    primary = Green,
    secondary = PurpleGrey40,
    tertiary = Pink40,
    background = White,
    onBackground = Black,

    onPrimary = Green80,

    onPrimaryContainer = BlackText,

    primaryContainer = WhiteOpac,

    /* Other default colors to override
    background = Color(0xFFFFFBFE),
    surface = Color(0xFFFFFBFE),
    onPrimary = Color.White,
    onSecondary = Color.White,
    onTertiary = Color.White,
    onBackground = Color(0xFF1C1B1F),
    onSurface = Color(0xFF1C1B1F),
    */
)

@Composable
fun SmartGardenTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is available on Android 12+
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    val typography = when{
        darkTheme -> TypographyDark
        else -> TypographyLight
    }

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor       = colorScheme.background.toArgb()
            window.navigationBarColor   = colorScheme.background.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars      = !darkTheme
            WindowCompat.getInsetsController(window, view).isAppearanceLightNavigationBars  = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = typography,
        content = content
    )
}