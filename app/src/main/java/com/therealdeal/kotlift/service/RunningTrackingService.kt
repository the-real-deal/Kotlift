package com.therealdeal.kotlift.service

import android.Manifest
import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.os.HandlerThread
import android.os.Looper
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.therealdeal.kotlift.model.Track
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import org.osmdroid.util.GeoPoint
import kotlin.time.Clock


class RunningTrackingService : Service() {

    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var locationCallback: LocationCallback
    private lateinit var locationManager: LocationManager


    companion object {
        val track = MutableStateFlow(Track())
        const val NOTIFICATION_ID = 1001
        const val CHANNEL_ID = "location_tracking"
        fun clearTrack() {
            track.update { it.copy(points = emptyList()) }
        }
    }

    override fun onCreate() {
        super.onCreate()
        locationManager = getSystemService(LOCATION_SERVICE) as LocationManager
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        createNotificationChannel()
    }

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        startForeground(NOTIFICATION_ID, buildNotification())
        startLocationUpdates()
        return START_STICKY
    }

    private fun createNotificationChannel() {
        val channel = NotificationChannel(
            CHANNEL_ID, "GPS Tracking",
            NotificationManager.IMPORTANCE_LOW
        )
        getSystemService(NotificationManager::class.java).createNotificationChannel(channel)
    }

    private fun buildNotification() = NotificationCompat.Builder(this, CHANNEL_ID)
        .setContentTitle("Hiking Active")
        .setContentText("Recording your route...")
        .setOngoing(true)
        .build()

    @SuppressLint("MissingPermission")
    private fun startLocationUpdates() {
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        val request = LocationRequest.Builder(
            Priority.PRIORITY_HIGH_ACCURACY,
            4000L // every 4 seconds
        ).build()

        // Add new point only if the distance between the last one and the new one is >= 5m
        locationCallback = object : LocationCallback() {
            override fun onLocationResult(result: LocationResult) {
                result.lastLocation?.let { location ->
                    val point = GeoPoint(location.latitude, location.longitude)
                    track.update { current ->
                        val addedMeters: Float = if (current.points.isNotEmpty()) {
                            val last = current.points.last()
                            val results = FloatArray(1)
                            Location.distanceBetween(
                                last.latitude, last.longitude,
                                point.latitude, point.longitude,
                                results
                            )
                            results[0]
                        } else 0f

                        if (addedMeters >= 5f || current.points.isEmpty()) {
                            Log.i("LOCATION", "Adding new location point to list")
                            current.copy(
                                points = current.points + point,
                                distanceKm = current.distanceKm + (addedMeters / 1000.0)
                            )
                        } else {
                            current
                        }
                    }
                }
            }
        }

        val locationEnabled = locationManager.isProviderEnabled(
            LocationManager.GPS_PROVIDER
        )

        if (!locationEnabled) throw IllegalStateException("Location is disabled")

        val permissionGranted = hasLocationPermission()

        if (!permissionGranted) throw SecurityException("Location permission not granted")

        track.update { it.copy(startTime = Clock.System.now()) }

        fusedLocationClient.requestLocationUpdates(request, locationCallback, serviceLooper)
    }

    private fun hasLocationPermission(): Boolean {
        return ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
    }

    override fun onDestroy() {
        fusedLocationClient.removeLocationUpdates(locationCallback)
        super.onDestroy()
    }

    private val serviceLooper: Looper by lazy {
        HandlerThread("LocationServiceThread").also { it.start() }.looper
    }

    override fun onBind(intent: Intent?) = null
}