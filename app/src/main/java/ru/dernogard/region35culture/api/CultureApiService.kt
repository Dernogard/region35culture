package ru.dernogard.region35culture.api

import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo
import io.reactivex.rxkotlin.flatMapIterable
import io.reactivex.schedulers.Schedulers
import ru.dernogard.region35culture.database.models.CultureObject
import ru.dernogard.region35culture.database.models.CultureObjectResponse
import ru.dernogard.region35culture.database.repo.CultureObjectLocalRepo
import javax.inject.Inject

interface CultureInternetApi {
    fun getDataAndSaveIt()
}

class CultureApiService @Inject constructor(
    private val cultureObjectRepo: CultureObjectLocalRepo,
    private val cultureObjectRetrofitLoader: CultureObjectApi
    ): CultureInternetApi {

    private val disposablesStorage = CompositeDisposable()

    override fun getDataAndSaveIt() {
        cultureObjectRetrofitLoader
            .getData("111504")
            .flatMapIterable()
            .skip(2) // skip first two elements - a header of a table
            .map { convertToCultureObject(it) }
            .filter { obj -> obj.title.isNotEmpty() }  // remove error parsing objects
            .toList()
            .toObservable()
            .onErrorReturn { emptyList() }
            .subscribeOn(Schedulers.io())
            .subscribe { list -> saveDataToDatabase(list) }
            .addTo(disposablesStorage)
    }

    private fun convertToCultureObject(resp: CultureObjectResponse): CultureObject {
        val regexSpace: Regex = "[\\s]+".toRegex()
        fun prepareString(string: String) = string.replace(regexSpace, " ")
        fun prepareDouble(string: String) = string.toDoubleOrNull() ?: 0.0

        return try {
            CultureObject(
                number = resp.number.toIntOrNull() ?: 0,
                title = prepareString(resp.title),
                addressGov = prepareString(resp.addressGov),
                address = prepareString(resp.address),
                documentName = prepareString(resp.documentName),
                latitude = prepareDouble(resp.latitude),
                longitude = prepareDouble(resp.longitude),
                type = prepareString(resp.type),
                borderX = prepareString(resp.borderX),
                borderY = prepareString(resp.borderY)
            )
        } catch (e: Exception) {
            //if any data are error just ignore object returning an empty culture object
            CultureObject(
                0, "", "", "",
                "", 0.0, 0.0, "", "", ""
            )
        }
    }

    private fun saveDataToDatabase(list: List<CultureObject>) {
        cultureObjectRepo.saveAllInLocalDatabase(list)
    }
}