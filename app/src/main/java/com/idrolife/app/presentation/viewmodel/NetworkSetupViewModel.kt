package com.idrolife.app.presentation.viewmodel

import android.content.Context
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.idrolife.app.service.TcpClient
import com.idrolife.app.utils.PrefManager
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

data class NetworkSetupUiState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val isSuccess: Boolean = false
)

@HiltViewModel
class NetworkSetupViewModel @Inject constructor(
    @ApplicationContext context: Context,
    private val tcpClient: TcpClient,
    savedStateHandle: SavedStateHandle
) : ViewModel() {
    private val ssid: String = checkNotNull(savedStateHandle["ssid"])
    private val _uiState = MutableStateFlow(NetworkSetupUiState())
    val uiState = _uiState.asStateFlow()
    private val prefManager = PrefManager(context)

    init {
        tcpClient.initialize { response ->
            processResponse(response)
        }
    }

    private var currentPassword = ""

    fun getSsid(): String {
        return ssid
    }

    fun configureNetwork(password: String) {
        currentPassword = password
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }

            try {
                withContext(Dispatchers.IO) {
                    tcpClient.run()
                    delay(2000) // Wait for connection
                    sendConfiguration(password)
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        error = e.message ?: "Connection error"
                    )
                }
            }
        }
    }

    private fun sendConfiguration(password: String) {
        tcpClient.sendMessage("\r\n\$SSID=$ssid,$password\r\n")
    }

    private fun processResponse(response: String) {
        when {
            response == TcpClient.TCP_EXCEPTION_ERROR -> {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        error = "Connection error"
                    )
                }
            }
            response.contains("\$SERVER=") -> {
                val serverData = response.substring(8)
                viewModelScope.launch {
                    prefManager.saveServerData(serverData)
                    triggerReboot()
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            isSuccess = true
                        )
                    }
                }
            }
            response.contains("\$SSID=") -> {
                if (response.contains("\$SSID=$ssid,$currentPassword")) {
                    tcpClient.sendMessage("\$SERVER?\r\n")
                } else {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            error = "Wrong password"
                        )
                    }
                }
            }
        }
    }

    fun clearError() {
        _uiState.update { it.copy(error = null) }
    }

    private fun triggerReboot() {
        tcpClient.sendMessage("\$REBOOT\r\n")
    }

    override fun onCleared() {
        tcpClient.stopClient()
        super.onCleared()
    }
}
