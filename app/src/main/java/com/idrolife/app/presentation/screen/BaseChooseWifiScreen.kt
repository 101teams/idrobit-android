package com.idrolife.app.presentation.screen

import android.Manifest
import android.content.Intent
import android.net.Uri
import android.net.wifi.ScanResult
import android.os.Build
import android.provider.Settings
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.AlertDialog
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.MultiplePermissionsState
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import com.idrolife.app.presentation.component.CustomTopBarSimple
import com.idrolife.app.presentation.component.LoadingIndicator
import com.idrolife.app.presentation.component.NotificationBarColorEffect
import com.idrolife.app.presentation.viewmodel.NetworkViewModel
import com.idrolife.app.theme.Black
import com.idrolife.app.theme.Primary2
import com.idrolife.app.theme.White

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun BaseChooseWifiScreen(
    screenName: String,
    navController: NavController,
    networkNameFilter: String = "",
    onNetworkSelected: (ScanResult) -> Unit
) {
    val viewModel = hiltViewModel<NetworkViewModel>()
    val uiState by viewModel.uiState.collectAsState()
    val wifiNetworks by remember {
        derivedStateOf { uiState.wifiNetworks.filter { it.SSID.contains(networkNameFilter) } }
    }
    val isScanning = uiState.isScanning
    val context = LocalContext.current

    // Define required permissions
    val permissions = listOf(
        Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.ACCESS_COARSE_LOCATION,
        Manifest.permission.CHANGE_WIFI_STATE,
        Manifest.permission.ACCESS_WIFI_STATE
    )

    // Add NEARBY_WIFI_DEVICES for Android 13+
    val permissionsList = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        permissions + Manifest.permission.NEARBY_WIFI_DEVICES
    } else {
        permissions
    }

    // Permission state
    val permissionState = rememberMultiplePermissionsState(permissionsList)

    // Handle permissions
    LaunchedEffect(permissionState.allPermissionsGranted) {
        viewModel.updatePermissionState(permissionState.allPermissionsGranted)
    }

    // Check if all permissions are granted and show appropriate content
    if (!permissionState.allPermissionsGranted) {
        // Show permission UI
        HandlePermissions(
            permissionState = permissionState,
            navController = navController
        )
    } else {
        // Permissions granted, show WiFi screen content
        WifiScreenContent(
            screenName = screenName,
            navController = navController,
            wifiNetworks = wifiNetworks,
            isScanning = isScanning,
            onNetworkSelected = onNetworkSelected,
            onScanClicked = { viewModel.scanWifiNetworks() }
        )

        // Scan when permissions are granted
        LaunchedEffect(Unit) {
            viewModel.scanWifiNetworks()
        }
    }

    // Show dialogs if needed
    if (uiState.showLocationEnableDialog) {
        LocationEnableDialog(
            onDismiss = { viewModel.onLocationDialogDismiss() },
            onEnableLocation = {
                viewModel.onLocationDialogDismiss()
                // Open location settings
                val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                context.startActivity(intent)
            }
        )
    }

    if (uiState.showWifiEnableDialog) {
        WifiEnableDialog(
            onDismiss = { viewModel.onWifiDialogDismiss() }
        )
    }
}

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun HandlePermissions(
    permissionState: MultiplePermissionsState,
    navController: NavController
) {
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            "Location permission is required to scan for WiFi networks",
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.h6
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Show appropriate UI based on permission state
        if (permissionState.shouldShowRationale || !permissionState.allPermissionsGranted) {
            // Either first request or user needs rationale
            Button(
                onClick = { permissionState.launchMultiplePermissionRequest() },
                colors = ButtonDefaults.buttonColors(backgroundColor = Primary2)
            ) {
                Text("Grant Permissions")
            }
        } else {
            // Permissions were denied - need to go to settings
            Text(
                "Permissions denied. Please enable location permissions in settings.",
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    // Open app settings
                    Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                        data = Uri.fromParts("package", context.packageName, null)
                        context.startActivity(this)
                    }
                },
                colors = ButtonDefaults.buttonColors(backgroundColor = Primary2)
            ) {
                Text("Open Settings")
            }

            Spacer(modifier = Modifier.height(8.dp))

            Button(
                onClick = { navController.navigateUp() },
                colors = ButtonDefaults.buttonColors(backgroundColor = Color.Gray)
            ) {
                Text("Go Back")
            }
        }
    }
}

@Composable
fun WifiScreenContent(
    screenName: String,
    navController: NavController,
    wifiNetworks: List<ScanResult>,
    isScanning: Boolean,
    onNetworkSelected: (ScanResult) -> Unit,
    onScanClicked: () -> Unit
) {
    NotificationBarColorEffect()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(White)
    ) {
        CustomTopBarSimple(navController, screenName)

        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .padding(horizontal = 24.dp)
                .padding(top = 24.dp)
                .padding(bottom = 8.dp)
        ) {
            Text(
                text = screenName,
                style = MaterialTheme.typography.subtitle1
            )

            Spacer(modifier = Modifier.width(10.dp))

            if (isScanning) {
                LoadingIndicator()
            } else {
                Text(
                    "Scan",
                    color = Primary2,
                    style = MaterialTheme.typography.body2,
                    modifier = Modifier
                        .clickable { onScanClicked() }
                )
            }
        }

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
fun LocationEnableDialog(onDismiss: () -> Unit, onEnableLocation: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Location Disabled") },
        text = { Text("Location services are required to scan for WiFi networks. Would you like to enable location?") },
        confirmButton = {
            Button(
                onClick = onEnableLocation,
                colors = ButtonDefaults.buttonColors(backgroundColor = Primary2)
            ) {
                Text("Enable")
            }
        },
        dismissButton = {
            Button(
                onClick = onDismiss,
                colors = ButtonDefaults.buttonColors(backgroundColor = Color.Gray)
            ) {
                Text("Cancel")
            }
        }
    )
}

@Composable
fun WifiEnableDialog(onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("WiFi Disabled") },
        text = { Text("WiFi is required to scan for networks. Please enable WiFi.") },
        confirmButton = {
            Button(
                onClick = onDismiss,
                colors = ButtonDefaults.buttonColors(backgroundColor = Primary2)
            ) {
                Text("OK")
            }
        },
    )
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