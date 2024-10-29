package dev.achmad.ownsafe

import android.os.Build
import android.os.Bundle
import android.view.Window
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import dagger.hilt.android.AndroidEntryPoint
import dev.achmad.core.AppTheme
import dev.achmad.core.network.NetworkPreferences
import dev.achmad.ownsafe.common.extension.navigateAndPop
import dev.achmad.ownsafe.common.extension.toast
import dev.achmad.ownsafe.ui.home.Home
import dev.achmad.ownsafe.ui.home.homeDestination
import dev.achmad.ownsafe.ui.login.Login
import dev.achmad.ownsafe.ui.login.loginDestination
import dev.achmad.ownsafe.ui.profile.Profile
import dev.achmad.ownsafe.ui.profile.profileDestination
import dev.achmad.ownsafe.ui.splash.Splash
import dev.achmad.ownsafe.ui.splash.splashDestination
import dev.achmad.ownsafe.ui.theme.DarkColorScheme
import dev.achmad.ownsafe.ui.theme.LightColorScheme
import dev.achmad.ownsafe.ui.theme.Typography
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import soup.compose.material.motion.animation.materialSharedAxisXIn
import soup.compose.material.motion.animation.materialSharedAxisXOut
import soup.compose.material.motion.animation.rememberSlideDistance
import javax.inject.Inject

val LocalDarkTheme = compositionLocalOf { false }
val LocalColorScheme = compositionLocalOf { LightColorScheme }
val LocalMainState = compositionLocalOf { MutableStateFlow(MainState()).asStateFlow() }

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val viewModel = hiltViewModel<MainActivityViewModel>()
            val navController = rememberNavController()
            val slideDistance = rememberSlideDistance()
            val context = LocalContext.current
            val appTheme by viewModel.appTheme.collectAsState()
            val dynamicColor by viewModel.dynamicColor.collectAsState()
            val cookies by viewModel.cookies.collectAsState()

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
                LocalDarkTheme provides darkTheme,
                LocalMainState provides viewModel.state
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
                        NavHost(
                            navController = navController,
                            startDestination = Splash,
                            enterTransition = { materialSharedAxisXIn(true, slideDistance) },
                            exitTransition = { materialSharedAxisXOut(true, slideDistance) },
                            popEnterTransition = { materialSharedAxisXIn(false, slideDistance) },
                            popExitTransition = { materialSharedAxisXOut(false, slideDistance) }
                        ) {
                            splashDestination(
                                onNavigate = {
                                    if (cookies.isEmpty()) {
                                        navController.navigateAndPop(Login, Splash)
                                        return@splashDestination
                                    }
                                    navController.navigateAndPop(Home, Splash)
                                }
                            )
                            loginDestination(
                                onLogin = { navController.navigateAndPop(Home, Login) }
                            )
                            homeDestination(
                                onGetUser = { viewModel.getUser() },
                                onNavigateToProfile = {
                                    navController.navigate(Profile)
                                }
                            )
                            profileDestination(
                                onLogout = { navController.navigateAndPop(Login, Home) },
                                onRefresh = { viewModel.getUser() },
                                onSaveProfile = { username, password ->
                                    viewModel.saveProfile(username, password) {
                                        val message = it ?: "Saved successfully"
                                        context.toast(message)
                                    }
                                },
                                onBack = { navController.navigateUp() }
                            )
                        }
                    }
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
}
