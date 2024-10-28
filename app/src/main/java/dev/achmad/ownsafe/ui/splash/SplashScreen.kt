package dev.achmad.ownsafe.ui.splash

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import dev.achmad.ownsafe.R
import dev.achmad.ownsafe.common.components.rememberResourceBitmapPainter
import dev.achmad.ownsafe.ui.theme.splashBackground
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable

@Serializable
object Splash

fun NavGraphBuilder.splashDestination(
    onNavigate: () -> Unit,
) {
    composable<Splash> {
        val scope = rememberCoroutineScope()
        LaunchedEffect(Unit) {
            scope.launch {
                delay(2000)
                // TODO handle login
                // TODO handle deeplink
                onNavigate()
            }
        }
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(splashBackground)
        ) {
            Image(
                modifier = Modifier.align(Alignment.Center).size(256.dp),
                painter = rememberResourceBitmapPainter(id = R.drawable.app_logo),
                contentDescription = null
            )
        }
    }
}