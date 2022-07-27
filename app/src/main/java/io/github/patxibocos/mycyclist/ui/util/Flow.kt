/* ktlint-disable filename */
package io.github.patxibocos.mycyclist.ui.util

import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.repeatOnLifecycle
import kotlinx.coroutines.flow.StateFlow

@Composable
fun <T> StateFlow<T>.rememberFlowWithLifecycle(): State<T> {
    val initialValue = remember(this) { this.value }
    val lifecycle: Lifecycle = LocalLifecycleOwner.current.lifecycle
    return produceState(
        key1 = this,
        initialValue = initialValue
    ) {
        lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
            this@rememberFlowWithLifecycle.collect {
                this@produceState.value = it
            }
        }
    }
}
