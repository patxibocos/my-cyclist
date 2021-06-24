package io.github.patxibocos.roadcyclingdata.worker

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import io.github.patxibocos.roadcyclingdata.data.db.AppDatabase
import io.github.patxibocos.roadcyclingdata.data.db.Team
import io.github.patxibocos.roadcyclingdata.data.json.TeamsAndRiders
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json

class SeedDatabaseWorker(
    context: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(context, workerParams) {

    override suspend fun doWork(): Result = withContext(Dispatchers.IO) {
        try {
            val fileName = inputData.getString("dataFile") ?: return@withContext Result.failure()
            val teamsAndRiders = applicationContext.assets.open(fileName).use { inputStream ->
                val jsonString = inputStream.bufferedReader().use { it.readText() }
                Json {
                    ignoreUnknownKeys = true
                }.decodeFromString<TeamsAndRiders>(jsonString)
            }
            val database = AppDatabase.getInstance(applicationContext)
            val teams = teamsAndRiders.teams.map { Team(it.id) }
            database.teamsDao().insertAll(teams)
            Result.success()
        } catch (e: Exception) {
            println(e)
            Result.failure()
        }
    }

}