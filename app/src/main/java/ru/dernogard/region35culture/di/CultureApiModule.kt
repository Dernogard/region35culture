package ru.dernogard.region35culture.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import io.reactivex.schedulers.Schedulers
import okhttp3.OkHttpClient
import retrofit2.CallAdapter
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import ru.dernogard.region35culture.api.CultureObjectApi
import javax.inject.Singleton

const val BASE_URL = "https://www.opendata.gov35.ru/datasets/"

@InstallIn(ApplicationComponent::class)
@Module
class CultureApiModule {

    @Singleton
    @Provides
    fun provideRxAdapter(): CallAdapter.Factory =
        RxJava2CallAdapterFactory.createWithScheduler(Schedulers.io())

    // This is bad practice and it'll be better don't use this hack in real project because of safety
    // https://stackoverflow.com/questions/31917988/okhttp-javax-net-ssl-sslpeerunverifiedexception-hostname-domain-com-not-verifie
    @Singleton
    @Provides
    fun provideOkHttpClient(): OkHttpClient =
        OkHttpClient
            .Builder()
            .hostnameVerifier { s, _ -> s == "www.opendata.gov35.ru" }
            .build()

    @Singleton
    @Provides
    fun provideGsonFactory(): GsonConverterFactory = GsonConverterFactory.create()

    @Singleton
    @Provides
    fun provideRetrofit(
        client: OkHttpClient,
        rxAdapter: CallAdapter.Factory,
        gsonConverterFactory: GsonConverterFactory
    ): Retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .client(client)
        .addConverterFactory(gsonConverterFactory)
        .addCallAdapterFactory(rxAdapter)
        .build()

    @Provides
    @Singleton
    fun provideCultureApi(retrofit: Retrofit): CultureObjectApi =
        retrofit.create(CultureObjectApi::class.java)

}