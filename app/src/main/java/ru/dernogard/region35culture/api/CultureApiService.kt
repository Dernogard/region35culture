package ru.dernogard.region35culture.api

import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo
import io.reactivex.rxkotlin.flatMapIterable
import io.reactivex.schedulers.Schedulers
import ru.dernogard.region35culture.database.models.CultureObject
import ru.dernogard.region35culture.database.models.CultureObjectResponse
import ru.dernogard.region35culture.database.repo.CultureObjectRepository
import ru.dernogard.region35culture.di.LocalCultureRepository
import javax.inject.Inject

interface CultureInternetApi {
    fun getDataAndSaveIt()
}

class CultureApiService @Inject constructor(
    @LocalCultureRepository private val cultureObjectRepo: CultureObjectRepository,
    private val cultureObjectRetrofitLoader: CultureObjectLoader
) : CultureInternetApi {

    private val disposablesStorage = CompositeDisposable()

    override fun getDataAndSaveIt() {
        cultureObjectRetrofitLoader
            .getCultureDataRetrofit()
            .retry(3)
            .flatMapIterable()
            .skip(2) // skip first two elements - a header of the table
            .map { convertToCultureObject(it) }
            .toList()
            .toObservable()
            .onErrorReturn { emptyList() }
            .subscribeOn(Schedulers.io())
            .subscribe { list -> saveDataToDatabase(list) }
            .addTo(disposablesStorage)
    }

    private fun convertToCultureObject(resp: CultureObjectResponse): CultureObject {
        val regexSpace: Regex = "[\\s]+".toRegex()
        // Few object's title have useless space characters
        fun prepareString(string: String) = string.replace(regexSpace, " ")
        fun prepareDouble(string: String) = string.toDoubleOrNull() ?: 0.0
        fun prepareInt(string: String) = string.toIntOrNull() ?: 0

        return CultureObject(
            number = prepareInt(resp.number),
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
    }

    private fun saveDataToDatabase(list: List<CultureObject>) {
        cultureObjectRepo.saveAll(list)
    }
}