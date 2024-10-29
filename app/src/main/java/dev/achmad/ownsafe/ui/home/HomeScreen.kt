package dev.achmad.ownsafe.ui.home

import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.DriveFolderUpload
import androidx.compose.material.icons.filled.Link
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import dev.achmad.ownsafe.MainState
import dev.achmad.ownsafe.common.components.Base64Image
import dev.achmad.ownsafe.ui.home.files.Files
import dev.achmad.ownsafe.ui.home.files.filesDestination
import dev.achmad.ownsafe.ui.home.me.Me
import dev.achmad.ownsafe.ui.home.me.meDestination
import dev.achmad.ownsafe.ui.home.media.Media
import dev.achmad.ownsafe.ui.home.media.mediaDestination
import dev.achmad.ownsafe.ui.home.shorturl.ShortURL
import dev.achmad.ownsafe.ui.home.shorturl.shortUrlDestination
import dev.achmad.ownsafe.ui.theme.LocalMainViewModel
import kotlinx.serialization.Serializable

@Serializable
object Home

data class HomeScreenMenu<T : Any>(
    val text: String,
    val icon: ImageVector,
    val route: T
)

val menus = listOf(
    HomeScreenMenu("Media", Icons.Default.PhotoLibrary, Media),
    HomeScreenMenu("Files", Icons.Default.DriveFolderUpload, Files),
    HomeScreenMenu("Short URLs", Icons.Default.Link, ShortURL),
    HomeScreenMenu("Me", Icons.Default.AccountCircle, Me),
)

fun NavGraphBuilder.homeDestination(
    onGetUser: () -> Unit,
    onNavigateToProfile: () -> Unit,
    onNavigateToStatistics: () -> Unit,
    onNavigateToDataAndStorage: () -> Unit,
    onNavigateToAppearance: () -> Unit,
) {
    composable<Home> { backStackEntry ->
        val tabNavController = rememberNavController()
        val snackbarHostState = remember { SnackbarHostState() }
        val mainState by LocalMainViewModel.current.state.collectAsState()
        val viewModel = hiltViewModel<HomeViewModel>()
        val alwaysShowNavLabels by viewModel.alwaysShowNavLabels.collectAsState()

        LaunchedEffect(Unit) {
            if (mainState.user == null) {
                onGetUser()
            }
        }

        HomeScreen(
            tabNavController = tabNavController,
            snackbarHostState = snackbarHostState,
            mainState = mainState,
            alwaysShowNavLabels = alwaysShowNavLabels,
            onNavigateToProfile = onNavigateToProfile,
            onNavigateToStatistics = onNavigateToStatistics,
            onNavigateToDataAndStorage = onNavigateToDataAndStorage,
            onNavigateToAppearance = onNavigateToAppearance
        )
    }
}

@Composable
private fun HomeScreen(
    tabNavController: NavHostController = rememberNavController(),
    snackbarHostState: SnackbarHostState = remember { SnackbarHostState() },
    mainState: MainState = MainState(),
    alwaysShowNavLabels: Boolean = true,
    onNavigateToProfile: () -> Unit = {},
    onNavigateToStatistics: () -> Unit = {},
    onNavigateToDataAndStorage: () -> Unit = {},
    onNavigateToAppearance: () -> Unit = {},
) {
    Scaffold(
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState)
        },
        bottomBar = {
            val navBackStackEntry by tabNavController.currentBackStackEntryAsState()
            val currentDestination = navBackStackEntry?.destination
            NavigationBar {
                menus.forEach { menu ->
                    NavigationBarItem(
                        selected = currentDestination?.hierarchy?.any { it.hasRoute(menu.route::class) } == true,
                        onClick = {
                            tabNavController.navigate(menu.route) {
                                // Pop up to the start destination of the graph to
                                // avoid building up a large stack of destinations
                                // on the back stack as users select items
                                popUpTo(tabNavController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                // Avoid multiple copies of the same destination when
                                // re-selecting the same item
                                launchSingleTop = true
                                // Restore state when re-selecting a previously selected item
                                restoreState = true
                            }
                        },
                        icon = {
                            if (menu.route is Me && mainState.user != null) {
                                Base64Image(
                                    modifier = Modifier
                                        .size(24.dp)
                                        .clip(CircleShape),
                                    base64String = mainState.user.avatar
                                )
                            } else {
                                Icon(imageVector = menu.icon, contentDescription = menu.text)
                            }
                        },
                        label = if (alwaysShowNavLabels) {
                            { Text(text = menu.text) }
                        } else null
                    )
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            modifier = Modifier.padding(innerPadding),
            navController = tabNavController,
            startDestination = Media,
            enterTransition = {
                fadeIn(animationSpec = tween(200))
            },
            exitTransition = {
                fadeOut(animationSpec = tween(200))
            }
        ) {
            mediaDestination()
            filesDestination()
            shortUrlDestination()
            meDestination(
                onNavigateToProfile = onNavigateToProfile,
                onNavigateToStatistics = onNavigateToStatistics,
                onNavigateToDataAndStorage = onNavigateToDataAndStorage,
                onNavigateToAppearance = onNavigateToAppearance,
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun PreviewHomeScreen() {
    HomeScreen()
}