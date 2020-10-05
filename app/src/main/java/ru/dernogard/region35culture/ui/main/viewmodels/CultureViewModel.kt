package ru.dernogard.region35culture.ui.main.viewmodels

import android.content.Context
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.ViewModel
import dagger.hilt.android.qualifiers.ActivityContext
import io.reactivex.Observable
import ru.dernogard.region35culture.R
import ru.dernogard.region35culture.database.models.CultureGroup
import ru.dernogard.region35culture.database.models.CultureObject
import ru.dernogard.region35culture.database.repo.CultureObjectLocalRepo
import java.util.*

class CultureViewModel @ViewModelInject constructor(
    @ActivityContext val context: Context,
    private val cultureObjectRepository: CultureObjectLocalRepo
) : ViewModel() {

    // A default group for showing all items
    val allInclusiveGroup = CultureGroup(context.getString(R.string.show_all))

    var currentCultureGroup: CultureGroup? = null

    val cultureGroupListObserver: Observable<List<CultureGroup>> =
        cultureObjectRepository
            .loadGroupFromLocalDatabaseObservable()
            .distinctUntilChanged()
            .map {
                convertStringToCultureGroupList(it)
            }

    private var cultureObjectListResultObserver: Observable<List<CultureObject>>? = null
    fun getCurrentListObservable(): Observable<List<CultureObject>>? =
        cultureObjectListResultObserver

    fun searchCultureObjectsByGroup(group: CultureGroup): Observable<List<CultureObject>> {
        val lastResult = cultureObjectListResultObserver
        if (group == currentCultureGroup && lastResult != null) return lastResult
        currentCultureGroup = group

        cultureObjectListResultObserver = getFreshListUsingDatabaseMethod(group)
        // Or we can do filtering with RxHelp help
        //cultureObjectListResultObserver = getFreshListUsingRxJava(group)

        return cultureObjectListResultObserver!!
    }

    // Function for example using RxJava
    private fun getFreshListUsingRxJava(group: CultureGroup): Observable<List<CultureObject>>? {
        val newResultObservable = cultureObjectRepository.loadAllFromLocalDatabaseObservable()
        return if (group == allInclusiveGroup) newResultObservable
        else newResultObservable.flatMap {
            Observable.fromIterable(it)
                .filter { building ->
                    building.type == group.title
                }
                .toList()
                .toObservable()
        }
    }

    private fun getFreshListUsingDatabaseMethod(group: CultureGroup): Observable<List<CultureObject>> {
        return if (group == allInclusiveGroup) cultureObjectRepository.loadAllFromLocalDatabaseObservable()
        else cultureObjectRepository.loadFromLocalDatabaseByGroupObservable(group.title).cache()
    }

    private fun convertStringToCultureGroupList(list: List<String>): List<CultureGroup> {
        val newList = LinkedList<CultureGroup>()
        // Adding "Show all" button for resetting filter.
        // I was use LinkedList for a guaranty first position for this element
        newList.addFirst(allInclusiveGroup)
        list.forEach {
            newList.add(CultureGroup(it))
        }
        return newList
    }

}