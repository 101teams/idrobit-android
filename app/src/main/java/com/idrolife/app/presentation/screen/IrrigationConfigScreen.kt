package com.idrolife.app.presentation.screen

import android.app.Activity
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.Divider
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.imageResource
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
import com.idrolife.app.presentation.component.NavigationBanner3
import com.idrolife.app.presentation.viewmodel.DeviceViewModel
import com.idrolife.app.theme.Black
import com.idrolife.app.theme.BrokenWhite
import com.idrolife.app.theme.Manrope
import com.idrolife.app.utils.Helper

@Composable
fun IrrigationConfigScreen(
    navController: NavController,
    deviceID: String,
    deviceCode: String
) {
    val context = LocalContext.current

    val window = (context as Activity).window
    val view = LocalView.current

    val viewModel = hiltViewModel<DeviceViewModel>()

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

    fun showToast(message: String, duration: Int = Toast.LENGTH_SHORT) {
        Toast.makeText(context, message, duration).show()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BrokenWhite),
    ) {
        NavigationBanner3(
            navController,
            "Configuration",
            R.drawable.img_header_detail3,
            viewModel.selectedDevice.value,
            viewModel.isLoading.value,
        )

        LazyColumn(
            modifier = Modifier
                .padding(horizontal = 24.dp)
        ) {
            item {
                IrrigationConfigButton(title = context.getString(R.string.nominal_flow)) {
                    navController.navigate(Screen.IrrigationConfigNominalFlow.withArgs(deviceID))
                }
                IrrigationConfigButton(title = context.getString(R.string.general_setting)) {
                    navController.navigate(Screen.IrrigationConfigGeneralSetting.withArgs(deviceID))
                }
                IrrigationConfigButton(title = context.getString(R.string.advanced_configuration)) {
                    navController.navigate(Screen.IrrigationConfigAdvanceConfig.withArgs(deviceID))
                }
                IrrigationConfigButton(title = context.getString(R.string.ev_radio_status)) {
                    navController.navigate(Screen.IrrigationConfigEVRadioStatus.withArgs(deviceID))
                }
                IrrigationConfigButton(title = context.getString(R.string.ev_configuration)) {
                    navController.navigate(Screen.IrrigationConfigEVConfig.withArgs(deviceID, deviceCode))
                }
                IrrigationConfigButton(title = context.getString(R.string.station_management)) {

                }
            }
        }
    }
}

@Composable
fun IrrigationConfigButton (
    title: String,
    onClick: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                onClick()
            },
    ) {
        Spacer(modifier = Modifier.height(24.dp))

        Row(
            modifier = Modifier
                .padding(horizontal = 12.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Text(
                title,
                fontSize = 16.sp,
                fontWeight = FontWeight.Normal,
                color = Black,
                fontFamily = Manrope,
            )

            Image(
                modifier = Modifier
                    .size(10.dp),
                bitmap = ImageBitmap.imageResource(R.drawable.ic_arrow_noline_right_black),
                contentDescription = ""
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        Divider(
            color = Black,
            thickness = 1.dp,
        )
    }
}
