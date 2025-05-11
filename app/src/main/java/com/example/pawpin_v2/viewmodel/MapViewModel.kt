package com.example.pawpin_v2.viewmodel

import android.annotation.SuppressLint
import android.app.Application
import com.example.pawpin_v2.BuildConfig
import android.location.Location
import android.util.Log
import android.widget.Toast
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.pawpin_v2.data.getDatabase
import com.example.pawpin_v2.data.Walk
import com.google.android.gms.location.*
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.util.Date
import kotlin.random.Random
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class MapViewModel(application: Application) : AndroidViewModel(application) {

    private val fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(application)

    private val _tracking = MutableStateFlow(false)
    val tracking: StateFlow<Boolean> = _tracking

    private val _routePoints = MutableStateFlow<List<LatLng>>(emptyList())
    val routePoints: StateFlow<List<LatLng>> = _routePoints

    private val _walkPathPoints = MutableStateFlow<List<LatLng>>(emptyList())
    val walkPathPoints: StateFlow<List<LatLng>> = _walkPathPoints

    private val _distance = MutableStateFlow(0f)
    val distance: StateFlow<Float> = _distance

    private var lastLocation: Location? = null
    private var _startTime: Long? = null

    private val _currentLocation = MutableStateFlow<LatLng?>(null)
    val currentLocation: StateFlow<LatLng?> = _currentLocation

    private val db = getDatabase(application)



    private val locationCallback = object : LocationCallback() {
        override fun onLocationResult(result: LocationResult) {
            super.onLocationResult(result)
            if (_tracking.value) {
                result.lastLocation?.let { location ->
                    updateLocation(location)
                }
            }
        }
    }

    @SuppressLint("MissingPermission")
    fun startWalk() {
        _tracking.value = true
        _walkPathPoints.value = emptyList() // 🔴 reset walked trail only
        _distance.value = 0f
        lastLocation = null
        _startTime = System.currentTimeMillis()

        val request = LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 5000L)
            .setMinUpdateIntervalMillis(2000L)
            .build()

        fusedLocationProviderClient.requestLocationUpdates(
            request,
            locationCallback,
            null
        )
    }

    fun endWalk() {

        _routePoints.value = emptyList()

        fusedLocationProviderClient.removeLocationUpdates(locationCallback)

        val durationSec = _startTime?.let { (System.currentTimeMillis() - it) / 1000 } ?: 0

        if (_walkPathPoints.value.isNotEmpty()) {
            val start = _walkPathPoints.value.first()
            val end = _walkPathPoints.value.last()

            val walk = Walk(
                date = Date(),
                distanceMeters = _distance.value,
                durationSeconds = durationSec,
                startLat = start.latitude,
                startLng = start.longitude,
                endLat = end.latitude,
                endLng = end.longitude
            )

            viewModelScope.launch {
                db.walkDao().insertWalk(walk)
            }
        }

        _tracking.value = false
        _distance.value = 0f
        _walkPathPoints.value = emptyList()
        _startTime = null
    }

    private fun updateLocation(location: Location) {
        val newPoint = LatLng(location.latitude, location.longitude)
        val points = _walkPathPoints.value.toMutableList()

        lastLocation?.let { lastLoc ->
            val distanceMeters = lastLoc.distanceTo(location)
            _distance.value += distanceMeters
        }

        points.add(newPoint)
        _walkPathPoints.value = points
        lastLocation = location
        _currentLocation.value = newPoint
    }

    override fun onCleared() {
        super.onCleared()
        fusedLocationProviderClient.removeLocationUpdates(locationCallback)
    }

    // Dog pickups
    private val _extraDogs = mutableStateListOf<DogMarker>()
    val extraDogs: List<DogMarker> get() = _extraDogs

    fun addDogMarker(dogName: String, baseLocation: LatLng) {
        val offset = generateRandomOffset()
        val newLatLng = LatLng(
            baseLocation.latitude + offset.first,
            baseLocation.longitude + offset.second
        )
        _extraDogs.add(DogMarker(dogName, newLatLng))
    }

    fun clearDogMarkers() {
        _extraDogs.clear()
    }

    private fun generateRandomOffset(): Pair<Double, Double> {
        val minOffset = 100.0 / 111_000
        val maxOffset = 1000.0 / 111_000
        val latOffset = Random.nextDouble(minOffset, maxOffset) * if (Random.nextBoolean()) 1 else -1
        val lngOffset = Random.nextDouble(minOffset, maxOffset) * if (Random.nextBoolean()) 1 else -1
        return Pair(latOffset, lngOffset)
    }

    fun removeDogMarker(dogName: String) {
        _extraDogs.removeAll { it.name == dogName }
    }

    fun setRoutePoints(points: List<LatLng>) {
        _routePoints.value = points
    }

    suspend fun getWalkingRoute(origin: LatLng, waypoints: List<LatLng>): List<LatLng> {
        return withContext(Dispatchers.IO) {
            val apiKey =  "AIzaSyAhhu5XtP2g2u0Jzo4xcsTR4vrZ97ojUcQ"//BuildConfig.MAPS_API_KEY
            Log.d("API_KEY", "Using API Key: $apiKey")

            val waypointsStr = waypoints.joinToString("|") { "${it.latitude},${it.longitude}" }
            val url = "https://maps.googleapis.com/maps/api/directions/json?origin=${origin.latitude},${origin.longitude}&destination=${waypoints.last().latitude},${waypoints.last().longitude}&waypoints=optimize:true|$waypointsStr&mode=walking&key=$apiKey"

            val client = OkHttpClient()
            val request = Request.Builder().url(url).build()

            val response = client.newCall(request).execute()
            val body = response.body?.string() ?: return@withContext emptyList()

            Log.d("DirectionsAPI", "Response: $body")

            val jsonObject = JSONObject(body)
            val routes = jsonObject.getJSONArray("routes")
            if (routes.length() == 0) return@withContext emptyList()

            val overviewPolyline = routes.getJSONObject(0)
                .getJSONObject("overview_polyline")
                .getString("points")

            return@withContext decodePolyline(overviewPolyline)
        }
    }

    private fun decodePolyline(encoded: String): List<LatLng> {
        val poly = ArrayList<LatLng>()
        var index = 0
        val len = encoded.length
        var lat = 0
        var lng = 0

        while (index < len) {
            var b: Int
            var shift = 0
            var result = 0
            do {
                b = encoded[index++].code - 63
                result = result or (b and 0x1f shl shift)
                shift += 5
            } while (b >= 0x20)
            val dlat = if (result and 1 != 0) (result shr 1).inv() else result shr 1
            lat += dlat

            shift = 0
            result = 0
            do {
                b = encoded[index++].code - 63
                result = result or (b and 0x1f shl shift)
                shift += 5
            } while (b >= 0x20)
            val dlng = if (result and 1 != 0) (result shr 1).inv() else result shr 1
            lng += dlng

            poly.add(LatLng(lat / 1E5, lng / 1E5))
        }

        return poly
    }

    @SuppressLint("MissingPermission")
    fun startLocationUpdates() {
        val request = LocationRequest.Builder(Priority.PRIORITY_BALANCED_POWER_ACCURACY, 5000L)
            .setMinUpdateIntervalMillis(2000L)
            .build()
        //Log.d("a", "aaaaaaaa")
        fusedLocationProviderClient.requestLocationUpdates(
            request,
            object : LocationCallback() {
                override fun onLocationResult(result: LocationResult) {
                    result.lastLocation?.let { location ->
                        _currentLocation.value = LatLng(location.latitude, location.longitude)

                        //Log.d("a", "latitude = AAA " + location.latitude)
                    }
                }
            },
            null
        )
    }


    data class DogMarker(val name: String, val position: LatLng)
}