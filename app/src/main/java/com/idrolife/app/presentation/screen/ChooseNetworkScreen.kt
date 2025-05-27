package com.idrolife.app.presentation.screen

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material.Button
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.idrolife.app.BuildConfig
import com.idrolife.app.R
import com.idrolife.app.navigation.Screen
import com.idrolife.app.presentation.viewmodel.NetworkSetupViewModel
import com.idrolife.app.theme.Black
import com.idrolife.app.theme.Primary2

@Composable
fun ChooseNetworkScreen(navController: NavController) {
    val viewModel = hiltViewModel<NetworkSetupViewModel>()
    val uiState by viewModel.uiState.collectAsState()
    val isReconnectError by remember {
        derivedStateOf {
            uiState.error?.contains("Failed to reconnect") ?: false
        }
    }

    if (BuildConfig.DEBUG) {
        Button(onClick = {
            viewModel.restorePreviousNetwork(navController)
        }) {
            Text("Restore network")
        }

        if (uiState.isLoading || uiState.isRestoring) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    CircularProgressIndicator(color = Primary2)
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = if (uiState.isRestoring) "Restoring network connection..." else "Configuring device...",
                        style = MaterialTheme.typography.body2,
                        color = Black
                    )
                }
            }
        }

        if (uiState.error != null) {
            ErrorDialog(
                confirmText = if (isReconnectError) "OK" else "Retry",
                errorMessage = uiState.error ?: "",
                onConfirm = {
                    if (isReconnectError) {
                        navController.navigate(Screen.Main.route) {
                            popUpTo(Screen.Main.route) { inclusive = true }
                        }
                    }
                },
                onDismiss = { viewModel.clearError() }
            )
        }
    } else {
        BaseChooseWifiScreen(
            screenName = stringResource(id = R.string.choose_network),
            navController = navController
        ) { network ->
                navController.navigate(Screen.NetworkSetup.withArgs(network.SSID))
        }
    }
}
