package io.github.patxibocos.roadcyclingdata

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.ui.Modifier
import dagger.hilt.android.AndroidEntryPoint
import io.github.patxibocos.roadcyclingdata.ui.home.Home
import io.github.patxibocos.roadcyclingdata.ui.theme.RoadCyclingDataTheme
import java.util.Locale

@AndroidEntryPoint
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
                    Home()
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

fun getEmoji(country: Country): String {
    // Based on https://dev.to/jorik/country-code-to-flag-emoji-a21
    val codePoints = country.code()
        .map { char -> 127397 + char.toString().codePointAt(0) }
        .toIntArray()
    return String(codePoints, 0, codePoints.size)
}