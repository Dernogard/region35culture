package ru.dernogard.region35culture.api

import io.reactivex.Observable
import io.reactivex.schedulers.Schedulers
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import ru.dernogard.region35culture.database.models.CultureObjectResponse
import javax.inject.Inject

interface CultureObjectLoader {
    fun getCultureDataRetrofit(): Observable<List<CultureObjectResponse>>
}

class CultureObjectLoaderRetrofitImpl @Inject constructor() : CultureObjectLoader {

    private val baseUrl = "https://www.opendata.gov35.ru/datasets/"
    private val rxAdapter = RxJava2CallAdapterFactory.createWithScheduler(Schedulers.io())

    // This is bad practice and it'll be better don't use this hack in real project because of safety
    // https://stackoverflow.com/questions/31917988/okhttp-javax-net-ssl-sslpeerunverifiedexception-hostname-domain-com-not-verifie
    private val client = OkHttpClient
        .Builder()
        .hostnameVerifier { s, _ -> s == "www.opendata.gov35.ru" }
        .build()

    private val retrofit = Retrofit.Builder()
        .baseUrl(baseUrl)
        .addConverterFactory(GsonConverterFactory.create())
        .addCallAdapterFactory(rxAdapter)
        .client(client)
        .build()

    // id_dataset is pointer to required dataset on the server
    override fun getCultureDataRetrofit(): Observable<List<CultureObjectResponse>> =
        retrofit.create(CultureObjectApi::class.java).getData("111504")

}