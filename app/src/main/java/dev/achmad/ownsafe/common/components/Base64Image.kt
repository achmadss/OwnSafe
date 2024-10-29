package dev.achmad.ownsafe.common.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Person
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import coil3.compose.AsyncImage
import kotlin.io.encoding.Base64
import kotlin.io.encoding.ExperimentalEncodingApi

@OptIn(ExperimentalEncodingApi::class)
@Composable
fun Base64Image(
    modifier: Modifier = Modifier,
    base64String: String,
) {
    AsyncImage(
        modifier = modifier,
        model = if (base64String.isNotEmpty())
            Base64.decode(base64String.substringAfter("base64,")) else null,
        contentDescription = null,
        placeholder = rememberVectorPainter(image = Icons.Outlined.Person),
        error = rememberVectorPainter(image = Icons.Outlined.Person)
    )
}
