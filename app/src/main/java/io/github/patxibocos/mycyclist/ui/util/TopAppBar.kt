package io.github.patxibocos.mycyclist.ui.util

import androidx.compose.foundation.layout.Box
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CenterAlignedTopAppBar(title: String) {
    androidx.compose.material3.CenterAlignedTopAppBar(
        colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
            containerColor = Color.Transparent
        ),
        title = {
            Text(text = title)
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SmallTopAppBar(title: String, onBackPressed: () -> Unit) {
    androidx.compose.material3.TopAppBar(
        colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
            containerColor = Color.Transparent
        ),
        title = {
            Text(text = title)
        },
        navigationIcon = {
            IconButton(onClick = onBackPressed) {
                Icon(Icons.Filled.ArrowBack, null)
            }
        }
    )
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun RefreshableContent(
    isRefreshing: Boolean,
    onRefreshed: () -> Unit,
    content: @Composable () -> Unit
) {
    val state = rememberPullRefreshState(isRefreshing, onRefreshed)
    Box(Modifier.pullRefresh(state)) {
        content()
        PullRefreshIndicator(isRefreshing, state, Modifier.align(Alignment.TopCenter))
    }
}
