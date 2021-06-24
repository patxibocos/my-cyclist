package io.github.patxibocos.roadcyclingdata.worker

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import io.github.patxibocos.roadcyclingdata.data.db.AppDatabase
import io.github.patxibocos.roadcyclingdata.data.db.Rider
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
            val teams = teamsAndRiders.teams.map {
                Team(
                    id = it.id,
                    name = it.name,
                    status = it.status,
                    abbreviation = it.abbreviation,
                    country = it.country,
                    bike = it.bike,
                    jersey = it.jersey,
                    website = it.website,
                    year = it.year,
                    riders = it.riders,
                )
            }
            val riders = teamsAndRiders.riders.map {
                Rider(
                    id = it.id,
                    firstName = it.firstName,
                    lastName = it.lastName,
                    country = it.country,
                    website = it.website,
                    birthDate = it.birthDate,
                    birthPlace = it.birthPlace,
                    weight = it.weight,
                    height = it.height,
                    photo = it.photo,
                )
            }
            println(riders.size)
            database.teamsDao().insertAll(teams)
            database.ridersDao().insertAll(riders)
            Result.success()
        } catch (e: Exception) {
            Result.failure()
        }
    }

}