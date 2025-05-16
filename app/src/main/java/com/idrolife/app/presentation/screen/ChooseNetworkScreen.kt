package com.idrolife.app.presentation.screen

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavController
import com.idrolife.app.R
import com.idrolife.app.navigation.Screen

@Composable
fun ChooseNetworkScreen(navController: NavController) {
    BaseChooseWifiScreen(
        screenName = stringResource(id = R.string.choose_network),
        navController = navController
    ) { network ->
        navController.navigate(Screen.NetworkSetup.withArgs(network.SSID))
    }
}
