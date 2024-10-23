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
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.navigation.NavController
import com.idrolife.app.R
import com.idrolife.app.navigation.Screen
import com.idrolife.app.presentation.component.Button2Image
import com.idrolife.app.presentation.component.NavigationBanner1
import com.idrolife.app.theme.BrokenWhite
import com.idrolife.app.theme.Green2
import com.idrolife.app.theme.GreenLight2
import com.idrolife.app.utils.Helper

@Composable
fun DetailDeviceScreen(
    navController: NavController,
    deviceID: String,
    deviceName: String,
) {
    val context = LocalContext.current

    val window = (context as Activity).window
    val view = LocalView.current

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
            "Idrosat",
            deviceName,
            R.drawable.img_header_detail1,
        )

        Column(
            modifier = Modifier.padding(24.dp)
        ) {
            Button2Image(
                Green2,
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

            Button2Image(
                Green2,
                R.drawable.ic_sprinkle_white,
                stringResource(id = R.string.irrigation),
                R.drawable.ic_arrow_up_white,
                onClick = {
                    navController.navigate(Screen.IrrigationDevice.withArgs(deviceID))
                },
                null,
                null,
            )

            Spacer(modifier = Modifier.height(24.dp))

            Button2Image(
                Green2,
                R.drawable.ic_fertilizer_white,
                stringResource(id = R.string.fertigation),
                R.drawable.ic_arrow_up_white,
                onClick = {

                },
                null,
                null,
            )
        }
    }
}
