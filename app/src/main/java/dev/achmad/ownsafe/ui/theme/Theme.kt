@file:Suppress("DEPRECATION")

package dev.achmad.ownsafe.ui.theme

import android.app.Activity
import android.os.Build
import android.view.Window
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import dev.achmad.core.AppTheme
import dev.achmad.ownsafe.MainActivityViewModel

val DarkColorScheme = darkColorScheme(
    primary = Purple80,
    secondary = PurpleGrey80,
    tertiary = Pink80
)

val LightColorScheme = lightColorScheme(
    primary = Purple40,
    secondary = PurpleGrey40,
    tertiary = Pink40

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

val LocalColorScheme = compositionLocalOf { LightColorScheme }
val LocalNavController = compositionLocalOf<NavHostController> {
    error("No NavController provided")
}
val LocalMainViewModel = compositionLocalOf<MainActivityViewModel> {
    error("No MainActivityViewModel provided")
}

@Composable
fun OwnSafeTheme(
    content: @Composable () -> Unit,
) {
    val context = LocalContext.current
    val navController = rememberNavController()
    val viewModel = hiltViewModel<MainActivityViewModel>()
    val window = (LocalView.current.context as Activity).window
    val appTheme by viewModel.appTheme.collectAsState()
    val dynamicColor by viewModel.dynamicColors.collectAsState()

    val darkTheme = when (appTheme) {
        AppTheme.SYSTEM -> isSystemInDarkTheme()
        AppTheme.DARK -> true
        AppTheme.LIGHT -> false
    }

    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            if (darkTheme) dynamicDarkColorScheme(context)
            else dynamicLightColorScheme(context)
        }

        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    configureSystemBarColor(window, darkTheme)

    CompositionLocalProvider(
        LocalColorScheme provides colorScheme,
        LocalMainViewModel provides viewModel,
        LocalNavController provides navController,
    ) {
        // Add box with background to prevent white background (transparent)
        // when animating on navigate
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(colorScheme.background),
        ) {
            MaterialTheme(
                colorScheme = colorScheme,
                typography = Typography
            ) {
                content()
            }
        }
    }
}

@Suppress("DEPRECATION")
private fun configureSystemBarColor(window: Window, darkTheme: Boolean) {
    window.decorView.let(ViewCompat::getWindowInsetsController)?.let {
        it.apply {
            isAppearanceLightStatusBars = !darkTheme
            isAppearanceLightNavigationBars = !darkTheme
        }
    }
    WindowCompat.setDecorFitsSystemWindows(window, false)
}

@Composable
fun SystemBarColor(color: Color, darkTheme: Boolean = false) {
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.navigationBarColor = color.toArgb()
            window.statusBarColor = color.toArgb()
            if (darkTheme) {
                val insetsController = window.decorView.let(ViewCompat::getWindowInsetsController)
                insetsController?.let {
                    it.apply {
                        isAppearanceLightStatusBars = false
                        isAppearanceLightNavigationBars = false
                    }
                }
            }
            WindowCompat.setDecorFitsSystemWindows(window, false)
        }
    }
}

@Composable
fun NavigationBarColor(color: Color) {
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.navigationBarColor = color.toArgb()
            WindowCompat.setDecorFitsSystemWindows(window, false)
        }
    }
}

@Composable
fun StatusBarColor(color: Color) {
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = color.toArgb()
            WindowCompat.setDecorFitsSystemWindows(window, false)
        }
    }
}