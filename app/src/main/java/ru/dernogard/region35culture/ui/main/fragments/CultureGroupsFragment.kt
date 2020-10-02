package ru.dernogard.region35culture.ui.main.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.ListAdapter
import dagger.hilt.android.AndroidEntryPoint
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo
import io.reactivex.schedulers.Schedulers
import ru.dernogard.region35culture.databinding.CultureGroupsFragmentBinding
import ru.dernogard.region35culture.ui.main.adapters.CultureGroupsAdapter
import ru.dernogard.region35culture.ui.main.viewmodels.CultureGroupsViewModel

@AndroidEntryPoint
class CultureGroupsFragment : Fragment() {

    private val mViewModel: CultureGroupsViewModel by viewModels()
    private lateinit var bind: CultureGroupsFragmentBinding
    private val adapter = CultureGroupsAdapter()
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
        setupRecyclerView()
        setupObservable()
    }

    private fun setupRecyclerView() {
        val rv = bind.rvCultureGroups
        rv.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        rv.adapter = adapter
    }

    private fun setupObservable() {
        mViewModel.cultureGroupListObserver
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                if (!it.isNullOrEmpty()) {
                    adapter.submitList(it)
                }
            }.addTo(disposableStorage)
    }


    override fun onDestroy() {
        super.onDestroy()
        disposableStorage.dispose()
    }

}