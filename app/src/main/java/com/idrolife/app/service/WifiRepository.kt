package com.idrolife.app.service

import android.net.wifi.ScanResult
import kotlinx.coroutines.flow.Flow

interface WifiRepository {
    fun scanWifiNetworks()
    fun getWifiScanResults(): Flow<List<ScanResult>>
    fun isWifiScanningInProgress(): Flow<Boolean>
    fun checkLocationEnabled(): Boolean
    fun isWifiEnabled(): Boolean
    fun connectToWifi(ssid: String, password: String?, callback: (success: Boolean) -> Unit)
    fun getCurrentWifiInfo(): Pair<String?, Int?>
    fun reconnectToWifi(ssid: String, callback: (Boolean) -> Unit)
    fun isInternetAvailable(): Boolean
    fun waitForInternetConnectivity(timeoutMs: Long = 10000, callback: (Boolean) -> Unit)
    
    // Enhanced network restoration methods
    fun disconnectCurrentWifi(callback: (Boolean) -> Unit)
    fun removeTemporaryNetwork(ssid: String, callback: (Boolean) -> Unit)
    fun getDetailedWifiInfo(): Triple<String?, Int?, String?>
    fun connectToWifiWithValidation(ssid: String, password: String?, timeoutMs: Long = 30000, callback: (Boolean) -> Unit)
    fun isConnectedToSpecificNetwork(ssid: String): Boolean
}
