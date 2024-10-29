package dev.achmad.ownsafe.ui.home.shorturl

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import kotlinx.serialization.Serializable

@Serializable
object ShortURL

fun NavGraphBuilder.shortUrlDestination() {
    composable<ShortURL> {
        ShortURLScreen()
    }
}

@Composable
private fun ShortURLScreen() {
    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        Text(
            modifier = Modifier.align(Alignment.Center),
            text = "Short URL Screen"
        )
    }
}