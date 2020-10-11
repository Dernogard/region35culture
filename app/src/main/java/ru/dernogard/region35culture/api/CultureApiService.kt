package ru.dernogard.region35culture.api

import io.reactivex.Observable
import io.reactivex.rxkotlin.flatMapIterable
import io.reactivex.schedulers.Schedulers
import ru.dernogard.region35culture.database.models.CultureObject
import ru.dernogard.region35culture.database.models.CultureObjectResponse
import javax.inject.Inject

interface CultureInternetApi {
    fun getData(): Observable<List<CultureObject>>
}

class CultureApiService @Inject constructor(
    private val cultureObjectRetrofitLoader: CultureObjectLoader
) : CultureInternetApi {

    override fun getData(): Observable<List<CultureObject>> =
        cultureObjectRetrofitLoader
            .getCultureDataRetrofit()
            .flatMapIterable()
            .skip(2) // skip first two elements - a header of the table
            .map { convertToCultureObject(it) }
            .toList()
            .toObservable()
            .subscribeOn(Schedulers.io())

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

}