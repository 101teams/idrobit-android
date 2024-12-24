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
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Card
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Slider
import androidx.compose.material.SliderDefaults
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
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.navigation.NavController
import com.google.accompanist.pager.ExperimentalPagerApi
import com.idrolife.app.BuildConfig
import com.idrolife.app.R
import com.idrolife.app.navigation.Screen
import com.idrolife.app.presentation.component.DropDown
import com.idrolife.app.presentation.component.NavigationBanner3
import com.idrolife.app.presentation.component.RangedSeekbar
import com.idrolife.app.presentation.component.ToggleWithTitle
import com.idrolife.app.presentation.component.TopToastDialog
import com.idrolife.app.presentation.component.VerticalMultipleCheckWithTitle
import com.idrolife.app.presentation.viewmodel.DeviceViewModel
import com.idrolife.app.theme.Black
import com.idrolife.app.theme.BrokenWhite
import com.idrolife.app.theme.GrayVeryLight
import com.idrolife.app.theme.GrayVeryVeryLight
import com.idrolife.app.theme.Manrope
import com.idrolife.app.theme.Primary
import com.idrolife.app.theme.Primary2
import com.idrolife.app.theme.PrimaryLight
import com.idrolife.app.theme.PrimaryPale
import com.idrolife.app.theme.White
import com.idrolife.app.utils.Helper
import kotlinx.coroutines.launch

