package com.idrolife.app.presentation.screen

import android.app.Activity
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.navigation.NavController
import com.google.accompanist.pager.ExperimentalPagerApi
import com.idrolife.app.R
import com.idrolife.app.data.api.irrigation.IrrigationStatusIdrosatStatus
import com.idrolife.app.navigation.Screen
import com.idrolife.app.presentation.component.NavigationBanner3
import com.idrolife.app.presentation.viewmodel.DeviceViewModel
import com.idrolife.app.theme.Black
import com.idrolife.app.theme.BrokenWhite
import com.idrolife.app.theme.GrayLight
import com.idrolife.app.theme.GrayVeryVeryLight
import com.idrolife.app.theme.Primary
import com.idrolife.app.theme.Manrope
import com.idrolife.app.theme.White
import com.idrolife.app.utils.Helper

@OptIn(ExperimentalPagerApi::class)
@Composable
fun IrrigationStatusIdrosatStatusScreen(
    navController: NavController,
    deviceID: String,
    deviceCode: String
) {
    val context = LocalContext.current

    val window = (context as Activity).window
    val view = LocalView.current

    val viewModel = hiltViewModel<DeviceViewModel>()
    val scope = rememberCoroutineScope()

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

        viewModel.getIrrigationStatusIdrosatStatus(deviceCode, context)

        viewModel.isLoading.value = false

        viewModel.startPeriodicFetchingDevicesByID(deviceID)
    }

    LaunchedEffect(Unit) {
        viewModel.startPeriodicFetchingIrrigationStatusIdrosatStatus(deviceCode, context)
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
            stringResource(id = R.string.idrosat_status),
            R.drawable.img_header_detail3,
            viewModel.selectedDevice.value,
            viewModel.isLoading.value,
        )

        if (!viewModel.isLoading.value) {
            LazyColumn(
                modifier = Modifier
                    .padding(horizontal = 24.dp)
                    .weight(1f),
            ) {
                item {
                    Spacer(modifier = Modifier.height(24.dp))
                    Card(
                        modifier = Modifier
                            .fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        elevation = 4.dp,
                        backgroundColor = GrayVeryVeryLight,
                    ) {
                        Column {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 12.dp, horizontal = 12.dp),
                                verticalAlignment = Alignment.CenterVertically,
                            ) {
                                Column(
                                    modifier = Modifier.weight(1f),
                                ) {
                                    Text(
                                        stringResource(id = R.string.idrosat),
                                        fontFamily = Manrope,
                                        fontWeight = FontWeight.Normal,
                                        fontSize = 14.sp,
                                        color = GrayLight,
                                        modifier = Modifier.padding(horizontal = 12.dp),
                                        textAlign = TextAlign.Start,
                                    )
                                    Text(
                                        viewModel.selectedDevice.value?.name ?: "-",
                                        fontFamily = Manrope,
                                        fontWeight = FontWeight.Normal,
                                        fontSize = 14.sp,
                                        color = Black,
                                        modifier = Modifier.padding(top = 2.dp, start = 12.dp, end = 12.dp),
                                        textAlign = TextAlign.Start,
                                    )
                                }
                                Column(
                                    modifier = Modifier.weight(1f),
                                ) {
                                    Text(
                                        stringResource(id = R.string.id_serial),
                                        fontFamily = Manrope,
                                        fontWeight = FontWeight.Normal,
                                        fontSize = 14.sp,
                                        color = GrayLight,
                                        modifier = Modifier.padding(horizontal = 12.dp),
                                        textAlign = TextAlign.Start,
                                    )
                                    Text(
                                        viewModel.selectedDevice.value?.code ?: "-",
                                        fontFamily = Manrope,
                                        fontWeight = FontWeight.Normal,
                                        fontSize = 14.sp,
                                        color = Black,
                                        modifier = Modifier.padding(top = 2.dp, start = 12.dp, end = 12.dp),
                                        textAlign = TextAlign.Start,
                                    )
                                }
                            }
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 12.dp, horizontal = 12.dp),
                                verticalAlignment = Alignment.CenterVertically,
                            ) {
                                Column(
                                    modifier = Modifier.weight(1f),
                                ) {
                                    Text(
                                        stringResource(id = R.string.firmware_version),
                                        fontFamily = Manrope,
                                        fontWeight = FontWeight.Normal,
                                        fontSize = 14.sp,
                                        color = GrayLight,
                                        modifier = Modifier.padding(horizontal = 12.dp),
                                        textAlign = TextAlign.Start,
                                    )
                                    Text(
                                        viewModel.selectedDevice.value?.fwIdrosat ?: "-",
                                        fontFamily = Manrope,
                                        fontWeight = FontWeight.Normal,
                                        fontSize = 14.sp,
                                        color = Black,
                                        modifier = Modifier.padding(top = 2.dp, start = 12.dp, end = 12.dp),
                                        textAlign = TextAlign.Start,
                                    )
                                }
                                Column(
                                    modifier = Modifier.weight(1f),
                                ) {
                                    Text(
                                        stringResource(id = R.string.esp32_version),
                                        fontFamily = Manrope,
                                        fontWeight = FontWeight.Normal,
                                        fontSize = 14.sp,
                                        color = GrayLight,
                                        modifier = Modifier.padding(horizontal = 12.dp),
                                        textAlign = TextAlign.Start,
                                    )
                                    Text(
                                        viewModel.selectedDevice.value?.fwEsp32 ?: "-",
                                        fontFamily = Manrope,
                                        fontWeight = FontWeight.Normal,
                                        fontSize = 14.sp,
                                        color = Black,
                                        modifier = Modifier.padding(top = 2.dp, start = 12.dp, end = 12.dp),
                                        textAlign = TextAlign.Start,
                                    )
                                }
                            }
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 12.dp, horizontal = 12.dp),
                                verticalAlignment = Alignment.CenterVertically,
                            ) {
                                Column(
                                    modifier = Modifier.weight(1f),
                                ) {
                                    Text(
                                        stringResource(id = R.string.hardware_version),
                                        fontFamily = Manrope,
                                        fontWeight = FontWeight.Normal,
                                        fontSize = 14.sp,
                                        color = GrayLight,
                                        modifier = Modifier.padding(horizontal = 12.dp),
                                        textAlign = TextAlign.Start,
                                    )
                                    Text(
                                        viewModel.selectedDevice.value?.hwVersion ?: "-",
                                        fontFamily = Manrope,
                                        fontWeight = FontWeight.Normal,
                                        fontSize = 14.sp,
                                        color = Black,
                                        modifier = Modifier.padding(top = 2.dp, start = 12.dp, end = 12.dp),
                                        textAlign = TextAlign.Start,
                                    )
                                }
                                Column(
                                    modifier = Modifier.weight(1f),
                                ) {
                                    Text(
                                        stringResource(id = R.string.mac_address),
                                        fontFamily = Manrope,
                                        fontWeight = FontWeight.Normal,
                                        fontSize = 14.sp,
                                        color = GrayLight,
                                        modifier = Modifier.padding(horizontal = 12.dp),
                                        textAlign = TextAlign.Start,
                                    )
                                    Text(
                                        viewModel.selectedDevice.value?.macAddressDevice ?: "-",
                                        fontFamily = Manrope,
                                        fontWeight = FontWeight.Normal,
                                        fontSize = 14.sp,
                                        color = Black,
                                        modifier = Modifier.padding(top = 2.dp, start = 12.dp, end = 12.dp),
                                        textAlign = TextAlign.Start,
                                    )
                                }
                            }
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 12.dp, horizontal = 12.dp),
                                verticalAlignment = Alignment.CenterVertically,
                            ) {
                                Column(
                                    modifier = Modifier.weight(1f),
                                ) {
                                    Text(
                                        stringResource(id = R.string.counter),
                                        fontFamily = Manrope,
                                        fontWeight = FontWeight.Normal,
                                        fontSize = 14.sp,
                                        color = GrayLight,
                                        modifier = Modifier.padding(horizontal = 12.dp),
                                        textAlign = TextAlign.Start,
                                    )
                                    Text(
                                        if(viewModel.selectedDevice.value?.consumption != null) viewModel.selectedDevice.value?.consumption.toString() else "-",
                                        fontFamily = Manrope,
                                        fontWeight = FontWeight.Normal,
                                        fontSize = 14.sp,
                                        color = Black,
                                        modifier = Modifier.padding(top = 2.dp, start = 12.dp, end = 12.dp),
                                        textAlign = TextAlign.Start,
                                    )
                                }
                                Column(
                                    modifier = Modifier.weight(1f),
                                ) {
                                    Text(
                                        stringResource(id = R.string.stations),
                                        fontFamily = Manrope,
                                        fontWeight = FontWeight.Normal,
                                        fontSize = 14.sp,
                                        color = GrayLight,
                                        modifier = Modifier.padding(horizontal = 12.dp),
                                        textAlign = TextAlign.Start,
                                    )
                                    Text(
                                        viewModel.selectedDevice.value?.activeStation ?: "-",
                                        fontFamily = Manrope,
                                        fontWeight = FontWeight.Normal,
                                        fontSize = 14.sp,
                                        color = Black,
                                        modifier = Modifier.padding(top = 2.dp, start = 12.dp, end = 12.dp),
                                        textAlign = TextAlign.Start,
                                    )
                                }
                            }
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 12.dp, horizontal = 12.dp),
                                verticalAlignment = Alignment.CenterVertically,
                            ) {
                                Column(
                                    modifier = Modifier.weight(1f),
                                ) {
                                    Text(
                                        stringResource(id = R.string.programs),
                                        fontFamily = Manrope,
                                        fontWeight = FontWeight.Normal,
                                        fontSize = 14.sp,
                                        color = GrayLight,
                                        modifier = Modifier.padding(horizontal = 12.dp),
                                        textAlign = TextAlign.Start,
                                    )
                                    Text(
                                        viewModel.selectedDevice.value?.activeProgram ?: "-",
                                        fontFamily = Manrope,
                                        fontWeight = FontWeight.Normal,
                                        fontSize = 14.sp,
                                        color = Black,
                                        modifier = Modifier.padding(top = 2.dp, start = 12.dp, end = 12.dp),
                                        textAlign = TextAlign.Start,
                                    )
                                }
                                Column(
                                    modifier = Modifier.weight(1f),
                                ) {
                                    Text(
                                        stringResource(id = R.string.pressure),
                                        fontFamily = Manrope,
                                        fontWeight = FontWeight.Normal,
                                        fontSize = 14.sp,
                                        color = GrayLight,
                                        modifier = Modifier.padding(horizontal = 12.dp),
                                        textAlign = TextAlign.Start,
                                    )
                                    Text(
                                        viewModel.selectedDevice.value?.systemPressure ?: "-",
                                        fontFamily = Manrope,
                                        fontWeight = FontWeight.Normal,
                                        fontSize = 14.sp,
                                        color = Black,
                                        modifier = Modifier.padding(top = 2.dp, start = 12.dp, end = 12.dp),
                                        textAlign = TextAlign.Start,
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    Text(
                        "${stringResource(id = R.string.instant_consumption)} M³/H o L/H",
                        fontFamily = Manrope,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = Black,
                        modifier = Modifier,
                        textAlign = TextAlign.Center,
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    IrrigationStatusIdrosatStatusPump(
                        data = viewModel.irrigationStatusIdrosatStatusInstantConsumption.value,
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    Text(
                        "${stringResource(id = R.string.total_consumption)} M³",
                        fontFamily = Manrope,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = Black,
                        modifier = Modifier,
                        textAlign = TextAlign.Center,
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    IrrigationStatusIdrosatStatusPump(
                        data = viewModel.irrigationStatusIdrosatStatusTotalConsumption.value,
                    )
                }


                item {
                    Spacer(modifier = Modifier.height(24.dp))
                }
            }
        }
        else {
            Box(
                modifier = Modifier.fillMaxSize(),
            ){
                CircularProgressIndicator(
                    color = Primary,
                    strokeCap = StrokeCap.Round,
                    strokeWidth = 2.dp,
                    modifier = Modifier
                        .width(18.dp)
                        .height(18.dp)
                        .align(Alignment.Center),
                )
            }
        }
    }
}

@Composable
fun IrrigationStatusIdrosatStatusPump(data: List<IrrigationStatusIdrosatStatus>) {
    for ((index, value) in data.withIndex()) {
        if (index % 2 == 0) {
            Row(
                modifier = Modifier.fillMaxWidth(),
            ) {
                for(i in 0..< 2) {
                    if (index + i < data.size) {
                        IrrigationStatusIdrosatStatusPumpItem(data[index + i], Modifier.weight(1f))
                        if (i == 0) {
                            Spacer(modifier = Modifier.width(8.dp))
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.height(12.dp))
        }
    }
    Row(
        modifier = Modifier.fillMaxWidth(),
    ) {

    }
}

@Composable
fun IrrigationStatusIdrosatStatusPumpItem(data: IrrigationStatusIdrosatStatus, modifier: Modifier){
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            data.name ?: "-",
            fontFamily = Manrope,
            fontWeight = FontWeight.SemiBold,
            fontSize = 14.sp,
            color = Black,
            modifier = Modifier.padding(horizontal = 12.dp),
            textAlign = TextAlign.Center,
        )
        
        Spacer(modifier = Modifier.height(4.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    GrayVeryVeryLight,
                    shape = RoundedCornerShape(8.dp)
                )
                .padding(vertical = 8.dp),
        ) {
            Text(
                data.value ?: "-",
                fontFamily = Manrope,
                fontWeight = FontWeight.Normal,
                fontSize = 20.sp,
                color = Black,
                modifier = Modifier
                    .padding(horizontal = 12.dp)
                    .align(Alignment.Center),
                textAlign = TextAlign.Center,
            )
        }
    }
}