package io.github.patxibocos.roadcyclingdata.ui.teams

import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import coil.compose.rememberImagePainter
import io.github.patxibocos.pcsscraper.protobuf.TeamOuterClass.Team
import io.github.patxibocos.roadcyclingdata.ui.preview.teamPreview

@Preview
@Composable
internal fun TeamsScreen(
    teams: List<Team> = listOf(teamPreview),
    onTeamSelected: (Team) -> Unit = {}
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 10.dp),
        verticalArrangement = Arrangement.spacedBy(5.dp),
    ) {
        teams.groupBy { it.status }.forEach { (status, teams) ->
            item {
                Text(text = status.name)
            }
            items(items = teams, key = Team::getId) { team ->
                TeamRow(team, onTeamSelected)
            }
        }
    }
}

@Composable
internal fun TeamRow(
    team: Team,
    onTeamSelected: (Team) -> Unit
) {
    ConstraintLayout(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onTeamSelected(team) }
    ) {
        val (jerseyImage, abbreviation, name, bike) = createRefs()
        Text(
            text = team.abbreviation,
            style = MaterialTheme.typography.h5.copy(color = Color.Gray),
            modifier = Modifier.constrainAs(abbreviation) {
                start.linkTo(jerseyImage.start)
                end.linkTo(jerseyImage.end)
                top.linkTo(jerseyImage.top)
                bottom.linkTo(jerseyImage.bottom)
            }
        )
        Image(
            modifier = Modifier
                .constrainAs(jerseyImage) {
                    start.linkTo(parent.start)
                }
                .padding(horizontal = 16.dp)
                .size(75.dp)
                .border(2.dp, Color.Gray, RoundedCornerShape(5.dp))
                .padding(horizontal = 2.dp),
            painter = rememberImagePainter(
                data = team.jersey,
            ),
            contentDescription = null,
        )
        Text(
            text = team.name,
            style = MaterialTheme.typography.body1,
            modifier = Modifier
                .constrainAs(name) {
                    start.linkTo(jerseyImage.end)
                    top.linkTo(jerseyImage.top)
                }
                .padding(top = 10.dp)
        )
        Text(
            text = "\uD83D\uDEB4 ${team.bike}",
            style = MaterialTheme.typography.body1,
            modifier = Modifier
                .constrainAs(bike) {
                    start.linkTo(jerseyImage.end)
                    bottom.linkTo(jerseyImage.bottom)
                }
                .padding(bottom = 10.dp)
        )
    }
}
