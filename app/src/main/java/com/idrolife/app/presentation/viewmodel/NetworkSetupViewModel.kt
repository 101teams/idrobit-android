package com.idrolife.app.presentation.viewmodel

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import com.idrolife.app.navigation.Screen
import com.idrolife.app.service.TcpClient
import com.idrolife.app.service.WifiRepository
import com.idrolife.app.utils.NetworkType
import com.idrolife.app.utils.OriginalNetworkInfo
import com.idrolife.app.utils.PrefManager
import com.idrolife.app.utils.RestorationStep
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class NetworkSetupUiState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val isSuccess: Boolean = false,
    val isRestoring: Boolean = false,
    val restorationStep: RestorationStep? = null,
    val restorationProgress: String? = null
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
            Log.d("NetworkSetupViewModel", "Starting network restoration process")
            _uiState.value = _uiState.value.copy(
                isRestoring = true,
                restorationStep = RestorationStep.DISCONNECTING_CURRENT,
                restorationProgress = "Disconnecting from IoT device..."
            )

            // Prevent too frequent restoration attempts
            val lastAttempt = prefManager.getLastRestorationAttempt()
            val timeSinceLastAttempt = System.currentTimeMillis() - lastAttempt
            if (timeSinceLastAttempt < 5000) { // 5 seconds
                Log.d("NetworkSetupViewModel", "Restoration attempted too recently, waiting...")
                delay(5000 - timeSinceLastAttempt)
            }
            
            prefManager.setLastRestorationAttempt(System.currentTimeMillis())

            // Add delay to allow device to reboot and disconnect
            delay(2000)

            // Get detailed network information
            val originalNetworkInfo = prefManager.getOriginalNetworkInfo()
            val fallbackSsid = prefManager.getPreviousWifi()
            
            Log.d("NetworkSetupViewModel", "Original network info: $originalNetworkInfo")
            Log.d("NetworkSetupViewModel", "Fallback SSID: $fallbackSsid")

            when {
                originalNetworkInfo?.networkType == NetworkType.WIFI && !originalNetworkInfo.ssid.isNullOrEmpty() -> {
                    restoreWifiConnection(originalNetworkInfo, navController)
                }
                !fallbackSsid.isNullOrEmpty() && fallbackSsid != "MOBILE_DATA" -> {
                    restoreWifiConnectionFallback(fallbackSsid, navController)
                }
                else -> {
                    restoreMobileDataConnection(navController)
                }
            }
        }
    }

    private suspend fun restoreWifiConnection(originalNetworkInfo: OriginalNetworkInfo, navController: NavController) {
        val ssid = originalNetworkInfo.ssid!!
        Log.d("NetworkSetupViewModel", "Restoring WiFi connection to: $ssid")
        
        // Step 1: Disconnect current WiFi
        _uiState.value = _uiState.value.copy(
            restorationStep = RestorationStep.DISCONNECTING_CURRENT,
            restorationProgress = "Disconnecting from current network..."
        )
        
        try {
            wifiRepository.disconnectCurrentWifi { disconnected ->
                viewModelScope.launch(Dispatchers.Main) {
                    if (disconnected) {
                        Log.d("NetworkSetupViewModel", "Successfully disconnected from current WiFi")
                        // Step 2: Remove temporary IoT network
                        _uiState.value = _uiState.value.copy(
                            restorationStep = RestorationStep.REMOVING_TEMPORARY,
                            restorationProgress = "Cleaning up temporary connections..."
                        )
                        
                        // Get current configured networks and remove IL_ networks
                        delay(1000)
                        
                        // Step 3: Connect to original network
                        _uiState.value = _uiState.value.copy(
                            restorationStep = RestorationStep.CONNECTING_TO_ORIGINAL,
                            restorationProgress = "Reconnecting to $ssid..."
                        )
                        
                        wifiRepository.connectToWifiWithValidation(ssid, null, 30000) { connected ->
                            viewModelScope.launch(Dispatchers.Main) {
                                if (connected) {
                                    Log.d("NetworkSetupViewModel", "Successfully restored WiFi connection to $ssid")
                                    handleRestorationSuccess(navController)
                                } else {
                                    Log.w("NetworkSetupViewModel", "Failed to restore WiFi connection, trying fallback")
                                    // Try fallback approach
                                    restoreWifiConnectionFallback(ssid, navController)
                                }
                            }
                        }
                    } else {
                        Log.w("NetworkSetupViewModel", "Failed to disconnect from current WiFi, continuing anyway")
                        restoreWifiConnectionFallback(ssid, navController)
                    }
                }
            }
        } catch (e: Exception) {
            Log.e("NetworkSetupViewModel", "Error during WiFi restoration: ${e.message}")
            restoreWifiConnectionFallback(ssid, navController)
        }
    }

    private suspend fun restoreWifiConnectionFallback(ssid: String, navController: NavController) {
        Log.d("NetworkSetupViewModel", "Using fallback WiFi restoration for: $ssid")
        
        _uiState.value = _uiState.value.copy(
            restorationStep = RestorationStep.CONNECTING_TO_ORIGINAL,
            restorationProgress = "Reconnecting to $ssid (fallback)..."
        )
        
        wifiRepository.reconnectToWifi(ssid) { success ->
            viewModelScope.launch(Dispatchers.Main) {
                if (success) {
                    Log.d("NetworkSetupViewModel", "Fallback reconnection successful. Waiting for connectivity.")
                    _uiState.value = _uiState.value.copy(
                        restorationStep = RestorationStep.VERIFYING_INTERNET,
                        restorationProgress = "Verifying internet connection..."
                    )
                    
                    wifiRepository.waitForInternetConnectivity(15000) { hasInternet ->
                        viewModelScope.launch(Dispatchers.Main) {
                            if (hasInternet) {
                                Log.d("NetworkSetupViewModel", "Internet connectivity restored via fallback")
                                handleRestorationSuccess(navController)
                            } else {
                                Log.w("NetworkSetupViewModel", "Fallback restoration failed, trying mobile data")
                                restoreMobileDataConnection(navController)
                            }
                        }
                    }
                } else {
                    Log.w("NetworkSetupViewModel", "Fallback WiFi reconnection failed, trying mobile data")
                    restoreMobileDataConnection(navController)
                }
            }
        }
    }

    private suspend fun restoreMobileDataConnection(navController: NavController) {
        Log.d("NetworkSetupViewModel", "Attempting to restore mobile data connection")
        
        _uiState.value = _uiState.value.copy(
            restorationStep = RestorationStep.WAITING_FOR_CONNECTION,
            restorationProgress = "Waiting for mobile data connection..."
        )
        
        wifiRepository.waitForInternetConnectivity(20000) { hasInternet ->
            viewModelScope.launch(Dispatchers.Main) {
                if (hasInternet) {
                    Log.d("NetworkSetupViewModel", "Mobile data connectivity established")
                    handleRestorationSuccess(navController)
                } else {
                    Log.e("NetworkSetupViewModel", "Failed to establish any internet connectivity")
                    handleRestorationFailure("Unable to restore internet connection. Please check your network settings manually.")
                }
            }
        }
    }

    private fun handleRestorationSuccess(navController: NavController) {
        Log.d("NetworkSetupViewModel", "Network restoration completed successfully")
        _uiState.value = _uiState.value.copy(
            isRestoring = false,
            restorationStep = RestorationStep.COMPLETED,
            restorationProgress = null
        )
        
        // Clean up stored network info
        prefManager.clearOriginalNetworkInfo()
        
        navigateToMain(navController)
    }

    private fun handleRestorationFailure(message: String) {
        Log.e("NetworkSetupViewModel", "Network restoration failed: $message")
        _uiState.value = _uiState.value.copy(
            isRestoring = false,
            restorationStep = RestorationStep.FAILED,
            restorationProgress = null,
            error = message
        )
    }

    private fun navigateToMain(navController: NavController) {
        _uiState.value = _uiState.value.copy(isRestoring = false, restorationStep = null, restorationProgress = null)
        navController.navigate(Screen.Main.route) {
            popUpTo(Screen.Main.route) { inclusive = true }
        }
    }
}
