package com.idrolife.app.presentation.screen

import android.app.Activity
import android.widget.Toast
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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
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
import com.idrolife.app.presentation.component.InputWithInitial
import com.idrolife.app.presentation.component.NavigationBanner3
import com.idrolife.app.presentation.component.PasswordInputWithInitial
import com.idrolife.app.presentation.component.RangedSeekbar
import com.idrolife.app.presentation.component.ToggleWithTitle
import com.idrolife.app.presentation.viewmodel.DeviceViewModel
import com.idrolife.app.theme.Black
import com.idrolife.app.theme.BrokenWhite
import com.idrolife.app.theme.GrayVeryVeryLight
import com.idrolife.app.theme.Manrope
import com.idrolife.app.theme.Primary
import com.idrolife.app.theme.Primary2
import com.idrolife.app.theme.PrimaryVeryLight
import com.idrolife.app.theme.White
import com.idrolife.app.utils.Helper
import kotlinx.coroutines.launch

@Composable
fun IrrigationConfigGeneralSettingScreen(
    navController: NavController,
    deviceID: String
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

        viewModel.getIrrigationConfigNominalFlow(viewModel.selectedDevice.value?.code ?: "")
        viewModel.getIrrigationConfigGeneralSatConfig(viewModel.selectedDevice.value?.code ?: "")
        viewModel.getIrrigationConfigGeneralPumpConfig(viewModel.selectedDevice.value?.code ?: "")
        viewModel.getIrrigationConfigGeneralMVConfig(viewModel.selectedDevice.value?.code ?: "")
        viewModel.isLoading.value = false

        viewModel.startPeriodicFetchingDevicesByID(deviceID)
    }

    fun showToast(message: String, duration: Int = Toast.LENGTH_SHORT) {
        Toast.makeText(context, message, duration).show()
    }

    //bindings
    val numberOfStation = remember { mutableStateOf("") }

    LaunchedEffect(viewModel.irrigationConfigNominalFlow.value) {
        val filteredData = viewModel.irrigationConfigNominalFlow.value.filter { it.evSerial != "FFFFFF" }
        val groupedData = filteredData.groupBy { it.station }
        numberOfStation.value = groupedData.size.toString()
    }
    

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(White),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        NavigationBanner3(
            navController,
            stringResource(id = R.string.general_setting),
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
                    .padding(horizontal = 24.dp)
                    .fillMaxWidth()
                    .weight(1f)
            ) {

                item{
                    Spacer(modifier = Modifier.height(24.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Text(stringResource(id = R.string.number_of_stations),
                            fontFamily = Manrope,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 14.sp,
                            color = Black
                        )

                        Spacer(modifier = Modifier.width(12.dp))

                        Box(
                            modifier = Modifier
                                .background(
                                    GrayVeryVeryLight,
                                    shape = RoundedCornerShape(14.dp)
                                )
                                .padding(vertical = 8.dp, horizontal = 24.dp)
                        ) {
                            Text(numberOfStation.value,
                                fontFamily = Manrope,
                                fontWeight = FontWeight.Normal,
                                fontSize = 14.sp,
                                color = Black
                            )
                        }

                        Spacer(modifier = Modifier.weight(1f))

                        Button(
                            modifier = Modifier
                                .clip(RoundedCornerShape(4.dp))
                                .height(36.dp),
                            contentPadding = PaddingValues(horizontal = 8.dp),
                            onClick = {
                                scope.launch {
                                    viewModel.setMarkerLoading.value = true
                                    viewModel.postIrrigationConfigGeneralConfigCleanMemory(
                                        viewModel.selectedDevice.value?.code ?: ""
                                    )
                                    viewModel.setMarkerLoading.value = false
                                }
                            },
                            colors = ButtonDefaults.buttonColors(backgroundColor = PrimaryVeryLight,),
                        ) {
                            if (viewModel.setMarkerLoading.value) {
                                CircularProgressIndicator(
                                    color = Primary,
                                    strokeCap = StrokeCap.Round,
                                    strokeWidth = 2.dp,
                                    modifier = Modifier
                                        .width(12.dp)
                                        .height(12.dp)
                                )
                            } else {
                                Text(stringResource(id = R.string.clean_memory), style = MaterialTheme.typography.button, fontSize = 12.sp, color = Primary)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    PasswordInputWithInitial(
                        modifier = Modifier,
                        field = stringResource(id = R.string.password),
                        placeholder = stringResource(id = R.string.password),
                        disabled = false,
                        initialValue = viewModel.irrigationConfigGeneralSatConfig.value?.password ?: "",
                        onTextChanged = {
                            viewModel.irrigationConfigGeneralSatConfig.value?.password = it
                        }
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier
                            .fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                    ) {
                        DropDown(
                            field = "${stringResource(id = R.string.entry)} 1",
                            mutableListOf(
                                Pair(
                                    stringResource(id = R.string.not_used),
                                    "0"
                                ),
                                Pair(
                                    stringResource(id = R.string.normally_opened),
                                    "1"
                                ),
                                Pair(
                                    stringResource(id = R.string.normally_closed),
                                    "2"
                                ),
                            ),
                            Modifier.weight(1f),
                            selectedValue = viewModel.irrigationConfigGeneralSatConfig.value?.entry1 ?: "",
                            onSelectItem = { _, it ->
                                viewModel.irrigationConfigGeneralSatConfig.value?.entry1 = it
                            }
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        DropDown(
                            field = "${stringResource(id = R.string.entry)} 2",
                            mutableListOf(
                                Pair(
                                    stringResource(id = R.string.not_used),
                                    "0"
                                ),
                                Pair(
                                    stringResource(id = R.string.normally_opened),
                                    "1"
                                ),
                                Pair(
                                    stringResource(id = R.string.normally_closed),
                                    "2"
                                ),
                            ),
                            Modifier.weight(1f),
                            selectedValue = viewModel.irrigationConfigGeneralSatConfig.value?.entry2 ?: "",
                            onSelectItem = { _, it ->

                            }
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier
                            .fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                    ) {
                        DropDown(
                            field = "${stringResource(id = R.string.entry)} 3",
                            mutableListOf(
                                Pair(
                                    stringResource(id = R.string.not_used),
                                    "0"
                                ),
                                Pair(
                                    stringResource(id = R.string.normally_opened),
                                    "1"
                                ),
                                Pair(
                                    stringResource(id = R.string.normally_closed),
                                    "2"
                                ),
                            ),
                            Modifier.weight(1f),
                            selectedValue = viewModel.irrigationConfigGeneralSatConfig.value?.entry3 ?: "",
                            onSelectItem = { _, it ->

                            }
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        DropDown(
                            field = "${stringResource(id = R.string.entry)} 4",
                            mutableListOf(
                                Pair(
                                    stringResource(id = R.string.not_used),
                                    "0"
                                ),
                                Pair(
                                    stringResource(id = R.string.normally_opened),
                                    "1"
                                ),
                                Pair(
                                    stringResource(id = R.string.normally_closed),
                                    "2"
                                ),
                                Pair(
                                    stringResource(id = R.string.emergency),
                                    "11"
                                ),
                            ),
                            Modifier.weight(1f),
                            selectedValue = viewModel.irrigationConfigGeneralSatConfig.value?.entry4 ?: "",
                            onSelectItem = { _, it ->

                            }
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier
                            .fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                    ) {
                        InputWithInitial(
                            modifier = Modifier,
                            field = stringResource(id = R.string.max_active_prog),
                            placeholder = stringResource(id = R.string.max_active_prog),
                            disabled = false,
                            keyboardOptions = KeyboardOptions.Default.copy(
                                keyboardType = KeyboardType.Text,
                                imeAction = ImeAction.Done
                            ),
                            modifierParent = Modifier.weight(1f),
                            initialValue = viewModel.irrigationConfigGeneralSatConfig.value?.maxActiveProgram ?: "",
                            onTextChanged = {
                                viewModel.irrigationConfigGeneralSatConfig.value?.maxActiveProgram = it
                            }
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier
                            .fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                    ) {
                        InputWithInitial(
                            modifier = Modifier,
                            field = stringResource(id = R.string.flow_off),
                            placeholder = stringResource(id = R.string.flow_off),
                            disabled = false,
                            keyboardOptions = KeyboardOptions.Default.copy(
                                keyboardType = KeyboardType.Text,
                                imeAction = ImeAction.Done
                            ),
                            modifierParent = Modifier.weight(1f),
                            trailingUnit = "L/h",
                            initialValue = viewModel.irrigationConfigGeneralSatConfig.value?.flowOff ?: "",
                            onTextChanged = {
                                viewModel.irrigationConfigGeneralSatConfig.value?.flowOff = it
                            }
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        InputWithInitial(
                            modifier = Modifier,
                            field = stringResource(id = R.string.flow_off_tolerance),
                            placeholder = stringResource(id = R.string.flow_off_tolerance),
                            disabled = false,
                            keyboardOptions = KeyboardOptions.Default.copy(
                                keyboardType = KeyboardType.Text,
                                imeAction = ImeAction.Done
                            ),
                            modifierParent = Modifier.weight(1f),
                            trailingUnit = "%",
                            initialValue = viewModel.irrigationConfigGeneralSatConfig.value?.flowOffTolerance ?: "",
                            onTextChanged = {
                                viewModel.irrigationConfigGeneralSatConfig.value?.flowOffTolerance = it
                            }
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier
                            .fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                    ) {
                        InputWithInitial(
                            modifier = Modifier,
                            field = stringResource(id = R.string.pulses),
                            placeholder = stringResource(id = R.string.pulses),
                            disabled = false,
                            keyboardOptions = KeyboardOptions.Default.copy(
                                keyboardType = KeyboardType.Text,
                                imeAction = ImeAction.Done
                            ),
                            modifierParent = Modifier.weight(1f),
                            trailingUnit = "L/h",
                            initialValue = viewModel.irrigationConfigGeneralPumpConfig.value?.pulses ?: "",
                            onTextChanged = {
                                viewModel.irrigationConfigGeneralPumpConfig.value?.pulses = it
                            }
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        DropDown(
                            field = stringResource(id = R.string.flow),
                            mutableListOf(
                                Pair(
                                    stringResource(id = R.string.high_flow) + " (M³/H)",
                                    "0"
                                ),
                                Pair(
                                    stringResource(id = R.string.normal_flow) + " (HL/H)",
                                    "1"
                                ),
                                Pair(
                                    stringResource(id = R.string.low_flow) + " (L/H)",
                                    "2"
                                ),
                            ),
                            Modifier.weight(1f),
                            selectedValue = viewModel.irrigationConfigGeneralSatConfig.value?.pulsesFlow ?: "",
                            onSelectItem = { _, it ->

                            }
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier
                            .fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                    ) {
                        InputWithInitial(
                            modifier = Modifier,
                            field = stringResource(id = R.string.solar_intensity),
                            placeholder = stringResource(id = R.string.solar_intensity),
                            disabled = false,
                            keyboardOptions = KeyboardOptions.Default.copy(
                                keyboardType = KeyboardType.Text,
                                imeAction = ImeAction.Done
                            ),
                            modifierParent = Modifier.weight(1f),
                            trailingUnit = "W/m²",
                            initialValue = viewModel.irrigationConfigGeneralSatConfig.value?.solarIntensity ?: "",
                            onTextChanged = {
                                viewModel.irrigationConfigGeneralSatConfig.value?.solarIntensity = it
                            }
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        InputWithInitial(
                            modifier = Modifier,
                            field = stringResource(id = R.string.wind_speed),
                            placeholder = stringResource(id = R.string.wind_speed),
                            disabled = false,
                            keyboardOptions = KeyboardOptions.Default.copy(
                                keyboardType = KeyboardType.Text,
                                imeAction = ImeAction.Done
                            ),
                            modifierParent = Modifier.weight(1f),
                            trailingUnit = "km/h",
                            initialValue = viewModel.irrigationConfigGeneralSatConfig.value?.windSpeed ?: "",
                            onTextChanged = {
                                viewModel.irrigationConfigGeneralSatConfig.value?.windSpeed = it
                            }
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier
                            .fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                    ) {
                        ToggleWithTitle(
                            field = stringResource(id = R.string.ev_master),
                            checkedTitle = "ON",
                            uncheckedTitle = "OFF",
                            modifier = Modifier.weight(1f),
                            selectedValue = viewModel.irrigationConfigGeneralSatConfig.value?.evMaster == "1",
                            onChecked = {
                                viewModel.irrigationConfigGeneralSatConfig.value?.evMaster = if (it) "1" else "0"
                            }
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        ToggleWithTitle(
                            field = stringResource(id = R.string.ec_command),
                            checkedTitle = "ON",
                            uncheckedTitle = "OFF",
                            modifier = Modifier.weight(1f),
                            selectedValue = viewModel.irrigationConfigGeneralSatConfig.value?.ecCommand == "1",
                            onChecked = {
                                viewModel.irrigationConfigGeneralSatConfig.value?.ecCommand = if (it) "1" else "0"
                            }
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    RangedSeekbar(
                        field = stringResource(id = R.string.pressure),
                        min= 0,
                        max = 16000,
                        modifier = null,
                        currentMin = viewModel.irrigationConfigGeneralSatConfig.value?.pressureMin?.toInt(),
                        currentMax = viewModel.irrigationConfigGeneralSatConfig.value?.pressureMax?.toInt(),
                        onValueChanged = {min, max ->
                            viewModel.irrigationConfigGeneralSatConfig.value?.pressureMin = min.toString()
                            viewModel.irrigationConfigGeneralSatConfig.value?.pressureMax = max.toString()
                        },
                        boxInputFieldModifier = null,
                        minText = "Min",
                        maxText = "Max",
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                GrayVeryVeryLight,
                                shape = RoundedCornerShape(14.dp)
                            )
                            .padding(vertical = 8.dp, horizontal = 18.dp)
                    ) {
                        Text(
                            stringResource(id = R.string.delay),
                            fontFamily = Manrope,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 18.sp,
                            color = Black
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    InputWithInitial(
                        modifier = Modifier,
                        field = stringResource(id = R.string.delay_alarm_time_low_pressure),
                        placeholder = stringResource(id = R.string.delay_alarm_time_low_pressure),
                        disabled = false,
                        keyboardOptions = KeyboardOptions.Default.copy(
                            keyboardType = KeyboardType.Text,
                            imeAction = ImeAction.Done
                        ),
                        trailingUnit = "sec",
                        initialValue = viewModel.irrigationConfigGeneralSatConfig.value?.delayAlarmTimeLowPressure ?: "",
                        onTextChanged = {
                            viewModel.irrigationConfigGeneralSatConfig.value?.delayAlarmTimeLowPressure = it
                        }
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    InputWithInitial(
                        modifier = Modifier,
                        field = stringResource(id = R.string.delay_alarm_time_high_pressure),
                        placeholder = stringResource(id = R.string.delay_alarm_time_high_pressure),
                        disabled = false,
                        keyboardOptions = KeyboardOptions.Default.copy(
                            keyboardType = KeyboardType.Text,
                            imeAction = ImeAction.Done
                        ),
                        trailingUnit = "sec",
                        initialValue = viewModel.irrigationConfigGeneralSatConfig.value?.delayAlarmTimeHighPressure ?: "",
                        onTextChanged = {
                            viewModel.irrigationConfigGeneralSatConfig.value?.delayAlarmTimeHighPressure = it
                        }
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier
                            .fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                    ) {
                        InputWithInitial(
                            modifier = Modifier,
                            field = "${stringResource(id = R.string.entry)} 1",
                            placeholder = "${stringResource(id = R.string.entry)} 1",
                            disabled = false,
                            keyboardOptions = KeyboardOptions.Default.copy(
                                keyboardType = KeyboardType.Text,
                                imeAction = ImeAction.Done
                            ),
                            modifierParent = Modifier.weight(1f),
                            trailingUnit = "sec",
                            initialValue = viewModel.irrigationConfigGeneralSatConfig.value?.entry1forDelay ?: "",
                            onTextChanged = {
                                viewModel.irrigationConfigGeneralSatConfig.value?.entry1forDelay = it
                            }
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        InputWithInitial(
                            modifier = Modifier,
                            field = "${stringResource(id = R.string.entry)} 2",
                            placeholder = "${stringResource(id = R.string.entry)} 2",
                            disabled = false,
                            keyboardOptions = KeyboardOptions.Default.copy(
                                keyboardType = KeyboardType.Text,
                                imeAction = ImeAction.Done
                            ),
                            modifierParent = Modifier.weight(1f),
                            trailingUnit = "sec",
                            initialValue = viewModel.irrigationConfigGeneralSatConfig.value?.entry2forDelay ?: "",
                            onTextChanged = {
                                viewModel.irrigationConfigGeneralSatConfig.value?.entry2forDelay = it
                            }
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))


                    Row(
                        modifier = Modifier
                            .fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                    ) {
                        InputWithInitial(
                            modifier = Modifier,
                            field = "${stringResource(id = R.string.entry)} 3",
                            placeholder = "${stringResource(id = R.string.entry)} 3",
                            disabled = false,
                            keyboardOptions = KeyboardOptions.Default.copy(
                                keyboardType = KeyboardType.Text,
                                imeAction = ImeAction.Done
                            ),
                            modifierParent = Modifier.weight(1f),
                            trailingUnit = "sec",
                            initialValue = viewModel.irrigationConfigGeneralSatConfig.value?.entry3forDelay ?: "",
                            onTextChanged = {
                                viewModel.irrigationConfigGeneralSatConfig.value?.entry3forDelay = it
                            }
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        InputWithInitial(
                            modifier = Modifier,
                            field = "${stringResource(id = R.string.entry)} 4",
                            placeholder = "${stringResource(id = R.string.entry)} 4",
                            disabled = false,
                            keyboardOptions = KeyboardOptions.Default.copy(
                                keyboardType = KeyboardType.Text,
                                imeAction = ImeAction.Done
                            ),
                            modifierParent = Modifier.weight(1f),
                            trailingUnit = "sec",
                            initialValue = viewModel.irrigationConfigGeneralSatConfig.value?.entry4forDelay ?: "",
                            onTextChanged = {
                                viewModel.irrigationConfigGeneralSatConfig.value?.entry4forDelay = it
                            }
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    InputWithInitial(
                        modifier = Modifier,
                        field = stringResource(id = R.string.pump_deactived_delay),
                        placeholder = stringResource(id = R.string.pump_deactived_delay),
                        disabled = false,
                        keyboardOptions = KeyboardOptions.Default.copy(
                            keyboardType = KeyboardType.Text,
                            imeAction = ImeAction.Done
                        ),
                        trailingUnit = "sec",
                        initialValue = viewModel.irrigationConfigGeneralPumpConfig.value?.pumpDeactivationDelay ?: "",
                        onTextChanged = {
                            viewModel.irrigationConfigGeneralPumpConfig.value?.pumpDeactivationDelay = it
                        }
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    InputWithInitial(
                        modifier = Modifier,
                        field = stringResource(id = R.string.flow_alarm_delay),
                        placeholder = stringResource(id = R.string.flow_alarm_delay),
                        disabled = false,
                        keyboardOptions = KeyboardOptions.Default.copy(
                            keyboardType = KeyboardType.Text,
                            imeAction = ImeAction.Done
                        ),
                        trailingUnit = "sec",
                        initialValue = viewModel.irrigationConfigGeneralSatConfig.value?.flowAlarmDelay ?: "",
                        onTextChanged = {
                            viewModel.irrigationConfigGeneralSatConfig.value?.flowAlarmDelay = it
                        }
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    InputWithInitial(
                        modifier = Modifier,
                        field = stringResource(id = R.string.delay_between_ms_and_ev),
                        placeholder = stringResource(id = R.string.delay_between_ms_and_ev),
                        disabled = false,
                        keyboardOptions = KeyboardOptions.Default.copy(
                            keyboardType = KeyboardType.Text,
                            imeAction = ImeAction.Done
                        ),
                        trailingUnit = "sec",
                        initialValue = viewModel.irrigationConfigGeneralMVConfig.value?.delayBetweenMSandEV ?: "",
                        onTextChanged = {
                            viewModel.irrigationConfigGeneralMVConfig.value?.delayBetweenMSandEV = it
                        }
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    InputWithInitial(
                        modifier = Modifier,
                        field = stringResource(id = R.string.temperature),
                        placeholder = stringResource(id = R.string.temperature),
                        disabled = false,
                        keyboardOptions = KeyboardOptions.Default.copy(
                            keyboardType = KeyboardType.Text,
                            imeAction = ImeAction.Done
                        ),
                        trailingUnit = "sec",
                        initialValue = viewModel.irrigationConfigGeneralSatConfig.value?.temperature ?: "",
                        onTextChanged = {
                            viewModel.irrigationConfigGeneralSatConfig.value?.temperature = it
                        }
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    InputWithInitial(
                        modifier = Modifier,
                        field = stringResource(id = R.string.humidity),
                        placeholder = stringResource(id = R.string.humidity),
                        disabled = false,
                        keyboardOptions = KeyboardOptions.Default.copy(
                            keyboardType = KeyboardType.Text,
                            imeAction = ImeAction.Done
                        ),
                        trailingUnit = "sec",
                        initialValue = viewModel.irrigationConfigGeneralSatConfig.value?.humidity ?: "",
                        onTextChanged = {
                            viewModel.irrigationConfigGeneralSatConfig.value?.humidity = it
                        }
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    InputWithInitial(
                        modifier = Modifier,
                        field = stringResource(id = R.string.solar_irradiation),
                        placeholder = stringResource(id = R.string.solar_irradiation),
                        disabled = false,
                        keyboardOptions = KeyboardOptions.Default.copy(
                            keyboardType = KeyboardType.Text,
                            imeAction = ImeAction.Done
                        ),
                        trailingUnit = "sec",
                        initialValue = viewModel.irrigationConfigGeneralSatConfig.value?.solarIrradiation ?: "",
                        onTextChanged = {
                            viewModel.irrigationConfigGeneralSatConfig.value?.solarIrradiation = it
                        }
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    InputWithInitial(
                        modifier = Modifier,
                        field = stringResource(id = R.string.wind_sensor),
                        placeholder = stringResource(id = R.string.wind_sensor),
                        disabled = false,
                        keyboardOptions = KeyboardOptions.Default.copy(
                            keyboardType = KeyboardType.Text,
                            imeAction = ImeAction.Done
                        ),
                        trailingUnit = "sec",
                        initialValue = viewModel.irrigationConfigGeneralSatConfig.value?.windSensor ?: "",
                        onTextChanged = {
                            viewModel.irrigationConfigGeneralSatConfig.value?.windSensor = it
                        }
                    )

                    //end lazycolumn
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
                    if (viewModel.irrigationConfigGeneralSatConfig.value != null &&
                        viewModel.irrigationConfigGeneralPumpConfig.value != null &&
                        viewModel.irrigationConfigGeneralMVConfig.value != null) {
                        if (
                            (viewModel.irrigationConfigGeneralSatConfig.value?.pressureMin?.toFloat() ?: 0f)
                            >
                            (viewModel.irrigationConfigGeneralSatConfig.value?.pressureMax?.toFloat() ?: 0f)
                        ) {
                           showToast(context.getString(R.string.min_pressure_more_than_max_alert))
                        } else {
                            scope.launch {
                                viewModel.postDataLoading.value = true
                                viewModel.postIrrigationConfigGeneralConfig(
                                    viewModel.selectedDevice.value?.code ?: "",
                                    viewModel.irrigationConfigGeneralSatConfig.value!!,
                                    viewModel.irrigationConfigGeneralPumpConfig.value!!,
                                    viewModel.irrigationConfigGeneralMVConfig.value!!
                                )
                                viewModel.postDataLoading.value = false
                            }
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
