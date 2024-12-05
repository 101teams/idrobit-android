package com.idrolife.app.presentation.screen

import android.app.Activity
import android.widget.Toast
import androidx.appcompat.content.res.AppCompatResources.getDrawable
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.navigation.NavController
import com.google.accompanist.drawablepainter.rememberDrawablePainter
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapType
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.MarkerComposable
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState
import com.idrolife.app.R
import com.idrolife.app.data.api.map.DeviceGeosItem
import com.idrolife.app.data.api.sensor.RhsItem
import com.idrolife.app.navigation.Screen
import com.idrolife.app.presentation.component.NavigationBanner2
import com.idrolife.app.presentation.viewmodel.DeviceViewModel
import com.idrolife.app.theme.Black
import com.idrolife.app.theme.BrokenWhite
import com.idrolife.app.theme.Primary
import com.idrolife.app.theme.PrimaryLight2
import com.idrolife.app.theme.Manrope
import com.idrolife.app.theme.White
import com.idrolife.app.utils.Helper

@Composable
fun MapScreen(
    navController: NavController,
    deviceID: String,
    deviceCode: String,
) {
    val context = LocalContext.current

    val window = (context as Activity).window
    val view = LocalView.current

    val viewModel = hiltViewModel<DeviceViewModel>()

    val mapCamLocation = remember { mutableStateOf(LatLng(40.9971, 29.1007)) }

    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(mapCamLocation.value, 15f)
    }

    var mapProperties by remember {
        mutableStateOf(MapProperties(mapType = MapType.SATELLITE))
    }

    var uiSettings by remember {
        mutableStateOf(MapUiSettings(zoomControlsEnabled = true, mapToolbarEnabled = false,))
    }

    val lifecycleOwner = LocalLifecycleOwner.current
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                Helper().setNotifBarColor(view, window, PrimaryLight2.toArgb(),false)
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
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

        viewModel.getDeviceGeo(deviceCode)
        viewModel.getSensorSoilHumidity(deviceCode)

        if (!viewModel.deviceGeoData.value?.coordinate.isNullOrEmpty()) {
            val coordinateSplit = viewModel.deviceGeoData.value?.coordinate!!.split(",")
            if (coordinateSplit.size > 1) {
                mapCamLocation.value = LatLng(coordinateSplit[0].toDouble(), coordinateSplit[1].toDouble())
                cameraPositionState.position = CameraPosition.fromLatLngZoom(mapCamLocation.value, 20f)
            }
        }

        viewModel.isLoading.value = false

        viewModel.startPeriodicFetchingDevicesByID(deviceID)
    }

    fun showToast(message: String, duration: Int = Toast.LENGTH_SHORT) {
        Toast.makeText(context, message, duration).show()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(White),
    ) {
        NavigationBanner2(
            navController,
            stringResource(id = R.string.map),
            R.drawable.img_header_detail3,
            viewModel.selectedDevice.value,
            viewModel.isLoading.value,
        )

        if (viewModel.isLoading.value) {
            Box(
                modifier = Modifier.fillMaxSize(),
            ) {
                CircularProgressIndicator(
                    color = Primary,
                    strokeCap = StrokeCap.Round,
                    strokeWidth = 2.dp,
                    modifier = Modifier
                        .width(24.dp)
                        .height(24.dp)
                        .align(Alignment.Center)
                )
            }
        } else {
            GoogleMap(
                modifier = Modifier.fillMaxSize(),
                cameraPositionState = cameraPositionState,
                properties = mapProperties,
                uiSettings = uiSettings,
            ) {
                if (viewModel.deviceGeoData.value != null && !viewModel.deviceGeoData.value?.coordinate.isNullOrEmpty()) {
                    val idrosatLoc = viewModel.deviceGeoData.value!!.coordinate!!.split(",")
                    if (idrosatLoc.size > 1) {
                        val markerLocation = remember { mutableStateOf(LatLng(idrosatLoc[0].toDouble(), idrosatLoc[1].toDouble())) }
                        MarkerComposable(
                            state = MarkerState(position = markerLocation.value),
                        ) {
                            Image(
                                modifier = Modifier
                                    .width(58.dp),
                                contentScale = ContentScale.Inside,
                                painter = rememberDrawablePainter(
                                    drawable = getDrawable(
                                        LocalContext.current,
                                        R.drawable.ic_map_idrosat,
                                    )
                                ),
                                contentDescription = "",
                            )
                        }
                    }
                }
                for (i in viewModel.deviceGeoItem.value) {
                    DeviceGeoMarker(i)
                }
                for ((index, data) in viewModel.sensorSoilHumidity.value.withIndex()) {
                    DeviceRHMarker(data, index)
                }
            }
        }
    }
}

