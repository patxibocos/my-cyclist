package io.github.patxibocos.roadcyclingdata

import android.app.Application
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.items
import com.google.accompanist.coil.rememberCoilPainter
import io.github.patxibocos.roadcyclingdata.data.RiderDataSource
import io.github.patxibocos.roadcyclingdata.data.db.AppDatabase
import io.github.patxibocos.roadcyclingdata.data.db.Rider
import io.github.patxibocos.roadcyclingdata.data.db.Team
import io.github.patxibocos.roadcyclingdata.ui.theme.RoadCyclingDataTheme
import kotlinx.coroutines.flow.Flow
import java.util.Locale

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            RoadCyclingDataTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    color = MaterialTheme.colors.background,
                    modifier = Modifier.fillMaxSize()
                ) {
//                    TeamsScreen()
                    RidersScreen()
                }
            }
        }
    }
}

@JvmInline
value class Country(private val code: String) {
    init {
        require(Locale.getISOCountries().contains(code.uppercase()))
    }

    fun code(): String {
        return code.uppercase()
    }
}

class TeamsViewModel(application: Application) : AndroidViewModel(application) {

    fun getTeams(): Flow<List<Team>> {
        return AppDatabase.getInstance(getApplication()).teamsDao().getTeams()
    }

}

class RidersViewModel(application: Application) : AndroidViewModel(application) {

    val riders: Flow<PagingData<Rider>> = Pager(PagingConfig(pageSize = 20)) {
        RiderDataSource(AppDatabase.getInstance(getApplication()).ridersDao())
    }.flow

}

@Composable
fun TeamsScreen(teamsViewModel: TeamsViewModel = viewModel()) {
    TeamsList(teamsViewModel.getTeams().collectAsState(initial = emptyList()).value)
}

@Composable
fun RidersScreen(ridersViewModel: RidersViewModel = viewModel()) {
    val riders: LazyPagingItems<Rider> = ridersViewModel.riders.collectAsLazyPagingItems()
    RidersList(riders)
}

@Composable
fun RidersList(riders: LazyPagingItems<Rider>) {
    LazyColumn(modifier = Modifier.fillMaxSize()) {
        items(riders) { rider ->
            if (rider != null) {
                RiderRow(rider)
            }
        }
    }
}

@Composable
fun RiderRow(rider: Rider) {
    Row {
        Text(
            text = "${rider.firstName} ${rider.lastName}",
            style = MaterialTheme.typography.body1,
        )
    }
}

@Composable
fun TeamsList(teams: List<Team>) {
    LazyColumn(modifier = Modifier.fillMaxSize()) {
        items(teams) { team ->
            TeamRow(team)
        }
    }
}

@Composable
fun TeamRow(team: Team) {
    Row {
        Box {
            Text(
                modifier = Modifier.padding(start = 75.dp),
                text = getEmoji(Country(team.country)),
                style = MaterialTheme.typography.h3,
            )
            Image(
                modifier = Modifier.size(100.dp),
                painter = rememberCoilPainter(team.jersey),
                contentDescription = null,
            )
        }
        Text(
            text = team.name,
            style = MaterialTheme.typography.body1,
            modifier = Modifier.padding(horizontal = 16.dp)
        )
    }
}

fun getEmoji(country: Country): String {
    // Based on https://dev.to/jorik/country-code-to-flag-emoji-a21
    val codePoints = country.code()
        .map { char -> 127397 + char.toString().codePointAt(0) }
        .toIntArray()
    return String(codePoints, 0, codePoints.size)
}