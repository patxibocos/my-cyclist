package io.github.patxibocos.roadcyclingdata

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import io.github.patxibocos.roadcyclingdata.data.db.AppDatabase
import io.github.patxibocos.roadcyclingdata.ui.theme.RoadCyclingDataTheme
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.util.Locale

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        GlobalScope.launch {
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

@Composable
fun CountryEmoji(country: Country) {
    Text(
        text = "Hello ${getEmoji(country)}!",
        fontSize = 30.sp,
    )
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