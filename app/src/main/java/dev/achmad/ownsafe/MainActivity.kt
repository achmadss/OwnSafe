package dev.achmad.ownsafe

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.compose.NavHost
import dagger.hilt.android.AndroidEntryPoint
import dev.achmad.ownsafe.common.extension.navigateAndPop
import dev.achmad.ownsafe.common.extension.toast
import dev.achmad.ownsafe.ui.home.HomeRoute
import dev.achmad.ownsafe.ui.home.homeDestination
import dev.achmad.ownsafe.ui.login.LoginRoute
import dev.achmad.ownsafe.ui.login.loginDestination
import dev.achmad.ownsafe.ui.profile.ProfileRoute
import dev.achmad.ownsafe.ui.profile.profileDestination
import dev.achmad.ownsafe.ui.settings.appearance.AppearanceRoute
import dev.achmad.ownsafe.ui.settings.appearance.appearanceDestination
import dev.achmad.ownsafe.ui.settings.statistics.StatisticsRoute
import dev.achmad.ownsafe.ui.settings.statistics.statisticsDestination
import dev.achmad.ownsafe.ui.splash.SplashRoute
import dev.achmad.ownsafe.ui.splash.splashDestination
import dev.achmad.ownsafe.ui.theme.LocalMainViewModel
import dev.achmad.ownsafe.ui.theme.LocalNavController
import dev.achmad.ownsafe.ui.theme.OwnSafeTheme
import soup.compose.material.motion.animation.materialSharedAxisXIn
import soup.compose.material.motion.animation.materialSharedAxisXOut
import soup.compose.material.motion.animation.rememberSlideDistance

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            OwnSafeTheme {
                val slideDistance = rememberSlideDistance()
                val navController = LocalNavController.current
                val mainViewModel = LocalMainViewModel.current
                val cookies by mainViewModel.cookies.collectAsState()
                val context = LocalContext.current

                NavHost(
                    navController = navController,
                    startDestination = SplashRoute,
                    enterTransition = { materialSharedAxisXIn(true, slideDistance) },
                    exitTransition = { materialSharedAxisXOut(true, slideDistance) },
                    popEnterTransition = { materialSharedAxisXIn(false, slideDistance) },
                    popExitTransition = { materialSharedAxisXOut(false, slideDistance) }
                ) {
                    splashDestination(
                        onNavigate = {
                            if (cookies.isEmpty()) {
                                navController.navigateAndPop(LoginRoute, SplashRoute)
                                return@splashDestination
                            }
                            navController.navigateAndPop(HomeRoute, SplashRoute)
                        }
                    )
                    loginDestination(
                        onLogin = { navController.navigateAndPop(HomeRoute, LoginRoute) }
                    )
                    homeDestination(
                        onGetUser = { mainViewModel.getUser() },
                        onNavigateToProfile = {
                            navController.navigate(ProfileRoute)
                        },
                        onNavigateToStatistics = {
                            navController.navigate(StatisticsRoute)
                        },
                        onNavigateToDataAndStorage = {
                            // TODO
                        },
                        onNavigateToAppearance = {
                            navController.navigate(AppearanceRoute)
                        },
                    )
                    profileDestination(
                        onLogout = { navController.navigateAndPop(LoginRoute, HomeRoute) },
                        onRefresh = { mainViewModel.getUser() },
                        onSaveProfile = { username, password ->
                            mainViewModel.saveProfile(username, password) {
                                val message = it ?: "Saved successfully"
                                context.toast(message)
                            }
                        },
                        onBack = { navController.navigateUp() }
                    )
                    statisticsDestination(
                        onBack = { navController.navigateUp() }
                    )
                    appearanceDestination(
                        onBack = { navController.navigateUp() }
                    )
                }
            }
        }
    }
}
