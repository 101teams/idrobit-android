package com.idrolife.app.presentation.screen

import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.idrolife.app.BuildConfig
import com.idrolife.app.R
import com.idrolife.app.navigation.Screen
import com.idrolife.app.presentation.viewmodel.NetworkViewModel
import com.idrolife.app.utils.PrefManager
import com.idrolife.app.utils.OriginalNetworkInfo
import com.idrolife.app.utils.NetworkType

@Composable
fun ChooseDeviceScreen(navController: NavController) {
    val viewModel = hiltViewModel<NetworkViewModel>()
    val password = remember { "12345678" }
    var connectingToSsid by remember { mutableStateOf<String?>(null) }
    val context = LocalContext.current
    val prefManager = remember { PrefManager(context) }

    BaseChooseWifiScreen(
        screenName = stringResource(id = R.string.choose_device),
        navController = navController,
        connectingToSsid = connectingToSsid,
        networkNameFilter = if (BuildConfig.DEBUG) "" else "IL_"
    ) {
        // Save detailed current network info before connecting to IoT device
        val (currentSsid, signalStrength, bssid) = viewModel.getDetailedWifiInfo()
        val cleanSsid = currentSsid?.replace("\"", "")?.takeIf { it.isNotEmpty() }
        
        val networkInfo = if (cleanSsid != null) {
            OriginalNetworkInfo(
                ssid = cleanSsid,
                networkType = NetworkType.WIFI,
                signalStrength = signalStrength,
                securityType = null, // Could be enhanced to detect security type
                frequency = null, // Could be enhanced to get frequency
                bssid = bssid
            )
        } else {
            OriginalNetworkInfo(
                ssid = null,
                networkType = NetworkType.MOBILE_DATA,
                signalStrength = null,
                securityType = null,
                frequency = null,
                bssid = null
            )
        }
        
        prefManager.saveOriginalNetworkInfo(networkInfo)
        // Keep the old method for backward compatibility
        prefManager.setPreviousWifi(cleanSsid ?: "MOBILE_DATA")
        
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