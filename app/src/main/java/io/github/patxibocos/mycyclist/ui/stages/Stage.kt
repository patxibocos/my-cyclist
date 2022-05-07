package io.github.patxibocos.mycyclist.ui.stages

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import io.github.patxibocos.mycyclist.data.Stage
import io.github.patxibocos.mycyclist.ui.preview.stagePreview
import io.github.patxibocos.mycyclist.ui.util.isoFormat

@Preview
@Composable
fun StageScreen(stage: Stage = stagePreview) {
    Column(modifier = Modifier.fillMaxSize()) {
        Text(text = isoFormat(stage.startDateTime))
        Text(text = stage.distance.toString())
    }
}
