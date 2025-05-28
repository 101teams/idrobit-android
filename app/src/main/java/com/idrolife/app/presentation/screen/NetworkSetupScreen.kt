package com.idrolife.app.presentation.screen

import android.util.Log
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
import androidx.compose.runtime.derivedStateOf
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
import com.idrolife.app.utils.RestorationStep

@Composable
fun NetworkSetupScreen(
    navController: NavController,
) {
    val viewModel: NetworkSetupViewModel = hiltViewModel()
    val uiState by viewModel.uiState.collectAsState()
    val password = remember { mutableStateOf("") }
    val isReconnectError by remember {
        derivedStateOf {
            uiState.error?.contains("Failed to reconnect") ?: false
        }
    }

    LaunchedEffect(uiState.isSuccess) {
        if (uiState.isSuccess) {
            viewModel.restorePreviousNetwork(navController)
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
                    
                    val loadingText = when {
                        uiState.isRestoring -> {
                            uiState.restorationProgress ?: getRestorationStepText(uiState.restorationStep)
                        }
                        else -> "Configuring device..."
                    }
                    
                    Text(
                        text = loadingText,
                        style = MaterialTheme.typography.body2,
                        color = Black
                    )
                    
                    // Show restoration step indicator
                    if (uiState.isRestoring && uiState.restorationStep != null) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Step ${getStepNumber(uiState.restorationStep)} of 5",
                            style = MaterialTheme.typography.caption,
                            color = Color.Gray
                        )
                    }
                }
            }
        }

        if (uiState.error != null) {
            EnhancedErrorDialog(
                errorMessage = uiState.error ?: "",
                isRestorationError = isReconnectError || uiState.restorationStep == RestorationStep.FAILED,
                onRetry = { viewModel.configureNetwork(password.value) },
                onRestoreRetry = { viewModel.restorePreviousNetwork(navController) },
                onNavigateToMain = {
                    navController.navigate(Screen.Main.route) {
                        popUpTo(Screen.Main.route) { inclusive = true }
                    }
                },
                onDismiss = { viewModel.clearError() }
            )
        }
    }
}

@Composable
fun EnhancedErrorDialog(
    errorMessage: String,
    isRestorationError: Boolean,
    onRetry: () -> Unit,
    onRestoreRetry: () -> Unit,
    onNavigateToMain: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = if (isRestorationError) { {} } else onDismiss,
        title = { 
            Text(if (isRestorationError) "Network Restoration Failed" else "Configuration Error") 
        },
        text = { 
            Column {
                Text(errorMessage)
                if (isRestorationError) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "The automatic network restoration process was unable to restore your connection. Please check your network settings manually.",
                        style = MaterialTheme.typography.caption,
                        color = Color.Gray
                    )
                }
            }
        },
        confirmButton = {
            if (isRestorationError) {
                TextButton(onClick = onNavigateToMain) {
                    Text("Go to Main Screen", color = Primary2)
                }
            } else {
                TextButton(onClick = onRetry) {
                    Text("Retry", color = Primary2)
                }
            }
        },
        dismissButton = {
            if (!isRestorationError) {
                TextButton(onClick = onDismiss) {
                    Text("Cancel", color = Color.Gray)
                }
            }
        }
    )
}

@Composable
fun ErrorDialog(
    errorMessage: String,
    confirmText: String? = null,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Error") },
        text = { Text(errorMessage) },
        confirmButton = {
            TextButton(onClick = onConfirm) {
                Text(confirmText ?: "Retry", color = Primary2)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel", color = Color.Gray)
            }
        }
    )
}

@Composable
private fun getRestorationStepText(step: RestorationStep?): String {
    return when (step) {
        RestorationStep.DISCONNECTING_CURRENT -> "Disconnecting from IoT device..."
        RestorationStep.REMOVING_TEMPORARY -> "Cleaning up temporary connections..."
        RestorationStep.CONNECTING_TO_ORIGINAL -> "Reconnecting to your network..."
        RestorationStep.WAITING_FOR_CONNECTION -> "Waiting for connection..."
        RestorationStep.VERIFYING_INTERNET -> "Verifying internet connection..."
        RestorationStep.COMPLETED -> "Restoration completed"
        RestorationStep.FAILED -> "Restoration failed"
        null -> "Restoring network connection..."
    }
}

private fun getStepNumber(step: RestorationStep?): Int {
    return when (step) {
        RestorationStep.DISCONNECTING_CURRENT -> 1
        RestorationStep.REMOVING_TEMPORARY -> 2
        RestorationStep.CONNECTING_TO_ORIGINAL -> 3
        RestorationStep.WAITING_FOR_CONNECTION -> 4
        RestorationStep.VERIFYING_INTERNET -> 5
        RestorationStep.COMPLETED -> 5
        RestorationStep.FAILED -> 5
        null -> 1
    }
}