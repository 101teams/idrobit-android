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
}
