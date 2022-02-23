package io.github.patxibocos.roadcyclingdata.ui.stages

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import io.github.patxibocos.roadcyclingdata.data.Stage
import io.github.patxibocos.roadcyclingdata.ui.preview.stagePreview
import io.github.patxibocos.roadcyclingdata.ui.util.isoFormat

@Preview
@Composable
fun StageScreen(stage: Stage = stagePreview) {
    Column(modifier = Modifier.fillMaxSize()) {
        Text(text = isoFormat(stage.startDateTime))
        Text(text = stage.distance.toString())
    }
}
