package ru.dernogard.region35culture.ui.main.viewmodels

import androidx.hilt.Assisted
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import io.reactivex.Observable
import io.reactivex.rxkotlin.flatMapIterable
import ru.dernogard.region35culture.database.models.CultureGroup
import ru.dernogard.region35culture.database.repo.CultureObjectLocalRepo

class CultureGroupsViewModel @ViewModelInject constructor(
    cultureObjectRepository: CultureObjectLocalRepo,
    @Assisted private val savedStateHandle: SavedStateHandle
): ViewModel() {

    val cultureGroupListObserver: Observable<List<CultureGroup>> =
        cultureObjectRepository
            .loadFromLocalDatabaseObservable()
            .flatMapIterable()
            .groupBy{ it.type }
            .map {
                convertToCultureGroup(it.key ?: "")
            }
            .filter { it.title.isEmpty() }
            .toList()
            .toObservable()

    private fun convertToCultureGroup(title: String) = CultureGroup(title)

}