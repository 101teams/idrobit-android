package com.idrolife.app.presentation.screen

import android.app.Activity
import android.widget.Toast
import androidx.compose.foundation.background
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
import androidx.compose.material.LocalTextStyle
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.navigation.NavController
import com.idrolife.app.R
import com.idrolife.app.navigation.Screen
import com.idrolife.app.presentation.component.DropDown
import com.idrolife.app.presentation.component.Input
import com.idrolife.app.presentation.component.NavigationBanner3
import com.idrolife.app.presentation.viewmodel.DeviceViewModel
import com.idrolife.app.theme.Black
import com.idrolife.app.theme.BrokenWhite
import com.idrolife.app.theme.GrayLight
import com.idrolife.app.theme.Primary
import com.idrolife.app.theme.Primary2
import com.idrolife.app.theme.Manrope
import com.idrolife.app.theme.White
import com.idrolife.app.utils.Helper
import kotlinx.coroutines.launch

@Composable
fun ManualEVStartScreen(
    navController: NavController,
    deviceID: String,
    deviceCode: String
) {
    val context = LocalContext.current

    val window = (context as Activity).window
    val view = LocalView.current

    val viewModel = hiltViewModel<DeviceViewModel>()
    val scope = rememberCoroutineScope()

    var availableSerial by remember { mutableStateOf(mutableListOf(Pair("",""))) }

    val selectedStation = remember { mutableStateOf("") }

    val hour = remember { mutableStateOf("00") }
    val minute = remember { mutableStateOf("00") }
    val second = remember { mutableStateOf("00") }
    val aValueIsNotEmpty by remember {
        derivedStateOf {
            hour.value.isNotBlank() || minute.value.isNotBlank() || second.value.isNotBlank()
        }
    }

    val stopStationN = remember { mutableStateOf("") }
    val skipStationN = remember { mutableStateOf("") }
    val stationName = remember { mutableStateOf("") }


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

        viewModel.getIrrigationConfigEVConfigList(deviceCode)
        viewModel.getEVStationName(deviceCode)

        val tempAvailableSerial: MutableList<Pair<String, String>> = mutableListOf()

        for (i in viewModel.irrigationConfigEvConfigList.value) {
            if (i.evSerial != "FFFFFF") {
                val initialIndex = (i.index * 6)

                val data = Pair((i.index+1).toString(), "${2000 + (initialIndex)}")
                tempAvailableSerial.add(data)
            }
        }

        availableSerial = tempAvailableSerial

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
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 24.dp)
            ) {
                item {
                    Spacer(modifier = Modifier.height(24.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        DropDown(
                            field = stringResource(id = R.string.station_number),
                            availableSerial,
                            selectedValue = "",
                            modifier = Modifier.weight(1f),
                            onSelectItem = { key, it ->
                                selectedStation.value = key
                                val sName = viewModel.evStationName.value.getOrNull(key.toInt() - 1)

                                stationName.value = sName?.second ?: "-"

                                stopStationN.value = key
                            }
                        )

                        Spacer(modifier = Modifier.width(8.dp))

                        Text(
                            stationName.value,
                            fontFamily = Manrope,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 18.sp,
                            color = Black,
                            modifier = Modifier
                                .weight(1f)
                                .padding(top = 22.dp),
                            textAlign = TextAlign.Center,
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        stringResource(id = R.string.irrigation_time),
                        fontFamily = Manrope,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 14.sp,
                        color = Black,
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Row(
                            modifier = Modifier
                                .weight(1f),
                            verticalAlignment = Alignment.CenterVertically,
                        ){
                            Input(
                                modifier = Modifier,
                                field = null,
                                placeholder = stringResource(id = R.string.hour),
                                disabled = false,
                                keyboardOptions = KeyboardOptions.Default.copy(
                                    keyboardType = KeyboardType.Number,
                                    imeAction = ImeAction.Next
                                ),
                                modifierParent = Modifier.weight(1f),
                                binding = hour,
                                inputTextStyle = LocalTextStyle.current.copy(textAlign = TextAlign.Center),
                            )
                        }

                        Text(
                            ":",
                            fontFamily = Manrope,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 18.sp,
                            color = Black,
                            modifier = Modifier.padding(start = 8.dp)
                        )

                        Spacer(modifier = Modifier.width(8.dp))

                        Row(
                            modifier = Modifier
                                .weight(1f),
                            verticalAlignment = Alignment.CenterVertically,
                        ){
                            Input(
                                modifier = Modifier,
                                field = null,
                                placeholder = "min",
                                disabled = false,
                                keyboardOptions = KeyboardOptions.Default.copy(
                                    keyboardType = KeyboardType.Number,
                                    imeAction = ImeAction.Next
                                ),
                                modifierParent = Modifier.weight(1f),
                                binding = minute,
                                inputTextStyle = LocalTextStyle.current.copy(textAlign = TextAlign.Center),
                            )
                        }

                        Text(
                            ":",
                            fontFamily = Manrope,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 18.sp,
                            color = Black,
                            modifier = Modifier.padding(start = 8.dp)
                        )

                        Spacer(modifier = Modifier.width(8.dp))

                        Row(
                            modifier = Modifier
                                .weight(1f),
                            verticalAlignment = Alignment.CenterVertically,
                        ){
                            Input(
                                modifier = Modifier,
                                field = null,
                                placeholder = "sec",
                                disabled = false,
                                keyboardOptions = KeyboardOptions.Default.copy(
                                    keyboardType = KeyboardType.Number,
                                    imeAction = ImeAction.Done
                                ),
                                modifierParent = Modifier.weight(1f),
                                binding = second,
                                inputTextStyle = LocalTextStyle.current.copy(textAlign = TextAlign.Center),
                            )
                        }

                        Spacer(modifier = Modifier.width(8.dp))

                        Button(
                            modifier = Modifier
                                .clip(RoundedCornerShape(4.dp))
                                .height(58.dp)
                                .width(64.dp),
                            contentPadding = PaddingValues(0.dp),
                            onClick = {
                                  scope.launch {
                                      if (!aValueIsNotEmpty) {
                                          return@launch
                                      }

                                      if (hour.value.isBlank()) { // set default value
                                        hour.value = "00"
                                      }

                                      if (minute.value.isBlank()) { // set default value
                                        minute.value = "00"
                                      }

                                      if (second.value.isBlank()) { // set default value
                                      second.value = "00"
                                      }

                                      if (
                                              selectedStation.value.isNotEmpty() &&
                                              hour.value.all { it in '0'..'9' } &&
                                              minute.value.all { it in '0'..'9' } &&
                                              second.value.all { it in '0'..'9' }
                                          )
                                      {
                                          viewModel.postDataLoading.value = true

                                          val sendData = mutableMapOf<String, String>()
                                          sendData["S996"] = "${selectedStation.value},${hour.value},${minute.value},${second.value}"
                                          viewModel.postManualIdrosatStatIrrigationName(deviceCode,sendData)

                                          viewModel.postDataLoading.value = false
                                      }
                                  }
                            },
                            colors =
                            if (selectedStation.value.isNotEmpty() && aValueIsNotEmpty &&
                                hour.value.all { it in '0'..'9' } &&
                                minute.value.all { it in '0'..'9' } &&
                                second.value.all { it in '0'..'9' })
                                ButtonDefaults.buttonColors(backgroundColor = Primary2)
                                else
                                ButtonDefaults.buttonColors(backgroundColor = GrayLight)
                            ,
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
                                Text(
                                    stringResource(id = R.string.start),
                                    style = MaterialTheme.typography.button,
                                    fontSize = 14.sp,
                                    color = White,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        stringResource(id = R.string.stop_station_n),
                        fontFamily = Manrope,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 14.sp,
                        color = Black,
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Row(
                            modifier = Modifier
                                .weight(1f),
                            verticalAlignment = Alignment.CenterVertically,
                        ){
                            Input(
                                modifier = Modifier,
                                field = null,
                                placeholder = "0",
                                disabled = false,
                                keyboardOptions = KeyboardOptions.Default.copy(
                                    keyboardType = KeyboardType.Number,
                                    imeAction = ImeAction.Done
                                ),
                                modifierParent = Modifier.weight(1f),
                                binding = stopStationN,
                            )
                        }

                        Spacer(modifier = Modifier.width(8.dp))

                        Button(
                            modifier = Modifier
                                .clip(RoundedCornerShape(4.dp))
                                .height(58.dp)
                                .width(64.dp),
                            contentPadding = PaddingValues(0.dp),
                            onClick = {
                                scope.launch {
                                    if (selectedStation.value.isNotEmpty() &&
                                        stopStationN.value.isNotEmpty() &&
                                        stopStationN.value.all { it in '0'..'9' }) {
                                        viewModel.setMarkerLoading.value = true

                                        val sendData = mutableMapOf<String, String>()
                                        sendData["S998"] = stopStationN.value
                                        viewModel.postManualIdrosatStatIrrigationName(deviceCode,sendData)

                                        viewModel.setMarkerLoading.value = false
                                    }
                                }
                            },
                            colors =
                            if (selectedStation.value.isNotEmpty() &&
                                stopStationN.value.isNotEmpty() &&
                                stopStationN.value.all { it in '0'..'9' })
                                ButtonDefaults.buttonColors(backgroundColor = Primary2)
                            else
                                ButtonDefaults.buttonColors(backgroundColor = GrayLight),
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
                                Text(
                                    stringResource(id = R.string.stop),
                                    style = MaterialTheme.typography.button,
                                    fontSize = 14.sp,
                                    color = White,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        stringResource(id = R.string.skip_station_n),
                        fontFamily = Manrope,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 14.sp,
                        color = Black,
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Row(
                            modifier = Modifier
                                .weight(1f),
                            verticalAlignment = Alignment.CenterVertically,
                        ){
                            Input(
                                modifier = Modifier,
                                field = null,
                                placeholder = "0",
                                disabled = false,
                                keyboardOptions = KeyboardOptions.Default.copy(
                                    keyboardType = KeyboardType.Number,
                                    imeAction = ImeAction.Done
                                ),
                                modifierParent = Modifier.weight(1f),
                                binding = skipStationN,
                            )
                        }

                        Spacer(modifier = Modifier.width(8.dp))

                        Button(
                            modifier = Modifier
                                .clip(RoundedCornerShape(4.dp))
                                .height(58.dp)
                                .width(64.dp),
                            contentPadding = PaddingValues(0.dp),
                            onClick = {
                                scope.launch {
                                    if (selectedStation.value.isNotEmpty() &&
                                        skipStationN.value.isNotEmpty() &&
                                        skipStationN.value.all { it in '0'..'9' }) {
                                        viewModel.postData2Loading.value = true

                                        val sendData = mutableMapOf<String, String>()
                                        sendData["S998"] = skipStationN.value
                                        viewModel.postManualIdrosatStatIrrigationName(deviceCode,sendData)

                                        viewModel.postData2Loading.value = false
                                    }
                                }
                            },
                            colors =
                            if (selectedStation.value.isNotEmpty() &&
                                skipStationN.value.isNotEmpty() &&
                                skipStationN.value.all { it in '0'..'9' })
                                ButtonDefaults.buttonColors(backgroundColor = Primary2)
                            else
                                ButtonDefaults.buttonColors(backgroundColor = GrayLight),
                        ) {
                            if (viewModel.postData2Loading.value) {
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
                                    stringResource(id = R.string.skip),
                                    style = MaterialTheme.typography.button,
                                    fontSize = 14.sp,
                                    color = White,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))
                }
            }
        }
    }
}