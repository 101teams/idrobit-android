package com.idrolife.app.presentation.viewmodel

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import com.idrolife.app.navigation.Screen
import com.idrolife.app.service.TcpClient
import com.idrolife.app.service.WifiRepository
import com.idrolife.app.utils.PrefManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class NetworkSetupUiState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val isSuccess: Boolean = false,
    val isRestoring: Boolean = false
)

@HiltViewModel
class NetworkSetupViewModel @Inject constructor(
    private val prefManager: PrefManager,
    private val tcpClient: TcpClient,
    private val wifiRepository: WifiRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {
    private val ssid: String = checkNotNull(savedStateHandle["ssid"] ?: "EXAMPLE")

    private val _uiState = MutableStateFlow(NetworkSetupUiState())
    val uiState = _uiState.asStateFlow()

    fun getSsid(): String {
        return ssid
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }

    fun configureNetwork(password: String) {
        val configMessage = "\r\n\$SSID=$ssid,$password\r\n"

        _uiState.value = _uiState.value.copy(isLoading = true, error = null, isSuccess = false)

        viewModelScope.launch {
            when (val result = tcpClient.sendAndReceive(configMessage)) {
                is TcpClient.Result.Success -> {
                    val response = result.response
                    if (response.contains("\$SSID=$ssid,$password")) {
                        tcpClient.send("\$REBOOT\r\n")
                        _uiState.value = _uiState.value.copy(isLoading = false, isSuccess = true)
                    } else {
                        _uiState.value = NetworkSetupUiState(error = "SSID or password incorrect")
                    }
                }

                is TcpClient.Result.Error -> {
                    _uiState.value = NetworkSetupUiState(error = "TCP Error: ${result.exception.message}")
                }
            }
        }
    }

    fun restorePreviousNetwork(navController: NavController) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isRestoring = true)

            // Add delay to allow device to reboot and disconnect
            delay(2000)

            val prevSsid = prefManager.getPreviousWifi()
            if (!prevSsid.isNullOrEmpty() && prevSsid != "MOBILE_DATA") {
                wifiRepository.reconnectToWifi(prevSsid) { success ->
                    if (success) {
                        Log.d("NetworkSetupViewModel", "Reconnected to WiFi: $prevSsid. Waiting for connectivity.")
                        wifiRepository.waitForInternetConnectivity(3000) { hasInternet ->
                            if (hasInternet) {
                                Log.d("NetworkSetupViewModel", "Internet connectivity restored.")
                                navigateToMain(navController)
                            } else {
                                handleError("Failed to restore internet connectivity.")
                            }
                        }
                    } else {
                        handleError("Failed to reconnect to WiFi: $prevSsid")
                    }
                }
            } else {
                wifiRepository.waitForInternetConnectivity(15000) { hasInternet ->
                    if (hasInternet) {
                        navigateToMain(navController)
                    } else {
                        handleError("Failed to establish mobile data connectivity.")
                    }
                }
            }
        }
    }

    private fun navigateToMain(navController: NavController) {
        _uiState.value = _uiState.value.copy(isRestoring = false)
        navController.navigate(Screen.Main.route) {
            popUpTo(Screen.Main.route) { inclusive = true }
        }
    }

    private fun handleError(message: String) {
        _uiState.value = _uiState.value.copy(isRestoring = false, error = message)
    }
}
