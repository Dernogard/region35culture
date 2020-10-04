package ru.dernogard.region35culture.ui.main.viewmodels

import android.content.Context
import androidx.core.content.ContextCompat
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.ViewModel
import dagger.hilt.android.qualifiers.ActivityContext
import io.reactivex.Flowable
import io.reactivex.Observable
import ru.dernogard.region35culture.R
import ru.dernogard.region35culture.database.models.CultureGroup
import ru.dernogard.region35culture.database.models.CultureObject
import ru.dernogard.region35culture.database.repo.CultureObjectLocalRepo
import java.util.*

class CultureGroupsViewModel @ViewModelInject constructor(
    @ActivityContext val context: Context,
    private val cultureObjectRepository: CultureObjectLocalRepo
) : ViewModel() {

    val allInclusiveGroup = CultureGroup(context.getString(R.string.show_all))

    val cultureGroupListObserver: Observable<List<CultureGroup>> =
        cultureObjectRepository
            .loadGroupFromLocalDatabaseObservable()
            .distinctUntilChanged()
            .map {
                convertToCultureGroupList(it)
            }

    private var cultureObjectListObserver: Observable<List<CultureObject>>? = null
    private var currentCultureGroup: CultureGroup? = null

    fun searchCultureObjects(group: CultureGroup): Observable<List<CultureObject>> {
        val lastResult = cultureObjectListObserver
        if (group == currentCultureGroup && lastResult != null) return lastResult
        currentCultureGroup = group

        // Also we can do filtering with SQLite help
        cultureObjectListObserver =
            if (group == allInclusiveGroup) cultureObjectRepository.loadAllFromLocalDatabaseObservable()
            else cultureObjectRepository.loadFromLocalDatabaseByGroupObservable(group.title)


        // Also we can do filtering with RxHelp help

        /*val newResultObservable = cultureObjectRepository.loadAllFromLocalDatabaseObservable()
        cultureObjectListObserver =
            if (group == allInclusiveGroup) newResultObservable
            else newResultObservable.flatMap {
                Observable.fromIterable(it)
                    .filter { building ->
                        building.type == group.title
                    }
                    .toList()
                    .toObservable()
            }*/

        return cultureObjectListObserver!!
    }

    private fun convertToCultureGroupList(list: List<String>): List<CultureGroup> {
        val newListSet = LinkedList<CultureGroup>()
        // Adding "Show all" button for resetting filter.
        // I was use LinkedList for a guaranty first position for this element
        newListSet.addFirst(allInclusiveGroup)
        list.forEach {
            newListSet.add(CultureGroup(it))
        }
        return newListSet
    }

}