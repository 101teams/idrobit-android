package com.idrolife.app.presentation.screen

import android.app.Activity
import android.widget.Toast
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
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
import com.idrolife.app.navigation.Screen
import com.idrolife.app.presentation.component.NavigationBanner3
import com.idrolife.app.presentation.viewmodel.DeviceViewModel
import com.idrolife.app.theme.Black
import com.idrolife.app.theme.BrokenWhite
import com.idrolife.app.theme.Gray
import com.idrolife.app.theme.GrayLight
import com.idrolife.app.theme.GrayVeryVeryLight
import com.idrolife.app.theme.Manrope
import com.idrolife.app.theme.Primary
import com.idrolife.app.theme.PrimaryLight2
import com.idrolife.app.theme.White
import com.idrolife.app.utils.Helper

@OptIn(ExperimentalPagerApi::class)
@Composable
fun IrrigationStatusProgramStatusScreen(
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

        viewModel.getIrrigationStatusProgramStatus(deviceCode)

        viewModel.isLoading.value = false

        viewModel.startPeriodicFetchingDevicesByID(deviceID)
    }

    LaunchedEffect(Unit) {
        viewModel.startPeriodicFetchingIrrigationStatusProgramStatus(deviceCode)
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
            stringResource(id = R.string.program_status),
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
                }
                items(viewModel.irrigationStatusProgramStatus.value) {programStatus ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        elevation = 4.dp,
                        backgroundColor = if (programStatus.status == "1") Color(0xFFC5FBC7) else GrayVeryVeryLight,
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                        ){
                            Box(
                                modifier = Modifier
                                    .padding(end = 8.dp)
                                    .size(10.dp)
                                    .clip(RoundedCornerShape(5.dp))
                                    .background(if (programStatus.status == "1") PrimaryLight2 else Gray),
                            )

                            Text(
                                programStatus.index.toString(),
                                fontFamily = Manrope,
                                fontWeight = FontWeight.Bold,
                                fontSize = 18.sp,
                                color = Black,
                                modifier = Modifier
                                    .padding(horizontal = 12.dp),
                            )

                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier
                                    .weight(1f),
                            ) {
                                Text(
                                    stringResource(id = R.string.station_used_in_program),
                                    fontFamily = Manrope,
                                    fontWeight = FontWeight.Normal,
                                    fontSize = 12.sp,
                                    color = GrayLight,
                                    modifier = Modifier.padding(horizontal = 12.dp),
                                    textAlign = TextAlign.Center,
                                )
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    programStatus.stationUsed ?: "-",
                                    fontFamily = Manrope,
                                    fontWeight = FontWeight.Normal,
                                    fontSize = 14.sp,
                                    color = Black,
                                    modifier = Modifier.padding(horizontal = 12.dp),
                                    textAlign = TextAlign.Center,
                                )
                            }

                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier
                                    .weight(1f),
                            ) {
                                Text(
                                    stringResource(id = R.string.remaining_time),
                                    fontFamily = Manrope,
                                    fontWeight = FontWeight.Normal,
                                    fontSize = 12.sp,
                                    color = GrayLight,
                                    modifier = Modifier.padding(horizontal = 12.dp),
                                    textAlign = TextAlign.Center,
                                )
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    programStatus.remainingTime ?: "-",
                                    fontFamily = Manrope,
                                    fontWeight = FontWeight.Normal,
                                    fontSize = 14.sp,
                                    color = Black,
                                    modifier = Modifier.padding(horizontal = 12.dp),
                                    textAlign = TextAlign.Center,
                                )
                            }

                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier
                                    .weight(1f),
                            ) {
                                Text(
                                    stringResource(id = R.string.action),
                                    fontFamily = Manrope,
                                    fontWeight = FontWeight.Normal,
                                    fontSize = 12.sp,
                                    color = GrayLight,
                                    modifier = Modifier.padding(horizontal = 12.dp),
                                    textAlign = TextAlign.Center,
                                )
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    when(programStatus.status) {
                                        "1" -> {
                                            stringResource(id = R.string.waiting_for_departure)
                                        }
                                        "2" -> {
                                            stringResource(id = R.string.mv_active)
                                        }
                                        "3" -> {
                                            stringResource(id = R.string.pause)
                                        }
                                        "4" -> {
                                            stringResource(id = R.string.ev_active)
                                        }
                                        else -> {
                                            stringResource(id = R.string.not_active)
                                        }
                                    },
                                    fontFamily = Manrope,
                                    fontWeight = FontWeight.Normal,
                                    fontSize = 14.sp,
                                    color = Black,
                                    modifier = Modifier.padding(horizontal = 12.dp),
                                    textAlign = TextAlign.Center,
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))
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