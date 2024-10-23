package com.idrolife.app.presentation.screen

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.os.Looper
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.navigation.NavController
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.idrolife.app.R
import com.idrolife.app.data.api.sensor.SoilMoistureMarkerRequest
import com.idrolife.app.navigation.Screen
import com.idrolife.app.presentation.component.CheckPermission
import com.idrolife.app.presentation.component.DataTableBody
import com.idrolife.app.presentation.component.DataTableHeader
import com.idrolife.app.presentation.component.NavigationBanner3
import com.idrolife.app.presentation.component.PermissionNeededDialog
import com.idrolife.app.presentation.viewmodel.DeviceViewModel
import com.idrolife.app.theme.Black
import com.idrolife.app.theme.BrokenWhite
import com.idrolife.app.theme.GrayVeryVeryLight
import com.idrolife.app.theme.Green
import com.idrolife.app.theme.Green2
import com.idrolife.app.theme.White
import com.idrolife.app.utils.Helper
import com.idrolife.app.utils.SystemBroadcastReceiver
import kotlinx.coroutines.launch

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun SensorSoilMoistureScreen(
    navController: NavController,
    deviceID: String
) {
    val context = LocalContext.current

    val window = (context as Activity).window
    val view = LocalView.current
    val viewModel = hiltViewModel<DeviceViewModel>()
    val scope = rememberCoroutineScope()

    var checkedPermission by remember { mutableStateOf(false) }
    val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
    var isLocationEnabled by remember { mutableStateOf(locationManager.isProviderEnabled(
        LocationManager.GPS_PROVIDER)) }
    val permissionState = rememberPermissionState(Manifest.permission.ACCESS_FINE_LOCATION)
    var showPermissionDialog by remember { mutableStateOf(false) }

    val fusedLocationClient = remember { LocationServices.getFusedLocationProviderClient(context) }
    val locationState = remember { mutableStateOf<Location?>(null) }

    val lifecycleOwner = LocalLifecycleOwner.current
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                Helper().setNotifBarColor(view, window, BrokenWhite.toArgb(),true)
                if (!permissionState.status.isGranted) {
                    permissionState.launchPermissionRequest()
                }
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    LaunchedEffect(isLocationEnabled) {
        if (!isLocationEnabled) {
            showPermissionDialog = true
        }
    }

    fun getCurrentLocation() {
        if (ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED) {

            val locationRequest = LocationRequest.Builder(
                Priority.PRIORITY_HIGH_ACCURACY,
                5000L
            ).apply {
                setMinUpdateIntervalMillis(2000L)
                setWaitForAccurateLocation(true)
            }.build()

            val locationCallback = object : LocationCallback() {
                override fun onLocationResult(locationResult: LocationResult) {
                    Log.d("adaaaaa","ssssss")
                    for (location in locationResult.locations) {
                        Log.d("adaaaaa","${location.latitude} ${location.longitude}")
                        locationState.value = location
                    }
                }
            }

            fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper())
        }
    }

    if (!checkedPermission) {
        CheckPermission(onPermissionGranted = {
            checkedPermission = true
            getCurrentLocation()
        }, onPermissionDenied = {
            checkedPermission = true
            showPermissionDialog = true
        })
    }


    SystemBroadcastReceiver(action = LocationManager.MODE_CHANGED_ACTION) {
        val action = it?.action ?: return@SystemBroadcastReceiver
        if (action != LocationManager.MODE_CHANGED_ACTION) return@SystemBroadcastReceiver

        isLocationEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
    }

    fun showToast(message: String, duration: Int = Toast.LENGTH_SHORT) {
        Toast.makeText(context, message, duration).show()
    }

    LaunchedEffect(Unit) {
        viewModel.isLoading.value = true

        val result = viewModel.getDeviceByID(deviceID)

        if (result.second == "Unauthorized") {
            navController.navigate(Screen.Login.route) {
                popUpTo(navController.graph.id) {
                    inclusive = true
                }
            }
        } else if (result.second.isNotBlank() && !result.second.contains("coroutine scope")) {
            Toast.makeText(context, result.second, Toast.LENGTH_LONG)
                .show()
        }

        val sensorSoilHumidity = viewModel.getSensorSoilHumidity(viewModel.selectedDevice.value?.code ?: "")
        if (sensorSoilHumidity.second.isNotBlank() && !sensorSoilHumidity.second.contains("coroutine scope")) {
            Toast.makeText(context, sensorSoilHumidity.second, Toast.LENGTH_LONG)
                .show()
        }

        viewModel.isLoading.value = false

        viewModel.startPeriodicFetchingDevicesByID(deviceID)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BrokenWhite),
    ) {
        NavigationBanner3(
            navController,
            "${stringResource(id = R.string.sensors)} / ${stringResource(id = R.string.soil_moisture_humidity)}",
            R.drawable.img_header_detail2,
            viewModel.selectedDevice.value,
            viewModel.isLoading.value,
        )
        LazyColumn(
            modifier = Modifier
                .padding(horizontal = 24.dp)
                .fillMaxSize()
        ) {
            item {
                Spacer(modifier = Modifier.height(24.dp))

                DataTableHeader(backgroundColor = Green2, fontColor = White, titles = mutableListOf("Name", "level 1", "level 2", "level 3", "level 4"))

                Spacer(modifier = Modifier.height(8.dp))
            }

            if (viewModel.isLoading.value) {
                item {
                    Box(
                        modifier = Modifier.fillMaxSize()
                    ) {
                        CircularProgressIndicator(
                            color = Green,
                            strokeCap = StrokeCap.Round,
                            strokeWidth = 2.dp,
                            modifier = Modifier
                                .width(18.dp)
                                .height(18.dp)
                                .align(Alignment.Center)
                        )
                    }
                }
            } else {
                items(viewModel.sensorSoilHumidity.value) {
                    var isLoading by remember { mutableStateOf(false) }

                    DataTableBody(
                        GrayVeryVeryLight,
                        Black,
                        it,
                        isLoading,
                        onClick = {
                            if (
                                locationState.value?.latitude != null &&
                                locationState.value?.longitude != null &&
                                viewModel.selectedDevice.value?.code != null
                            ) {
                                val markerData = SoilMoistureMarkerRequest(
                                    name = it?.name ?: "",
                                    latitude = locationState.value?.latitude.toString(),
                                    longitude = locationState.value?.longitude.toString(),
                                    deviceCode = viewModel.selectedDevice.value?.code ?: ""
                                )

                                scope.launch {
                                    isLoading = true
                                    viewModel.postSoilMoistureMarker(markerData)
                                    viewModel.getSensorSoilHumidity(viewModel.selectedDevice.value?.code ?: "")
                                    isLoading = false
                                }
                            }
                        }
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }
        }

        if (showPermissionDialog) {
            PermissionNeededDialog {
                showPermissionDialog = false
                navController.popBackStack()
            }
        }
    }
}