@Composable
fun DeviceGeoMarker(data: DeviceGeosItem?) {
    if (data != null) {
        if (!data.latitude.isNullOrEmpty() && !data.longitude.isNullOrEmpty()) {
            val markerLocation = remember { mutableStateOf(LatLng(data.latitude.toDouble(), data.longitude.toDouble())) }
            val mapInfoShown = remember { mutableStateOf(false) }

            MarkerComposable(
                state = MarkerState(position = markerLocation.value),
                visible = mapInfoShown.value,
                onClick = {
                    mapInfoShown.value = false
                    true
                }
            ) {
                Box(
                    modifier = Modifier
                        .width(200.dp)
                        .padding(bottom = 58.dp)
                        .clip(RoundedCornerShape(4.dp))
                        .background(BrokenWhite)
                        .padding(12.dp)
                        .zIndex(1f)
                ) {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                        ) {
                            Text(
                                "${stringResource(id = R.string.ev_name)}:",
                                fontFamily = Manrope,
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 14.sp,
                                color = Black,
                                textAlign = TextAlign.Center,
                                maxLines = 1,
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                data.groupName ?: "-",
                                fontFamily = Manrope,
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 14.sp,
                                color = Black,
                                textAlign = TextAlign.Center,
                                maxLines = 1,
                            )
                        }
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                        ) {
                            Text(
                                "${stringResource(id = R.string.station_number)}:",
                                fontFamily = Manrope,
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 14.sp,
                                color = Black,
                                textAlign = TextAlign.Center,
                                maxLines = 1,
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                data.stationNumber ?: "-",
                                fontFamily = Manrope,
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 14.sp,
                                color = Black,
                                textAlign = TextAlign.Center,
                                maxLines = 1,
                            )
                        }
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                        ) {
                            Text(
                                "${stringResource(id = R.string.station_serial)}:",
                                fontFamily = Manrope,
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 14.sp,
                                color = Black,
                                textAlign = TextAlign.Center,
                                maxLines = 1,
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                data.evSerial ?: "-",
                                fontFamily = Manrope,
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 14.sp,
                                color = Black,
                                textAlign = TextAlign.Center,
                                maxLines = 1,
                            )
                        }
                    }
                }
            }

            MarkerComposable(
                state = MarkerState(position = markerLocation.value),
                onClick = {
                    mapInfoShown.value = !mapInfoShown.value
                    true
                }
            ) {
                IconButton(
                    modifier = Modifier
                        .width(58.dp),
                    onClick = {
                        mapInfoShown.value = !mapInfoShown.value
                    }
                ) {
                    Box(){
                        Image(
                            contentScale = ContentScale.Inside,
                            painter = rememberDrawablePainter(
                                drawable = getDrawable(
                                    LocalContext.current,
                                    when(data.status) {
                                        "0" -> {
                                            R.drawable.ic_map_ev_off
                                        }
                                        "1" -> {
                                            R.drawable.ic_map_ev
                                        }
                                        "2" -> {
                                            R.drawable.ic_map_ev_alarm
                                        }
                                        else -> {
                                            R.drawable.ic_map_ev_off
                                        }
                                    }
                                )
                            ),
                            contentDescription = "",
                        )

                        Box(
                            modifier = Modifier
                                .padding(start = 8.dp, end = 8.dp, top = 12.dp)
                                .clip(RoundedCornerShape(4.dp))
                                .background(BrokenWhite)
                                .align(Alignment.BottomCenter)
                        ) {
                            Text(
                                data.stationNumber ?: "-",
                                fontFamily = Manrope,
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 14.sp,
                                color = Black,
                                modifier = Modifier.fillMaxWidth(),
                                textAlign = TextAlign.Center,
                                maxLines = 1,
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun DeviceRHMarker(data: RhsItem?, index: Int) {
    if (data != null) {
        if (!data.latitude.isNullOrEmpty() && !data.longitude.isNullOrEmpty()) {
            val level = data.level?.split(",")

            val markerLocation = remember { mutableStateOf(LatLng(data.latitude.toDouble(), data.longitude.toDouble())) }
            val mapInfoShown = remember { mutableStateOf(false) }

            MarkerComposable(
                state = MarkerState(position = markerLocation.value),
                visible = mapInfoShown.value,
                onClick = {
                    mapInfoShown.value = false
                    true
                }
            ) {
                Box(
                    modifier = Modifier
                        .width(200.dp)
                        .padding(bottom = 58.dp)
                        .clip(RoundedCornerShape(4.dp))
                        .background(BrokenWhite)
                        .padding(12.dp)
                        .zIndex(1f)
                ) {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                        ) {
                            Text(
                                "${stringResource(id = R.string.level)} 1 :",
                                fontFamily = Manrope,
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 14.sp,
                                color = Black,
                                textAlign = TextAlign.Center,
                                maxLines = 1,
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                level?.getOrNull(0) ?: "-",
                                fontFamily = Manrope,
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 14.sp,
                                color = Black,
                                textAlign = TextAlign.Center,
                                maxLines = 1,
                            )
                        }
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                        ) {
                            Text(
                                "${stringResource(id = R.string.level)} 2 :",
                                fontFamily = Manrope,
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 14.sp,
                                color = Black,
                                textAlign = TextAlign.Center,
                                maxLines = 1,
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                level?.getOrNull(1) ?: "-",
                                fontFamily = Manrope,
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 14.sp,
                                color = Black,
                                textAlign = TextAlign.Center,
                                maxLines = 1,
                            )
                        }
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                        ) {
                            Text(
                                "${stringResource(id = R.string.level)} 3 :",
                                fontFamily = Manrope,
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 14.sp,
                                color = Black,
                                textAlign = TextAlign.Center,
                                maxLines = 1,
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                level?.getOrNull(2) ?: "-",
                                fontFamily = Manrope,
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 14.sp,
                                color = Black,
                                textAlign = TextAlign.Center,
                                maxLines = 1,
                            )
                        }
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                        ) {
                            Text(
                                "${stringResource(id = R.string.level)} 4 :",
                                fontFamily = Manrope,
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 14.sp,
                                color = Black,
                                textAlign = TextAlign.Center,
                                maxLines = 1,
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                level?.getOrNull(3) ?: "-",
                                fontFamily = Manrope,
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 14.sp,
                                color = Black,
                                textAlign = TextAlign.Center,
                                maxLines = 1,
                            )
                        }
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                        ) {
                            Text(
                                "${stringResource(id = R.string.name)} :",
                                fontFamily = Manrope,
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 14.sp,
                                color = Black,
                                textAlign = TextAlign.Center,
                                maxLines = 1,
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                "RH${index}",
                                fontFamily = Manrope,
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 14.sp,
                                color = Black,
                                textAlign = TextAlign.Center,
                                maxLines = 1,
                            )
                        }
                    }
                }
            }

            MarkerComposable(
                state = MarkerState(position = markerLocation.value),
                onClick = {
                    mapInfoShown.value = !mapInfoShown.value
                    true
                }
            ) {
                IconButton(
                    modifier = Modifier
                        .width(58.dp),
                    onClick = {
                        mapInfoShown.value = !mapInfoShown.value
                    }
                ) {
                    Box(){
                        Image(
                            contentScale = ContentScale.Inside,
                            painter = rememberDrawablePainter(
                                drawable = getDrawable(
                                    LocalContext.current,
                                    R.drawable.ic_map_rh
                                )
                            ),
                            contentDescription = "",
                        )
                    }
                }
            }
        }
    }
}
