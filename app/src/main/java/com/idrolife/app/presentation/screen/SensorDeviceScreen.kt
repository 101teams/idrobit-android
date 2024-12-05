package com.idrolife.app.presentation.screen

import android.app.Activity
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.Divider
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
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
import com.idrolife.app.R
import com.idrolife.app.navigation.Screen
import com.idrolife.app.presentation.component.Button2Image
import com.idrolife.app.presentation.component.NavigationBanner2
import com.idrolife.app.presentation.component.SensorDataItem
import com.idrolife.app.presentation.viewmodel.DeviceViewModel
import com.idrolife.app.theme.Manrope
import com.idrolife.app.theme.Primary2
import com.idrolife.app.theme.PrimaryLight2
import com.idrolife.app.theme.White
import com.idrolife.app.utils.Helper

@Composable
fun SensorDeviceScreen(
    navController: NavController,
    deviceID: String,
) {
    val context = LocalContext.current

    val window = (context as Activity).window
    val view = LocalView.current

    val viewModel = hiltViewModel<DeviceViewModel>()

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

        val meteostat = viewModel.getSensorMeteostat(viewModel.selectedDevice.value?.code ?: "")
        if (meteostat.second.isNotBlank() && !meteostat.second.contains("coroutine scope")) {
            Toast.makeText(context, meteostat.second, Toast.LENGTH_LONG)
                .show()
        }

        val satstat = viewModel.getSensorSatstat(viewModel.selectedDevice.value?.code ?: "")
        if (satstat.second.isNotBlank() && !satstat.second.contains("coroutine scope")) {
            Toast.makeText(context, satstat.second, Toast.LENGTH_LONG)
                .show()
        }

        viewModel.isLoading.value = false

        viewModel.startPeriodicFetchingDevicesByID(deviceID)
        viewModel.startPeriodicFetchingMeteostatByCode(deviceID)
        viewModel.startPeriodicFetchingSatstatByCode(deviceID)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(White),
    ) {
        NavigationBanner2(
            navController,
            stringResource(id = R.string.sensors),
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

                Text(stringResource(id = R.string.plant), fontSize = 20.sp, fontFamily = Manrope, fontWeight = FontWeight.Normal, color = Color.Black)
                Divider(
                    modifier = Modifier
                        .padding(top = 12.dp)
                        .background(Primary2)
                )

                Spacer(modifier = Modifier.padding(top=14.dp))

                Button2Image(
                    backgroundColor = White,
                    leftImage = R.drawable.ic_soil_moisture_green,
                    title = stringResource(id = R.string.soil_moisture_humidity),
                    rightImage = R.drawable.ic_arrow_up_green,
                    onClick = {
                        navController.navigate(Screen.SensorSoilMoisture.withArgs(deviceID))
                    },
                    outlineColor = Primary2,
                    fontColor = Primary2,
                )

                Row(
                    modifier = Modifier
                        .padding(top = 14.dp)
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                ) {
                    SensorDataItem(
                        value = viewModel.deviceSensorSatstat.value?.s131 ?: "-",
                        unit = "pH",
                        name = "pH",
                        modifier = Modifier.weight(1f)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    SensorDataItem(
                        value = viewModel.deviceSensorSatstat.value?.s130 ?: "-",
                        unit = "µS",
                        name = stringResource(id = R.string.conductivity),
                        modifier = Modifier.weight(1f)
                    )
                }

                Spacer(modifier = Modifier.padding(top=14.dp))

                Text(stringResource(id = R.string.environment), fontSize = 20.sp, fontFamily = Manrope, fontWeight = FontWeight.Normal, color = Color.Black)
                Divider(
                    modifier = Modifier
                        .padding(top = 12.dp)
                        .background(Primary2)
                )

                Spacer(modifier = Modifier.padding(top=14.dp))

                Row(
                    modifier = Modifier
                        .padding(top = 14.dp)
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                ) {
                    SensorDataItem(
                        value = viewModel.deviceSensorMeteostat.value?.m7 ?: "-",
                        unit = "W/m²",
                        name = stringResource(id = R.string.solar_intensity),
                        modifier = Modifier.weight(1f)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    SensorDataItem(
                        value = viewModel.deviceSensorMeteostat.value?.m4 ?: "-",
                        unit = "mm/m²",
                        name = stringResource(id = R.string.rain_sensor),
                        modifier = Modifier.weight(1f)
                    )
                }

                Row(
                    modifier = Modifier
                        .padding(top = 14.dp)
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                ) {
                    SensorDataItem(
                        value = viewModel.deviceSensorMeteostat.value?.m6 ?: "-",
                        unit = "C",
                        name = stringResource(id = R.string.temperature),
                        modifier = Modifier.weight(1f)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    SensorDataItem(
                        value = viewModel.deviceSensorMeteostat.value?.m5 ?: "-",
                        unit = "%",
                        name = stringResource(id = R.string.air_humidity),
                        modifier = Modifier.weight(1f)
                    )
                }
                
                Spacer(modifier = Modifier.height(12.dp))

                SensorDataItem(
                    value = "${viewModel.deviceSensorMeteostat.value?.m31 ?: "-"} - ${viewModel.deviceSensorMeteostat.value?.m8 ?: "-"}",
                    unit = "km/h",
                    name = stringResource(id = R.string.wind_intensity_direction),
                    modifier = Modifier.weight(1f)
                )

                Spacer(modifier = Modifier.padding(top=14.dp))

                Text(stringResource(id = R.string.device), fontSize = 20.sp, fontFamily = Manrope, fontWeight = FontWeight.Normal, color = Color.Black)
                Divider(
                    modifier = Modifier
                        .padding(top = 12.dp)
                        .background(Primary2)
                )

                Spacer(modifier = Modifier.padding(top=14.dp))

                SensorDataItem(
                    value = viewModel.deviceSensorSatstat.value?.s4 ?: "-",
                    unit = "m³",
                    name = stringResource(id = R.string.flow_counter),
                    modifier = Modifier.weight(1f)
                )
                Spacer(modifier = Modifier.height(12.dp))
                SensorDataItem(
                    value = viewModel.deviceSensorMeteostat.value?.m33 ?: "-",
                    unit = "Bar",
                    name = stringResource(id = R.string.system_pressure),
                    modifier = Modifier.weight(1f)
                )

                Spacer(modifier = Modifier.height(12.dp))
                SensorDataItem(
                    value = viewModel.deviceSensorMeteostat.value?.m12 ?: "-",
                    unit = "",
                    name = stringResource(id = R.string.network_signal),
                    modifier = Modifier.weight(1f)
                )

                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}
