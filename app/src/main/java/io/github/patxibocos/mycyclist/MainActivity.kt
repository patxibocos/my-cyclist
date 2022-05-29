package io.github.patxibocos.mycyclist

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.SideEffect
import androidx.core.view.WindowCompat
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import dagger.hilt.android.AndroidEntryPoint
import io.github.patxibocos.mycyclist.ui.home.Home
import io.github.patxibocos.mycyclist.ui.theme.AppTheme

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        setContent {
            AppTheme {
                val systemUiController = rememberSystemUiController()
                val navigationBarColor = MaterialTheme.colorScheme.surface
                SideEffect {
                    systemUiController.setSystemBarsColor(navigationBarColor)
                }
                Home()
            }
        }
    }
}
