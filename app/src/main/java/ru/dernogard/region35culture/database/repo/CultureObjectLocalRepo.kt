package ru.dernogard.region35culture.database.repo

import io.reactivex.Flowable
import ru.dernogard.region35culture.database.dao.CultureObjectDao
import ru.dernogard.region35culture.database.models.CultureObject
import javax.inject.Inject

class CultureObjectLocalRepo @Inject constructor(private val cultureObjectDao: CultureObjectDao)
    : CultureObjectRepository {

    override fun saveAll(list: List<CultureObject>) =
        cultureObjectDao.saveAll(list)

    override fun loadGroupsFlowable(): Flowable<List<String>> =
        cultureObjectDao.getAllGroupFlowable()

    override fun loadAllObjectsFlowable(): Flowable<List<CultureObject>> =
        cultureObjectDao.getAllFlowable()

    override fun loadObjectsByGroupFlowable(groupName: String): Flowable<List<CultureObject>> =
        cultureObjectDao.getByGroupFlowable(groupName)

}