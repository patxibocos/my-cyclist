package io.github.patxibocos.roadcyclingdata

import android.app.Application
import io.github.patxibocos.roadcyclingdata.data.db.AppDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class RoadCyclingDataApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        CoroutineScope(Dispatchers.IO).launch {
            AppDatabase.getInstance(applicationContext).ridersDao().init()
        }
    }

}