package com.idrolife.app.presentation.screen

import android.app.Activity
import android.os.Handler
import android.os.Looper
import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Card
import androidx.compose.material.Checkbox
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
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
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.imageResource
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
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.rememberPagerState
import com.idrolife.app.R
import com.idrolife.app.data.api.irrigation.IrrigationSettingGeneralParameter
import com.idrolife.app.data.api.irrigation.IrrigationSettingScheduleStart
import com.idrolife.app.data.api.irrigation.StationDuration
import com.idrolife.app.navigation.Screen
import com.idrolife.app.presentation.component.CheckBoxWithTitle
import com.idrolife.app.presentation.component.DropDown
import com.idrolife.app.presentation.component.DropDownSmall
import com.idrolife.app.presentation.component.InputWithInitial
import com.idrolife.app.presentation.component.NavigationBanner3
import com.idrolife.app.presentation.component.ToggleWithTitle
import com.idrolife.app.presentation.component.TopToastDialog
import com.idrolife.app.presentation.component.VerticalMultipleCheckWithTitle
import com.idrolife.app.presentation.viewmodel.DeviceViewModel
import com.idrolife.app.theme.Black
import com.idrolife.app.theme.BrokenWhite
import com.idrolife.app.theme.Gray
import com.idrolife.app.theme.GrayLight
import com.idrolife.app.theme.GrayVeryLight
import com.idrolife.app.theme.GrayVeryVeryLight
import com.idrolife.app.theme.Primary
import com.idrolife.app.theme.Primary2
import com.idrolife.app.theme.Manrope
import com.idrolife.app.theme.White
import com.idrolife.app.utils.Helper
import kotlinx.coroutines.launch

