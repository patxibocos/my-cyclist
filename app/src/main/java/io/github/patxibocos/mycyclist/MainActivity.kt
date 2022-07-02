package io.github.patxibocos.mycyclist

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.LocalAbsoluteTonalElevation
import androidx.compose.material3.Surface
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.unit.dp
import androidx.core.view.WindowCompat
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
                Surface(tonalElevation = 2.dp) {
                    CompositionLocalProvider(LocalAbsoluteTonalElevation provides 0.dp) {
                        Home()
                    }
                }
            }
        }
    }
}
