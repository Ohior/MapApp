package com.example.mymapapp

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.location.Location
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.app.ActivityCompat
import com.example.mymapapp.managemap.ManageMapLifeCycle
import com.example.mymapapp.managemap.ManageMapLifeCycleImpl
import com.example.mymapapp.utils.Tools
import com.mapbox.android.core.location.LocationEngine
import com.mapbox.android.core.location.LocationEngineListener
import com.mapbox.android.core.location.LocationEnginePriority
import com.mapbox.android.core.location.LocationEngineProvider
import com.mapbox.android.core.permissions.PermissionsListener
import com.mapbox.android.core.permissions.PermissionsManager
import com.mapbox.mapboxsdk.Mapbox
import com.mapbox.mapboxsdk.camera.CameraUpdate
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory
import com.mapbox.mapboxsdk.geometry.LatLng
import com.mapbox.mapboxsdk.maps.MapView
import com.mapbox.mapboxsdk.maps.MapboxMap
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback
import com.mapbox.mapboxsdk.plugins.locationlayer.LocationLayerPlugin
import com.mapbox.mapboxsdk.plugins.locationlayer.modes.CameraMode
import com.mapbox.mapboxsdk.plugins.locationlayer.modes.RenderMode


class MainActivity :
    AppCompatActivity(),
    OnMapReadyCallback,
    LocationEngineListener,
    PermissionsListener,
    ManageMapLifeCycle by ManageMapLifeCycleImpl() {
    private lateinit var mapView: MapView
    private lateinit var map: MapboxMap
    private lateinit var permissionsManager: PermissionsManager
    private var locationEngine: LocationEngine? = null
    private var locationLayerPlugin: LocationLayerPlugin? = null
    private lateinit var originLocation: Location

    override fun onSaveInstanceState(outState: Bundle) {
        mapView.onSaveInstanceState(outState)
        super.onSaveInstanceState(outState)
    }

    override fun onLowMemory() {
        mapView.onLowMemory()
        super.onLowMemory()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Mapbox.getInstance(this, getString(R.string.access_token))
        setContentView(R.layout.activity_main)
//        mapView = findViewById(R.id.id_mapview)
        mapView = registerMapLifeCycle(this, locationEngine, locationLayerPlugin)
        mapView.onCreate(savedInstanceState)

        // get callback to get ui update
        mapView.getMapAsync(this)
    }

    override fun onMapReady(mapboxMap: MapboxMap?) {
        if (mapboxMap != null) map = mapboxMap
        enableLocation()
    }

    @SuppressLint("MissingPermission")
    override fun onConnected() {
        locationEngine?.requestLocationUpdates()
    }

    override fun onLocationChanged(location: Location?) {
        if (location != null) {
            originLocation = location
            setCameraLocation(location)
        }
    }

    override fun onExplanationNeeded(permissionsToExplain: MutableList<String>?) {
        Tools.displayPopup(this, "Location permission is needed to display map")
    }

    override fun onPermissionResult(granted: Boolean) {
        if (granted) {
            enableLocation()
        } else finish()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        permissionsManager.onRequestPermissionsResult(requestCode, permissions, grantResults)
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    private fun enableLocation() {
        if (PermissionsManager.areLocationPermissionsGranted(this)) {
            initializeLocationEngine()
            initializeLocationLayer()
        } else {
            permissionsManager = PermissionsManager(this)
            permissionsManager.requestLocationPermissions(this)
        }
    }

    @SuppressLint("MissingPermission")
    private fun initializeLocationEngine() {
        locationEngine = LocationEngineProvider(this).obtainBestLocationEngineAvailable()
        locationEngine!!.priority = LocationEnginePriority.HIGH_ACCURACY
        locationEngine!!.activate()
        val lastLocation = locationEngine!!.lastLocation
        if (lastLocation != null) {
            originLocation = lastLocation
            setCameraLocation(lastLocation)
        } else {
            locationEngine!!.addLocationEngineListener(this)
        }
    }

    @SuppressLint("MissingPermission")
    private fun initializeLocationLayer() {
        locationLayerPlugin = LocationLayerPlugin(mapView, map, locationEngine)
        locationLayerPlugin!!.setLocationLayerEnabled(true)
        locationLayerPlugin!!.cameraMode = CameraMode.TRACKING
        locationLayerPlugin!!.renderMode = RenderMode.NORMAL
    }

    private fun setCameraLocation(location: Location) {
        map.animateCamera(
            CameraUpdateFactory.newLatLngZoom(
                LatLng(
                    location.latitude,
                    location.longitude
                ), 13.0
            )
        )
    }
}


//class MainActivity :
//    AppCompatActivity(),
//    OnMapReadyCallback,
//    LocationEngineListener,
//    PermissionsListener {
//    private lateinit var mapView: MapView
//    private lateinit var map: MapboxMap
//    private lateinit var permissionsManager: PermissionsManager
//    private lateinit var locationEngine: LocationEngine
//    private lateinit var locationLayerPlugin: LocationLayerPlugin
//    private lateinit var originLocation: Location
//
//    @SuppressLint("MissingPermission")
//    override fun onStart() {
//        super.onStart()
//        mapView.onStart()
//    }
//
//    override fun onResume() {
//        super.onResume()
//        mapView.onResume()
//    }
//
//    override fun onPause() {
//        mapView.onPause()
//        super.onPause()
//    }
//
//    @SuppressLint("MissingPermission")
//    override fun onStop() {
//        // stop leaks
//        if (locationEngine.isConnected) {
//            locationEngine.removeLocationUpdates()
//        }
//        locationLayerPlugin.onStop()
//        mapView.onStop()
//        super.onStop()
//    }
//
//    override fun onSaveInstanceState(outState: Bundle) {
//        mapView.onSaveInstanceState(outState)
//        super.onSaveInstanceState(outState)
//    }
//
//    override fun onLowMemory() {
//        mapView.onLowMemory()
//        super.onLowMemory()
//    }
//
//    override fun onDestroy() {
//        locationEngine.deactivate()
//        mapView.onDestroy()
//        super.onDestroy()
//    }
//
//    override fun onRequestPermissionsResult(
//        requestCode: Int,
//        permissions: Array<out String>,
//        grantResults: IntArray
//    ) {
//        permissionsManager.onRequestPermissionsResult(requestCode, permissions, grantResults)
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
//    }
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        Mapbox.getInstance(this, getString(R.string.access_token))
//        setContentView(R.layout.activity_main)
//
//        mapView = findViewById(R.id.id_mapview)
//        mapView.onCreate(savedInstanceState)
//
//        // get callback to get ui update
//        mapView.getMapAsync(this)
//    }
//
//    override fun onMapReady(mapboxMap: MapboxMap?) {
//        // initialize map
//        map = mapboxMap!!
//        enableLocation()
//    }
//
//    @SuppressLint("MissingPermission")
//    override fun onConnected() {
//        //  update the location engine
//        locationEngine.requestLocationUpdates()
//    }
//
//    override fun onLocationChanged(location: Location?) {
//        if (location != null) {
//            // update location and camera
//            originLocation = location
//            setCameraPosition(location)
//        }
//    }
//
//    override fun onExplanationNeeded(permissionsToExplain: MutableList<String>?) {
//        // explain to user if permission denied
//    }
//
//    override fun onPermissionResult(granted: Boolean) {
//        // permission granted or not
//        if (granted) {
//            enableLocation()
//        }
//    }
//
//    // BEGINNING OF PRIVATE FUNCTION
//    private fun enableLocation() {
//        // enable location
//        if (PermissionsManager.areLocationPermissionsGranted(this)) {
//            // execute
//            initializeLocationEngine()
//            initializeLocationLayer()
//        } else {
//            permissionsManager = PermissionsManager(this)
//            permissionsManager.requestLocationPermissions(this)
//        }
//    }
//
//    @SuppressLint("MissingPermission")
//    private fun initializeLocationEngine() {
//        locationEngine = LocationEngineProvider(this).obtainBestLocationEngineAvailable()
//        // get highest location accuracy
//        locationEngine.priority = LocationEnginePriority.HIGH_ACCURACY
//        locationEngine.activate()
//        // get last location
//        val lastLocation: Location? = locationEngine.lastLocation
//        if (lastLocation != null) {
//            originLocation = lastLocation
//            setCameraPosition(lastLocation)
//        } else {
//            locationEngine.addLocationEngineListener(this)
//        }
//    }
//
//    @SuppressLint("MissingPermission")
//    private fun initializeLocationLayer() {
//        // display the user location o map
//        locationLayerPlugin = LocationLayerPlugin(mapView, map, locationEngine)
//        // enable or disable camera ( hide or show icon0
//        locationLayerPlugin.setLocationLayerEnabled(true)
//        locationLayerPlugin.cameraMode = CameraMode.TRACKING
//        locationLayerPlugin.renderMode = RenderMode.NORMAL
//    }
//
//    private fun setCameraPosition(location: Location) {
//        map.animateCamera(
//            CameraUpdateFactory.newLatLngZoom(
//                LatLng(
//                    location.latitude,
//                    location.longitude
//                ),
//                13.0
//            )
//        )
//    }
//}