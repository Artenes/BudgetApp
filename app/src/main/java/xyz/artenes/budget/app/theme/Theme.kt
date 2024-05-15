package xyz.artenes.budget.app.theme

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
import androidx.core.view.ViewCompat

val primaryDark = Color(0XFFC2185B)
val secondaryDark = Color(0xFF009688)
val tertiaryDark = Color(0XFF512DA8)
val white = Color(0XFFFFFFFF)
val darkGray = Color(0xFF424040)
val tertiaryContainerDark = Color(0xFF9E1048)

val primaryDarker = Color(0xFF63092C)
val tertiaryDarker = Color(0xFF381886)
val tertiaryContainerDarker = Color(0xFF47041E)
val lightGray = Color(0XFF757575)

private val DarkColorScheme = darkColorScheme(
    primary = primaryDarker,
    secondary = secondaryDark,
    tertiary = tertiaryDarker,
    background = primaryDarker,
    onBackground = white,
    secondaryContainer = white,
    onSecondaryContainer = lightGray,
    onTertiary = white,
    scrim = lightGray,
    primaryContainer = tertiaryDarker,
    tertiaryContainer = tertiaryContainerDarker
)

private val LightColorScheme = lightColorScheme(
    primary = primaryDark,
    secondary = secondaryDark,
    tertiary = tertiaryDark,
    background = primaryDark,
    onBackground = white,
    secondaryContainer = white,
    onSecondaryContainer = lightGray,
    onTertiary = white,
    scrim = darkGray,
    primaryContainer = tertiaryDark,
    tertiaryContainer = tertiaryContainerDark
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is available on Android 12+
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
            (view.context as Activity).window.statusBarColor = colorScheme.primary.toArgb()
            ViewCompat.getWindowInsetsController(view)?.isAppearanceLightStatusBars = false
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
