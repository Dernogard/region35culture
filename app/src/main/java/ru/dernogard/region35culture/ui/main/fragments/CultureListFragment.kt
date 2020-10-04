package ru.dernogard.region35culture.ui.main.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import dagger.hilt.android.AndroidEntryPoint
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo
import io.reactivex.schedulers.Schedulers
import ru.dernogard.region35culture.CultureApiService
import ru.dernogard.region35culture.database.models.CultureGroup
import ru.dernogard.region35culture.databinding.CultureGroupsFragmentBinding
import ru.dernogard.region35culture.ui.main.adapters.CultureGroupsAdapter
import ru.dernogard.region35culture.ui.main.adapters.CultureObjectAdapter
import ru.dernogard.region35culture.ui.main.viewmodels.CultureGroupsViewModel
import ru.dernogard.region35culture.R
import java.util.*


@AndroidEntryPoint
class CultureListFragment : Fragment() {

    private val mViewModel: CultureGroupsViewModel by viewModels()
    private lateinit var bind: CultureGroupsFragmentBinding
    private val groupAdapter = CultureGroupsAdapter(this)
    private val objectAdapter = CultureObjectAdapter()
    private val disposableStorage = CompositeDisposable()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        bind = CultureGroupsFragmentBinding.inflate(layoutInflater)
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
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
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
        val defaultGroup = mViewModel.allInclusiveGroup
        showCultureObjectByGroup(defaultGroup)
    }

    private fun showCultureObjectByGroup(group: CultureGroup) {
        mViewModel.searchCultureObjects(group)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe { list ->
                objectAdapter.submitList(list.sortedBy { it.number })
                objectAdapter.notifyDataSetChanged()
                changeAppBarTitle(group)
            }.addTo(disposableStorage)
    }

    fun changeCultureObjectList(group: CultureGroup) {
        showCultureObjectByGroup(group)
    }

    private fun changeAppBarTitle(group: CultureGroup) {
        (requireActivity() as AppCompatActivity).supportActionBar?.title = group.title
    }

    override fun onDestroy() {
        super.onDestroy()
        disposableStorage.dispose()
    }

}