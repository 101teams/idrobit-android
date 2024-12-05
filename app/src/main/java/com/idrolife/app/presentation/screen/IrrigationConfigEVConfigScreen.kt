package com.idrolife.app.presentation.screen

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.CameraSelector
import androidx.camera.core.ExperimentalGetImage
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Card
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.navigation.NavController
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.rememberPagerState
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberPermissionState
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.tasks.CancellationTokenSource
import com.google.mlkit.vision.barcode.BarcodeScanner
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.common.InputImage
import com.idrolife.app.R
import com.idrolife.app.data.api.irrigation.IrrigationConfigDeviceGeoRequest
import com.idrolife.app.data.api.irrigation.IrrigationConfigEVConfigList
import com.idrolife.app.navigation.Screen
import com.idrolife.app.presentation.component.CheckPermission
import com.idrolife.app.presentation.component.DropDown
import com.idrolife.app.presentation.component.Input
import com.idrolife.app.presentation.component.NavigationBanner3
import com.idrolife.app.presentation.viewmodel.DeviceViewModel
import com.idrolife.app.theme.Black
import com.idrolife.app.theme.BrokenWhite
import com.idrolife.app.theme.DefaultRed
import com.idrolife.app.theme.GrayLight
import com.idrolife.app.theme.GrayVeryVeryLight
import com.idrolife.app.theme.Primary
import com.idrolife.app.theme.Primary2
import com.idrolife.app.theme.Manrope
import com.idrolife.app.theme.White
import com.idrolife.app.utils.Helper
import com.idrolife.app.utils.SystemBroadcastReceiver
import kotlinx.coroutines.launch

@OptIn(ExperimentalPagerApi::class)
@Composable
fun IrrigationConfigEVConfigScreen(
    navController: NavController,
    deviceID: String,
    deviceCode: String
) {
    val context = LocalContext.current

    val window = (context as Activity).window
    val view = LocalView.current

    val viewModel = hiltViewModel<DeviceViewModel>()
    val scope = rememberCoroutineScope()

    val pagerState = rememberPagerState()
    val tabs = listOf(context.getString(R.string.ev_list), context.getString(R.string.manual), context.getString(R.string.with_qr_code))

    val lifecycleOwner = LocalLifecycleOwner.current
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                Helper().setNotifBarColor(view, window, BrokenWhite.toArgb(),true)
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
        NavigationBanner3(
            navController,
            stringResource(id = R.string.ev_configuration),
            R.drawable.img_header_detail3,
            viewModel.selectedDevice.value,
            viewModel.isLoading.value,
        )

        Column(
            modifier = Modifier
                .fillMaxSize(),
        ) {
            Spacer(modifier = Modifier.height(24.dp))
            // Tab bar
            if (!viewModel.setMarkerLoading.value || pagerState.currentPage == 1 || pagerState.currentPage == 2) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Start,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp)
                ) {
                    tabs.forEachIndexed { index, title ->
                        Text(
                            text = title,
                            modifier = Modifier
                                .background(
                                    if (pagerState.currentPage == index) Primary2 else Color.Transparent,
                                    shape = RoundedCornerShape(24.dp)
                                )
                                .border(
                                    width = 1.dp,
                                    color = Primary2,
                                    shape = RoundedCornerShape(24.dp)
                                )
                                .clickable {
                                    scope.launch {
                                        pagerState.animateScrollToPage(index)
                                    }
                                }
                                .padding(horizontal = 16.dp, vertical = 8.dp),
                            color = if (pagerState.currentPage == index) Color.White else Primary,
                            fontSize = 12.sp,
                            fontFamily = Manrope,
                            fontWeight = FontWeight.SemiBold,
                        )

                        Spacer(modifier = Modifier.width(8.dp))
                    }
                }
            }

            // Horizontal pager
            HorizontalPager(
                count = tabs.size,
                state = pagerState,
                modifier = Modifier.fillMaxSize(),
            ) { page ->
                when (page) {
                    0 -> {
                        IrrigationConfigEVConfigPage1(
                            deviceCode
                        )
                    }
                    1 -> {
                        IrrigationConfigEVConfigPage2(viewModel)
                    }
                    else -> {
                        IrrigationConfigEVConfigPage3(viewModel)
                    }
                }
            }
        }
    }
}