@OptIn(ExperimentalPagerApi::class)
@Composable
fun IrrigationSettingGeneralParameterScreen(
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
    val tabs = listOf(context.getString(R.string.general_parameters), context.getString(R.string.schedule_start), context.getString(R.string.stations_duration))

    val showToast = remember { mutableStateOf(false) }

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

    Box() {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(White),
        ) {
            NavigationBanner3(
                navController,
                stringResource(id = R.string.general_parameters),
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
                if (!viewModel.isLoading.value || pagerState.currentPage == 1 || pagerState.currentPage == 2) {
                    LazyRow(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Start,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 24.dp)
                    ) {
                        itemsIndexed(tabs) {index, title ->
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
                            IrrigationSettingGeneralParameterPage1(
                                viewModel, deviceCode, navController, showToast
                            )
                        }
                        1 -> {
                            IrrigationSettingGeneralParameterPage2(deviceCode, showToast)
                        }
                        else -> {
                            IrrigationSettingGeneralParameterPage3(deviceCode, showToast)
                        }
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
    }
}

@Composable
fun IrrigationSettingGeneralParameterPage1(viewModel: DeviceViewModel, deviceCode: String, navController: NavController, showToast: MutableState<Boolean>) {
    var startMode by remember { mutableStateOf(viewModel.irrigationSettingGeneralParameter.value?.startMode) }
    var programNum by remember { mutableStateOf(1) }

    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        viewModel.setMarkerLoading.value = true

        val result = viewModel.getIrrigationSettingGeneralParameter(deviceCode, programNum)

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

        startMode = viewModel.irrigationSettingGeneralParameter.value?.startMode

        viewModel.setMarkerLoading.value = false

    }

    if (!viewModel.setMarkerLoading.value) {
        Column {
            LazyColumn(
                modifier = Modifier
                    .padding(horizontal = 24.dp)
                    .weight(1f)
            ) {
                item{
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
                                viewModel.setMarkerLoading.value = true
                                viewModel.getIrrigationSettingGeneralParameter(deviceCode, it.toInt())
                                viewModel.setMarkerLoading.value = false
                            }
                        }
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    InputWithInitial(
                        modifier = Modifier,
                        field = stringResource(id = R.string.program_name),
                        placeholder = stringResource(id = R.string.program_name),
                        disabled = false,
                        keyboardOptions = KeyboardOptions.Default.copy(
                            keyboardType = KeyboardType.Text,
                            imeAction = ImeAction.Done
                        ),
                        modifierParent = Modifier,
                        initialValue = viewModel.irrigationSettingGeneralParameter.value?.programName ?: "",
                        onTextChanged = {

                        }
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Row() {
                        ToggleWithTitle(
                            field = stringResource(id = R.string.program_mode),
                            checkedTitle = "ON",
                            uncheckedTitle = "OFF",
                            modifier = Modifier.weight(1f),
                            selectedValue = viewModel.irrigationSettingGeneralParameter.value?.programMode == "0",
                            onChecked = {
                                viewModel.irrigationSettingGeneralParameter.value?.programMode = if (it) "0" else "1"
                            }
                        )

                        Spacer(modifier = Modifier.width(8.dp))

                        DropDown(
                            field = stringResource(id = R.string.minifert_program_related),
                            mutableListOf(
                                Pair("OFF","0"),
                                Pair("1","1"),
                                Pair("2","2"),
                                Pair("3","3"),
                                Pair("4","4"),
                                Pair("5","5"),
                                Pair("6","6"),
                                Pair("7","7"),
                                Pair("8","8"),
                            ),
                            Modifier.weight(1f),
                            selectedValue = viewModel.irrigationSettingGeneralParameter.value?.minifertProgramRelated ?: "0",
                            onSelectItem = { _, it ->
                                viewModel.irrigationSettingGeneralParameter.value?.minifertProgramRelated = it
                            }
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    CheckBoxWithTitle(
                        field = stringResource(id = R.string.choice_of_time_mode),
                        items = mutableListOf(
                            Pair("${stringResource(id = R.string.minute)}/${stringResource(id = R.string.second)}","1"),
                            Pair("${stringResource(id = R.string.hour)}/${stringResource(id = R.string.minute)}","2"),
                            Pair("Volume","0"),
                        ),
                        modifier = Modifier,
                        selectedValue = viewModel.irrigationSettingGeneralParameter.value?.choiceTimeMode ?: "",
                        onChecked = {
                            viewModel.irrigationSettingGeneralParameter.value?.choiceTimeMode = it
                        }
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    CheckBoxWithTitle(
                        field = stringResource(id = R.string.cycles_of_time),
                        items = mutableListOf(
                            Pair(stringResource(id = R.string.cycles),"1"),
                            Pair(stringResource(id = R.string.time),"0"),
                        ),
                        modifier = Modifier,
                        selectedValue = viewModel.irrigationSettingGeneralParameter.value?.cycletime ?: "",
                        onChecked = {
                            viewModel.irrigationSettingGeneralParameter.value?.cycletime = it
                        }
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    InputWithInitial(
                        modifier = Modifier,
                        field = stringResource(id = R.string.delay_between_stations),
                        placeholder = stringResource(id = R.string.delay_between_stations),
                        disabled = false,
                        keyboardOptions = KeyboardOptions.Default.copy(
                            keyboardType = KeyboardType.Text,
                            imeAction = ImeAction.Done
                        ),
                        trailingUnit = "sec",
                        initialValue = viewModel.irrigationSettingGeneralParameter.value?.delayBetweenStation ?: "",
                        onTextChanged = {
                            viewModel.irrigationSettingGeneralParameter.value?.delayBetweenStation = it
                        }
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    InputWithInitial(
                        modifier = Modifier,
                        field = stringResource(id = R.string.delay_between_cycles),
                        placeholder = stringResource(id = R.string.delay_between_cycles),
                        disabled = false,
                        keyboardOptions = KeyboardOptions.Default.copy(
                            keyboardType = KeyboardType.Text,
                            imeAction = ImeAction.Done
                        ),
                        trailingUnit = "sec",
                        initialValue = viewModel.irrigationSettingGeneralParameter.value?.delayBetweenCycle ?: "",
                        onTextChanged = {
                            viewModel.irrigationSettingGeneralParameter.value?.delayBetweenCycle = it
                        }
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    CheckBoxWithTitle(
                        field = stringResource(id = R.string.start_mode),
                        items = mutableListOf(
                            Pair(stringResource(id = R.string.selected_days),"0"),
                            Pair(stringResource(id = R.string.skipped_days),"1"),
                        ),
                        modifier = Modifier,
                        selectedValue = viewModel.irrigationSettingGeneralParameter.value?.startMode ?: "",
                        onChecked = {
                            startMode = it
                            viewModel.irrigationSettingGeneralParameter.value?.startMode = it
                        }
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        stringResource(id = R.string.biweekly_calendar),
                        fontFamily = Manrope,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 14.sp,
                        color = Black,
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Row() {
                        VerticalMultipleCheckWithTitle(
                            field = stringResource(id = R.string.first_week),
                            items = mutableListOf(
                                Pair(stringResource(id = R.string.monday), viewModel.irrigationSettingGeneralParameter.value?.biweeklyCalendar?.get(0) == '1'),
                                Pair(stringResource(id = R.string.tuesday), viewModel.irrigationSettingGeneralParameter.value?.biweeklyCalendar?.getOrNull(1) == '1'),
                                Pair(stringResource(id = R.string.wednesday), viewModel.irrigationSettingGeneralParameter.value?.biweeklyCalendar?.getOrNull(2) == '1'),
                                Pair(stringResource(id = R.string.thursday), viewModel.irrigationSettingGeneralParameter.value?.biweeklyCalendar?.getOrNull(3) == '1'),
                                Pair(stringResource(id = R.string.friday), viewModel.irrigationSettingGeneralParameter.value?.biweeklyCalendar?.getOrNull(4) == '1'),
                                Pair(stringResource(id = R.string.saturday), viewModel.irrigationSettingGeneralParameter.value?.biweeklyCalendar?.getOrNull(5) == '1'),
                                Pair(stringResource(id = R.string.sunday), viewModel.irrigationSettingGeneralParameter.value?.biweeklyCalendar?.getOrNull(6) == '1'),
                            ),
                            modifier = Modifier.weight(1f),
                            onChecked = {index, data ->
                                val charArray = viewModel.irrigationSettingGeneralParameter.value?.biweeklyCalendar?.toCharArray()
                                charArray!![index] = if(data.second) '1' else '0'
                                viewModel.irrigationSettingGeneralParameter.value?.biweeklyCalendar = String(charArray)
                            },
                            disableAll = startMode == "1",
                            columnCount = 1,
                        )

                        Spacer(modifier = Modifier.width(4.dp))

                        VerticalMultipleCheckWithTitle(
                            field = stringResource(id = R.string.second_week),
                            items = mutableListOf(
                                Pair(stringResource(id = R.string.monday), viewModel.irrigationSettingGeneralParameter.value?.biweeklyCalendar?.getOrNull(7) == '1'),
                                Pair(stringResource(id = R.string.tuesday), viewModel.irrigationSettingGeneralParameter.value?.biweeklyCalendar?.getOrNull(8) == '1'),
                                Pair(stringResource(id = R.string.wednesday), viewModel.irrigationSettingGeneralParameter.value?.biweeklyCalendar?.getOrNull(9) == '1'),
                                Pair(stringResource(id = R.string.thursday), viewModel.irrigationSettingGeneralParameter.value?.biweeklyCalendar?.getOrNull(10) == '1'),
                                Pair(stringResource(id = R.string.friday), viewModel.irrigationSettingGeneralParameter.value?.biweeklyCalendar?.getOrNull(11) == '1'),
                                Pair(stringResource(id = R.string.saturday), viewModel.irrigationSettingGeneralParameter.value?.biweeklyCalendar?.getOrNull(12) == '1'),
                                Pair(stringResource(id = R.string.sunday), viewModel.irrigationSettingGeneralParameter.value?.biweeklyCalendar?.getOrNull(13) == '1'),
                            ),
                            modifier = Modifier.weight(1f),
                            onChecked = {index, data ->
                                val charArray = viewModel.irrigationSettingGeneralParameter.value?.biweeklyCalendar?.toCharArray()
                                charArray!![index + 7] = if(data.second) '1' else '0'
                                viewModel.irrigationSettingGeneralParameter.value?.biweeklyCalendar = String(charArray)
                            },
                            disableAll = startMode == "1",
                            columnCount = 1,
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        stringResource(id = R.string.active_week),
                        fontFamily = Manrope,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 14.sp,
                        color = Black,
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        "${viewModel.irrigationSettingGeneralParameter.value?.activeWeek}°" ?: "",
                        fontFamily = Manrope,
                        fontWeight = FontWeight.Normal,
                        fontSize = 14.sp,
                        color = Black,
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier
                            .fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                    ) {
                        InputWithInitial(
                            modifier = Modifier,
                            field = stringResource(id = R.string.skipped_days),
                            placeholder = stringResource(id = R.string.skipped_days),
                            disabled = startMode == "0",
                            keyboardOptions = KeyboardOptions.Default.copy(
                                keyboardType = KeyboardType.Text,
                                imeAction = ImeAction.Done
                            ),
                            modifierParent = Modifier.weight(1f),
                            trailingUnit = "",
                            initialValue = viewModel.irrigationSettingGeneralParameter.value?.skippedDays ?: "",
                            onTextChanged = {
                                viewModel.irrigationSettingGeneralParameter.value?.skippedDays = it
                            }
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        InputWithInitial(
                            modifier = Modifier,
                            field = stringResource(R.string.days_before_start),
                            placeholder = stringResource(R.string.days_before_start),
                            disabled = startMode == "0",
                            keyboardOptions = KeyboardOptions.Default.copy(
                                keyboardType = KeyboardType.Text,
                                imeAction = ImeAction.Done
                            ),
                            modifierParent = Modifier.weight(1f),
                            trailingUnit = "",
                            initialValue = viewModel.irrigationSettingGeneralParameter.value?.daysBeforeStart ?: "",
                            onTextChanged = {
                                viewModel.irrigationSettingGeneralParameter.value?.daysBeforeStart = it
                            }
                        )
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
                            viewModel.postDataLoading.value = true
                            val result = viewModel.postIrrigationSettingGeneralParameter(deviceCode, viewModel.irrigationSettingGeneralParameter.value!!, programNum)
                            if (result == null) {
                                showToast.value = true
                            }  else {
                                Toast.makeText(context, result, Toast.LENGTH_LONG).show()
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
        CircularProgressIndicator(
            color = Primary,
            strokeCap = StrokeCap.Round,
            strokeWidth = 2.dp,
            modifier = Modifier
                .width(18.dp)
                .height(18.dp)
        )
    }
}

@Composable
fun IrrigationSettingGeneralParameterPage2(deviceCode: String, showToast: MutableState<Boolean>) {
    var programNum by remember { mutableStateOf(1) }

    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val viewModel = hiltViewModel<DeviceViewModel>(key="general-parameter-page2")

    LaunchedEffect(Unit) {
        viewModel.isLoading.value = true

        val result = viewModel.getIrrigationSettingGeneralParameter(deviceCode, programNum)
        viewModel.getIrrigationSettingScheduleStart(deviceCode, programNum, result.first?.cycletime == "0")

        viewModel.isLoading.value = false

    }

    if (!viewModel.isLoading.value) {
        Column {
            LazyColumn(
                modifier = Modifier
                    .padding(horizontal = 24.dp)
                    .weight(1f)
            ) {
                item{
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
                                val result = viewModel.getIrrigationSettingGeneralParameter(deviceCode, it.toInt())
                                viewModel.getIrrigationSettingScheduleStart(deviceCode, it.toInt(), result.first?.cycletime == "0")
                                viewModel.isLoading.value = false
                            }
                        }
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    for ((index, value) in viewModel.irrigationSettingScheduleStart.value.withIndex()) {
                        IrrigationSettingGeneralParameterPage2Card(
                            index,
                            value,
                            viewModel.irrigationSettingGeneralParameter.value!!,
                            onValueChanged = {
                                viewModel._irrigationSettingScheduleStart.value = viewModel._irrigationSettingScheduleStart.value.mapIndexed { idx, item ->
                                    if (idx == index) {
                                        it
                                    } else {
                                        item
                                    }
                                }
                            }
                        )
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
                            viewModel.postDataLoading.value = true
                            val result = viewModel.postIrrigationSettingScheduleStart(deviceCode, viewModel.irrigationSettingScheduleStart.value, programNum, viewModel.irrigationSettingGeneralParameter.value?.cycletime == "0")
                            if (result == null) {
                                showToast.value = true
                            }  else {
                                Toast.makeText(context, result, Toast.LENGTH_LONG).show()
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
    } else {
        CircularProgressIndicator(
            color = Primary,
            strokeCap = StrokeCap.Round,
            strokeWidth = 2.dp,
            modifier = Modifier
                .width(18.dp)
                .height(18.dp)
        )
    }
}

@Composable
fun IrrigationSettingGeneralParameterPage2Card(
    index: Int,
    scheduleStartData: IrrigationSettingScheduleStart,
    generalParamData: IrrigationSettingGeneralParameter,
    onValueChanged: (IrrigationSettingScheduleStart) -> Unit,
    ) {
    var checked by remember { mutableStateOf(scheduleStartData.activate == "1") }
    val startHour = remember { mutableStateOf(scheduleStartData.startHour) }
    val startMinute = remember { mutableStateOf(scheduleStartData.startMinute) }
    val endHour = remember { mutableStateOf(scheduleStartData.endHour) }
    val endMinute = remember { mutableStateOf(scheduleStartData.endMinute) }
    val cycle = remember { mutableStateOf(scheduleStartData.cycle) }

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
                .padding(vertical = 8.dp, horizontal = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Row(
                modifier = Modifier.weight(1f),
            ){
                Column() {
                    Text(
                        stringResource(id = R.string.activate),
                        modifier = Modifier,
                        fontFamily = Manrope,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 14.sp,
                        color = Black,
                    )

                    Row(
                        modifier = Modifier,
                        verticalAlignment = Alignment.CenterVertically
                    ){
                        Text(
                            "${index + 1}",
                            modifier = Modifier,
                            fontFamily = Manrope,
                            fontWeight = FontWeight.Normal,
                            fontSize = 14.sp,
                            color = Black,
                        )

                        Checkbox(
                            checked = checked,
                            onCheckedChange = { isChecked ->
                                checked = isChecked
                                scheduleStartData.activate = if(isChecked) "1" else "0"
                                onValueChanged(scheduleStartData)
                            },
                            modifier = Modifier
                        )
                    }
                }
            }
            
            Column(
                modifier = Modifier.weight(2f),
            ) {
                Text(
                    stringResource(id = R.string.start_time),
                    modifier = Modifier,
                    fontFamily = Manrope,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 14.sp,
                    color = Black,
                )

                Spacer(modifier = Modifier.height(4.dp))

                Row(){
                    Row(
                        modifier = Modifier
                            .weight(1f),
                        verticalAlignment = Alignment.CenterVertically,
                    ){
                        Box(
                            modifier = Modifier
                                .width(60.dp)
                                .background(
                                    if (checked) Color(0xFFC8D4CF) else GrayVeryLight,
                                    shape = RoundedCornerShape(4.dp)
                                )
                        ) {
                            BasicTextField(
                                value = startHour.value ?: "0",
                                onValueChange = {
                                    startHour.value = it
                                    scheduleStartData.startHour = it
                                    onValueChanged(scheduleStartData)
                                },
                                modifier = Modifier
                                    .padding(4.dp)
                                    .fillMaxWidth(),
                                textStyle = TextStyle(
                                    fontSize = 14.sp,
                                    fontFamily = Manrope,
                                    fontWeight = FontWeight.Medium,
                                    textAlign = TextAlign.Center,
                                    color = if (!checked) GrayLight else Black,
                                ),
                                decorationBox = { innerTextField ->
                                    if (startHour.value.isNullOrEmpty()) {
                                        Text(
                                            text = "",
                                            color = Gray,
                                            fontSize = 14.sp,
                                            fontWeight = FontWeight.Medium,
                                            fontFamily = Manrope,
                                            textAlign = TextAlign.Left,
                                        )
                                    }
                                    innerTextField()
                                },
                                singleLine = true,
                                keyboardOptions = KeyboardOptions.Default.copy(
                                    imeAction = ImeAction.Done,
                                    keyboardType = KeyboardType.Number,
                                ),
                                enabled = checked
                            )
                        }

                        Spacer(modifier = Modifier.width(4.dp))

                        Text(
                            stringResource(id = R.string.hour),
                            modifier = Modifier.weight(1f),
                            fontFamily = Manrope,
                            fontWeight = FontWeight.Normal,
                            fontSize = 14.sp,
                            color = Black,
                        )
                    }

                    Row(
                        modifier = Modifier
                            .weight(1f),
                        verticalAlignment = Alignment.CenterVertically,
                    ){
                        Box(
                            modifier = Modifier
                                .width(60.dp)
                                .background(
                                    if (checked) Color(0xFFC8D4CF) else GrayVeryLight,
                                    shape = RoundedCornerShape(4.dp)
                                )
                        ) {
                            BasicTextField(
                                value = startMinute.value ?: "0",
                                onValueChange = {
                                    startMinute.value = it
                                    scheduleStartData.startMinute = it
                                    onValueChanged(scheduleStartData)
                                },
                                modifier = Modifier
                                    .padding(4.dp)
                                    .fillMaxWidth(),
                                textStyle = TextStyle(
                                    fontSize = 14.sp,
                                    fontFamily = Manrope,
                                    fontWeight = FontWeight.Medium,
                                    textAlign = TextAlign.Center,
                                    color = if (!checked) GrayLight else Black,
                                ),
                                decorationBox = { innerTextField ->
                                    if (startMinute.value.isNullOrEmpty()) {
                                        Text(
                                            text = "",
                                            color = Gray,
                                            fontSize = 14.sp,
                                            fontWeight = FontWeight.Medium,
                                            fontFamily = Manrope,
                                            textAlign = TextAlign.Left,
                                        )
                                    }
                                    innerTextField()
                                },
                                singleLine = true,
                                keyboardOptions = KeyboardOptions.Default.copy(
                                    imeAction = ImeAction.Done,
                                    keyboardType = KeyboardType.Number,
                                ),
                                enabled = checked
                            )
                        }

                        Spacer(modifier = Modifier.width(4.dp))

                        Text(
                            stringResource(id = R.string.minute),
                            modifier = Modifier.weight(1f),
                            fontFamily = Manrope,
                            fontWeight = FontWeight.Normal,
                            fontSize = 14.sp,
                            color = Black,
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    if (generalParamData.cycletime == "0") stringResource(id = R.string.end_time) else stringResource(
                        id = R.string.cycles
                    ),
                    modifier = Modifier,
                    fontFamily = Manrope,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 14.sp,
                    color = Black,
                )

                Spacer(modifier = Modifier.height(4.dp))

                if (generalParamData.cycletime == "0") {
                    Row(){
                        Row(
                            modifier = Modifier
                                .weight(1f),
                            verticalAlignment = Alignment.CenterVertically,
                        ){
                            Box(
                                modifier = Modifier
                                    .width(60.dp)
                                    .background(
                                        if (checked) Color(0xFFC8D4CF) else GrayVeryLight,
                                        shape = RoundedCornerShape(4.dp)
                                    )
                            ) {
                                BasicTextField(
                                    value = endHour.value ?: "0",
                                    onValueChange = {
                                        endHour.value = it
                                        scheduleStartData.endHour = it
                                        onValueChanged(scheduleStartData)
                                    },
                                    modifier = Modifier
                                        .padding(4.dp)
                                        .fillMaxWidth(),
                                    textStyle = TextStyle(
                                        fontSize = 14.sp,
                                        fontFamily = Manrope,
                                        fontWeight = FontWeight.Medium,
                                        textAlign = TextAlign.Center,
                                        color = if (!checked) GrayLight else Black,
                                    ),
                                    decorationBox = { innerTextField ->
                                        if (endHour.value.isNullOrEmpty()) {
                                            Text(
                                                text = "",
                                                color = Gray,
                                                fontSize = 14.sp,
                                                fontWeight = FontWeight.Medium,
                                                fontFamily = Manrope,
                                                textAlign = TextAlign.Left,
                                            )
                                        }
                                        innerTextField()
                                    },
                                    singleLine = true,
                                    keyboardOptions = KeyboardOptions.Default.copy(
                                        imeAction = ImeAction.Done,
                                        keyboardType = KeyboardType.Number,
                                    ),
                                    enabled = checked
                                )
                            }

                            Spacer(modifier = Modifier.width(4.dp))

                            Text(
                                stringResource(id = R.string.hour),
                                modifier = Modifier.weight(1f),
                                fontFamily = Manrope,
                                fontWeight = FontWeight.Normal,
                                fontSize = 14.sp,
                                color = Black,
                            )
                        }

                        Row(
                            modifier = Modifier
                                .weight(1f),
                            verticalAlignment = Alignment.CenterVertically,
                        ){
                            Box(
                                modifier = Modifier
                                    .width(60.dp)
                                    .background(
                                        if (checked) Color(0xFFC8D4CF) else GrayVeryLight,
                                        shape = RoundedCornerShape(4.dp)
                                    )
                            ) {
                                BasicTextField(
                                    value = endMinute.value ?: "0",
                                    onValueChange = {
                                        endMinute.value = it
                                        scheduleStartData.endMinute = it
                                        onValueChanged(scheduleStartData)
                                    },
                                    modifier = Modifier
                                        .padding(4.dp)
                                        .fillMaxWidth(),
                                    textStyle = TextStyle(
                                        fontSize = 14.sp,
                                        fontFamily = Manrope,
                                        fontWeight = FontWeight.Medium,
                                        textAlign = TextAlign.Center,
                                        color = if (!checked) GrayLight else Black,
                                    ),
                                    decorationBox = { innerTextField ->
                                        if (endMinute.value.isNullOrEmpty()) {
                                            Text(
                                                text = "",
                                                color = Gray,
                                                fontSize = 14.sp,
                                                fontWeight = FontWeight.Medium,
                                                fontFamily = Manrope,
                                                textAlign = TextAlign.Left,
                                            )
                                        }
                                        innerTextField()
                                    },
                                    singleLine = true,
                                    keyboardOptions = KeyboardOptions.Default.copy(
                                        imeAction = ImeAction.Done,
                                        keyboardType = KeyboardType.Number,
                                    ),
                                    enabled = checked
                                )
                            }

                            Spacer(modifier = Modifier.width(4.dp))

                            Text(
                                stringResource(id = R.string.minute),
                                modifier = Modifier.weight(1f),
                                fontFamily = Manrope,
                                fontWeight = FontWeight.Normal,
                                fontSize = 14.sp,
                                color = Black,
                            )
                        }
                    }
                }
                else {
                    Row(
                        modifier = Modifier,
                        verticalAlignment = Alignment.CenterVertically,
                    ){
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .background(
                                    if (checked) Color(0xFFC8D4CF) else GrayVeryLight,
                                    shape = RoundedCornerShape(4.dp)
                                )
                        ) {
                            BasicTextField(
                                value = cycle.value ?: "",
                                onValueChange = {
                                    cycle.value = it
                                    scheduleStartData.cycle = it
                                    onValueChanged(scheduleStartData)
                                },
                                modifier = Modifier
                                    .padding(4.dp)
                                    .fillMaxWidth(),
                                textStyle = TextStyle(
                                    fontSize = 14.sp,
                                    fontFamily = Manrope,
                                    fontWeight = FontWeight.Medium,
                                    textAlign = TextAlign.Center,
                                    color = if (!checked) GrayLight else Black,
                                ),
                                decorationBox = { innerTextField ->
                                    if (cycle.value.isNullOrEmpty()) {
                                        Text(
                                            text = "",
                                            color = Gray,
                                            fontSize = 14.sp,
                                            fontWeight = FontWeight.Medium,
                                            fontFamily = Manrope,
                                            textAlign = TextAlign.Left,
                                        )
                                    }
                                    innerTextField()
                                },
                                singleLine = true,
                                keyboardOptions = KeyboardOptions.Default.copy(
                                    imeAction = ImeAction.Done,
                                    keyboardType = KeyboardType.Number,
                                ),
                                enabled = checked
                            )
                        }

                        Spacer(modifier = Modifier.width(4.dp))

                        Text(
                            stringResource(id = R.string.cycles),
                            modifier = Modifier,
                            fontFamily = Manrope,
                            fontWeight = FontWeight.Normal,
                            fontSize = 14.sp,
                            color = Black,
                        )
                    }
                }
            }
        }
    }

    Spacer(modifier = Modifier.height(12.dp))
}

@Composable
fun IrrigationSettingGeneralParameterPage3(deviceCode: String, showToast: MutableState<Boolean>) {
    var programNum by remember { mutableStateOf(1) }
    val selectedTesi = remember { mutableStateOf(Pair("","")) }

    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val viewModel = hiltViewModel<DeviceViewModel>(key="general-parameter-page3")

    val groupData = remember { mutableStateOf(mutableListOf(Pair("",""))) }
    val groupDataCard = remember { mutableStateOf(mutableListOf(Pair("",""))) }

    fun filterGroupData () {
        val tempGroupDataCard: MutableList<Pair<String, String>> = mutableListOf()

        for (card in viewModel.stationDurationData.value) {
            val station = viewModel.evStationName.value.find {
                it.first == card.group
            }
            if (station != null) {
                val dataCard = Pair(station.second, station.first)
                tempGroupDataCard.add(dataCard)
            }
        }
        groupData.value = viewModel.availableGroup.value.toMutableList()
        if (groupData.value.isNotEmpty()) {
            selectedTesi.value = Pair(groupData.value[0].first, groupData.value[0].second)
        }
        groupDataCard.value = tempGroupDataCard
    }

    LaunchedEffect(Unit) {
        viewModel.isLoading.value = true

        viewModel.getIrrigationConfigEVConfigList(deviceCode)
        viewModel.getStationDuration(deviceCode, programNum)

        filterGroupData()

        viewModel.isLoading.value = false

    }

    if (!viewModel.isLoading.value) {
        Column {
            LazyColumn(
                modifier = Modifier
                    .padding(horizontal = 24.dp)
                    .weight(1f)
            ) {
                item{
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
                                viewModel.getIrrigationConfigEVConfigList(deviceCode)
                                viewModel.getStationDuration(deviceCode, it.toInt())

                                filterGroupData()
                                viewModel.isLoading.value = false
                            }
                        }
                    )

                    Spacer(modifier = Modifier.height(24.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Text(
                            text = "${stringResource(id = R.string.status)}: ${stringResource(id = R.string.on)}",
                            color = Black,
                            fontSize = 14.sp,
                            fontFamily = Manrope,
                            fontWeight = FontWeight.SemiBold,
                        )

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            DropDownSmall(
                                field = null,
                                groupData.value,
                                Modifier.weight(1f),
                                selectedValue = selectedTesi.value.second,
                                onSelectItem = { index, key, it ->
                                    selectedTesi.value = Pair(key, it)
                                },
                                textAlign = null,
                            )
                            
                            Spacer(modifier = Modifier.width(8.dp))

                            Button(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(4.dp)),
                                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                                onClick = {
                                    if (viewModel.stationDurationData.value.size < 96) {
                                        val data = viewModel.stationDurationData.value.toMutableList()
                                        val emptyData = viewModel.stationDurationDataAll.value.find {
                                            it.group == selectedTesi.value.second
                                        }
                                        if (emptyData != null) {
                                            data.add(
                                                emptyData,
                                            )
                                            viewModel._stationDurationData.value = data
                                        }

                                        //add new group data card
                                        val temp = groupDataCard.value
                                        temp.add(Pair(selectedTesi.value.first, selectedTesi.value.second))
                                        groupDataCard.value = temp
                                    }
                                },
                                colors = ButtonDefaults.buttonColors(backgroundColor = White,),
                                border = BorderStroke(1.dp, Primary),
                            ) {
                                Text(stringResource(id = R.string.add_group), style = MaterialTheme.typography.button, fontSize = 12.sp, color = Primary)
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                }

               itemsIndexed(viewModel.stationDurationData.value) {index, value ->
                   IrrigationSettingGeneralParameterPage3Card(
                       value,
                       viewModel,
                       index,
                       groupDataCard.value,
                   )
               }

                item {
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
                            viewModel.postDataLoading.value = true
                            val result = viewModel.postStationDuration(deviceCode, programNum)
                            if (result == null) {
                                showToast.value = true
                            }  else {
                                Toast.makeText(context, result, Toast.LENGTH_LONG).show()
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
    } else {
        CircularProgressIndicator(
            color = Primary,
            strokeCap = StrokeCap.Round,
            strokeWidth = 2.dp,
            modifier = Modifier
                .width(18.dp)
                .height(18.dp)
        )
    }
}

@Composable
fun IrrigationSettingGeneralParameterPage3Card(
    data: StationDuration,
    viewModel: DeviceViewModel,
    index: Int,
    groupItem: MutableList<Pair<String, String>>
) {
    val groupItemData = remember { mutableStateOf(groupItem) }

    val edVolume = remember { mutableStateOf(data.volume) }
    val edSecond = remember { mutableStateOf(data.second) }
    val edMinute = remember { mutableStateOf(data.minute) }
    val edHour = remember { mutableStateOf(data.hour) }

    LaunchedEffect(data) {
        edVolume.value = data.volume
        edSecond.value = data.second
        edMinute.value = data.minute
        edHour.value = data.hour
    }

    fun swapCard(fromIndex: Int, toIndex: Int) {
        val currentData = viewModel._stationDurationData.value.toMutableList()

        val groupItemDataCurrent = groupItemData.value

        viewModel.setMarkerLoading.value = true

        val swapData = currentData[toIndex]
        currentData[fromIndex] = swapData
        currentData[toIndex] = data

        viewModel._stationDurationData.value = currentData

        val swapDataGroup = groupItemDataCurrent[toIndex]
        groupItemDataCurrent[toIndex] = groupItemDataCurrent[fromIndex]
        groupItemDataCurrent[fromIndex] = swapDataGroup
        groupItemData.value = groupItemDataCurrent

        Handler(Looper.getMainLooper()).postDelayed({
            viewModel.setMarkerLoading.value = false
        }, 50)
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
                .fillMaxWidth(),
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp, horizontal = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Column(
                    modifier = Modifier.weight(1f),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Text(
                        stringResource(id = R.string.shift),
                        style = TextStyle(
                            fontSize = 14.sp,
                            fontFamily = Manrope,
                            fontWeight = FontWeight.Normal,
                            textAlign = TextAlign.Center,
                            color = GrayLight,
                        ),
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Row {
                        IconButton(
                            onClick = {
                                val currentIndex = viewModel.stationDurationData.value.indexOf(data)

                                if (currentIndex != 0) {
                                    swapCard(currentIndex, currentIndex - 1)
                                }
                            },
                            modifier = Modifier
                                .size(36.dp)
                        ) {
                            Image(
                                modifier = Modifier
                                    .size(30.dp),
                                bitmap = ImageBitmap.imageResource(R.drawable.ic_button_arrow_up_green),
                                contentDescription = "back button"
                            )
                        }
                        IconButton(
                            onClick = {
                                val currentIndex = viewModel.stationDurationData.value.indexOf(data)

                                if (currentIndex != viewModel._stationDurationData.value.size - 1) {
                                    swapCard(currentIndex, currentIndex + 1)
                                }
                            },
                            modifier = Modifier
                                .size(36.dp)
                        ) {
                            Image(
                                modifier = Modifier
                                    .size(30.dp),
                                bitmap = ImageBitmap.imageResource(R.drawable.ic_button_arrow_down_green),
                                contentDescription = "back button"
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.width(4.dp))
                Column(
                    modifier = Modifier.weight(1f),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Text(
                        stringResource(id = R.string.step_number),
                        style = TextStyle(
                            fontSize = 14.sp,
                            fontFamily = Manrope,
                            fontWeight = FontWeight.Normal,
                            textAlign = TextAlign.Center,
                            color = GrayLight,
                        ),
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        "${index + 1}",
                        style = TextStyle(
                            fontSize = 14.sp,
                            fontFamily = Manrope,
                            fontWeight = FontWeight.Normal,
                            textAlign = TextAlign.Center,
                            color = Black,
                        ),
                    )
                }
                Spacer(modifier = Modifier.width(4.dp))
                Column(
                    modifier = Modifier.weight(1f),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Text(
                        stringResource(id = R.string.station),
                        style = TextStyle(
                            fontSize = 14.sp,
                            fontFamily = Manrope,
                            fontWeight = FontWeight.Normal,
                            textAlign = TextAlign.Center,
                            color = GrayLight,
                        ),
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        data.station,
                        style = TextStyle(
                            fontSize = 14.sp,
                            fontFamily = Manrope,
                            fontWeight = FontWeight.Normal,
                            textAlign = TextAlign.Center,
                            color = Black,
                        ),
                    )
                }
                Spacer(modifier = Modifier.width(4.dp))
                Column(
                    modifier = Modifier.weight(1f),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Text(
                        stringResource(id = R.string.group),
                        style = TextStyle(
                            fontSize = 14.sp,
                            fontFamily = Manrope,
                            fontWeight = FontWeight.Normal,
                            textAlign = TextAlign.Center,
                            color = GrayLight,
                        ),
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(width = 1.dp, color = Black, shape = RoundedCornerShape(8.dp))
                            .padding(4.dp),
                    ) {
                        if (viewModel.setMarkerLoading.value) {
                            Spacer(modifier = Modifier
                                .fillMaxWidth()
                                .height(25.dp))
                        } else {
                            DropDownSmall(
                                field = null,
                                groupItemData.value,
                                Modifier.fillMaxWidth(),
                                selectedValue = data.group,
                                onSelectItem = { index, _, it ->
                                    val currentIndex = viewModel.stationDurationData.value.indexOf(data)
                                    swapCard(currentIndex, index)
                                },
                                textAlign = TextAlign.Center,
                            )
                        }
                    }
                }
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(GrayVeryLight)
                    .padding(vertical = 8.dp, horizontal = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Column(
                    modifier = Modifier.weight(1f),
                ) {
                    Text(
                        "E.V",
                        style = TextStyle(
                            fontSize = 14.sp,
                            fontFamily = Manrope,
                            fontWeight = FontWeight.Normal,
                            textAlign = TextAlign.Center,
                            color = GrayLight,
                        ),
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        data.ev,
                        style = TextStyle(
                            fontSize = 18.sp,
                            fontFamily = Manrope,
                            fontWeight = FontWeight.SemiBold,
                            textAlign = TextAlign.Start,
                            color = Black,
                        ),
                    )
                }

                Spacer(modifier = Modifier.width(8.dp))

                Column(
                    modifier = Modifier.weight(1f),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Text(
                        stringResource(id = R.string.status),
                        style = TextStyle(
                            fontSize = 14.sp,
                            fontFamily = Manrope,
                            fontWeight = FontWeight.Normal,
                            textAlign = TextAlign.Center,
                            color = GrayLight,
                        ),
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(width = 1.dp, color = Black, shape = RoundedCornerShape(8.dp))
                            .padding(4.dp),
                    ) {
                        if (viewModel.setMarkerLoading.value) {
                            Spacer(modifier = Modifier
                                .fillMaxWidth()
                                .height(25.dp))
                        } else {
                            DropDownSmall(
                                field = null,
                                mutableListOf(
                                    Pair("ON","1"),
                                    Pair("OFF","0"),
                                ),
                                Modifier.fillMaxWidth(),
                                selectedValue = data.status,
                                onSelectItem = { index, _, it ->
                                    data.status = it

                                    val currentIndex = viewModel.stationDurationData.value.indexOf(data)
                                    viewModel._stationDurationData.value[currentIndex].status = it
                                },
                                textAlign = TextAlign.Center,
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.width(8.dp))

                Row(
                    modifier = Modifier.weight(1f),
                ) {
                    if (data.flowMode == "0") {
                        Column(
                            modifier = Modifier.weight(1f),
                            horizontalAlignment = Alignment.CenterHorizontally,
                        ) {
                            Text(
                                "Volume",
                                style = TextStyle(
                                    fontSize = 14.sp,
                                    fontFamily = Manrope,
                                    fontWeight = FontWeight.Normal,
                                    textAlign = TextAlign.Center,
                                    color = GrayLight,
                                ),
                            )
                            Spacer(modifier = Modifier.height(4.dp))

                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(
                                        Color(0xFFC8D4CF),
                                        shape = RoundedCornerShape(4.dp)
                                    )
                            ) {
                                BasicTextField(
                                    value = edVolume.value,
                                    onValueChange = {
                                        edVolume.value = it
                                        data.volume = it
//                                        val currentIndex = viewModel.stationDurationData.value.indexOf(data)
//                                        viewModel._stationDurationData.value[currentIndex].volume = it
                                    },
                                    modifier = Modifier
                                        .padding(4.dp)
                                        .fillMaxWidth(),
                                    textStyle = TextStyle(
                                        fontSize = 14.sp,
                                        fontFamily = Manrope,
                                        fontWeight = FontWeight.Medium,
                                        textAlign = TextAlign.Center,
                                        color = Black,
                                    ),
                                    decorationBox = { innerTextField ->
                                        innerTextField()
                                    },
                                    singleLine = true,
                                    keyboardOptions = KeyboardOptions.Default.copy(
                                        imeAction = ImeAction.Done,
                                        keyboardType = KeyboardType.Number,
                                    ),
                                )
                            }
                        }
                    } else {
                        Column(
                            modifier = Modifier.weight(1f),
                            horizontalAlignment = Alignment.CenterHorizontally,
                        ) {
                            Text(
                                if(data.flowMode == "1") stringResource(id = R.string.minute) else stringResource(id = R.string.hour),
                                style = TextStyle(
                                    fontSize = 14.sp,
                                    fontFamily = Manrope,
                                    fontWeight = FontWeight.Normal,
                                    textAlign = TextAlign.Center,
                                    color = GrayLight,
                                ),
                            )
                            Spacer(modifier = Modifier.height(4.dp))

                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(
                                        Color(0xFFC8D4CF),
                                        shape = RoundedCornerShape(4.dp)
                                    )
                            ) {
                                BasicTextField(
                                    value = if(data.flowMode == "1") edMinute.value else edHour.value,
                                    onValueChange = {
//                                        val currentIndex = viewModel.stationDurationData.value.indexOf(data)
                                        if (data.flowMode == "1") {
                                            edMinute.value = it
                                            data.minute = it
//                                            viewModel._stationDurationData.value[currentIndex].minute = it
                                        } else {
                                            edHour.value = it
                                            data.hour = it
//                                            viewModel._stationDurationData.value[currentIndex].hour = it
                                        }
                                    },
                                    modifier = Modifier
                                        .padding(4.dp)
                                        .fillMaxWidth(),
                                    textStyle = TextStyle(
                                        fontSize = 14.sp,
                                        fontFamily = Manrope,
                                        fontWeight = FontWeight.Medium,
                                        textAlign = TextAlign.Center,
                                        color = Black,
                                    ),
                                    decorationBox = { innerTextField ->
                                        innerTextField()
                                    },
                                    singleLine = true,
                                    keyboardOptions = KeyboardOptions.Default.copy(
                                        imeAction = ImeAction.Done,
                                        keyboardType = KeyboardType.Number,
                                    ),
                                )
                            }
                        }
                        Spacer(modifier = Modifier.width(4.dp))
                        Column(
                            modifier = Modifier.weight(1f),
                            horizontalAlignment = Alignment.CenterHorizontally,
                        ) {
                            Text(
                                if(data.flowMode == "1") stringResource(id = R.string.second) else stringResource(id = R.string.minute),
                                style = TextStyle(
                                    fontSize = 14.sp,
                                    fontFamily = Manrope,
                                    fontWeight = FontWeight.Normal,
                                    textAlign = TextAlign.Center,
                                    color = GrayLight,
                                ),
                            )
                            Spacer(modifier = Modifier.height(4.dp))

                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(
                                        Color(0xFFC8D4CF),
                                        shape = RoundedCornerShape(4.dp)
                                    )
                            ) {
                                BasicTextField(
                                    value = if(data.flowMode == "1") edSecond.value else edMinute.value,
                                    onValueChange = {
//                                        val currentIndex = viewModel.stationDurationData.value.indexOf(data)
                                        if (data.flowMode == "1") {
                                            edSecond.value = it
                                            data.second = it
//                                            viewModel._stationDurationData.value[currentIndex].second = it
                                        } else {
                                            edMinute.value = it
                                            data.minute = it
//                                            viewModel._stationDurationData.value[currentIndex].minute = it
                                        }
                                    },
                                    modifier = Modifier
                                        .padding(4.dp)
                                        .fillMaxWidth(),
                                    textStyle = TextStyle(
                                        fontSize = 14.sp,
                                        fontFamily = Manrope,
                                        fontWeight = FontWeight.Medium,
                                        textAlign = TextAlign.Center,
                                        color = Black,
                                    ),
                                    decorationBox = { innerTextField ->
                                        innerTextField()
                                    },
                                    singleLine = true,
                                    keyboardOptions = KeyboardOptions.Default.copy(
                                        imeAction = ImeAction.Done,
                                        keyboardType = KeyboardType.Number,
                                    ),
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.width(8.dp))

                Column(
                    modifier = Modifier,
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Text(
                        "",
                        style = TextStyle(
                            fontSize = 14.sp,
                            fontFamily = Manrope,
                            fontWeight = FontWeight.Normal,
                            textAlign = TextAlign.Center,
                            color = GrayLight,
                        ),
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    IconButton(
                        onClick = {
                            viewModel.setMarkerLoading.value = true
                            val currentIndex = viewModel.stationDurationData.value.indexOf(data)
                            val groupItemDataCurrent = groupItemData.value
                            groupItemDataCurrent.remove(groupItemDataCurrent[currentIndex])
                            groupItemData.value = groupItemDataCurrent

                            val datax = viewModel.stationDurationData.value.toMutableList()
                            datax.remove(data)
                            viewModel._stationDurationData.value = datax

                            Handler(Looper.getMainLooper()).postDelayed({
                                viewModel.setMarkerLoading.value = false
                            }, 50)
                        },
                        modifier = Modifier
                            .size(40.dp)
                    ) {
                        Image(
                            modifier = Modifier
                                .height(32.dp)
                                .width(40.dp),
                            bitmap = ImageBitmap.imageResource(R.drawable.ic_button_delete_red),
                            contentDescription = "back button",
                            contentScale = ContentScale.Fit,
                        )
                    }
                }
            }
        }
    }

    Spacer(modifier = Modifier.height(12.dp))
}