package ru.dernogard.region35culture.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import ru.dernogard.region35culture.api.CultureApiService
import ru.dernogard.region35culture.api.CultureInternetApi
import ru.dernogard.region35culture.api.CultureObjectLoader
import ru.dernogard.region35culture.api.CultureObjectLoaderRetrofitImpl
import javax.inject.Singleton

@Module
@InstallIn(ApplicationComponent::class)
abstract class CultureApiServiceModule {

    @Binds
    @Singleton
    abstract fun provideCultureServiceApi(apiService: CultureApiService): CultureInternetApi

    @Binds
    @Singleton
    abstract fun provideCultureObjectLoader(implRetrofit: CultureObjectLoaderRetrofitImpl): CultureObjectLoader

}