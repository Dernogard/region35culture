package ru.dernogard.region35culture.worker

import android.content.Context
import android.util.Log
import androidx.work.*
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo
import io.reactivex.rxkotlin.flatMapIterable
import io.reactivex.schedulers.Schedulers
import ru.dernogard.region35culture.api.CultureObjectLoader
import ru.dernogard.region35culture.database.models.CultureObject
import ru.dernogard.region35culture.database.models.CultureObjectResponse
import ru.dernogard.region35culture.database.repo.CultureObjectLocalRepo
import java.lang.NumberFormatException
import java.util.concurrent.TimeUnit
import javax.inject.Inject

/**
 * This class works every 12 hours (if it is not blocking user's phone) for
 * updating information about culture objects from API
 */

class UpdateCultureWorker(context: Context, params: WorkerParameters) : Worker(context, params) {

    private val disposablesStorage = CompositeDisposable()
    @Inject lateinit var cultureObjectRepo: CultureObjectLocalRepo

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
            CultureObjectLoader
                .getCultureDataRetrofit()
                .flatMapIterable()
                .map { convertToCultureObject(it) }
                .filter { o -> o.title.isEmpty() }
                .toList()
                .toObservable()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { list -> saveDataToDatabase(list) }
                .addTo(disposablesStorage)

            Result.success()
        } catch (e: Exception) {
            Result.failure()
        }
    }

    private fun convertToCultureObject(resp: CultureObjectResponse): CultureObject {
        return try {
            CultureObject(
                number = resp.number.toLong(),
                title = resp.title,
                addressGov = resp.addressGov,
                address = resp.address,
                documentName = resp.documentName,
                latitude = resp.latitude.toFloat(),
                longitude = resp.longitude.toFloat(),
                type = resp.type,
                borderX = resp.borderX,
                borderY = resp.borderY
            )
        } catch (e: NumberFormatException) {
            //if any data are error just ignore object returning an empty culture object
            CultureObject(
                0L, "", "", "",
                "", 0f, 0f, "", "", ""
            )
        }
    }

    private fun saveDataToDatabase(list: List<CultureObject>) {
     Log.e(javaClass.simpleName, "Size list for save = ${list.size}")
        cultureObjectRepo.saveAllInLocalDatabase(list)
    }

    override fun onStopped() {
        super.onStopped()
        disposablesStorage.dispose()
    }

}