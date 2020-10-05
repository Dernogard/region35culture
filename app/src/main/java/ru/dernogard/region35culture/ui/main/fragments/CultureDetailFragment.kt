package ru.dernogard.region35culture.ui.main.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import ru.dernogard.region35culture.R
import ru.dernogard.region35culture.database.models.CultureObject
import ru.dernogard.region35culture.databinding.CultureDetailFragmentBinding
import ru.dernogard.region35culture.ui.main.viewmodels.CultureDetailViewModel

class CultureDetailFragment: Fragment() {

    private val mViewModel: CultureDetailViewModel by viewModels()
    private val mapFragment: CultureMapLocationFragment = CultureMapLocationFragment()
    private var cultureObject: CultureObject? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return CultureDetailFragmentBinding.inflate(layoutInflater).apply {
            viewModel = mViewModel
            lifecycleOwner = this@CultureDetailFragment
        }.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        fillCultureObjectInfo()
        createMapFragment()
    }

    override fun onResume() {
        super.onResume()
        showObjectOnMap()
    }

    private fun fillCultureObjectInfo() {
        arguments?.let {
            cultureObject = CultureDetailFragmentArgs.fromBundle(it).cultureObject
        }
        // If it will be null try to go back
        if (cultureObject == null) findNavController().popBackStack()
        mViewModel.cultureObject = cultureObject
        changeAppBarTitle(cultureObject)
    }

    private fun createMapFragment() {
        val fm = childFragmentManager
        fm.beginTransaction().replace(R.id.map, mapFragment).commit()
    }

    private fun showObjectOnMap() {
        mapFragment.showCultureObjectOnMap(cultureObject)
    }

    private fun changeAppBarTitle(cObj: CultureObject?) {
        (requireActivity() as AppCompatActivity).supportActionBar?.title = cObj?.title ?: ""
    }

}