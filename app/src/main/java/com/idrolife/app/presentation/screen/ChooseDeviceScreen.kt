package com.idrolife.app.presentation.screen

import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.idrolife.app.BuildConfig
import com.idrolife.app.R
import com.idrolife.app.navigation.Screen
import com.idrolife.app.presentation.viewmodel.NetworkViewModel

@Composable
fun ChooseDeviceScreen(navController: NavController) {
    val viewModel = hiltViewModel<NetworkViewModel>()
    val password = remember { "12345678" }
    var connectingToSsid by remember { mutableStateOf<String?>(null) }

    BaseChooseWifiScreen(
        screenName = stringResource(id = R.string.choose_device),
        navController = navController,
        connectingToSsid = connectingToSsid,
        networkNameFilter = if (BuildConfig.DEBUG) "" else "IL_"
    ) {
        connectingToSsid = it.SSID
        viewModel.connectToWifi(it.SSID, password) { success ->
            if (success) {
                navController.navigate(Screen.ChooseNetwork.route)
            } else {
                Toast.makeText(
                    navController.context,
                    "Failed to connect to ${it.SSID}",
                    Toast.LENGTH_SHORT
                ).show()
            }

            connectingToSsid = null
        }
    }
}