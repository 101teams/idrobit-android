package com.idrolife.app.presentation.screen

import android.net.wifi.ScanResult
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.LinearProgressIndicator
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.idrolife.app.presentation.component.CustomTopBarSimple
import com.idrolife.app.presentation.component.NotificationBarColorEffect
import com.idrolife.app.presentation.viewmodel.NetworkViewModel
import com.idrolife.app.theme.Black
import com.idrolife.app.theme.Primary2
import com.idrolife.app.theme.White

@Composable
fun BaseChooseWifiScreen(
    screenName: String,
    navController: NavController,
    onNetworkSelected: (ScanResult) -> Unit
) {
    val viewModel = hiltViewModel<NetworkViewModel>()
    val uiState by viewModel.uiState.collectAsState()
    val wifiNetworks = uiState.wifiNetworks
    val isScanning = uiState.isScanning

    LaunchedEffect(Unit) {
        viewModel.scanWifiNetworks()
    }

    NotificationBarColorEffect()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(White)
    ) {
        CustomTopBarSimple(navController, screenName)

        if (isScanning) {
            LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
        }

        Spacer(modifier = Modifier.height(21.dp))

        Column(modifier = Modifier.padding(horizontal = 24.dp)) {
            if (wifiNetworks.isEmpty() && !isScanning) {
                Text("No Wi-Fi networks found.", color = Black)
            } else {
                NetworkList(
                    networks = wifiNetworks,
                    onNetworkSelected = { network ->
                        onNetworkSelected(network)
                    }
                )
            }
        }
    }
}

@Composable
fun NetworkList(networks: List<ScanResult>, onNetworkSelected: (ScanResult) -> Unit) {
    LazyColumn {
        items(networks) { network ->
            NetworkItem(network, onNetworkSelected)
        }
    }
}

@Composable
fun NetworkItem(network: ScanResult, onNetworkSelected: (ScanResult) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = network.SSID.ifEmpty { "(No Name)" }, color = Black)
        Button(
            colors = ButtonDefaults.buttonColors(backgroundColor = Primary2),
            onClick = { onNetworkSelected(network) }) {
            Text("Connect")
        }
    }
}