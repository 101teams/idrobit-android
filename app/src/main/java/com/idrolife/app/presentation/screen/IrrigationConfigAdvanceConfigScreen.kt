package com.idrolife.app.presentation.screen

import android.app.Activity
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
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
import com.idrolife.app.presentation.component.InputWithInitial
import com.idrolife.app.presentation.component.NavigationBanner3
import com.idrolife.app.presentation.viewmodel.DeviceViewModel
import com.idrolife.app.theme.BrokenWhite
import com.idrolife.app.theme.Green
import com.idrolife.app.theme.White
import com.idrolife.app.utils.Helper
import kotlinx.coroutines.launch

@Composable
fun IrrigationConfigAdvanceConfigScreen(
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

        viewModel.getIrrigationConfigAdvanceConfig(viewModel.selectedDevice.value?.code ?: "")
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
            stringResource(id = R.string.advanced_configuration),
            R.drawable.img_header_detail3,
            viewModel.selectedDevice.value,
            viewModel.isLoading.value,
        )

        if (viewModel.isLoading.value) {
            Box(
                modifier = Modifier.fillMaxSize(),
            ) {
                CircularProgressIndicator(
                    color = Green,
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
                item {
                    Spacer(modifier = Modifier.height(24.dp))

                    InputWithInitial(
                        modifier = Modifier,
                        field = stringResource(id = R.string.opened_circuit_ampere),
                        placeholder = stringResource(id = R.string.opened_circuit_ampere),
                        disabled = false,
                        keyboardOptions = KeyboardOptions.Default.copy(
                            keyboardType = KeyboardType.Number,
                            imeAction = ImeAction.Next
                        ),
                        modifierParent = Modifier.weight(1f),
                        trailingUnit = "mA",
                        initialValue = viewModel.irrigationConfigAdvanceConfig.value?.openedCircuit ?: "",
                        onTextChanged = {
                            viewModel.irrigationConfigAdvanceConfig.value?.openedCircuit = it
                        }
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    InputWithInitial(
                        modifier = Modifier,
                        field = stringResource(id = R.string.acknowledge_pulse_time),
                        placeholder = stringResource(id = R.string.acknowledge_pulse_time),
                        disabled = false,
                        keyboardOptions = KeyboardOptions.Default.copy(
                            keyboardType = KeyboardType.Number,
                            imeAction = ImeAction.Next
                        ),
                        modifierParent = Modifier.weight(1f),
                        trailingUnit = "ms",
                        initialValue = viewModel.irrigationConfigAdvanceConfig.value?.acknowledgePulseTime ?: "",
                        onTextChanged = {
                            viewModel.irrigationConfigAdvanceConfig.value?.acknowledgePulseTime = it
                        }
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    InputWithInitial(
                        modifier = Modifier,
                        field = stringResource(id = R.string.minimum_ampere_threshold_in_self_search),
                        placeholder = stringResource(id = R.string.minimum_ampere_threshold_in_self_search),
                        disabled = false,
                        keyboardOptions = KeyboardOptions.Default.copy(
                            keyboardType = KeyboardType.Number,
                            imeAction = ImeAction.Next
                        ),
                        modifierParent = Modifier.weight(1f),
                        trailingUnit = "mA",
                        initialValue = viewModel.irrigationConfigAdvanceConfig.value?.minimumAmpere ?: "",
                        onTextChanged = {
                            viewModel.irrigationConfigAdvanceConfig.value?.minimumAmpere = it
                        }
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    InputWithInitial(
                        modifier = Modifier,
                        field = stringResource(id = R.string.activation_delay_between_master_ev_and_first_ev),
                        placeholder = stringResource(id = R.string.activation_delay_between_master_ev_and_first_ev),
                        disabled = false,
                        keyboardOptions = KeyboardOptions.Default.copy(
                            keyboardType = KeyboardType.Number,
                            imeAction = ImeAction.Next
                        ),
                        modifierParent = Modifier.weight(1f),
                        trailingUnit = "ms",
                        initialValue = viewModel.irrigationConfigAdvanceConfig.value?.activationDelayMaster ?: "",
                        onTextChanged = {
                            viewModel.irrigationConfigAdvanceConfig.value?.activationDelayMaster = it
                        }
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    InputWithInitial(
                        modifier = Modifier,
                        field = stringResource(id = R.string.activation_delay_between_two_evs),
                        placeholder = stringResource(id = R.string.activation_delay_between_two_evs),
                        disabled = false,
                        keyboardOptions = KeyboardOptions.Default.copy(
                            keyboardType = KeyboardType.Number,
                            imeAction = ImeAction.Next
                        ),
                        modifierParent = Modifier.weight(1f),
                        trailingUnit = "ms",
                        initialValue = viewModel.irrigationConfigAdvanceConfig.value?.activationDelayEV ?: "",
                        onTextChanged = {
                            viewModel.irrigationConfigAdvanceConfig.value?.activationDelayEV = it
                        }
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    InputWithInitial(
                        modifier = Modifier,
                        field = stringResource(id = R.string.ev_holding_voltage),
                        placeholder = stringResource(id = R.string.ev_holding_voltage),
                        disabled = false,
                        keyboardOptions = KeyboardOptions.Default.copy(
                            keyboardType = KeyboardType.Number,
                            imeAction = ImeAction.Next
                        ),
                        modifierParent = Modifier.weight(1f),
                        trailingUnit = "V",
                        initialValue = viewModel.irrigationConfigAdvanceConfig.value?.evHoldingVoltage ?: "",
                        onTextChanged = {
                            viewModel.irrigationConfigAdvanceConfig.value?.evHoldingVoltage = it
                        }
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    InputWithInitial(
                        modifier = Modifier,
                        field = stringResource(id = R.string.trigger_pulse_time),
                        placeholder = stringResource(id = R.string.trigger_pulse_time),
                        disabled = false,
                        keyboardOptions = KeyboardOptions.Default.copy(
                            keyboardType = KeyboardType.Number,
                            imeAction = ImeAction.Done
                        ),
                        modifierParent = Modifier.weight(1f),
                        trailingUnit = "ms",
                        initialValue = viewModel.irrigationConfigAdvanceConfig.value?.triggerPulseTime ?: "",
                        onTextChanged = {
                            viewModel.irrigationConfigAdvanceConfig.value?.triggerPulseTime = it
                        }
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
                          if (viewModel.irrigationConfigAdvanceConfig.value != null) {
                              viewModel.postDataLoading.value = true
                              viewModel.postIrrigationConfigAdvanceConfig(
                                  deviceCode = viewModel.selectedDevice.value?.code ?: "",
                                  postData = viewModel.irrigationConfigAdvanceConfig.value!!
                              )
                              viewModel.postDataLoading.value = false
                          }
                      }
                },
                colors = ButtonDefaults.buttonColors(backgroundColor = Green),
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
