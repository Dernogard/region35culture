package ru.dernogard.region35culture.ui.main.fragments

import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.jakewharton.rxbinding2.widget.RxSearchView
import dagger.hilt.android.AndroidEntryPoint
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.culture_list_fragment.*
import ru.dernogard.region35culture.R
import ru.dernogard.region35culture.api.CultureInternetApi
import ru.dernogard.region35culture.database.models.CultureGroup
import ru.dernogard.region35culture.databinding.CultureListFragmentBinding
import ru.dernogard.region35culture.ui.main.adapters.CultureGroupsAdapter
import ru.dernogard.region35culture.ui.main.adapters.CultureObjectAdapter
import ru.dernogard.region35culture.ui.main.viewmodels.CultureViewModel
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@AndroidEntryPoint
class CultureListFragment : Fragment() {

    private val mViewModel: CultureViewModel by viewModels()
    private lateinit var bind: CultureListFragmentBinding
    @Inject lateinit var cultureServiceApi: CultureInternetApi
    private val groupAdapter = CultureGroupsAdapter(this)
    private val objectAdapter = CultureObjectAdapter()
    private val disposableStorage = CompositeDisposable()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        setHasOptionsMenu(true)
        bind = CultureListFragmentBinding.inflate(layoutInflater)
        return bind.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupCultureGroupRV()
        setupCultureObjectRV()
        fillCultureGroupRV()
        fillCultureObjectRVInFirstStart()
    }

    private fun setupCultureGroupRV() {
        val rv = bind.rvCultureGroups
        rv.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        rv.adapter = groupAdapter
    }

    private fun setupCultureObjectRV() {
        val rv = bind.rvCultureObjects
        rv.layoutManager =
            GridLayoutManager(requireContext(), 2)
        rv.adapter = objectAdapter
    }

    private fun fillCultureGroupRV() {
        mViewModel.cultureGroupListObserver
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe { list ->
                if (!list.isNullOrEmpty()) {
                    groupAdapter.submitList(list)
                    groupAdapter.notifyDataSetChanged()
                } else {
                    //CultureApiService().getDataAndSaveIt()
                    Log.e(javaClass.simpleName, "Empty list")
                }
            }.addTo(disposableStorage)
    }

    private fun fillCultureObjectRVInFirstStart() {


// just check, replace to correct place
        cultureServiceApi.getDataAndSaveIt()




        val defaultGroup = mViewModel.currentCultureGroup ?: mViewModel.allInclusiveGroup
        showCultureObjectByGroup(defaultGroup)
    }

    private fun showCultureObjectByGroup(group: CultureGroup) {
        mViewModel.searchCultureObjectsByGroup(group)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe { list ->
                objectAdapter.submitList(list)
                objectAdapter.notifyDataSetChanged()
                rv_culture_objects.scrollToPosition(0)
                changeAppBarTitle(group)
            }.addTo(disposableStorage)
    }

    private fun changeAppBarTitle(group: CultureGroup) {
        (requireActivity() as AppCompatActivity).supportActionBar?.title = group.title
    }

    fun changeCultureObjectList(group: CultureGroup) {
        showCultureObjectByGroup(group)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.culture_list, menu)
        setSearchViewAction(menu)
    }

    private fun setSearchViewAction(menu: Menu) {
        val searchItem = menu.findItem(R.id.action_search)
        val searchView = searchItem.actionView as android.widget.SearchView

        RxSearchView.queryTextChanges(searchView)
            .distinctUntilChanged()
            .debounce(500, TimeUnit.MILLISECONDS)
            .switchMap {
                mViewModel.getCurrentListObservable()?.flatMap {list ->
                    Observable.fromIterable(list)
                        .filter { building ->
                            building.title.contains(it, false)
                        }.toList()
                        .toObservable()
                }
            }
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe {list ->
                objectAdapter.submitList(list)
                objectAdapter.notifyDataSetChanged()
            }
            .addTo(disposableStorage)
    }

    override fun onDestroy() {
        super.onDestroy()
        disposableStorage.dispose()
    }

}