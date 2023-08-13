package io.github.patxibocos.mycyclist.ui

import app.cash.paparazzi.DeviceConfig
import app.cash.paparazzi.Paparazzi
import io.github.patxibocos.mycyclist.ui.preview.RiderPreview
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
}
