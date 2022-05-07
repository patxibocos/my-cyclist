package io.github.patxibocos.mycyclist

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import dagger.hilt.android.AndroidEntryPoint
import io.github.patxibocos.mycyclist.ui.home.Home
import io.github.patxibocos.mycyclist.ui.theme.AppTheme

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AppTheme {
                Surface {
                    val systemUiController = rememberSystemUiController()
                    val navigationBarColor = MaterialTheme.colorScheme.surfaceVariant
                    SideEffect {
                        systemUiController.setSystemBarsColor(color = Color.Transparent)
                        systemUiController.setNavigationBarColor(navigationBarColor)
                        systemUiController.setStatusBarColor(navigationBarColor)
                    }
                    Home()
                }
            }
        }
    }
}
