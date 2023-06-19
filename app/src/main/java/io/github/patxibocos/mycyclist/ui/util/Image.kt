package io.github.patxibocos.mycyclist.ui.util

import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import coil.compose.AsyncImage

@Composable
fun RemoteImage(
    url: String,
    modifier: Modifier = Modifier,
    alignment: Alignment = Alignment.Center,
    contentScale: ContentScale = ContentScale.Fit,
) {
    AsyncImage(
        model = url,
        modifier = modifier,
        alignment = alignment,
        contentScale = contentScale,
        contentDescription = null,
    )
}