@Composable
fun IrrigationConfigEVConfigPage1(deviceCode: String) {
    val viewModel = hiltViewModel<DeviceViewModel>()
    val scope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        viewModel.setMarkerLoading.value = true
        viewModel.getIrrigationConfigEVConfigList(deviceCode)
        viewModel.setMarkerLoading.value = false
    }

    Column {
        if (viewModel.setMarkerLoading.value) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(76.dp)
                    .weight(1f)
            ) {
                CircularProgressIndicator(
                    color = Primary,
                    strokeCap = StrokeCap.Round,
                    strokeWidth = 2.dp,
                    modifier = Modifier
                        .width(18.dp)
                        .height(18.dp)
                        .align(Alignment.Center)
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp)
                    .weight(1f)
            ) {
                item {
                    Spacer(modifier = Modifier.height(24.dp))
                }
                var index = 0
                items(viewModel.irrigationConfigEvConfigList.value) {
                    if (it.evSerial != "FFFFFF") {
                        IrrigationConfigEVConfigPage1Card(it, viewModel, index)
                    }
                    index += 1
                }
                item {
                    Spacer(modifier = Modifier.height(12.dp))
                }
            }
        }
    }
}


@Composable
fun IrrigationConfigEVConfigPage1Card(data: IrrigationConfigEVConfigList, viewModel: DeviceViewModel, index: Int) {
    val scope = rememberCoroutineScope()
    var deleteLoading by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        elevation = 4.dp,
        backgroundColor = GrayVeryVeryLight,
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp, horizontal = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Text(
                    stringResource(id = R.string.id_serial),
                    style = TextStyle(
                        fontSize = 14.sp,
                        fontFamily = Manrope,
                        fontWeight = FontWeight.Normal,
                        textAlign = TextAlign.Left,
                        color = GrayLight,
                    ),
                )
                Text(data.evSerial ?: "-",
                    style = TextStyle(
                        fontSize = 14.sp,
                        fontFamily = Manrope,
                        fontWeight = FontWeight.Normal,
                        textAlign = TextAlign.Left,
                        color = Black,
                    ),
                )
            }
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Text(
                    stringResource(id = R.string.group),
                    style = TextStyle(
                        fontSize = 14.sp,
                        fontFamily = Manrope,
                        fontWeight = FontWeight.Normal,
                        textAlign = TextAlign.Left,
                        color = GrayLight,
                    ),
                )
                Text(data.station ?: "-",
                    style = TextStyle(
                        fontSize = 14.sp,
                        fontFamily = Manrope,
                        fontWeight = FontWeight.Normal,
                        textAlign = TextAlign.Left,
                        color = Black,
                    ),
                )
            }

            Button(
                modifier = Modifier
                    .clip(RoundedCornerShape(4.dp))
                    .height(40.dp)
                    .width(100.dp),
                contentPadding = PaddingValues(0.dp),
                onClick = {
                    scope.launch {
                        deleteLoading = true
                        val initialIndex = data.index * 6
                        val sendData = mutableMapOf<String, String>()

                        sendData["S${2000 + (initialIndex)}"] = "FFFFFF"
                        sendData["S${2000 + (initialIndex + 2)}"] = "0"

                        viewModel.postIrrigationConfigNominalFlow(
                            deviceCode = viewModel.selectedDevice.value?.code ?: "",
                            data = sendData,
                        )

                        viewModel._irrigationConfigEvConfigList.value = viewModel._irrigationConfigEvConfigList.value.map { item ->
                            if (item.index == data.index) {
                                item.copy(evSerial = "FFFFFF")
                            } else {
                                item
                            }
                        }

                        deleteLoading = false
                    }
                },
                colors = ButtonDefaults.buttonColors(backgroundColor = DefaultRed),
            ) {
                if (deleteLoading) {
                    CircularProgressIndicator(
                        color = White,
                        strokeCap = StrokeCap.Round,
                        strokeWidth = 2.dp,
                        modifier = Modifier
                            .width(18.dp)
                            .height(18.dp)
                    )
                } else {
                    Text(
                        modifier = Modifier
                            .padding(horizontal = 24.dp),
                        text = stringResource(id = R.string.delete),
                        style = MaterialTheme.typography.button,
                        fontSize = 14.sp
                    )
                }
            }
        }
    }

    Spacer(modifier = Modifier.height(12.dp))
}

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun IrrigationConfigEVConfigPage2(viewModel: DeviceViewModel) {

    val context = LocalContext.current
    val permissionState = rememberPermissionState(Manifest.permission.ACCESS_FINE_LOCATION)
    val scope = rememberCoroutineScope()

    var availableSerial by remember { mutableStateOf(mutableListOf(Pair("",""))) }

    val fusedLocationClient = remember { LocationServices.getFusedLocationProviderClient(context) }
    val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
    var isLocationEnabled by remember { mutableStateOf(locationManager.isProviderEnabled(
        LocationManager.GPS_PROVIDER)) }
    var checkedPermission by remember { mutableStateOf(true) }
    var getLocationLoading by remember { mutableStateOf(false) }

    val coordinate = remember { mutableStateOf("") }
    val idSerial = remember { mutableStateOf("") }
    val group = remember { mutableStateOf("") }

    val selectedStation = remember { mutableStateOf("") }

    fun getCurrentLocation() {
        if (ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED) {

            if (!isLocationEnabled) {
                Toast.makeText(context, context.getString(R.string.bt_location_request_usage), Toast.LENGTH_SHORT).show()
                return
            }

            getLocationLoading = true
            fusedLocationClient.getCurrentLocation(LocationRequest.PRIORITY_HIGH_ACCURACY, CancellationTokenSource().token)
                .addOnSuccessListener { location: Location? ->
                    if (location != null) {
                        getLocationLoading = false
                        coordinate.value = "${location.latitude}, ${location.longitude}"
                    } else {
                        getLocationLoading = false
                        Toast.makeText(context, context.getText(R.string.location_not_found), Toast.LENGTH_SHORT).show()
                    }
                }.addOnFailureListener { exception ->
                    getLocationLoading = false
                    Toast.makeText(context, "${context.getText(R.string.failed_get_location)}: ${exception.message}", Toast.LENGTH_SHORT).show()
                }

        }
    }

    if (!checkedPermission) {
        CheckPermission(onPermissionGranted = {
            checkedPermission = true
            getCurrentLocation()
        }, onPermissionDenied = {
            checkedPermission = true
            permissionState.launchPermissionRequest()
        })
    }

    SystemBroadcastReceiver(action = LocationManager.MODE_CHANGED_ACTION) {
        val action = it?.action ?: return@SystemBroadcastReceiver
        if (action != LocationManager.MODE_CHANGED_ACTION) return@SystemBroadcastReceiver

        isLocationEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
    }


    LaunchedEffect(viewModel.irrigationConfigEvConfigList.value) {
        val tempAvailableSerial: MutableList<Pair<String, String>> = mutableListOf()

        for (i in viewModel.irrigationConfigEvConfigList.value) {
            if (i.evSerial == "FFFFFF") {
                val initialIndex = (i.index * 6)

                val data = Pair((i.index+1).toString(), "${2000 + (initialIndex)}")
                tempAvailableSerial.add(data)
            }
        }

        availableSerial = tempAvailableSerial
    }

    Column {
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
                .weight(1f)
        ) {
            item {
                Spacer(modifier = Modifier.height(24.dp))

                Text(
                    stringResource(id = R.string.ev_configuration),
                    fontFamily = Manrope,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 14.sp,
                    color = Black,
                )

                Spacer(modifier = Modifier.height(6.dp))

                DropDown(
                    field = stringResource(id = R.string.station_number),
                    availableSerial,
                    selectedValue = "",
                    modifier = Modifier,
                    onSelectItem = { _, it ->
                        selectedStation.value = it
                    }
                )

                Spacer(modifier = Modifier.height(24.dp))

                Input(
                    modifier = Modifier,
                    field = stringResource(id = R.string.id_serial),
                    placeholder = stringResource(id = R.string.id_serial),
                    disabled = false,
                    keyboardOptions = KeyboardOptions.Default.copy(
                        keyboardType = KeyboardType.Text,
                        imeAction = ImeAction.Next
                    ),
                    modifierParent = Modifier,
                    binding = idSerial,
                )

                Spacer(modifier = Modifier.height(12.dp))

                Input(
                    modifier = Modifier,
                    field = stringResource(id = R.string.group),
                    placeholder = stringResource(id = R.string.group),
                    disabled = false,
                    keyboardOptions = KeyboardOptions.Default.copy(
                        keyboardType = KeyboardType.Number,
                        imeAction = ImeAction.Next
                    ),
                    modifierParent = Modifier,
                    binding = group,
                )

                Spacer(modifier = Modifier.height(12.dp))

                Input(
                    modifier = Modifier,
                    field = stringResource(id = R.string.coordinate),
                    placeholder = stringResource(id = R.string.coordinate),
                    disabled = false,
                    keyboardOptions = KeyboardOptions.Default.copy(
                        keyboardType = KeyboardType.Number,
                        imeAction = ImeAction.Done
                    ),
                    modifierParent = Modifier,
                    binding = coordinate,
                )

                Spacer(modifier = Modifier.height(12.dp))

                Button(
                    modifier = Modifier
                        .clip(RoundedCornerShape(4.dp))
                        .height(62.dp)
                        .fillMaxWidth()
                        .padding(top = 12.dp),
                    contentPadding = PaddingValues(0.dp),
                    onClick = {
                        scope.launch {
                            checkedPermission = false
                        }
                    },
                    colors = ButtonDefaults.buttonColors(backgroundColor = White,),
                    border = BorderStroke(1.dp, Primary),
                ) {
                    if (getLocationLoading) {
                        CircularProgressIndicator(
                            color = Primary,
                            strokeCap = StrokeCap.Round,
                            strokeWidth = 2.dp,
                            modifier = Modifier
                                .width(18.dp)
                                .height(18.dp)
                        )
                    } else {
                        Text(stringResource(id = R.string.get_current_location), style = MaterialTheme.typography.button, fontSize = 18.sp, color = Primary)
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))
            }
        }

        Column(
            modifier = Modifier
                .background(White)
                .padding(start = 24.dp, top = 12.dp, end = 24.dp, bottom = 24.dp)
        ) {
            Button(
                modifier = Modifier
                    .clip(RoundedCornerShape(4.dp))
                    .height(62.dp)
                    .fillMaxWidth()
                    .padding(top = 12.dp),
                contentPadding = PaddingValues(0.dp),
                onClick = {
                    scope.launch {
                        if (selectedStation.value.isNotEmpty()) {
                            viewModel.postDataLoading.value = true
                            val sendData = mutableMapOf<String, String>()

                            sendData["S${selectedStation.value}"] = idSerial.value
                            sendData["S${selectedStation.value.toInt() + 2}"] = group.value

                            viewModel.postIrrigationConfigNominalFlow(
                                deviceCode = viewModel.selectedDevice.value?.code ?: "",
                                data = sendData,
                            )

                            val coord = coordinate.value.split(",")
                            var lat = ""
                            var lng = ""
                            if (coord.isNotEmpty()) {
                                lat = coord[0]
                            }
                            if (coord.size > 1) {
                                lng = coord[1]
                            }
                            viewModel.postIrrigationConfigDeviceGeo(
                                request = IrrigationConfigDeviceGeoRequest(
                                    deviceCode = viewModel.selectedDevice.value?.code ?: "",
                                    evSerial = idSerial.value,
                                    latitude = lat,
                                    longitude = lng,
                                )
                            )
                            viewModel.postDataLoading.value = false
                        }
                    }
                },
                colors = ButtonDefaults.buttonColors(backgroundColor = Primary2),
            ) {
                if (viewModel.postDataLoading.value) {
                    CircularProgressIndicator(
                        color = White,
                        strokeCap = StrokeCap.Round,
                        strokeWidth = 2.dp,
                        modifier = Modifier
                            .width(18.dp)
                            .height(18.dp)
                    )
                } else {
                    Text(stringResource(id = R.string.save), style = MaterialTheme.typography.button, fontSize = 18.sp)
                }
            }
        }
    }
}

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun IrrigationConfigEVConfigPage3(viewModel: DeviceViewModel) {

    val context = LocalContext.current
    val permissionState = rememberPermissionState(Manifest.permission.ACCESS_FINE_LOCATION)
    val scope = rememberCoroutineScope()

    var availableSerial by remember { mutableStateOf(mutableListOf(Pair("",""))) }

    val fusedLocationClient = remember { LocationServices.getFusedLocationProviderClient(context) }
    val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
    var isLocationEnabled by remember { mutableStateOf(locationManager.isProviderEnabled(
        LocationManager.GPS_PROVIDER)) }
    var checkedPermission by remember { mutableStateOf(true) }
    var getLocationLoading by remember { mutableStateOf(false) }

    val coordinate = remember { mutableStateOf("") }
    val idSerial = remember { mutableStateOf("") }
    val group = remember { mutableStateOf("") }

    val selectedStation = remember { mutableStateOf("") }

    fun getCurrentLocation() {
        if (ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED) {

            if (!isLocationEnabled) {
                Toast.makeText(context, context.getString(R.string.bt_location_request_usage), Toast.LENGTH_SHORT).show()
                return
            }

            getLocationLoading = true
            fusedLocationClient.getCurrentLocation(LocationRequest.PRIORITY_HIGH_ACCURACY, CancellationTokenSource().token)
                .addOnSuccessListener { location: Location? ->
                    if (location != null) {
                        getLocationLoading = false
                        coordinate.value = "${location.latitude}, ${location.longitude}"
                    } else {
                        getLocationLoading = false
                        Toast.makeText(context, context.getText(R.string.location_not_found), Toast.LENGTH_SHORT).show()
                    }
                }.addOnFailureListener { exception ->
                    getLocationLoading = false
                    Toast.makeText(context, "${context.getText(R.string.failed_get_location)}: ${exception.message}", Toast.LENGTH_SHORT).show()
                }

        }
    }

    if (!checkedPermission) {
        CheckPermission(onPermissionGranted = {
            checkedPermission = true
            getCurrentLocation()
        }, onPermissionDenied = {
            checkedPermission = true
            permissionState.launchPermissionRequest()
        })
    }

    SystemBroadcastReceiver(action = LocationManager.MODE_CHANGED_ACTION) {
        val action = it?.action ?: return@SystemBroadcastReceiver
        if (action != LocationManager.MODE_CHANGED_ACTION) return@SystemBroadcastReceiver

        isLocationEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
    }


    LaunchedEffect(viewModel.irrigationConfigEvConfigList.value) {
        val tempAvailableSerial: MutableList<Pair<String, String>> = mutableListOf()

        for (i in viewModel.irrigationConfigEvConfigList.value) {
            if (i.evSerial == "FFFFFF") {
                val initialIndex = (i.index * 6)

                val data = Pair((i.index+1).toString(), "${2000 + (initialIndex)}")
                tempAvailableSerial.add(data)
            }
        }

        availableSerial = tempAvailableSerial
    }

    Column {
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
                .weight(1f)
        ) {
            item {
                Spacer(modifier = Modifier.height(24.dp))

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                ) {
                    QRCodeScannerScreen(onQRCodeScanned = {
                        idSerial.value = it
                    })
                }

                Column(
                    modifier = Modifier.background(White)
                ) {
                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        stringResource(id = R.string.ev_configuration),
                        fontFamily = Manrope,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 14.sp,
                        color = Black,
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    DropDown(
                        field = stringResource(id = R.string.station_number),
                        availableSerial,
                        selectedValue = "",
                        modifier = Modifier,
                        onSelectItem = { _, it ->
                            selectedStation.value = it
                        }
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    Input(
                        modifier = Modifier,
                        field = stringResource(id = R.string.id_serial),
                        placeholder = stringResource(id = R.string.id_serial),
                        disabled = true,
                        keyboardOptions = KeyboardOptions.Default.copy(
                            keyboardType = KeyboardType.Text,
                            imeAction = ImeAction.Next
                        ),
                        modifierParent = Modifier,
                        binding = idSerial,
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Input(
                        modifier = Modifier,
                        field = stringResource(id = R.string.group),
                        placeholder = stringResource(id = R.string.group),
                        disabled = false,
                        keyboardOptions = KeyboardOptions.Default.copy(
                            keyboardType = KeyboardType.Number,
                            imeAction = ImeAction.Next
                        ),
                        modifierParent = Modifier,
                        binding = group,
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Input(
                        modifier = Modifier,
                        field = stringResource(id = R.string.coordinate),
                        placeholder = stringResource(id = R.string.coordinate),
                        disabled = false,
                        keyboardOptions = KeyboardOptions.Default.copy(
                            keyboardType = KeyboardType.Number,
                            imeAction = ImeAction.Done
                        ),
                        modifierParent = Modifier,
                        binding = coordinate,
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Button(
                        modifier = Modifier
                            .clip(RoundedCornerShape(4.dp))
                            .height(62.dp)
                            .fillMaxWidth()
                            .padding(top = 12.dp),
                        contentPadding = PaddingValues(0.dp),
                        onClick = {
                            scope.launch {
                                checkedPermission = false
                            }
                        },
                        colors = ButtonDefaults.buttonColors(backgroundColor = White,),
                        border = BorderStroke(1.dp, Primary),
                    ) {
                        if (getLocationLoading) {
                            CircularProgressIndicator(
                                color = Primary,
                                strokeCap = StrokeCap.Round,
                                strokeWidth = 2.dp,
                                modifier = Modifier
                                    .width(18.dp)
                                    .height(18.dp)
                            )
                        } else {
                            Text(stringResource(id = R.string.get_current_location), style = MaterialTheme.typography.button, fontSize = 18.sp, color = Primary)
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))
                }
            }
        }

        Column(
            modifier = Modifier
                .background(White)
                .padding(start = 24.dp, top = 12.dp, end = 24.dp, bottom = 24.dp)
        ) {
            Button(
                modifier = Modifier
                    .clip(RoundedCornerShape(4.dp))
                    .height(62.dp)
                    .fillMaxWidth()
                    .padding(top = 12.dp),
                contentPadding = PaddingValues(0.dp),
                onClick = {
                    scope.launch {
                        if (selectedStation.value.isNotEmpty()) {
                            viewModel.postDataLoading.value = true
                            val sendData = mutableMapOf<String, String>()

                            sendData["S${selectedStation.value}"] = idSerial.value
                            sendData["S${selectedStation.value.toInt() + 2}"] = group.value

                            viewModel.postIrrigationConfigNominalFlow(
                                deviceCode = viewModel.selectedDevice.value?.code ?: "",
                                data = sendData,
                            )

                            val coord = coordinate.value.split(",")
                            var lat = ""
                            var lng = ""
                            if (coord.isNotEmpty()) {
                                lat = coord[0]
                            }
                            if (coord.size > 1) {
                                lng = coord[1]
                            }
                            viewModel.postIrrigationConfigDeviceGeo(
                                request = IrrigationConfigDeviceGeoRequest(
                                    deviceCode = viewModel.selectedDevice.value?.code ?: "",
                                    evSerial = idSerial.value,
                                    latitude = lat,
                                    longitude = lng,
                                )
                            )
                            viewModel.postDataLoading.value = false
                        }
                    }
                },
                colors = ButtonDefaults.buttonColors(backgroundColor = Primary2),
            ) {
                if (viewModel.postDataLoading.value) {
                    CircularProgressIndicator(
                        color = White,
                        strokeCap = StrokeCap.Round,
                        strokeWidth = 2.dp,
                        modifier = Modifier
                            .width(18.dp)
                            .height(18.dp)
                    )
                } else {
                    Text(stringResource(id = R.string.save), style = MaterialTheme.typography.button, fontSize = 18.sp)
                }
            }
        }
    }
}

@Composable
fun QRCodeScannerScreen(onQRCodeScanned: (String) -> Unit) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    var hasCameraPermission by remember { mutableStateOf(false) }
    val cameraPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { granted ->
            hasCameraPermission = granted
        }
    )
    val cameraProviderFuture = remember { ProcessCameraProvider.getInstance(context) }

    DisposableEffect(cameraProviderFuture) {
        onDispose {
            cameraProviderFuture.get().unbindAll()
        }
    }

    // Cek izin kamera
    LaunchedEffect(Unit) {
        hasCameraPermission = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.CAMERA
        ) == PackageManager.PERMISSION_GRANTED

        if (!hasCameraPermission) {
            cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
        }
    }

    if (hasCameraPermission) {
        AndroidView(
            modifier = Modifier
                .fillMaxWidth(),
            factory = { ctx ->
                PreviewView(ctx).apply {
                    layoutParams = ViewGroup.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT
                    )
                    this.scaleType = PreviewView.ScaleType.FILL_START
                    implementationMode = PreviewView.ImplementationMode.COMPATIBLE
                }
            },
            update = {previewView ->
                val cameraProvider = cameraProviderFuture.get()
                val preview = Preview.Builder().build().also {
                    it.surfaceProvider = previewView.surfaceProvider
                }

                val barcodeScanner = BarcodeScanning.getClient()
                val imageAnalyzer = ImageAnalysis.Builder()
                    .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                    .build()
                    .also {
                        it.setAnalyzer(ContextCompat.getMainExecutor(context)) { imageProxy ->
                            processImageProxy(barcodeScanner, imageProxy, onQRCodeScanned)
                        }
                    }

                val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA
                cameraProvider.unbindAll()
                cameraProvider.bindToLifecycle(lifecycleOwner, cameraSelector, preview, imageAnalyzer)
            }
        )
    } else {
        // Tampilkan pesan jika izin kamera belum diberikan
        Text("Izin kamera diperlukan untuk memindai QR Code")
    }
}

@androidx.annotation.OptIn(ExperimentalGetImage::class)
private fun processImageProxy(
    barcodeScanner: BarcodeScanner,
    imageProxy: ImageProxy,
    onQRCodeScanned: (String) -> Unit
) {
    val mediaImage = imageProxy.image
    if (mediaImage != null) {
        val inputImage = InputImage.fromMediaImage(mediaImage, imageProxy.imageInfo.rotationDegrees)
        barcodeScanner.process(inputImage)
            .addOnSuccessListener { barcodes ->
                for (barcode in barcodes) {
                    // Mendapatkan hasil dari QR Code
                    val qrCodeValue = barcode.rawValue
                    if (qrCodeValue != null) {
                        onQRCodeScanned(qrCodeValue)
                    }
                }
            }
            .addOnFailureListener {
                // Gagal memindai
            }
            .addOnCompleteListener {
                imageProxy.close()
            }
    }
}