package ru.dernogard.region35culture.worker

import android.content.Context
import androidx.work.*
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.android.EntryPointAccessors
import dagger.hilt.android.components.ApplicationComponent
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo
import ru.dernogard.region35culture.api.CultureInternetApi
import ru.dernogard.region35culture.database.models.CultureObject
import ru.dernogard.region35culture.database.repo.CultureObjectRepository
import ru.dernogard.region35culture.di.LocalCultureRepository
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
        fun getCultureApiService(): CultureInternetApi
        @LocalCultureRepository
        fun getCultureObjectRepository(): CultureObjectRepository
    }

    private val disposableStorage = CompositeDisposable()
    private val hiltEntryPoint = EntryPointAccessors
        .fromApplication(applicationContext, UpdateCultureWorkEntryPoint::class.java)

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
            hiltEntryPoint
                .getCultureApiService()
                .getData()
                .onErrorReturn { emptyList() }
                .subscribe { list ->
                    saveDataToDatabase(list)
                }
                .addTo(disposableStorage)

            Result.success()
        } catch (e: Exception) {
            Result.failure()
        }
    }

    private fun saveDataToDatabase(list: List<CultureObject>) {
        hiltEntryPoint.getCultureObjectRepository().saveAll(list)
    }

    override fun onStopped() {
        super.onStopped()
        disposableStorage.clear()
    }
}