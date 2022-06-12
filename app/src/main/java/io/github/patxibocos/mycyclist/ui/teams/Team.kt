package io.github.patxibocos.mycyclist.ui.teams

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SmallTopAppBar
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import io.github.patxibocos.mycyclist.data.Rider
import io.github.patxibocos.mycyclist.ui.data.TeamDetails
import io.github.patxibocos.mycyclist.ui.preview.riderPreview
import io.github.patxibocos.mycyclist.ui.preview.teamPreview

@OptIn(ExperimentalMaterial3Api::class)
@Preview
@Composable
internal fun TeamScreen(
    teamDetails: TeamDetails = TeamDetails(
        teamPreview,
        listOf(riderPreview)
    ),
    onRiderSelected: (Rider) -> Unit = {},
    onBackPressed: () -> Unit = {},
) {
    Scaffold(topBar = {
        SmallTopAppBar(
            title = {
                Text(text = teamDetails.team.name)
            }, navigationIcon = {
                IconButton(onClick = onBackPressed) {
                    Icon(Icons.Filled.ArrowBack, null)
                }
            }
        )
    }) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(it)
        ) {
            Text(text = teamDetails.team.name)
            RidersList(teamDetails.riders, onRiderSelected)
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
