package com.idrolife.app.presentation.screen

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.widget.Toast
import androidx.camera.core.ExperimentalGetImage
import androidx.camera.core.ImageProxy
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.MutableState
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
import com.google.mlkit.vision.common.InputImage
import com.idrolife.app.BuildConfig
import com.idrolife.app.R
import com.idrolife.app.data.api.device.CreatePlantRequest
import com.idrolife.app.navigation.Screen
import com.idrolife.app.presentation.component.CheckPermission
import com.idrolife.app.presentation.component.CustomTopBarSimple
import com.idrolife.app.presentation.component.DialogCancelCreatePlant
import com.idrolife.app.presentation.component.Input
import com.idrolife.app.presentation.component.PasswordInput
import com.idrolife.app.presentation.component.TopToastDialog
import com.idrolife.app.presentation.viewmodel.DeviceViewModel
import com.idrolife.app.theme.Black
import com.idrolife.app.theme.BrokenWhite
import com.idrolife.app.theme.GrayLight
import com.idrolife.app.theme.Manrope
import com.idrolife.app.theme.Primary
import com.idrolife.app.theme.Primary2
import com.idrolife.app.theme.PrimarySoft
import com.idrolife.app.theme.White
import com.idrolife.app.utils.Helper
import com.idrolife.app.utils.SystemBroadcastReceiver
import kotlinx.coroutines.launch

