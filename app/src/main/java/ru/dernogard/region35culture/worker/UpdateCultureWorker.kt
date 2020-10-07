package ru.dernogard.region35culture.worker

import android.content.Context
import androidx.work.*
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.android.EntryPointAccessors
import dagger.hilt.android.components.ApplicationComponent
import ru.dernogard.region35culture.api.CultureInternetApi
import java.util.concurrent.TimeUnit

/**
 * This class works every few hours
 * (or after starting app if background work is blocked by user's phone OS)
 * for updating information about culture objects from API.
 */

const val UPDATE_CULTURE_WORK_TAG = "updateCultureWorkTag"

class UpdateCultureWorker(context: Context, params: WorkerParameters) : Worker(context, params) {

    // Need for hilt's inject
    @EntryPoint
    @InstallIn(ApplicationComponent::class)
    interface UpdateCultureWorkEntryPoint{
        fun cultureApiService(): CultureInternetApi
    }

    companion object {
        fun installBackgroundWork(): PeriodicWorkRequest {
            val constraint = Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build()

            // Update objects of the culture from api every 24 hours
            return PeriodicWorkRequest
                .Builder(UpdateCultureWorker::class.java, 24, TimeUnit.HOURS)
                .setConstraints(constraint)
                .addTag(UPDATE_CULTURE_WORK_TAG)
                .build()
        }
    }

    override fun doWork() = checkUpdate()

    private fun checkUpdate(): Result {
        return try {
            val hiltEntryPoint =
                EntryPointAccessors
                    .fromApplication(applicationContext, UpdateCultureWorkEntryPoint::class.java)

            hiltEntryPoint.cultureApiService().getDataAndSaveIt()
            Result.success()
        } catch (e: Exception) {
            Result.failure()
        }
    }

}