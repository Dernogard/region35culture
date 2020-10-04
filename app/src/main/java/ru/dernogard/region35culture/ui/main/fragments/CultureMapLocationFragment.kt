package ru.dernogard.region35culture.ui.main.fragments

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import ru.dernogard.region35culture.R
import ru.dernogard.region35culture.database.models.CultureObject

/**
 * GoogleMaps representation fragment
 * Show the culture object on the map
 */

class CultureMapLocationFragment : Fragment(R.layout.fragment_maps) {

    private lateinit var mapFragment: SupportMapFragment
    private val mapZoom = 13.5F

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
    }

    fun showCultureObjectOnMap(building: CultureObject?) {
        if (building == null) return
        val callback = OnMapReadyCallback { googleMap ->
            val storagePosition = LatLng(building.latitude, building.longitude)
            googleMap.addMarker(MarkerOptions().position(storagePosition).title(building.title))
            googleMap.moveCamera(CameraUpdateFactory.newLatLng(storagePosition))
            googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(storagePosition, mapZoom))
        }
        mapFragment.getMapAsync(callback)
    }
}