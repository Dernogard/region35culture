package ru.dernogard.region35culture.worker

import android.content.Context
import androidx.work.*
import ru.dernogard.region35culture.CultureApiService
import java.util.concurrent.TimeUnit

/**
 * This class works every 12 hours (if it is not blocking user's phone) for
 * updating information about culture objects from API
 */

class UpdateCultureWorker(context: Context, params: WorkerParameters) : Worker(context, params) {

    companion object {
        fun installBackgroundWork(): PeriodicWorkRequest {
            val constraint = Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build()

            // Update Culture Objects List from api every 12 hours
            return PeriodicWorkRequest
                .Builder(UpdateCultureWorker::class.java, 12, TimeUnit.HOURS)
                .setConstraints(constraint)
                .build()
        }
    }

    override fun doWork() = checkUpdate()

    private fun checkUpdate(): Result {
        return try {
            //CultureApiService().getDataAndSaveIt()

            Result.success()
        } catch (e: Exception) {
            Result.failure()
        }
    }

}