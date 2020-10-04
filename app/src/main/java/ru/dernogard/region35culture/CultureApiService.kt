package ru.dernogard.region35culture

import android.content.Context
import android.util.Log
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.android.EntryPointAccessors
import dagger.hilt.android.components.ApplicationComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo
import io.reactivex.rxkotlin.flatMapIterable
import retrofit2.Retrofit
import ru.dernogard.region35culture.api.CultureObjectLoader
import ru.dernogard.region35culture.database.models.CultureObject
import ru.dernogard.region35culture.database.models.CultureObjectResponse
import ru.dernogard.region35culture.database.repo.CultureObjectLocalRepo
import javax.inject.Inject

class CultureApiService @Inject constructor(
    private val cultureObjectRepo: CultureObjectLocalRepo) {

    private val disposablesStorage = CompositeDisposable()

    fun getDataAndSaveIt() {
        CultureObjectLoader
            .getCultureDataRetrofit()
            .flatMapIterable()
            .skip(2) // skip first two elements - a header of a table
            .map { convertToCultureObject(it) }
            //.filter { o -> o.title.isEmpty() }  // remove error parsing objects
            .toList()
            .toObservable()
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
                //latitude = resp.latitude.toDouble(),
                //longitude = resp.longitude.toDouble(),
                type = prepareString(resp.type),
                borderX = prepareString(resp.borderX),
                borderY = prepareString(resp.borderY)
            )
        } catch (e: Exception) {
            //if any data are error just ignore object returning an empty culture object
            Log.e(javaClass.simpleName, e.message.toString())
            Log.e(javaClass.simpleName, resp.toString())
            Log.e(javaClass.simpleName, "--------------------------------")
            CultureObject(
                0, "", "", "",
                "", 0.0, 0.0, "", "", ""
            )
        }
    }

    private fun saveDataToDatabase(list: List<CultureObject>) {
        Log.e(javaClass.simpleName, "Size list for save = ${list.size}")
        cultureObjectRepo.saveAllInLocalDatabase(list)
        Log.e(javaClass.simpleName, "Sucess!")
    }
}