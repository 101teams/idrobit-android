package com.idrolife.app.presentation.screen

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.idrolife.app.R
import com.idrolife.app.navigation.Screen
import com.idrolife.app.presentation.viewmodel.NetworkViewModel

@Composable
fun ChooseDeviceScreen(navController: NavController) {
    val viewModel = hiltViewModel<NetworkViewModel>()
    val password = remember { "12345678" }

    BaseChooseWifiScreen(
        screenName = stringResource(id = R.string.choose_device),
        navController = navController
    ) {
//        viewModel.connectToWifi(it.SSID, password)
        navController.navigate(Screen.ChooseNetwork.route)
    }
}