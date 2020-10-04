package ru.dernogard.region35culture.di

import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import ru.dernogard.region35culture.database.AppRoomDatabase
import ru.dernogard.region35culture.database.dao.CultureObjectDao
import ru.dernogard.region35culture.database.repo.CultureObjectLocalRepo
import javax.inject.Singleton

/**
 * The Hilt DI module for database injections.
 */

@InstallIn(ApplicationComponent::class)
@Module
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext appContext: Context) =
        Room.databaseBuilder(
            appContext, AppRoomDatabase::class.java, "Region35Culture.db"
        ).build()

    @Provides
    @Singleton
    fun provideCultureObjectDao(database: AppRoomDatabase): CultureObjectDao =
        database.cultureObjectDao()

    @Provides
    @Singleton
    fun provideCultureObjectRepo(dao: CultureObjectDao): CultureObjectLocalRepo =
        CultureObjectLocalRepo(dao)

}