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
import com.idrolife.app.presentation.component.CheckBoxWithTitle
import com.idrolife.app.presentation.component.DropDown
import com.idrolife.app.presentation.component.InputWithInitial
import com.idrolife.app.presentation.component.NavigationBanner3
import com.idrolife.app.presentation.viewmodel.DeviceViewModel
import com.idrolife.app.theme.BrokenWhite
import com.idrolife.app.theme.Primary
import com.idrolife.app.theme.Primary2
import com.idrolife.app.theme.White
import com.idrolife.app.utils.Helper
import kotlinx.coroutines.launch

@Composable
fun FertigationProgrammationECSettingScreen(
    navController: NavController,
    deviceID: String,
    deviceCode: String
) {
    val context = LocalContext.current

    val window = (context as Activity).window
    val view = LocalView.current

    val viewModel = hiltViewModel<DeviceViewModel>()

    val scope = rememberCoroutineScope()

    val selectedProgramNum = remember { mutableStateOf("1") }

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

        val programNum = selectedProgramNum.value.toIntOrNull() ?: 0
        viewModel.getFertigationProgrammation(deviceCode, programNum)

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
            stringResource(id = R.string.fertigation_ec_settings),
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
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(horizontal = 24.dp)
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
                        ),
                        Modifier.fillMaxWidth(),
                        selectedValue = selectedProgramNum.value,
                        onSelectItem = { _, it ->
                            scope.launch {
                                selectedProgramNum.value = it
                                viewModel.isLoading.value = true
                                val programNum = it.toIntOrNull() ?: 0
                                viewModel.getFertigationProgrammation(deviceCode, programNum)
                                viewModel.isLoading.value = false
                            }
                        }
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    InputWithInitial(
                        modifier = Modifier,
                        field = stringResource(id = R.string.hysteresis),
                        placeholder = stringResource(id = R.string.hysteresis),
                        disabled = false,
                        keyboardOptions = KeyboardOptions.Default.copy(
                            keyboardType = KeyboardType.Number,
                            imeAction = ImeAction.Done
                        ),
                        modifierParent = Modifier,
                        initialValue = viewModel.fertigationProgrammation.value?.hysteresis ?: "",
                        onTextChanged = {
                            viewModel.fertigationProgrammation.value?.hysteresis = it
                        },
                        trailingUnit = "uS"
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        verticalAlignment = Alignment.Bottom,
                    ) {
                        InputWithInitial(
                            modifier = Modifier,
                            field = stringResource(id = R.string.check_every),
                            placeholder = stringResource(id = R.string.check_every),
                            disabled = false,
                            keyboardOptions = KeyboardOptions.Default.copy(
                                keyboardType = KeyboardType.Number,
                                imeAction = ImeAction.Done
                            ),
                            modifierParent = Modifier
                                .weight(1f),
                            initialValue = viewModel.fertigationProgrammation.value?.checkEvery ?: "",
                            onTextChanged = {
                                viewModel.fertigationProgrammation.value?.checkEvery = it
                            },
                            trailingUnit = "uS",
                        )
                        CheckBoxWithTitle(
                            field = null,
                            items = mutableListOf(
                                Pair(stringResource(id = R.string.second),"0"),
                                Pair(stringResource(id = R.string.cycles),"1"),
                            ),
                            modifier = Modifier,
                            selectedValue = viewModel.fertigationProgrammation.value?.checkEveryType ?: "",
                            onChecked = {
                                viewModel.fertigationProgrammation.value?.checkEveryType = it
                            }
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    InputWithInitial(
                        modifier = Modifier,
                        field = stringResource(id = R.string.setpoint_ec),
                        placeholder = stringResource(id = R.string.setpoint_ec),
                        disabled = false,
                        keyboardOptions = KeyboardOptions.Default.copy(
                            keyboardType = KeyboardType.Number,
                            imeAction = ImeAction.Done
                        ),
                        modifierParent = Modifier,
                        initialValue = viewModel.fertigationProgrammation.value?.setpointEC ?: "",
                        onTextChanged = {
                            viewModel.fertigationProgrammation.value?.setpointEC = it
                        },
                        trailingUnit = "uS"
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    InputWithInitial(
                        modifier = Modifier,
                        field = stringResource(id = R.string.setpoint_ph),
                        placeholder = stringResource(id = R.string.setpoint_ph),
                        disabled = false,
                        keyboardOptions = KeyboardOptions.Default.copy(
                            keyboardType = KeyboardType.Number,
                            imeAction = ImeAction.Done
                        ),
                        modifierParent = Modifier,
                        initialValue = viewModel.fertigationProgrammation.value?.setpointPh ?: "",
                        onTextChanged = {
                            viewModel.fertigationProgrammation.value?.setpointPh = it
                        },
                        trailingUnit = "Ph"
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    CheckBoxWithTitle(
                        field = stringResource(id = R.string.dosage_ph),
                        items = mutableListOf(
                            Pair(stringResource(id = R.string.ph_basic),"0"),
                            Pair(stringResource(id = R.string.ph_acid),"1"),
                        ),
                        modifier = Modifier,
                        selectedValue = viewModel.fertigationProgrammation.value?.dosagePh ?: "",
                        onChecked = {
                            viewModel.fertigationProgrammation.value?.dosagePh = it
                        }
                    )

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
                              val selectedProgram = selectedProgramNum.value.toIntOrNull() ?: 0
                              viewModel.postFertigationProgrammation(deviceCode, selectedProgram, viewModel.fertigationProgrammation.value!!)
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
}
