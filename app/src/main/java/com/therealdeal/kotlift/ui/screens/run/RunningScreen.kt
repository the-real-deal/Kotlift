package com.therealdeal.kotlift.ui.screens.run

import android.Manifest
import android.content.Intent
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import com.therealdeal.kotlift.navigation.RunNavigation
import org.koin.androidx.compose.koinViewModel
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Polyline
import android.graphics.Paint
import android.net.Uri
import android.os.Build
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.therealdeal.kotlift.ui.composables.commonComponents.LocationDisabledAlert
import com.therealdeal.kotlift.utils.PermissionStatus
import com.therealdeal.kotlift.utils.rememberMultiplePermissions
import android.provider.Settings
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.therealdeal.kotlift.model.Track
import com.therealdeal.kotlift.ui.composables.commonComponents.PermissionDeniedAlert
import com.therealdeal.kotlift.ui.composables.commonComponents.PermissionPermanentlyDeniedSnackbar
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlin.time.Clock

@Composable
fun RunningScreen(
    viewModel: RunningViewModel = koinViewModel(),
    onNavigate: (RunNavigation) -> Unit) {

    val context = LocalContext.current
    val runningStats by viewModel.trackPoints.collectAsStateWithLifecycle()
    val isTracking by viewModel.isTracking.collectAsStateWithLifecycle()

    var showLocationDisableAlert by remember { mutableStateOf(false) }
    var showLocationDeniedAlert by remember { mutableStateOf(false) }
    var showLocationPermanentlyDeniedAlert by remember { mutableStateOf(false) }

    val permissionList = if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
        listOf(
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.FOREGROUND_SERVICE_LOCATION
        )
    } else {
        listOf(
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION
        )
    }

    val locationPermission = rememberMultiplePermissions(
            permissionList
    ) { statuses ->
        when {
            statuses.any { it.value == PermissionStatus.Granted } -> viewModel.startTracking(context)
            statuses.all { it.value == PermissionStatus.PermanentlyDenied } ->
                showLocationPermanentlyDeniedAlert = true
            else -> showLocationDeniedAlert = true
        }
    }
     val snackBarHostState = remember { SnackbarHostState() }

    fun startTrackingOrRequestPermission() {
        if (locationPermission.statuses.any { it.value.isGranted }) {
            viewModel.startTracking(context)
        } else {
            locationPermission.launchPermissionRequest()
        }
    }

    fun openLocationSettings() {
        val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }
        if (intent.resolveActivity(context.packageManager) != null) {
            context.startActivity(intent)
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        snackbarHost = { SnackbarHost(snackBarHostState) }) { paddingValues ->
        Box(modifier = Modifier.padding(paddingValues)) {

            // Map fills the screen
            OsmMapView(
                trackPoints = runningStats.points,
                modifier = Modifier.fillMaxSize()
            )

            // Stats overlay
            if (runningStats.points.isNotEmpty()) {
                TrackStatsOverlay(
                    track = runningStats,
                    modifier = Modifier
                        .align(Alignment.TopCenter)
                        .padding(16.dp)
                )
            }

            Column(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Button(
                    onClick = {
                        if (isTracking) {
                            viewModel.stopTracking(context)
                            // [TODO] push new session to db
                        } else {
                            startTrackingOrRequestPermission()
                        }
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isTracking) Color.Red else Color(0xFF4CAF50)
                    )
                ) {
                    Icon(
                        imageVector = if (isTracking) Icons.Default.Stop else Icons.Default.PlayArrow,
                        contentDescription = null
                    )
                    Spacer(Modifier.width(8.dp))
                    Text(if (isTracking) "Stop Hike" else "Start Hike",
                        color = MaterialTheme.colorScheme.onPrimary)
                }

                if (!isTracking && runningStats.points.isNotEmpty()) {
                    OutlinedButton(onClick = {
                        viewModel.clearTrack()
                        viewModel.saveRun()
                    }) {
                        Text("Clear Track")
                    }
                }
            }
        }
        LocationDisabledAlert(
            show = showLocationDisableAlert,
            onAction = ::openLocationSettings,
            onHide = {
                showLocationDisableAlert = false
            }
        )

        PermissionDeniedAlert(
            show = showLocationDeniedAlert,
            onAction = locationPermission::launchPermissionRequest,
            onHide = { showLocationDeniedAlert = false }
        )

        PermissionPermanentlyDeniedSnackbar(
            snackBarHostState,
            show = showLocationPermanentlyDeniedAlert,
            onAction = {
                val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                    data = Uri.fromParts("package", context.packageName, null)
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK
                }

                if (intent.resolveActivity(context.packageManager) != null) {
                    context.startActivity(intent)
                }
            },
            onHide = { showLocationPermanentlyDeniedAlert = false }
        )
    }

}

