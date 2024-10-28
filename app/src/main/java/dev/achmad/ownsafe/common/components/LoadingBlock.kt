package dev.achmad.ownsafe.common.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import dev.achmad.ownsafe.common.extension.disableGestures

@Composable
fun LoadingBlock() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0x8C000000))
            .disableGestures(),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator()
    }
}