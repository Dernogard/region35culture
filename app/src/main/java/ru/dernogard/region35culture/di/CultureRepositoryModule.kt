package ru.dernogard.region35culture.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import ru.dernogard.region35culture.database.repo.CultureObjectLocalRepo
import ru.dernogard.region35culture.database.repo.CultureObjectRepository
import javax.inject.Qualifier
import javax.inject.Singleton

@Qualifier
annotation class LocalCultureRepository

@Module
@InstallIn(ApplicationComponent::class)
abstract class CultureRepositoryModule {

    @Binds
    @Singleton
    @LocalCultureRepository
    abstract fun provideCultureObjectRepository(repository: CultureObjectLocalRepo): CultureObjectRepository
}