@OptIn(ExperimentalPagerApi::class)
@Composable
fun CreatePlantScreen(
    navController: NavController,
) {
    val context = LocalContext.current

    val window = (context as Activity).window
    val view = LocalView.current

    val viewModel = hiltViewModel<DeviceViewModel>()
    val scope = rememberCoroutineScope()

    val pagerState = rememberPagerState()
    val tabs = listOf(context.getString(R.string.manual), context.getString(R.string.with_qr_code))

    val showToast = remember { mutableStateOf(false) }
    val showCancelDialog = remember { mutableStateOf(false) }

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

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(White),
    ) {
        CustomTopBarSimple(navController, stringResource(id = R.string.create_plant))

        Box() {
            Column(
                modifier = Modifier
                    .fillMaxSize(),
            ) {
                Spacer(modifier = Modifier.height(24.dp))
                // Tab bar
                if (!viewModel.setMarkerLoading.value || pagerState.currentPage == 1) {
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
                            CreatePlantPage1(viewModel, navController, showToast, showCancelDialog)
                        }
                        1 -> {
                            CreatePlantPage2(viewModel, navController, showToast, showCancelDialog)
                        }
                    }
                }
            }

            TopToastDialog(
                stringResource(id = R.string.success_post_data),
                showToast.value,
                onDismiss = {
                    showToast.value = false
                }
            )

            if (showCancelDialog.value) {
                DialogCancelCreatePlant(
                    onDismiss = {
                        showCancelDialog.value = false
                    },
                    onClickCancel = {
                        showCancelDialog.value = false
                    },
                    onClickContinue = {
                        showCancelDialog.value = false
                        navController.popBackStack()
                    },
                )
            }
        }
    }
}

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun CreatePlantPage1(viewModel: DeviceViewModel, navController: NavController, showToast: MutableState<Boolean>, showCancelDialog: MutableState<Boolean>) {

    val context = LocalContext.current
    val permissionState = rememberPermissionState(Manifest.permission.ACCESS_FINE_LOCATION)
    val scope = rememberCoroutineScope()

    val fusedLocationClient = remember { LocationServices.getFusedLocationProviderClient(context) }
    val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
    var isLocationEnabled by remember { mutableStateOf(locationManager.isProviderEnabled(
        LocationManager.GPS_PROVIDER)) }
    var checkedPermission by remember { mutableStateOf(true) }
    var getLocationLoading by remember { mutableStateOf(false) }

    val coordinate = remember { mutableStateOf("") }

    val deviceID = remember { mutableStateOf("") }
    val name = remember { mutableStateOf("") }
    val password = remember { mutableStateOf("") }
    val type = remember { mutableStateOf("") }

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
                    stringResource(id = R.string.create_plant),
                    fontFamily = Manrope,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 14.sp,
                    color = Black,
                )

                Spacer(modifier = Modifier.height(18.dp))

                Input(
                    modifier = Modifier,
                    field = stringResource(id = R.string.device_id),
                    placeholder = stringResource(id = R.string.device_id),
                    disabled = false,
                    keyboardOptions = KeyboardOptions.Default.copy(
                        keyboardType = KeyboardType.Text,
                        imeAction = ImeAction.Next
                    ),
                    modifierParent = Modifier,
                    binding = deviceID,
                )

                Spacer(modifier = Modifier.height(12.dp))

                Input(
                    modifier = Modifier,
                    field = stringResource(id = R.string.name),
                    placeholder = stringResource(id = R.string.name),
                    disabled = false,
                    keyboardOptions = KeyboardOptions.Default.copy(
                        keyboardType = KeyboardType.Text,
                        imeAction = ImeAction.Next
                    ),
                    modifierParent = Modifier,
                    binding = name,
                )

                Spacer(modifier = Modifier.height(12.dp))

                PasswordInput(
                    modifier = Modifier,
                    field = stringResource(id = R.string.password),
                    placeholder = stringResource(id = R.string.password),
                    disabled = false,
                    binding = password,
                )

                Spacer(modifier = Modifier.height(12.dp))

                Input(
                    modifier = Modifier,
                    field = stringResource(id = R.string.type),
                    placeholder = stringResource(id = R.string.type),
                    disabled = false,
                    keyboardOptions = KeyboardOptions.Default.copy(
                        keyboardType = KeyboardType.Text,
                        imeAction = ImeAction.Next
                    ),
                    modifierParent = Modifier,
                    binding = type,
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
                    colors = ButtonDefaults.buttonColors(
                        backgroundColor = when(BuildConfig.FLAVOR) {
                          "idroPro", "idroRes", "irriLife" -> {
                              PrimarySoft
                          } else -> {
                              White
                          }
                       }
                    ,),
                    border = BorderStroke(1.dp, when(BuildConfig.FLAVOR) {
                        "idroPro", "idroRes", "irriLife" -> {
                            PrimarySoft
                        } else -> {
                            Primary
                        }
                    }),
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
                        if (
                            deviceID.value.isNotEmpty() &&
                            name.value.isNotEmpty() &&
                            password.value.isNotEmpty() &&
                            type.value.isNotEmpty() &&
                            coordinate.value.isNotEmpty()
                        ) {
                            viewModel.postDataLoading.value = true
                            val postData = CreatePlantRequest(
                                code = deviceID.value,
                                name = name.value,
                                password = password.value,
                                type = type.value,
                                coordinate = coordinate.value,
                                company = Helper().getCompanyByFlavor(),
                            )

                            val result = viewModel.postCreatePlant(postData)
                            if (result.second == "Unauthorized") {
                                navController.navigate(Screen.Login.route) {
                                    popUpTo(navController.graph.id) {
                                        inclusive = true
                                    }
                                }
                            } else if (!result.second.isNullOrEmpty() && !result.second!!.contains("coroutine scope")) {
                                Toast.makeText(context, result.second, Toast.LENGTH_LONG)
                                    .show()
                            }

                            if (result.first) {
                                deviceID.value = ""
                                name.value = ""
                                password.value = ""
                                type.value = ""
                                coordinate.value = ""
                                showToast.value = true
                            }
                            viewModel.postDataLoading.value = false
                        }
                    }
                },
                colors = ButtonDefaults.buttonColors(
                    backgroundColor = if (
                        deviceID.value.isNotEmpty() &&
                        name.value.isNotEmpty() &&
                        password.value.isNotEmpty() &&
                        type.value.isNotEmpty() &&
                        coordinate.value.isNotEmpty()
                    ) Primary2 else GrayLight
                ),
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

            Spacer(modifier = Modifier.height(6.dp))

            Button(
                modifier = Modifier
                    .clip(RoundedCornerShape(4.dp))
                    .height(62.dp)
                    .fillMaxWidth()
                    .padding(top = 12.dp),
                contentPadding = PaddingValues(0.dp),
                onClick = {
                    showCancelDialog.value = true
                },
                colors = ButtonDefaults.buttonColors(backgroundColor = White,),
                border = BorderStroke(1.dp, Primary2),
            ) {
                Text(stringResource(id = R.string.cancel), style = MaterialTheme.typography.button, fontSize = 18.sp, color = Primary2)
            }
        }
    }
}

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun CreatePlantPage2(viewModel: DeviceViewModel, navController: NavController, showToast: MutableState<Boolean>, showCancelDialog: MutableState<Boolean>) {

    val context = LocalContext.current
    val permissionState = rememberPermissionState(Manifest.permission.ACCESS_FINE_LOCATION)
    val scope = rememberCoroutineScope()

    val fusedLocationClient = remember { LocationServices.getFusedLocationProviderClient(context) }
    val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
    var isLocationEnabled by remember { mutableStateOf(locationManager.isProviderEnabled(
        LocationManager.GPS_PROVIDER)) }
    var checkedPermission by remember { mutableStateOf(true) }
    var getLocationLoading by remember { mutableStateOf(false) }

    val coordinate = remember { mutableStateOf("") }

    val deviceID = remember { mutableStateOf("") }
    val name = remember { mutableStateOf("") }
    val password = remember { mutableStateOf("") }
    val type = remember { mutableStateOf("") }

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
                        val data = it.split(" ")
                        if (data.size > 1) {
                            deviceID.value = data[0]
                            password.value = data[1]
                        }
                    })
                }

                Column(
                    modifier = Modifier.background(White)
                ) {
                    Spacer(modifier = Modifier.height(24.dp))

                    Text(
                        stringResource(id = R.string.create_plant),
                        fontFamily = Manrope,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 14.sp,
                        color = Black,
                    )

                    Spacer(modifier = Modifier.height(18.dp))

                    Input(
                        modifier = Modifier,
                        field = stringResource(id = R.string.device_id),
                        placeholder = stringResource(id = R.string.device_id),
                        disabled = true,
                        keyboardOptions = KeyboardOptions.Default.copy(
                            keyboardType = KeyboardType.Text,
                            imeAction = ImeAction.Next
                        ),
                        modifierParent = Modifier,
                        binding = deviceID,
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Input(
                        modifier = Modifier,
                        field = stringResource(id = R.string.name),
                        placeholder = stringResource(id = R.string.name),
                        disabled = false,
                        keyboardOptions = KeyboardOptions.Default.copy(
                            keyboardType = KeyboardType.Text,
                            imeAction = ImeAction.Next
                        ),
                        modifierParent = Modifier,
                        binding = name,
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    PasswordInput(
                        modifier = Modifier,
                        field = stringResource(id = R.string.password),
                        placeholder = stringResource(id = R.string.password),
                        disabled = true,
                        binding = password,
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Input(
                        modifier = Modifier,
                        field = stringResource(id = R.string.type),
                        placeholder = stringResource(id = R.string.type),
                        disabled = false,
                        keyboardOptions = KeyboardOptions.Default.copy(
                            keyboardType = KeyboardType.Text,
                            imeAction = ImeAction.Next
                        ),
                        modifierParent = Modifier,
                        binding = type,
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
                        colors = ButtonDefaults.buttonColors(
                            backgroundColor = when(BuildConfig.FLAVOR) {
                                "idroPro", "idroRes", "irriLife" -> {
                                    PrimarySoft
                                } else -> {
                                    White
                                }
                            }
                            ,),
                        border = BorderStroke(1.dp, when(BuildConfig.FLAVOR) {
                            "idroPro", "idroRes", "irriLife" -> {
                                PrimarySoft
                            } else -> {
                                Primary
                            }
                        }),
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
                        if (
                            deviceID.value.isNotEmpty() &&
                            name.value.isNotEmpty() &&
                            password.value.isNotEmpty() &&
                            type.value.isNotEmpty() &&
                            coordinate.value.isNotEmpty()
                        ) {
                            viewModel.postDataLoading.value = true
                            val postData = CreatePlantRequest(
                                code = deviceID.value,
                                name = name.value,
                                password = password.value,
                                type = type.value,
                                coordinate = coordinate.value,
                                company = Helper().getCompanyByFlavor(),
                            )

                            val result = viewModel.postCreatePlant(postData)
                            if (result.second == "Unauthorized") {
                                navController.navigate(Screen.Login.route) {
                                    popUpTo(navController.graph.id) {
                                        inclusive = true
                                    }
                                }
                            } else if (!result.second.isNullOrEmpty() && !result.second!!.contains("coroutine scope")) {
                                Toast.makeText(context, result.second, Toast.LENGTH_LONG)
                                    .show()
                            }

                            if (result.first) {
                                deviceID.value = ""
                                name.value = ""
                                password.value = ""
                                type.value = ""
                                coordinate.value = ""
                                showToast.value = true
                            }
                            viewModel.postDataLoading.value = false
                        }
                    }
                },
                colors = ButtonDefaults.buttonColors(
                    backgroundColor = if (
                        deviceID.value.isNotEmpty() &&
                        name.value.isNotEmpty() &&
                        password.value.isNotEmpty() &&
                        type.value.isNotEmpty() &&
                        coordinate.value.isNotEmpty()
                    ) Primary2 else GrayLight
                ),
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

            Spacer(modifier = Modifier.height(6.dp))

            Button(
                modifier = Modifier
                    .clip(RoundedCornerShape(4.dp))
                    .height(62.dp)
                    .fillMaxWidth()
                    .padding(top = 12.dp),
                contentPadding = PaddingValues(0.dp),
                onClick = {
                    showCancelDialog.value = true
                },
                colors = ButtonDefaults.buttonColors(backgroundColor = White,),
                border = BorderStroke(1.dp, Primary2),
            ) {
                Text(stringResource(id = R.string.cancel), style = MaterialTheme.typography.button, fontSize = 18.sp, color = Primary2)
            }
        }
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