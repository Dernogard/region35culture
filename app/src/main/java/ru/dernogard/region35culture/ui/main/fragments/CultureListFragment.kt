package ru.dernogard.region35culture.ui.main.fragments

import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.annotation.StringRes
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.jakewharton.rxbinding2.widget.RxSearchView
import dagger.hilt.android.AndroidEntryPoint
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.culture_list_fragment.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.launch
import ru.dernogard.region35culture.R
import ru.dernogard.region35culture.database.models.CultureGroup
import ru.dernogard.region35culture.database.models.CultureObject
import ru.dernogard.region35culture.databinding.CultureListFragmentBinding
import ru.dernogard.region35culture.ui.main.adapters.CultureGroupsAdapter
import ru.dernogard.region35culture.ui.main.adapters.CultureObjectAdapter
import ru.dernogard.region35culture.ui.main.viewmodels.CultureViewModel
import ru.dernogard.region35culture.utils.ErrorHandler
import java.util.concurrent.TimeUnit

@AndroidEntryPoint
class CultureListFragment : Fragment() {

    private val mViewModel: CultureViewModel by viewModels()

    private lateinit var groupAdapter: CultureGroupsAdapter
    private lateinit var objectAdapter: CultureObjectAdapter

    private val disposableStorage = CompositeDisposable()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        setHasOptionsMenu(true)
        mViewModel.fragmentOwner = this
        return CultureListFragmentBinding.inflate(layoutInflater).root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRetryButton()
        setupRvAdapters()
        setupCultureGroupRV()
        setupCultureObjectRV()
    }

    override fun onResume() {
        super.onResume()
        fillCultureGroupRV()
        fillCultureObjectRVInFirstStart()
    }

    private fun setupRvAdapters() {
        groupAdapter = CultureGroupsAdapter()
        groupAdapter.groupSelectedListener = object : CultureGroupsAdapter.GroupSelectedListener {
            override fun onGroupSelected(group: CultureGroup) {
                showCultureObjectByGroup(group)
                rv_culture_objects.scrollToPosition(0)
            }
        }
        objectAdapter = CultureObjectAdapter()
        objectAdapter.stateRestorationPolicy =
            RecyclerView.Adapter.StateRestorationPolicy.PREVENT_WHEN_EMPTY
    }

    /**
     * This button is used if the app don't have any data for showing
     * In most of case it not necessary because of
     * @see ru.dernogard.region35culture.worker.UpdateCultureWorker
     * will do auto-reloading job after reconnect
     */
    private fun setupRetryButton() {
        button_retry.setOnClickListener {
            mViewModel.reloadDataFromInternet()
        }
    }

    private fun setupCultureGroupRV() {
        rv_culture_groups.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        rv_culture_groups.adapter = groupAdapter
    }

    private fun setupCultureObjectRV() {
        rv_culture_objects.layoutManager =
            GridLayoutManager(requireContext(), 2)
        rv_culture_objects.adapter = objectAdapter
    }

    private fun fillCultureGroupRV() {
        mViewModel.cultureGroupListObserver
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .onErrorReturn { emptyList() }
            .subscribe { groupsList ->
                // never empty because of first button created by default
                groupAdapter.submitList(groupsList)
                groupAdapter.notifyDataSetChanged()
            }.addTo(disposableStorage)
    }

    private fun changeEmptyListLabelVisibility(isShow: Boolean) {
        rv_culture_objects.visibility = (!isShow).toViewVisibility()
        empty_list_label.visibility = isShow.toViewVisibility()
    }

    // set default group if variable from viewModel is null
    private fun fillCultureObjectRVInFirstStart() {
        val defaultGroup = mViewModel.currentCultureGroup ?: mViewModel.allInclusiveGroup
        showCultureObjectByGroup(defaultGroup)
    }

    private fun showCultureObjectByGroup(group: CultureGroup) {
        mViewModel.searchCultureObjectsByGroup(group)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .onErrorReturn { emptyList() }
            .subscribe { list ->
                changeAppBarTitle(group)

                if (!list.isNullOrEmpty()) {
                    changeEmptyListLabelVisibility(isShow = false)
                    objectAdapter.submitList(list)
                    objectAdapter.notifyDataSetChanged()
                } else {
                    // if the result is empty try to load fresh data from server
                    // usually it's redundant but as a precaution in case if first loading
                    // data from Internet is failing
                    reloadDataFromInternet()
                }
            }.addTo(disposableStorage)
    }

    private fun reloadDataFromInternet() {
        changeEmptyListLabelVisibility(isShow = true)
        mViewModel.reloadDataFromInternet()
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
                mViewModel.getCurrentListFlowable()?.toObservable()?.flatMap { list ->
                    Observable.fromIterable(list)
                        .filter { building ->
                            building.title.contains(it, false)
                        }
                        .toList()
                        .toObservable()
                }
            }
            .observeOn(AndroidSchedulers.mainThread())
            .onErrorReturn { emptyList<CultureObject>() }
            .subscribe { list ->
                objectAdapter.submitList(list)
                objectAdapter.notifyDataSetChanged()
            }
            .addTo(disposableStorage)
    }

    private fun changeAppBarTitle(group: CultureGroup) {
        (requireActivity() as AppCompatActivity).supportActionBar?.title = group.title
    }

    override fun onPause() {
        super.onPause()
        disposableStorage.clear()
    }

    fun changeUiLoadDataFromInternet(isStart: Boolean) {
        CoroutineScope(Main).launch {
            progress_bar.visibility = isStart.toViewVisibility()
            button_retry.isEnabled = !isStart
        }
    }

    fun handleGetDataFromInternetError(throwable: Throwable) {
        val errorResId = ErrorHandler().getErrorStringResId(throwable)
        showErrorMessage(errorResId)
    }

    private fun showErrorMessage(@StringRes messageId: Int) =
        CoroutineScope(Main).launch {
            val errorCause = requireContext().getString(messageId)
            val errorMessage = requireContext().getString(R.string.error_getting_data, errorCause)
            Toast.makeText(requireContext(), errorMessage, Toast.LENGTH_SHORT).show()
        }

}

// show view if boolean is true
private fun Boolean.toViewVisibility(): Int {
    return if (this) View.VISIBLE else View.INVISIBLE
}