@Composable
fun TrackStatsOverlay(
    track: Track,
    modifier: Modifier = Modifier
) {
    val elapsed = remember(track.startTime) {
        Clock.System.now() - track.startTime
    }

    val hours   = elapsed.inWholeHours
    val minutes = (elapsed.inWholeMinutes % 60).toString().padStart(2, '0')
    val seconds = (elapsed.inWholeSeconds % 60).toString().padStart(2, '0')
    val durationStr = if (hours > 0) "$hours:$minutes:$seconds" else "$minutes:$seconds"

    val distanceKm  = track.distanceKm
    val pointCount  = track.points.size

    val paceStr = if (distanceKm > 0.0) {
        val paceSecPerKm = (elapsed.inWholeSeconds / distanceKm).toInt()
        val pm = paceSecPerKm / 60
        val ps = (paceSecPerKm % 60).toString().padStart(2, '0')
        "$pm:$ps"
    } else "—"

    Card(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        border = BorderStroke(0.5.dp, MaterialTheme.colorScheme.outlineVariant)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 10.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Canvas(modifier = Modifier.size(8.dp)) {
                    drawCircle(color = Color(0xFF4CAF50))
                }
                Text("Tracking", fontSize = 13.sp, fontWeight = FontWeight.Medium)
            }
            Text("▲ $durationStr", fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }

        HorizontalDivider(thickness = 0.5.dp)

        // 2×2 stats grid
        Column {
            Row(modifier = Modifier.fillMaxWidth()) {
                StatCell(
                    label = "Distance",
                    value = "%.1f".format(distanceKm),
                    unit = "km",
                    modifier = Modifier.weight(1f),
                    borderEnd = true,
                    borderBottom = true
                )
                StatCell(
                    label = "Avg pace",
                    value = paceStr,
                    unit = "min/km",
                    modifier = Modifier.weight(1f),
                    borderBottom = true
                )
            }
            Row(modifier = Modifier.fillMaxWidth()) {
                StatCell(
                    label = "Points",
                    value = "$pointCount",
                    unit = "recorded",
                    modifier = Modifier.weight(1f),
                    borderEnd = true
                )

                StatCell(
                    label = "Elevation",
                    value = "—",
                    unit = "m gain",
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
private fun StatCell(
    label: String,
    value: String,
    unit: String,
    modifier: Modifier = Modifier,
    borderEnd: Boolean = false,
    borderBottom: Boolean = false
) {
    val outline = MaterialTheme.colorScheme.outlineVariant
    Box(
        modifier = modifier
            .then(if (borderEnd)    Modifier.drawBehind { drawLine(outline.copy(alpha = 0.5f), Offset(size.width, 0f), Offset(size.width, size.height), 1f) } else Modifier)
            .then(if (borderBottom) Modifier.drawBehind { drawLine(outline.copy(alpha = 0.5f), Offset(0f, size.height), Offset(size.width, size.height), 1f) } else Modifier)
            .padding(horizontal = 16.dp, vertical = 14.dp)
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
            Text(label.uppercase(), fontSize = 11.sp, letterSpacing = 0.5.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Text(value, fontSize = 26.sp, fontWeight = FontWeight.Medium, lineHeight = 26.sp)
            Text(unit, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

@Composable
fun OsmMapView(
    trackPoints: List<GeoPoint>,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val primary = MaterialTheme.colorScheme.primary.toArgb()

    val mapView = remember {
        MapView(context).apply {
            setTileSource(TileSourceFactory.MAPNIK)
            setMultiTouchControls(true)
            controller.setZoom(6.5)
            controller.setCenter(GeoPoint(41.9, 12.5))
            isTilesScaledToDpi = true
            zoomController.setVisibility(
                org.osmdroid.views.CustomZoomButtonsController.Visibility.NEVER
            )
        }
    }

    val polyline = remember {
        Polyline().apply {
            outlinePaint.color = primary
            outlinePaint.strokeWidth = 8f
            outlinePaint.strokeCap = Paint.Cap.ROUND
            outlinePaint.strokeJoin = Paint.Join.ROUND
        }
    }

    LaunchedEffect(trackPoints) {
        withContext(Dispatchers.Main) {
            if (trackPoints.isNotEmpty()) {
                polyline.setPoints(trackPoints)
                if (!mapView.overlays.contains(polyline)) {
                    mapView.overlays.add(polyline)
                }
                mapView.controller.animateTo(trackPoints.last(), 17.0, 1000L)
                mapView.invalidate()
            }
        }
    }

    val lifecycle = LocalLifecycleOwner.current.lifecycle
    DisposableEffect(lifecycle) {
        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_RESUME -> mapView.onResume()
                Lifecycle.Event.ON_PAUSE -> mapView.onPause()
                else -> {}
            }
        }
        lifecycle.addObserver(observer)
        onDispose { lifecycle.removeObserver(observer) }
    }

    AndroidView(
        factory = { mapView },
        modifier = modifier
    )
}
