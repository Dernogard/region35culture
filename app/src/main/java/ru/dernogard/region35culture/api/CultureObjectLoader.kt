package ru.dernogard.region35culture.api

import io.reactivex.Observable
import io.reactivex.schedulers.Schedulers
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import ru.dernogard.region35culture.database.models.CultureObjectResponse

/**
 * The class for loading data from Internet
 */

object CultureObjectLoader {
    private const val BASE_URL = "https://www.opendata.gov35.ru/datasets/"

    private val rxAdapter = RxJava2CallAdapterFactory.createWithScheduler(Schedulers.io())

    private val retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .addCallAdapterFactory(rxAdapter)
        .build()

    fun getCultureDataRetrofit(): Observable<List<CultureObjectResponse>> =
        retrofit.create(CultureObjectApi::class.java).getData("111504")
}