package ru.dernogard.region35culture.ui.main.viewmodels

import android.content.Context
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.ViewModel
import dagger.hilt.android.qualifiers.ActivityContext
import io.reactivex.Flowable
import ru.dernogard.region35culture.R
import ru.dernogard.region35culture.database.models.CultureGroup
import ru.dernogard.region35culture.database.models.CultureObject
import ru.dernogard.region35culture.database.repo.CultureObjectRepository
import ru.dernogard.region35culture.di.LocalCultureRepository
import java.util.*

class CultureViewModel @ViewModelInject constructor(
    @ActivityContext val context: Context,
    @LocalCultureRepository private val cultureObjectRepository: CultureObjectRepository
) : ViewModel() {

    // A default group for showing all items
    val allInclusiveGroup = CultureGroup(context.getString(R.string.show_all))

    var currentCultureGroup: CultureGroup? = null

    val cultureGroupListObserver: Flowable<List<CultureGroup>> =
        cultureObjectRepository
            .loadGroupsFlowable()
            .distinctUntilChanged()
            .map {
                convertStringToCultureGroupList(it)
            }

    private var cultureObjectListResultFlowable: Flowable<List<CultureObject>>? = null
    fun getCurrentListFlowable(): Flowable<List<CultureObject>>? =
        cultureObjectListResultFlowable

    fun searchCultureObjectsByGroup(group: CultureGroup): Flowable<List<CultureObject>> {
        val lastResult = cultureObjectListResultFlowable
        if (group == currentCultureGroup && lastResult != null) return lastResult
        currentCultureGroup = group

        cultureObjectListResultFlowable = getFreshListUsingDatabaseMethod(group)
        // Or we can do filtering with RxJava help
        //cultureObjectListResultObserver = getFreshListUsingRxJava(group)

        return cultureObjectListResultFlowable!!
    }

    // Function for example using RxJava
    private fun getFreshListUsingRxJava(group: CultureGroup): Flowable<List<CultureObject>>? {
        val newResultFlowable = cultureObjectRepository.loadAllObjectsFlowable()
        return if (group == allInclusiveGroup) newResultFlowable
        else newResultFlowable.flatMap {
            Flowable.fromIterable(it)
                .filter { building ->
                    building.type == group.title
                }
                .toList()
                .toFlowable()
        }
    }

    private fun getFreshListUsingDatabaseMethod(group: CultureGroup): Flowable<List<CultureObject>> {
        return if (group == allInclusiveGroup) cultureObjectRepository.loadAllObjectsFlowable()
        else cultureObjectRepository.loadObjectsByGroupFlowable(group.title).cache()
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