package com.udacity.project4.locationreminders.savereminder.selectreminderlocation


import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.content.IntentSender
import android.content.pm.PackageManager
import android.content.res.Resources
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.*
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.google.android.material.snackbar.Snackbar
import com.udacity.project4.R
import com.udacity.project4.base.BaseFragment
import com.udacity.project4.base.NavigationCommand
import com.udacity.project4.databinding.FragmentSelectLocationBinding
import com.udacity.project4.locationreminders.savereminder.SaveReminderViewModel
import com.udacity.project4.utils.setDisplayHomeAsUpEnabled
import org.koin.android.ext.android.inject

class SelectLocationFragment : BaseFragment(), OnMapReadyCallback {

    //Use Koin to get the view model of the SaveReminder
    override val _viewModel: SaveReminderViewModel by inject()
    private lateinit var binding: FragmentSelectLocationBinding

    private lateinit var _map: GoogleMap

    private val REQUEST_LOCATION_PERMISSION = 1

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        binding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_select_location, container, false)

        binding.viewModel = _viewModel
        binding.lifecycleOwner = this
        val mapFragment = childFragmentManager
            .findFragmentById(R.id.map_fragment) as SupportMapFragment
        mapFragment.getMapAsync(this)
        setHasOptionsMenu(true)
        setDisplayHomeAsUpEnabled(true)

//        TODO: add the map setup implementation
//        TODO: zoom to the user location after taking his permission
//        TODO: add style to the map
//        TODO: put a marker to location that the user selected


        return binding.root
    }

    private fun onLocationSelected() {
        _map.setOnPoiClickListener { poi ->
            _viewModel.showToast.postValue("Point of interest selected")
            _viewModel.selectedPOI.postValue(poi)
            _viewModel.latitude.postValue(poi.latLng.latitude)
            _viewModel.longitude.postValue(poi.latLng.longitude)
            _viewModel.reminderSelectedLocationStr.postValue(poi.name)
            _viewModel.navigationCommand.postValue(
                NavigationCommand.Back
            )

        }
    }


    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.map_options, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) {
        // TODO: Change the map type based on the user's selection.
        R.id.normal_map -> {
            Log.i("MENU DE MAPA", "1")
            _map.mapType = GoogleMap.MAP_TYPE_NORMAL
            true
        }
        R.id.hybrid_map -> {
            Log.i("MENU DE MAPA", "2")
            _map.mapType = GoogleMap.MAP_TYPE_HYBRID
            true
        }
        R.id.satellite_map -> {
            Log.i("MENU DE MAPA", "3")
            _map.mapType = GoogleMap.MAP_TYPE_SATELLITE
            true
        }
        R.id.terrain_map -> {
            Log.i("MENU DE MAPA", "4")
            _map.mapType = GoogleMap.MAP_TYPE_TERRAIN
            true
        }
        else -> super.onOptionsItemSelected(item)
    }

    override fun onMapReady(mMap: GoogleMap?) {
        _map = mMap!!
        setPointOfInsterest()
        onLocationSelected()
        setMapLongCLick()
        enableMyLocation()
//        _map.moveCamera(CameraUpdateFactory.newLatLngZoom(startLocation(), 15f))
    }

    private fun setMapLongCLick() {
        _map.setOnMapLongClickListener {
            _map.addMarker(
                MarkerOptions().position(it)
            )

            _viewModel.showToast.postValue("Random Point of interest selected")

            _viewModel.latitude.postValue(it.latitude)
            _viewModel.longitude.postValue(it.longitude)
            _viewModel.reminderSelectedLocationStr.postValue("New place")
            _viewModel.navigationCommand.postValue(
                NavigationCommand.Back
            )
        }
    }


    private fun setPointOfInsterest() {
        _map.setOnPoiClickListener { poi ->
            val poiMarker = _map.addMarker(
                MarkerOptions()
                    .position(poi.latLng)
                    .title(poi.name)
            )
            poiMarker.showInfoWindow()
        }
    }

    private fun startLocation(): LatLng {
        return LatLng(-22.904079099378386, -43.17523517375369)
    }


    private fun isPermissionGranted(): Boolean {
        @Suppress("DEPRECATED_IDENTITY_EQUALS")
        return ContextCompat.checkSelfPermission(
            requireContext(),
            Manifest.permission.ACCESS_FINE_LOCATION
        ) === PackageManager.PERMISSION_GRANTED
    }

    @SuppressLint("MissingPermission")
    private fun enableMyLocation() {
        if (isPermissionGranted()) {
            _map.isMyLocationEnabled = true
            _map.getUiSettings().setMyLocationButtonEnabled(true)
        } else {
            requestPermissions(
                arrayOf<String>(Manifest.permission.ACCESS_FINE_LOCATION),
                REQUEST_LOCATION_PERMISSION
            )
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            REQUEST_LOCATION_PERMISSION -> {
                if (grantResults.size > 0 && (grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    enableMyLocation()
                }
                else
                    Toast.makeText(requireContext(), "Permission Denied", Toast.LENGTH_SHORT).show()

            }
            else ->Toast.makeText(requireContext(), "Permission Denied", Toast.LENGTH_SHORT).show()
        }
    }

}
