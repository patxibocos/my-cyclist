package io.github.patxibocos.roadcyclingdata

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.primarySurface
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import dagger.hilt.android.AndroidEntryPoint
import io.github.patxibocos.roadcyclingdata.ui.home.Home
import io.github.patxibocos.roadcyclingdata.ui.theme.RoadCyclingDataTheme

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            RoadCyclingDataTheme {
                val systemUiController = rememberSystemUiController()
                val navigationBarColor = MaterialTheme.colors.primarySurface
                SideEffect {
                    systemUiController.setSystemBarsColor(color = Color.Transparent)
                    systemUiController.setNavigationBarColor(navigationBarColor)
                    systemUiController.setStatusBarColor(navigationBarColor)
                }
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
