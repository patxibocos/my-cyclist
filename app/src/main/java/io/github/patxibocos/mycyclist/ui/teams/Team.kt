/* ktlint-disable filename */
package io.github.patxibocos.mycyclist.ui.teams

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import io.github.patxibocos.mycyclist.data.Rider
import io.github.patxibocos.mycyclist.ui.preview.riderPreview
import io.github.patxibocos.mycyclist.ui.preview.teamPreview
import io.github.patxibocos.mycyclist.ui.util.SmallTopAppBar

@Composable
internal fun TeamRoute(
    onRiderSelected: (Rider) -> Unit = {},
    onBackPressed: () -> Unit = {},
    viewModel: TeamViewModel = hiltViewModel()
) {
    val teamViewState by viewModel.teamViewState.collectAsState()
    TeamScreen(
        teamViewState = teamViewState,
        onRiderSelected = onRiderSelected,
        onBackPressed = onBackPressed
    )
}

@Preview
@Composable
private fun TeamScreen(
    teamViewState: TeamViewState = TeamViewState(
        teamPreview,
        listOf(riderPreview)
    ),
    onRiderSelected: (Rider) -> Unit = {},
    onBackPressed: () -> Unit = {}
) {
    Column {
        SmallTopAppBar(title = teamViewState.team?.name.toString(), onBackPressed)
        if (teamViewState.team != null) {
            Text(text = teamViewState.team.name)
            RidersList(teamViewState.riders, onRiderSelected)
        }
    }
}

@Composable
private fun RidersList(riders: List<Rider>, onRiderSelected: (Rider) -> Unit) {
    LazyColumn(modifier = Modifier.fillMaxSize()) {
        items(items = riders, key = Rider::id, itemContent = { rider ->
            RiderRow(rider, onRiderSelected)
        })
    }
}

@Composable
private fun RiderRow(rider: Rider, onRiderSelected: (Rider) -> Unit) {
    Text(
        text = rider.lastName,
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onRiderSelected(rider) }
    )
}
