package com.idrolife.app.presentation.viewmodel

import android.net.wifi.ScanResult
import android.widget.Toast
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.idrolife.app.service.WifiRepository
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

    init {
        viewModelScope.launch {
            combine(
                wifiRepository.getWifiScanResults(),
                wifiRepository.isWifiScanningInProgress()
            ) { networks, isScanning ->
                NetworkUiState(
                    wifiNetworks = networks,
                    isScanning = isScanning
                )
            }.collect { state ->
                _uiState.value = state
            }
        }

        _uiState.update { it.copy(wifiNetworks = listOf(
            ScanResult().apply {SSID = "SSID1" },
            ScanResult().apply {SSID = "SSID2" },
            ScanResult().apply {SSID = "SSID3" },
        )) }
    }

    fun scanWifiNetworks() {
        if (!wifiRepository.checkLocationEnabled()) {
            _uiState.update { it.copy(showLocationEnableDialog = true) }
            return
        }

        if (!wifiRepository.enableWifi()) {
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

    fun connectToWifi(ssid: String, password: String?) {
        wifiRepository.connectToWifi(ssid, password) { success ->
            _uiState.update {
                it.copy(
                    connectionResult = success,
                    showLocationEnableDialog = false,
                    showWifiEnableDialog = false
                )
            }

            Toast.makeText(
                null,
                if (success) "Connected to $ssid" else "Failed to connect to $ssid",
                Toast.LENGTH_SHORT
            ).show()
        }
    }
}