package io.github.patxibocos.mycyclist

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.compositeOver
import androidx.compose.ui.unit.dp
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import dagger.hilt.android.AndroidEntryPoint
import io.github.patxibocos.mycyclist.ui.home.Home
import io.github.patxibocos.mycyclist.ui.theme.AppTheme
import kotlin.math.ln

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AppTheme {
                val systemUiController = rememberSystemUiController()
                val alpha = ((4.5f * ln(3.dp.value + 1)) + 2f) / 100f
                val navigationBarColor =
                    MaterialTheme.colorScheme.surfaceTint.copy(alpha = alpha)
                        .compositeOver(MaterialTheme.colorScheme.surface)
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
