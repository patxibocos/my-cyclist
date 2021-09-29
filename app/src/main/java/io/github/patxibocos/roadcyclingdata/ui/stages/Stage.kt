package io.github.patxibocos.roadcyclingdata.ui.stages

import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import io.github.patxibocos.pcsscraper.protobuf.race.RaceOuterClass.Stage
import io.github.patxibocos.roadcyclingdata.ui.preview.stagePreview

@Preview
@Composable
fun StageScreen(stage: Stage? = stagePreview) {
    if (stage != null) {
        Text(text = stage.distance.toString())
    }
}