@OptIn(ExperimentalPagerApi::class)
@Composable
fun IrrigationSettingSensorManagementScreen(
    navController: NavController,
    deviceID: String,
    deviceCode: String
) {
    val context = LocalContext.current

    val window = (context as Activity).window
    val view = LocalView.current

    val viewModel = hiltViewModel<DeviceViewModel>()
    val scope = rememberCoroutineScope()
    val showToast = remember { mutableStateOf(false) }

    var programNum by remember { mutableStateOf(1) }

    val edValueWaterBudgetMin = remember { mutableStateOf(0) }

    val waterBudgetAuto = remember { mutableStateOf(false) }

    val waterBudgetMin = 0
    val waterBudgetMax = 250

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

        viewModel.getIrrigationSettingSensorManagement(deviceCode, programNum)
        waterBudgetAuto.value = viewModel.irrigationSettingSensorManagement.value?.waterBudgetAuto ?: true

        val waterBudget = viewModel.irrigationSettingSensorManagement.value?.waterBudget?.toIntOrNull()

        if (waterBudget != null) {
            edValueWaterBudgetMin.value = waterBudget
        }

        viewModel.isLoading.value = false

        viewModel.startPeriodicFetchingDevicesByID(deviceID)
    }

    fun showToast(message: String, duration: Int = Toast.LENGTH_SHORT) {
        Toast.makeText(context, message, duration).show()
    }

    Box() {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(White),
        ) {
            NavigationBanner3(
                navController,
                stringResource(id = R.string.sensors_management),
                R.drawable.img_header_detail3,
                viewModel.selectedDevice.value,
                viewModel.isLoading.value,
            )

            if (!viewModel.isLoading.value) {
                Column {
                    LazyColumn(
                        modifier = Modifier
                            .padding(horizontal = 24.dp)
                            .weight(1f),
                    ) {
                        item {
                            Spacer(modifier = Modifier.height(24.dp))

                            DropDown(
                                field = stringResource(id = R.string.program_number),
                                mutableListOf(
                                    Pair("1","1"),
                                    Pair("2","2"),
                                    Pair("3","3"),
                                    Pair("4","4"),
                                    Pair("5","5"),
                                    Pair("6","6"),
                                    Pair("7","7"),
                                    Pair("8","8"),
                                    Pair("9","9"),
                                    Pair("10","10"),
                                    Pair("11","11"),
                                    Pair("12","12"),
                                    Pair("13","13"),
                                    Pair("14","14"),
                                    Pair("15","15"),
                                    Pair("16","16"),
                                    Pair("17","17"),
                                    Pair("18","18"),
                                    Pair("19","19"),
                                    Pair("20","20"),
                                    Pair("21","21"),
                                    Pair("22","22"),
                                    Pair("23","23"),
                                    Pair("24","24"),
                                    Pair("25","25"),
                                    Pair("26","26"),
                                    Pair("27","27"),
                                    Pair("28","28"),
                                    Pair("29","29"),
                                    Pair("30","30"),
                                ),
                                Modifier.fillMaxWidth(),
                                selectedValue = programNum.toString(),
                                onSelectItem = { _, it ->
                                    programNum = it.toInt()
                                    scope.launch {
                                        viewModel.isLoading.value = true
                                        viewModel.getIrrigationSettingSensorManagement(deviceCode, it.toInt())
                                        viewModel.isLoading.value = false
                                    }
                                }
                            )

                            Spacer(modifier = Modifier.height(12.dp))

                            Text(
                                stringResource(id = R.string.sensor_setting),
                                fontFamily = Manrope,
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 14.sp,
                                color = Black,
                            )

                            Spacer(modifier = Modifier.height(12.dp))

                            if (BuildConfig.FLAVOR == "idroLife" || BuildConfig.FLAVOR == "idroPro") {
                                Card(
                                    modifier = Modifier
                                        .fillMaxWidth(),
                                    shape = RoundedCornerShape(12.dp),
                                    elevation = 4.dp,
                                    backgroundColor = GrayVeryVeryLight,
                                ) {
                                    Column(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(12.dp),
                                    ) {
                                        RangedSeekbar(
                                            field = stringResource(id = R.string.temperature),
                                            min= -20,
                                            max = 100,
                                            modifier = null,
                                            currentMin = viewModel.irrigationSettingSensorManagement.value?.lowTemp?.toInt(),
                                            currentMax = viewModel.irrigationSettingSensorManagement.value?.highTemp?.toInt(),
                                            onValueChanged = {min, max ->
                                                viewModel.irrigationSettingSensorManagement.value?.lowTemp = min.toString()
                                                viewModel.irrigationSettingSensorManagement.value?.highTemp = max.toString()
                                            },
                                            boxInputFieldModifier = Modifier.background(
                                                PrimaryPale,
                                                shape = RoundedCornerShape(8.dp),
                                            ),
                                            minText = "${stringResource(id = R.string.low)} °C",
                                            maxText = "${stringResource(id = R.string.high)} °C",
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(12.dp))
                            }

                            Card(
                                modifier = Modifier
                                    .fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp),
                                elevation = 4.dp,
                                backgroundColor = GrayVeryVeryLight,
                            ) {
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(12.dp),
                                ) {
                                    RangedSeekbar(
                                        field = stringResource(id = R.string.humidity),
                                        min= 0,
                                        max = 100,
                                        modifier = null,
                                        currentMin = viewModel.irrigationSettingSensorManagement.value?.lowHumidity?.toInt(),
                                        currentMax = viewModel.irrigationSettingSensorManagement.value?.highHumidity?.toInt(),
                                        onValueChanged = {min, max ->
                                            viewModel.irrigationSettingSensorManagement.value?.lowHumidity = min.toString()
                                            viewModel.irrigationSettingSensorManagement.value?.highHumidity = max.toString()
                                        },
                                        boxInputFieldModifier = Modifier.background(
                                            Color(0xFFC8D4CF),
                                            shape = RoundedCornerShape(8.dp),
                                        ),
                                        minText = "${stringResource(id = R.string.low)} %",
                                        maxText = "${stringResource(id = R.string.high)} %",
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            Row(
                                modifier = Modifier
                                    .fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                            ) {
                                DropDown(
                                    field = stringResource(id = R.string.humidity_sensor_type),
                                    mutableListOf(
                                        Pair(stringResource(id = R.string.air_sensor),"0"),
                                        Pair("${stringResource(id = R.string.soil_sensor)} 1","1"),
                                        Pair("${stringResource(id = R.string.soil_sensor)} 2","2"),
                                        Pair("${stringResource(id = R.string.soil_sensor)} 3","3"),
                                        Pair("${stringResource(id = R.string.soil_sensor)} 4","4"),
                                        Pair("${stringResource(id = R.string.soil_sensor)} 5","5"),
                                        Pair("${stringResource(id = R.string.soil_sensor)} 6","6"),
                                        Pair("${stringResource(id = R.string.soil_sensor)} 7","7"),
                                        Pair("${stringResource(id = R.string.soil_sensor)} 8","8"),
                                        Pair("${stringResource(id = R.string.soil_sensor)} 9","9"),
                                        Pair("${stringResource(id = R.string.soil_sensor)} 10","10"),
                                        Pair("${stringResource(id = R.string.soil_sensor)} 11","11"),
                                        Pair("${stringResource(id = R.string.soil_sensor)} 12","12"),
                                        Pair("${stringResource(id = R.string.soil_sensor)} 13","13"),
                                        Pair("${stringResource(id = R.string.soil_sensor)} 14","14"),
                                        Pair("${stringResource(id = R.string.soil_sensor)} 15","15"),
                                        Pair("${stringResource(id = R.string.soil_sensor)} 16","16"),
                                        Pair("${stringResource(id = R.string.soil_sensor)} 17","17"),
                                        Pair("${stringResource(id = R.string.soil_sensor)} 18","18"),
                                        Pair("${stringResource(id = R.string.soil_sensor)} 19","19"),
                                        Pair("${stringResource(id = R.string.soil_sensor)} 20","20"),
                                        Pair("${stringResource(id = R.string.soil_sensor)} 21","21"),
                                        Pair("${stringResource(id = R.string.soil_sensor)} 22","22"),
                                        Pair("${stringResource(id = R.string.soil_sensor)} 23","23"),
                                        Pair("${stringResource(id = R.string.soil_sensor)} 24","24"),
                                        Pair("${stringResource(id = R.string.soil_sensor)} 25","25"),
                                        Pair("${stringResource(id = R.string.soil_sensor)} 26","26"),
                                        Pair("${stringResource(id = R.string.soil_sensor)} 27","27"),
                                        Pair("${stringResource(id = R.string.soil_sensor)} 28","28"),
                                        Pair("${stringResource(id = R.string.soil_sensor)} 29","29"),
                                        Pair("${stringResource(id = R.string.soil_sensor)} 30","30"),
                                    ),
                                    Modifier.weight(1f),
                                    selectedValue = viewModel.irrigationSettingSensorManagement.value?.humiditySensorType ?: "",
                                    onSelectItem = { _, it ->
                                        viewModel.irrigationSettingSensorManagement.value?.humiditySensorType = it
                                    }
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                DropDown(
                                    field = stringResource(id = R.string.humidity_sensor_level),
                                    mutableListOf(
                                        Pair(
                                            "1",
                                            "1"
                                        ),
                                        Pair(
                                            "2",
                                            "2"
                                        ),
                                        Pair(
                                            "3",
                                            "3"
                                        ),
                                        Pair(
                                            "4",
                                            "4"
                                        ),
                                    ),
                                    Modifier.weight(1f),
                                    selectedValue = viewModel.irrigationSettingSensorManagement.value?.humiditySensorLevel ?: "",
                                    onSelectItem = { _, it ->
                                        viewModel.irrigationSettingSensorManagement.value?.humiditySensorLevel = it
                                    }
                                )
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            Card(
                                modifier = Modifier
                                    .fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp),
                                elevation = 4.dp,
                                backgroundColor = GrayVeryVeryLight,
                            ) {
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(12.dp),
                                ) {
                                    Text(
                                        "${stringResource(id = R.string.water_budget)} %",
                                        fontFamily = Manrope,
                                        fontWeight = FontWeight.SemiBold,
                                        fontSize = 14.sp,
                                        color = Black,
                                    )

                                    Spacer(modifier = Modifier.height(12.dp))

                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                    ) {
                                        Column(
                                            horizontalAlignment = Alignment.CenterHorizontally,
                                        ) {
                                            Text(
                                                "%",
                                                fontFamily = Manrope,
                                                fontWeight = FontWeight.Normal,
                                                fontSize = 14.sp,
                                                color = Black,
                                            )

                                            Spacer(modifier= Modifier.height(4.dp))

                                            Box(
                                                modifier = Modifier
                                                    .padding(top = 4.dp)
                                                    .width(70.dp)
                                                    .background(
                                                        Color(0xFFC8D4CF),
                                                        shape = RoundedCornerShape(8.dp)
                                                    )
                                            ) {
                                                Row(
                                                    modifier = Modifier.padding(4.dp),
                                                    verticalAlignment = Alignment.CenterVertically,
                                                ) {
                                                    BasicTextField(
                                                        value = edValueWaterBudgetMin.value.toString(),
                                                        onValueChange = {
                                                            var str = it.replace(" ", "")
                                                            if (str.isEmpty()) {
                                                                str = "0"
                                                            }

                                                            if (str.toFloat() > waterBudgetMax) {
                                                                str = waterBudgetMax.toString()
                                                            } else if (str.toFloat() < waterBudgetMin) {
                                                                str = waterBudgetMin.toString()
                                                            }

                                                            edValueWaterBudgetMin.value = str.toFloat().toInt()

                                                            viewModel.irrigationSettingSensorManagement.value?.waterBudget = it
                                                        },
                                                        modifier = Modifier
                                                            .padding(4.dp)
                                                            .fillMaxWidth(),
                                                        textStyle = TextStyle(
                                                            fontSize = 14.sp,
                                                            fontFamily = Manrope,
                                                            fontWeight = FontWeight.Medium,
                                                            textAlign = TextAlign.Left,
                                                            color = Black,
                                                        ),
                                                        singleLine = true,
                                                        keyboardOptions = KeyboardOptions.Default.copy(
                                                            imeAction = ImeAction.Done,
                                                            keyboardType = KeyboardType.Number,
                                                        ),
                                                    )
                                                }
                                            }
                                        }
                                        Column(
                                            horizontalAlignment = Alignment.CenterHorizontally,
                                        ) {
                                            Text(
                                                "Auto",
                                                fontFamily = Manrope,
                                                fontWeight = FontWeight.Normal,
                                                fontSize = 14.sp,
                                                color = Black,
                                            )

                                            Spacer(modifier= Modifier.height(6.dp))

                                            ToggleWithTitle(
                                                field = null,
                                                checkedTitle = "ON",
                                                uncheckedTitle = "OFF",
                                                modifier = Modifier,
                                                selectedValue = viewModel.irrigationSettingSensorManagement.value?.waterBudgetAuto ?: false,
                                                onChecked = {
                                                    viewModel.irrigationSettingSensorManagement.value?.waterBudgetAuto = it
                                                    waterBudgetAuto.value = it
                                                }
                                            )
                                        }
                                    }

                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(50.dp) // Set the height for the track thickness
                                    ) {
                                        Slider(
                                            value = edValueWaterBudgetMin.value.toFloat(),
                                            steps = waterBudgetMax - 1,
                                            onValueChange = { range ->
                                                edValueWaterBudgetMin.value = range.toInt()
                                                viewModel.irrigationSettingSensorManagement.value?.waterBudget = range.toInt().toString()
                                            },
                                            valueRange = waterBudgetMin.toFloat()..waterBudgetMax.toFloat(),
                                            onValueChangeFinished = {

                                            },
                                            colors = SliderDefaults.colors(
                                                thumbColor = Primary2,
                                                activeTickColor = PrimaryLight,
                                                inactiveTickColor = GrayVeryLight,
                                            ),
                                            enabled = !waterBudgetAuto.value
                                        )
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(24.dp))

                            VerticalMultipleCheckWithTitle(
                                field = stringResource(id = R.string.program_stop),
                                items =
                                if (BuildConfig.FLAVOR == "idroLife" || BuildConfig.FLAVOR == "idroPro") {
                                    mutableListOf(
                                        Pair(stringResource(id = R.string.low_temperature), viewModel.irrigationSettingSensorManagement.value?.programStop?.get(0) == '1'),
                                        Pair(stringResource(id = R.string.low_humidity), viewModel.irrigationSettingSensorManagement.value?.programStop?.get(1) == '1'),
                                        Pair(stringResource(id = R.string.wind), viewModel.irrigationSettingSensorManagement.value?.programStop?.get(2) == '1'),
                                        Pair(stringResource(id = R.string.solar_intensity), viewModel.irrigationSettingSensorManagement.value?.programStop?.get(3) == '1'),
                                        Pair(stringResource(id = R.string.first_entry), viewModel.irrigationSettingSensorManagement.value?.programStop?.get(4) == '1'),
                                        Pair(stringResource(id = R.string.second_entry), viewModel.irrigationSettingSensorManagement.value?.programStop?.get(5) == '1'),
                                        Pair(stringResource(id = R.string.high_temperature), viewModel.irrigationSettingSensorManagement.value?.programStop?.get(6) == '1'),
                                        Pair(stringResource(id = R.string.high_humidity), viewModel.irrigationSettingSensorManagement.value?.programStop?.get(7) == '1'),
                                        Pair(stringResource(id = R.string.third_entry), viewModel.irrigationSettingSensorManagement.value?.programStop?.get(8) == '1'),
                                        Pair(stringResource(id = R.string.fourth_entry), viewModel.irrigationSettingSensorManagement.value?.programStop?.get(9) == '1'),
                                        Pair(stringResource(id = R.string.low_pressure), viewModel.irrigationSettingSensorManagement.value?.programStop?.get(10) == '1'),
                                        Pair(stringResource(id = R.string.high_pressure), viewModel.irrigationSettingSensorManagement.value?.programStop?.get(11) == '1'),
                                    )
                                 } else {
                                    mutableListOf(
                                        Pair(stringResource(id = R.string.low_humidity), viewModel.irrigationSettingSensorManagement.value?.programStop?.get(1) == '1'),
                                        Pair(stringResource(id = R.string.first_entry), viewModel.irrigationSettingSensorManagement.value?.programStop?.get(4) == '1'),
                                        Pair(stringResource(id = R.string.high_humidity), viewModel.irrigationSettingSensorManagement.value?.programStop?.get(7) == '1'),
                                        Pair(stringResource(id = R.string.low_pressure), viewModel.irrigationSettingSensorManagement.value?.programStop?.get(10) == '1'),
                                        Pair(stringResource(id = R.string.high_pressure), viewModel.irrigationSettingSensorManagement.value?.programStop?.get(11) == '1'),
                                    )
                                },
                                modifier = Modifier.fillMaxWidth(),
                                onChecked = {index, data ->
                                    val charArray = viewModel.irrigationSettingSensorManagement.value?.programStop?.toCharArray()
                                    charArray!![index] = if(data.second) '1' else '0'
                                    viewModel.irrigationSettingSensorManagement.value?.programStop = String(charArray)
                                },
                                disableAll = false,
                                columnCount = 2,
                            )

                            Spacer(modifier = Modifier.height(24.dp))

                            if (BuildConfig.FLAVOR == "idroLife" || BuildConfig.FLAVOR == "idroPro") {
                                VerticalMultipleCheckWithTitle(
                                    field = stringResource(id = R.string.program_standby),
                                    items = mutableListOf(
                                        Pair(stringResource(id = R.string.low_temperature), viewModel.irrigationSettingSensorManagement.value?.programStandBy?.get(0) == '1'),
                                        Pair(stringResource(id = R.string.low_humidity), viewModel.irrigationSettingSensorManagement.value?.programStandBy?.get(1) == '1'),
                                        Pair(stringResource(id = R.string.wind), viewModel.irrigationSettingSensorManagement.value?.programStandBy?.get(2) == '1'),
                                        Pair(stringResource(id = R.string.solar_intensity), viewModel.irrigationSettingSensorManagement.value?.programStandBy?.get(3) == '1'),
                                        Pair(stringResource(id = R.string.first_entry), viewModel.irrigationSettingSensorManagement.value?.programStandBy?.get(4) == '1'),
                                        Pair(stringResource(id = R.string.second_entry), viewModel.irrigationSettingSensorManagement.value?.programStandBy?.get(5) == '1'),
                                        Pair(stringResource(id = R.string.high_temperature), viewModel.irrigationSettingSensorManagement.value?.programStandBy?.get(6) == '1'),
                                        Pair(stringResource(id = R.string.high_humidity), viewModel.irrigationSettingSensorManagement.value?.programStandBy?.get(7) == '1'),
                                        Pair(stringResource(id = R.string.third_entry), viewModel.irrigationSettingSensorManagement.value?.programStandBy?.get(8) == '1'),
                                        Pair(stringResource(id = R.string.fourth_entry), viewModel.irrigationSettingSensorManagement.value?.programStandBy?.get(9) == '1'),
                                        Pair(stringResource(id = R.string.low_pressure), viewModel.irrigationSettingSensorManagement.value?.programStandBy?.get(10) == '1'),
                                        Pair(stringResource(id = R.string.high_pressure), viewModel.irrigationSettingSensorManagement.value?.programStandBy?.get(11) == '1'),
                                    ),
                                    modifier = Modifier.fillMaxWidth(),
                                    onChecked = {index, data ->
                                        val charArray = viewModel.irrigationSettingSensorManagement.value?.programStandBy?.toCharArray()
                                        charArray!![index] = if(data.second) '1' else '0'
                                        viewModel.irrigationSettingSensorManagement.value?.programStandBy = String(charArray)
                                    },
                                    disableAll = false,
                                    columnCount = 2,
                                )

                                Spacer(modifier = Modifier.height(24.dp))
                            }

                            if (BuildConfig.FLAVOR == "idroPro" || BuildConfig.FLAVOR == "idroLife") {
                                VerticalMultipleCheckWithTitle(
                                    field = stringResource(id = R.string.program_start),
                                    items = mutableListOf(
                                        Pair(stringResource(id = R.string.low_temperature), viewModel.irrigationSettingSensorManagement.value?.programStart?.get(0) == '1'),
                                        Pair(stringResource(id = R.string.low_humidity), viewModel.irrigationSettingSensorManagement.value?.programStart?.get(1) == '1'),
                                        Pair(stringResource(id = R.string.wind), viewModel.irrigationSettingSensorManagement.value?.programStart?.get(2) == '1'),
                                        Pair(stringResource(id = R.string.solar_intensity), viewModel.irrigationSettingSensorManagement.value?.programStart?.get(3) == '1'),
                                        Pair(stringResource(id = R.string.first_entry), viewModel.irrigationSettingSensorManagement.value?.programStart?.get(4) == '1'),
                                        Pair(stringResource(id = R.string.second_entry), viewModel.irrigationSettingSensorManagement.value?.programStart?.get(5) == '1'),
                                        Pair(stringResource(id = R.string.high_temperature), viewModel.irrigationSettingSensorManagement.value?.programStart?.get(6) == '1'),
                                        Pair(stringResource(id = R.string.high_humidity), viewModel.irrigationSettingSensorManagement.value?.programStart?.get(7) == '1'),
                                        Pair(stringResource(id = R.string.third_entry), viewModel.irrigationSettingSensorManagement.value?.programStart?.get(8) == '1'),
                                        Pair(stringResource(id = R.string.fourth_entry), viewModel.irrigationSettingSensorManagement.value?.programStart?.get(9) == '1'),
                                        Pair(stringResource(id = R.string.low_pressure), viewModel.irrigationSettingSensorManagement.value?.programStart?.get(10) == '1'),
                                        Pair(stringResource(id = R.string.high_pressure), viewModel.irrigationSettingSensorManagement.value?.programStart?.get(11) == '1'),
                                    ),
                                    modifier = Modifier.fillMaxWidth(),
                                    onChecked = {index, data ->
                                        val charArray = viewModel.irrigationSettingSensorManagement.value?.programStart?.toCharArray()
                                        charArray!![index] = if(data.second) '1' else '0'
                                        viewModel.irrigationSettingSensorManagement.value?.programStart = String(charArray)
                                    },
                                    disableAll = false,
                                    columnCount = 2,
                                )

                                Spacer(modifier = Modifier.height(24.dp))

                                VerticalMultipleCheckWithTitle(
                                    field = stringResource(id = R.string.program_skip),
                                    items = mutableListOf(
                                        Pair(stringResource(id = R.string.low_temperature), viewModel.irrigationSettingSensorManagement.value?.programSkip?.get(0) == '1'),
                                        Pair(stringResource(id = R.string.low_humidity), viewModel.irrigationSettingSensorManagement.value?.programSkip?.get(1) == '1'),
                                        Pair(stringResource(id = R.string.wind), viewModel.irrigationSettingSensorManagement.value?.programSkip?.get(2) == '1'),
                                        Pair(stringResource(id = R.string.solar_intensity), viewModel.irrigationSettingSensorManagement.value?.programSkip?.get(3) == '1'),
                                        Pair(stringResource(id = R.string.first_entry), viewModel.irrigationSettingSensorManagement.value?.programSkip?.get(4) == '1'),
                                        Pair(stringResource(id = R.string.second_entry), viewModel.irrigationSettingSensorManagement.value?.programSkip?.get(5) == '1'),
                                        Pair(stringResource(id = R.string.high_temperature), viewModel.irrigationSettingSensorManagement.value?.programSkip?.get(6) == '1'),
                                        Pair(stringResource(id = R.string.high_humidity), viewModel.irrigationSettingSensorManagement.value?.programSkip?.get(7) == '1'),
                                        Pair(stringResource(id = R.string.third_entry), viewModel.irrigationSettingSensorManagement.value?.programSkip?.get(8) == '1'),
                                        Pair(stringResource(id = R.string.fourth_entry), viewModel.irrigationSettingSensorManagement.value?.programSkip?.get(9) == '1'),
                                        Pair(stringResource(id = R.string.low_pressure), viewModel.irrigationSettingSensorManagement.value?.programSkip?.get(10) == '1'),
                                        Pair(stringResource(id = R.string.high_pressure), viewModel.irrigationSettingSensorManagement.value?.programSkip?.get(11) == '1'),
                                    ),
                                    modifier = Modifier.fillMaxWidth(),
                                    onChecked = {index, data ->
                                        val charArray = viewModel.irrigationSettingSensorManagement.value?.programSkip?.toCharArray()
                                        charArray!![index] = if(data.second) '1' else '0'
                                        viewModel.irrigationSettingSensorManagement.value?.programSkip = String(charArray)
                                    },
                                    disableAll = false,
                                    columnCount = 2,
                                )

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
                                    viewModel.postDataLoading.value = true
                                    val result = viewModel.postIrrigationSettingSensorManagement(deviceCode, viewModel.irrigationSettingSensorManagement.value!!, programNum)
                                    if (result == null) {
                                        showToast.value = true
                                    }
                                    viewModel.postDataLoading.value = false
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

        TopToastDialog(
            stringResource(id = R.string.success_post_data),
            showToast.value,
            onDismiss = {
                showToast.value = false
            }
        )
    }
}