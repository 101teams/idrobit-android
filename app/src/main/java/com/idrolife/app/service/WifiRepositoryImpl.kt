package com.idrolife.app.service

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.location.LocationManager
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import android.net.wifi.ScanResult
import android.net.wifi.WifiManager
import android.net.wifi.WifiNetworkSpecifier
import android.os.Build
import androidx.annotation.RequiresApi
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import javax.inject.Singleton
import javax.inject.Inject

@Singleton
class WifiRepositoryImpl @Inject constructor(
    private val wifiManager: WifiManager,
    private val locationManager: LocationManager,
    @ApplicationContext private val context: Context
) : WifiRepository {

    private val _wifiNetworks = MutableStateFlow<List<ScanResult>>(emptyList())
    private val _isScanning = MutableStateFlow(false)

    private val wifiScanReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            if (intent.action == WifiManager.SCAN_RESULTS_AVAILABLE_ACTION) {
                val results = wifiManager.scanResults.filter { !it.SSID.contains("MQAIR-") }
                _wifiNetworks.value = results
                _isScanning.value = false
            }
        }
    }

    init {
        context.registerReceiver(
            wifiScanReceiver,
            IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION)
        )
    }

    override fun scanWifiNetworks() {
        _isScanning.value = true
        if (wifiManager.isWifiEnabled) {
            wifiManager.startScan()
        }
    }

    override fun getWifiScanResults(): Flow<List<ScanResult>> = _wifiNetworks

    override fun isWifiScanningInProgress(): Flow<Boolean> = _isScanning

    override fun checkLocationEnabled(): Boolean {
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
    }

    override fun enableWifi(): Boolean {
        return if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
            wifiManager.setWifiEnabled(true)
        } else {
            false // Not allowed to enable Wi-Fi from app starting Android 10
        }
    }

    override fun connectToWifi(ssid: String, password: String?, callback: (Boolean) -> Unit) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            connectWithNetworkSpecifier(ssid, password, callback)
        } else {
            connectWithWifiManager(ssid, password, callback)
        }
    }

    private fun connectWithWifiManager(ssid: String, password: String?, callback: (Boolean) -> Unit) {
        val config = android.net.wifi.WifiConfiguration().apply {
            SSID = "\"$ssid\""
            if (password.isNullOrEmpty()) {
                allowedKeyManagement.set(android.net.wifi.WifiConfiguration.KeyMgmt.NONE)
            } else {
                preSharedKey = "\"$password\""
            }
        }

        val networkId = wifiManager.addNetwork(config)
        if (networkId == -1) {
            callback(false)
            return
        }

        val enabled = wifiManager.enableNetwork(networkId, true)
        wifiManager.reconnect()
        callback(enabled)
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    private fun connectWithNetworkSpecifier(ssid: String, password: String?, callback: (Boolean) -> Unit) {
        val specifierBuilder = WifiNetworkSpecifier.Builder()
            .setSsid(ssid)

        if (!password.isNullOrEmpty()) {
            specifierBuilder.setWpa2Passphrase(password)
        }

        val specifier = specifierBuilder.build()

        val request = NetworkRequest.Builder()
            .addTransportType(NetworkCapabilities.TRANSPORT_WIFI)
            .setNetworkSpecifier(specifier)
            .build()

        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

        val callbackImpl = object : ConnectivityManager.NetworkCallback() {
            override fun onAvailable(network: Network) {
                super.onAvailable(network)
                connectivityManager.bindProcessToNetwork(network)
                callback(true)
            }

            override fun onUnavailable() {
                super.onUnavailable()
                callback(false)
            }

            override fun onLost(network: Network) {
                super.onLost(network)
                callback(false)
            }
        }

        connectivityManager.requestNetwork(request, callbackImpl)
    }
}
