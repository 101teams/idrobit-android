package com.idrolife.app.presentation.screen

import android.app.Activity
import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
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
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.MaterialTheme
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.navigation.NavController
import com.idrolife.app.BuildConfig
import com.idrolife.app.R
import com.idrolife.app.navigation.Screen
import com.idrolife.app.presentation.component.Button2Image
import com.idrolife.app.presentation.component.NavigationBanner1
import com.idrolife.app.presentation.component.ToggleWithTitle
import com.idrolife.app.presentation.viewmodel.DeviceViewModel
import com.idrolife.app.theme.BrokenWhite
import com.idrolife.app.theme.Primary
import com.idrolife.app.theme.Primary2
import com.idrolife.app.theme.PrimaryLight2
import com.idrolife.app.theme.White
import com.idrolife.app.utils.Helper
import kotlinx.coroutines.launch

@Composable
fun DetailDeviceScreen(
    navController: NavController,
    deviceID: String,
    deviceName: String,
    deviceCode: String,
    deviceRole: String,
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
        viewModel.getIrrigationConfigGeneralSatConfig(deviceCode)
        viewModel.isLoading.value = false
    }

    fun showToast(message: String, duration: Int = Toast.LENGTH_SHORT) {
        Toast.makeText(context, message, duration).show()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BrokenWhite),
    ) {
        NavigationBanner1(
            navController,
            stringResource(id = R.string.idrosat),
            deviceName,
            R.drawable.img_header_detail1,
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
            LazyColumn(
                modifier = Modifier.padding(24.dp)
            ) {
                item {
                    if (BuildConfig.FLAVOR == "idroLife" || BuildConfig.FLAVOR == "idroPro") {
                        Button2Image(
                            Primary2,
                            R.drawable.ic_sensor_white,
                            stringResource(R.string.sensors),
                            R.drawable.ic_arrow_up_white,
                            onClick = {
                                navController.navigate(Screen.SensorDevice.withArgs(deviceID))
                            },
                            null,
                            null,
                        )

                        Spacer(modifier = Modifier.height(24.dp))
                    }

                    Button2Image(
                        Primary2,
                        R.drawable.ic_sprinkle_white,
                        stringResource(id = R.string.irrigation),
                        R.drawable.ic_arrow_up_white,
                        onClick = {
                            navController.navigate(Screen.IrrigationDevice.withArgs(deviceID, deviceCode, deviceRole))
                        },
                        null,
                        null,
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    if (BuildConfig.FLAVOR == "idroLife" || BuildConfig.FLAVOR == "idroPro") {
                        Button2Image(
                            Primary2,
                            R.drawable.ic_fertilizer_white,
                            stringResource(id = R.string.fertigation),
                            R.drawable.ic_arrow_up_white,
                            onClick = {
                                navController.navigate(Screen.FertigationDevice.withArgs(deviceID, deviceCode, deviceRole))
                            },
                            null,
                            null,
                        )

                        Spacer(modifier = Modifier.height(24.dp))
                    }

                    if (deviceRole != "user") {
                        Button2Image(
                            Primary2,
                            R.drawable.ic_map_white,
                            stringResource(id = R.string.map),
                            R.drawable.ic_arrow_up_white,
                            onClick = {
                                navController.navigate(Screen.Map.withArgs(deviceID, deviceCode))
                            },
                            null,
                            null,
                        )

                        Spacer(modifier = Modifier.height(24.dp))
                    }

                    Button2Image(
                        Primary2,
                        null,
                        stringResource(id = R.string.manual_ev_start),
                        R.drawable.ic_arrow_up_white,
                        onClick = {
                            navController.navigate(Screen.ManualEVStart.withArgs(deviceID, deviceCode))
                        },
                        null,
                        null,
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    Button2Image(
                        Primary2,
                        null,
                        stringResource(id = R.string.manual_program_start),
                        R.drawable.ic_arrow_up_white,
                        onClick = {
                            navController.navigate(Screen.ManualProgramStart.withArgs(deviceID, deviceCode))
                        },
                        null,
                        null,
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    Button(
                        modifier = Modifier
                            .clip(RoundedCornerShape(4.dp))
                            .height(62.dp)
                            .fillMaxWidth(),
                        contentPadding = PaddingValues(0.dp),
                        onClick = {

                        },
                        colors = ButtonDefaults.buttonColors(backgroundColor = Primary2),
                        border = BorderStroke(1.dp, Color.Transparent),
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 18.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Text(
                                stringResource(id = R.string.rain_mode),
                                style = MaterialTheme.typography.button,
                                fontSize = 16.sp,
                                color = White,
                                fontWeight = FontWeight.Medium
                            )

                            ToggleWithTitle(
                                field = null,
                                checkedTitle = "ON",
                                uncheckedTitle = "OFF",
                                modifier = Modifier,
                                selectedValue = viewModel.irrigationConfigGeneralSatConfig.value?.plantOperationStatus == "0",
                                onChecked = {
                                    viewModel.irrigationConfigGeneralSatConfig.value?.plantOperationStatus = if(it) "0" else "1"
                                    scope.launch {
                                        viewModel.postDataLoading.value = true
                                        viewModel.postRainMode(
                                            deviceCode,
                                            viewModel.irrigationConfigGeneralSatConfig.value!!,
                                        )
                                        viewModel.postDataLoading.value = false
                                    }
                                }
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    Button2Image(
                        Primary2,
                        null,
                        stringResource(id = R.string.config_network),
                        R.drawable.ic_arrow_up_white,
                        onClick = {
                            navController.navigate(Screen.ChooseDevice.route)
                        },
                        null,
                        null,
                    )
                }
            }
        }
    }
}
