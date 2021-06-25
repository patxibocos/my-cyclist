package io.github.patxibocos.roadcyclingdata

import android.app.Application
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.work.WorkInfo
import androidx.work.WorkManager
import io.github.patxibocos.roadcyclingdata.data.db.AppDatabase
import io.github.patxibocos.roadcyclingdata.ui.theme.RoadCyclingDataTheme
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.Locale

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        CoroutineScope(Dispatchers.IO).launch {
            AppDatabase.getInstance(applicationContext)
        }
        setContent {
            RoadCyclingDataTheme {
                // A surface container using the 'background' color from the theme
                Surface(color = MaterialTheme.colors.background) {
                    CountryEmoji(Country("es"))
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

class WhateverViewModel(application: Application) : AndroidViewModel(application) {

    private val progressWorkInfoItems: LiveData<List<WorkInfo>>
    private val workManager: WorkManager = WorkManager.getInstance(application)
    private val _workState: MutableLiveData<Boolean> = MutableLiveData(false)
    val workState: LiveData<Boolean> = _workState

    init {
        progressWorkInfoItems = workManager.getWorkInfosForUniqueWorkLiveData("workName")
    }

    private val observer =
        Observer<List<WorkInfo>> { workInfos ->
            val workersDone = workInfos.all { it.state.isFinished }
            _workState.value = workersDone
        }

    fun whatever() {
        progressWorkInfoItems.observeForever(observer)
    }

    override fun onCleared() {
        super.onCleared()
        progressWorkInfoItems.removeObserver(observer)
    }

}

@Composable
fun CountryEmoji(country: Country, whateverViewModel: WhateverViewModel = viewModel()) {
    whateverViewModel.whatever()
    Column {
        Text(
            text = "Hello ${getEmoji(country)}!",
            fontSize = 30.sp,
        )
        WorkState(whateverViewModel.workState.observeAsState(false).value)
    }
}

@Composable
fun WorkState(workState: Boolean) {
    Text("Work state is done? $workState")
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    RoadCyclingDataTheme {
        CountryEmoji(Country("es"))
    }
}

fun getEmoji(country: Country): String {
    // Based on https://dev.to/jorik/country-code-to-flag-emoji-a21
    val codePoints = country.code()
        .map { char -> 127397 + char.toString().codePointAt(0) }
        .toIntArray()
    return String(codePoints, 0, codePoints.size)
}