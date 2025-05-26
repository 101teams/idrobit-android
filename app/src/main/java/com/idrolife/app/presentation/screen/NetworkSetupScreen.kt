package com.idrolife.app.presentation.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.AlertDialog
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.idrolife.app.R
import com.idrolife.app.navigation.Screen
import com.idrolife.app.presentation.component.CustomTopBarSimple
import com.idrolife.app.presentation.component.Input
import com.idrolife.app.presentation.component.NotificationBarColorEffect
import com.idrolife.app.presentation.viewmodel.NetworkSetupViewModel
import com.idrolife.app.presentation.viewmodel.NetworkViewModel
import com.idrolife.app.theme.Black
import com.idrolife.app.theme.Primary2
import com.idrolife.app.theme.White
import com.idrolife.app.utils.PrefManager

@Composable
fun NetworkSetupScreen(
    navController: NavController,
) {
    val viewModel: NetworkSetupViewModel = hiltViewModel()
    val networkViewModel: NetworkViewModel = hiltViewModel()
    val uiState by viewModel.uiState.collectAsState()
    val password = remember { mutableStateOf("") }
    val context = LocalContext.current
    val prefManager = remember { PrefManager(context) }

    LaunchedEffect(uiState.isSuccess) {
        if (uiState.isSuccess) {
            // Restore previous network after setup
            val prevSsid = prefManager.getPreviousWifi()
            if (!prevSsid.isNullOrEmpty() && prevSsid != "MOBILE_DATA") {
                networkViewModel.reconnectToWifi(prevSsid) { }
            }
            // If prevSsid is MOBILE_DATA or null, do nothing (Android will use mobile data automatically)
            navController.navigate(Screen.ChooseDevice.route) {
                popUpTo(Screen.ChooseDevice.route) { inclusive = true }
            }
        }
    }

    NotificationBarColorEffect()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(White)
    ) {
        CustomTopBarSimple(navController, stringResource(id = R.string.choose_network))

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "SSID: ${viewModel.getSsid()}",
            style = MaterialTheme.typography.h6,
            color = Black,
            modifier = Modifier.padding(horizontal = 24.dp),
        )

        Spacer(modifier = Modifier.height(16.dp))

        Input(
            placeholder = "Password", binding = password, modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp)
        )

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            modifier = Modifier
                .clip(RoundedCornerShape(4.dp))
                .height(62.dp)
                .fillMaxWidth()
                .padding(horizontal = 24.dp),
            contentPadding = PaddingValues(0.dp),
            onClick = { viewModel.configureNetwork(password.value) },
            enabled = !uiState.isLoading && password.value.isNotEmpty(),
            colors = ButtonDefaults.buttonColors(backgroundColor = Primary2)
        ) {
            Text(stringResource(id = R.string.connect), style = MaterialTheme.typography.button, fontSize = 18.sp)
        }

        if (uiState.isLoading) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = Primary2)
            }
        }

        if (uiState.error != null) {
            ErrorDialog(
                errorMessage = uiState.error ?: "",
                onRetry = { viewModel.configureNetwork(password.value) },
                onDismiss = { viewModel.clearError() }
            )
        }
    }
}

@Composable
fun ErrorDialog(
    errorMessage: String,
    onRetry: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Error") },
        text = { Text(errorMessage) },
        confirmButton = {
            TextButton(onClick = onRetry) {
                Text("Retry", color = Primary2)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel", color = Color.Gray)
            }
        }
    )
}