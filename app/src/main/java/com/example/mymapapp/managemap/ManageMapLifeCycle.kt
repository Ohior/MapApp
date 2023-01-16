package com.example.mymapapp.managemap

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import com.example.mymapapp.MainActivity
import com.example.mymapapp.R
import com.mapbox.android.core.location.LocationEngine
import com.mapbox.mapboxsdk.maps.MapView
import com.mapbox.mapboxsdk.plugins.locationlayer.LocationLayerPlugin

//interface ManageMapLifeCycle{
//    fun registerMapLifeCycle(owner: LifecycleOwner)
//}
//
//class ManageMapLifeCycleImpl:ManageMapLifeCycle, LifecycleEventObserver{
//    private lateinit var mapView: MapView
//    override fun registerMapLifeCycle(owner: LifecycleOwner) {
//        owner.lifecycle.addObserver(this)
//    }
//
//    override fun onStateChanged(source: LifecycleOwner, event: Lifecycle.Event) {
//        when(event){
//            Lifecycle.Event.ON_START->{
//            }
//            Lifecycle.Event.ON_RESUME->{
//            }
//            Lifecycle.Event.ON_CREATE->{
//            }
//            else->Unit
//        }
//    }
//
//}

interface ManageMapLifeCycle {
    fun registerMapLifeCycle(
        activity: AppCompatActivity,
        locationEngine: LocationEngine?,
        locationLayerPlugin: LocationLayerPlugin?
    ): MapView
}

class ManageMapLifeCycleImpl : ManageMapLifeCycle, LifecycleEventObserver {
    private lateinit var mapView: MapView
    private var locationEngine: LocationEngine? = null
    private var locationLayerPlugin: LocationLayerPlugin? = null

    override fun registerMapLifeCycle(
        activity: AppCompatActivity,
        le: LocationEngine?,
        llp: LocationLayerPlugin?
    ): MapView {
        mapView = activity.findViewById(R.id.id_mapview)
        locationEngine = le
        locationLayerPlugin = llp
        return mapView
    }

    @SuppressLint("MissingPermission")
    override fun onStateChanged(source: LifecycleOwner, event: Lifecycle.Event) {
        when (event) {
            Lifecycle.Event.ON_START -> {
                locationEngine?.requestLocationUpdates()
                mapView.onStart()
            }
            Lifecycle.Event.ON_RESUME -> {
                mapView.onResume()
            }
            Lifecycle.Event.ON_STOP -> {
                locationEngine?.removeLocationUpdates()
                locationLayerPlugin?.onStop()
                mapView.onStop()
            }
            Lifecycle.Event.ON_DESTROY -> {
                locationEngine?.deactivate()
                mapView.onDestroy()
            }
            Lifecycle.Event.ON_PAUSE -> {
                mapView.onPause()
            }
            else -> Unit
        }
    }

}