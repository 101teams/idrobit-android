package com.idrolife.app.service

import android.net.wifi.ScanResult
import kotlinx.coroutines.flow.Flow

interface WifiRepository {
    fun scanWifiNetworks()
    fun getWifiScanResults(): Flow<List<ScanResult>>
    fun isWifiScanningInProgress(): Flow<Boolean>
    fun checkLocationEnabled(): Boolean
    fun enableWifi(): Boolean
    fun connectToWifi(ssid: String, password: String?, callback: (success: Boolean) -> Unit)
}
