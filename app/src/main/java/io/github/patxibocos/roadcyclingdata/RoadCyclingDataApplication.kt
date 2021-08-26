package io.github.patxibocos.roadcyclingdata

import android.app.Application
import coil.Coil
import coil.ImageLoader
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.Dispatchers

@HiltAndroidApp
class RoadCyclingDataApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        Coil.setImageLoader {
            ImageLoader.Builder(this)
                .dispatcher(Dispatchers.Default)
                .crossfade(true)
                .build()
        }
    }
}
