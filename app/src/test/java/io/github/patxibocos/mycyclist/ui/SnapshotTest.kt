package io.github.patxibocos.mycyclist.ui

import app.cash.paparazzi.DeviceConfig
import app.cash.paparazzi.Paparazzi
import io.github.patxibocos.mycyclist.ui.preview.RacePreview
import io.github.patxibocos.mycyclist.ui.preview.RiderPreview
import io.github.patxibocos.mycyclist.ui.preview.TeamPreview
import org.junit.Rule
import org.junit.Test

class SnapshotTest {

    @get:Rule
    val paparazzi = Paparazzi(
        deviceConfig = DeviceConfig.PIXEL_6_PRO,
    )

    @Test
    fun rider() {
        paparazzi.snapshot {
            RiderPreview()
        }
    }

    @Test
    fun team() {
        paparazzi.snapshot {
            TeamPreview()
        }
    }

    @Test
    fun race() {
        paparazzi.snapshot {
            RacePreview()
        }
    }
}
