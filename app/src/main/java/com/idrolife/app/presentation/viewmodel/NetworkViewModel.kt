package com.idrolife.app.presentation.viewmodel

import android.net.wifi.ScanResult
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.idrolife.app.service.WifiRepository
import com.idrolife.app.service.WifiRepositoryImpl
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class NetworkUiState(
    val wifiNetworks: List<ScanResult> = emptyList(),
    val isScanning: Boolean = false,
    val showLocationEnableDialog: Boolean = false,
    val showWifiEnableDialog: Boolean = false,
    val connectionResult: Boolean? = null // null = idle, true/false = result
)

@HiltViewModel
class NetworkViewModel @Inject constructor(
    private val wifiRepository: WifiRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(NetworkUiState())
    val uiState = _uiState.asStateFlow()

    // Track permission state
    private val _permissionsGranted = MutableStateFlow(false)

    init {
        viewModelScope.launch {
            combine(
                wifiRepository.getWifiScanResults(),
                wifiRepository.isWifiScanningInProgress(),
                _permissionsGranted
            ) { networks, isScanning, permissionsGranted ->
                NetworkUiState(
                    wifiNetworks = networks,
                    isScanning = isScanning,
                    // Keep other dialog states
                    showLocationEnableDialog = _uiState.value.showLocationEnableDialog,
                    showWifiEnableDialog = _uiState.value.showWifiEnableDialog,
                    connectionResult = _uiState.value.connectionResult
                )
            }.collect { state ->
                _uiState.value = state
            }
        }
    }

    /**
     * Updates permission state and triggers scan if permissions are granted
     */
    fun updatePermissionState(granted: Boolean) {
        _permissionsGranted.value = granted
        if (granted) {
            // If permissions were just granted, start scan
            performScan()
        }
    }

    fun scanWifiNetworks() {
        if (!_permissionsGranted.value) {
            // Don't try to scan without permissions
            _uiState.update { it.copy(connectionResult = false) }
            return
        }

        performScan()
    }

    private fun performScan() {
        if (!wifiRepository.checkLocationEnabled()) {
            _uiState.update { it.copy(showLocationEnableDialog = true) }
            return
        }

        if (!wifiRepository.isWifiEnabled()) {
            _uiState.update { it.copy(showWifiEnableDialog = true) }
            return
        }

        wifiRepository.scanWifiNetworks()
    }

    fun onLocationDialogDismiss() {
        _uiState.update { it.copy(showLocationEnableDialog = false) }
    }

    fun onWifiDialogDismiss() {
        _uiState.update { it.copy(showWifiEnableDialog = false) }
    }



    fun connectToWifi(ssid: String, password: String?, callback: (success: Boolean) -> Unit) {
        wifiRepository.connectToWifi(ssid, password) { success ->
            viewModelScope.launch {
                _uiState.update {
                    it.copy(
                        connectionResult = success,
                        showLocationEnableDialog = false,
                        showWifiEnableDialog = false
                    )
                }

                callback(success)
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        // Clean up if your WifiRepositoryImpl has a cleanup method
        if (wifiRepository is WifiRepositoryImpl) {
            wifiRepository.cleanup()
        }
    }
}