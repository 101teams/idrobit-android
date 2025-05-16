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
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.annotation.RequiresApi
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class WifiRepositoryImpl @Inject constructor(
    @ApplicationContext private val context: Context,
    private val wifiManager: WifiManager,
    private val locationManager: LocationManager,
) : WifiRepository {
    private val _wifiNetworks = MutableStateFlow<List<ScanResult>>(emptyList())
    private val _isScanning = MutableStateFlow(false)
    private var scanResultsReceiver: BroadcastReceiver? = null

    override fun scanWifiNetworks() {
        _isScanning.value = true

        // Register receiver dynamically each time to avoid issues with context
        unregisterReceiver() // Remove any existing receiver
        registerReceiver()   // Register a new one

        if (!wifiManager.isWifiEnabled) {
            Log.d("WifiRepository", "WiFi is not enabled")
            _isScanning.value = false
            return
        }

        // On Android 10+ startScan is deprecated and may not work reliably
        val success = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            // For Android 11+, use alternative approach - get current scan results
            Log.d("WifiRepository", "Android 11+: Getting existing scan results")
            val currentResults = wifiManager.scanResults
            _wifiNetworks.value = currentResults

            // Still call startScan, but don't rely on it fully
            wifiManager.startScan()
            true
        } else {
            // For Android 10 and below
            val success = wifiManager.startScan()
            Log.d("WifiRepository", "Started scan: $success")
            success
        }

        // Set a timeout in case the broadcast is never received
        Handler(Looper.getMainLooper()).postDelayed({
            if (_isScanning.value) {
                Log.d("WifiRepository", "Scan timeout - getting available results")
                _isScanning.value = false
                // Get whatever results are available
                val results = wifiManager.scanResults
                _wifiNetworks.value = results
            }
        }, 10000) // 10 seconds timeout

        if (!success) {
            Log.d("WifiRepository", "Scan start failed - getting available results")
            _isScanning.value = false
            // Get whatever results are available anyway
            val results = wifiManager.scanResults
            _wifiNetworks.value = results
        }
    }

    private fun registerReceiver() {
        scanResultsReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context, intent: Intent) {
                Log.d("WifiRepository", "Scan results broadcast received")
                if (intent.action == WifiManager.SCAN_RESULTS_AVAILABLE_ACTION) {
                    val success = intent.getBooleanExtra(WifiManager.EXTRA_RESULTS_UPDATED, false)
                    Log.d("WifiRepository", "Scan results updated: $success")

                    val results = wifiManager.scanResults
                    Log.d("WifiRepository", "Found ${results.size} networks")
                    _wifiNetworks.value = results
                    _isScanning.value = false
                }
            }
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            context.registerReceiver(
                scanResultsReceiver,
                IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION),
                Context.RECEIVER_EXPORTED
            )
        } else {
            context.registerReceiver(
                scanResultsReceiver,
                IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION)
            )
        }
    }

    private fun unregisterReceiver() {
        scanResultsReceiver?.let {
            try {
                context.unregisterReceiver(it)
            } catch (e: IllegalArgumentException) {
                // Receiver not registered, ignore
            }
        }

        scanResultsReceiver = null
    }

    override fun getWifiScanResults(): Flow<List<ScanResult>> = _wifiNetworks

    override fun isWifiScanningInProgress(): Flow<Boolean> = _isScanning

    override fun checkLocationEnabled(): Boolean {
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
    }

    override fun isWifiEnabled(): Boolean { // check wifi real state
        return wifiManager.isWifiEnabled
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

    // Clean up when repository is no longer needed
    fun cleanup() {
        unregisterReceiver()
    }
}
