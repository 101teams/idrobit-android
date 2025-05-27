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
import com.idrolife.app.utils.PrefManager

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
                    } else {
                        viewModel.configureNetwork(password.value)
                    }
                },
                onDismiss = { viewModel.clearError() }
            )
        }
    }
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