package com.idrolife.app.presentation.screen

import android.app.Activity
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.navigation.NavController
import com.idrolife.app.R
import com.idrolife.app.navigation.Screen
import com.idrolife.app.presentation.component.Button2Image
import com.idrolife.app.presentation.component.NavigationBanner2
import com.idrolife.app.presentation.viewmodel.DeviceViewModel
import com.idrolife.app.theme.Green2
import com.idrolife.app.theme.GreenLight2
import com.idrolife.app.theme.White
import com.idrolife.app.utils.Helper

@Composable
fun IrrigationDeviceScreen(
    navController: NavController,
    deviceID: String,
    deviceCode: String,
) {
    val context = LocalContext.current

    val window = (context as Activity).window
    val view = LocalView.current

    val viewModel = hiltViewModel<DeviceViewModel>()

    val lifecycleOwner = LocalLifecycleOwner.current
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                Helper().setNotifBarColor(view, window, GreenLight2.toArgb(),false)
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
            .background(White),
    ) {
        NavigationBanner2(
            navController,
            stringResource(id = R.string.irrigation),
            R.drawable.img_header_detail3,
            viewModel.selectedDevice.value,
            viewModel.isLoading.value,
        )

        Column(
            modifier = Modifier.padding(24.dp)
        ) {
            Button2Image(
                White,
                R.drawable.ic_irrigation_config_green,
                stringResource(id = R.string.irrigation_configuration),
                R.drawable.ic_arrow_up_green,
                onClick = {
                    navController.navigate(Screen.IrrigationConfig.withArgs(deviceID, deviceCode))
                },
                Green2,
                Green2,
            )

            Spacer(modifier = Modifier.height(24.dp))

            Button2Image(
                White,
                R.drawable.ic_irrigation_setting_green,
                stringResource(id = R.string.irrigation_setting),
                R.drawable.ic_arrow_up_green,
                onClick = {

                },
                Green2,
                Green2,
            )

            Spacer(modifier = Modifier.height(24.dp))

            Button2Image(
                White,
                R.drawable.ic_irrigation_status_green,
                stringResource(id = R.string.irrigation_status),
                R.drawable.ic_arrow_up_green,
                onClick = {

                },
                Green2,
                Green2,
            )
        }
    }
}
