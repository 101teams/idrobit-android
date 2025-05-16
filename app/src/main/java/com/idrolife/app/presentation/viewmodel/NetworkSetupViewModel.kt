package com.idrolife.app.presentation.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.idrolife.app.service.TcpClient
import com.idrolife.app.utils.PrefManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class NetworkSetupUiState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val isSuccess: Boolean = false
)

@HiltViewModel
class NetworkSetupViewModel @Inject constructor(
    private val prefManager: PrefManager,
    private val tcpClient: TcpClient,
    savedStateHandle: SavedStateHandle
) : ViewModel() {
    private val ssid: String = checkNotNull(savedStateHandle["ssid"])

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
                        handleServerSetup()
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

    private suspend fun handleServerSetup() {
        when (val serverResp = tcpClient.sendAndReceive("\$SERVER?\r\n")) {
            is TcpClient.Result.Success -> {
                val response = serverResp.response
                if (response.startsWith("\$SERVER=")) {
                    val serverData = response.removePrefix("\$SERVER=")
                    prefManager.setServerData(serverData)
                    tcpClient.sendAndReceive("\$REBOOT\r\n")
                    _uiState.value = NetworkSetupUiState(isSuccess = true)
                } else {
                    _uiState.value = NetworkSetupUiState(error = "Unexpected server response")
                }
            }

            is TcpClient.Result.Error -> {
                _uiState.value = NetworkSetupUiState(error = "Error fetching server info")
            }
        }
    }
}
