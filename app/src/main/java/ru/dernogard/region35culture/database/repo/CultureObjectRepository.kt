package ru.dernogard.region35culture.database.repo

import io.reactivex.Flowable
import ru.dernogard.region35culture.database.models.CultureObject

interface CultureObjectRepository {

    fun saveAll(list: List<CultureObject>)

    fun loadGroupsFlowable(): Flowable<List<String>>

    fun loadAllObjectsFlowable(): Flowable<List<CultureObject>>

    fun loadObjectsByGroupFlowable(groupName: String): Flowable<List<CultureObject>>

}