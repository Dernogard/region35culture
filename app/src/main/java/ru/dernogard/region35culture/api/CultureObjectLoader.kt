package ru.dernogard.region35culture.api

import io.reactivex.Observable
import io.reactivex.schedulers.Schedulers
import okhttp3.OkHttpClient
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

    // This is bad practice and it'll be better don't use this hack in real project because of safety
    // https://stackoverflow.com/questions/31917988/okhttp-javax-net-ssl-sslpeerunverifiedexception-hostname-domain-com-not-verifie
    private val client = OkHttpClient
        .Builder()
        .hostnameVerifier { s, _ -> s == "www.opendata.gov35.ru" }
        .build()

    private val retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .client(client)
        .addConverterFactory(GsonConverterFactory.create())
        .addCallAdapterFactory(rxAdapter)
        .build()

    // Get list of culture objects from dataset 111504
    fun getCultureDataRetrofit(): Observable<List<CultureObjectResponse>> =
        retrofit.create(CultureObjectApi::class.java).getData("111504")
}