package ru.dernogard.region35culture.database.repo

import ru.dernogard.region35culture.database.dao.CultureObjectDao
import ru.dernogard.region35culture.database.models.CultureObject
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CultureObjectLocalRepo @Inject constructor(private val cultureObjectDao: CultureObjectDao) {

    fun saveAllInLocalDatabase(list: List<CultureObject>) = cultureObjectDao.saveAll(list)

    fun loadFromLocalDatabaseObservable() = cultureObjectDao.getAllObservable()

}