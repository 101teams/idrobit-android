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
    private val connectivityManager: ConnectivityManager
) : WifiRepository {
    private val _wifiNetworks = MutableStateFlow<List<ScanResult>>(emptyList())
    private val _isScanning = MutableStateFlow(false)
    private var scanResultsReceiver: BroadcastReceiver? = null
    private var activeNetworkCallback: ConnectivityManager.NetworkCallback? = null

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
        // Clean up any existing network callback first
        activeNetworkCallback?.let {
            try {
                connectivityManager.unregisterNetworkCallback(it)
                connectivityManager.bindProcessToNetwork(null) // Clear existing binding
            } catch (e: Exception) {
                Log.d("WifiRepository", "Error clearing existing network callback: ${e.message}")
            }
        }
        
        val specifierBuilder = WifiNetworkSpecifier.Builder()
            .setSsid(ssid)

        if (!password.isNullOrEmpty()) {
            specifierBuilder.setWpa2Passphrase(password)
        }

        val specifier = specifierBuilder.build()

        val request = NetworkRequest.Builder()
            .addTransportType(NetworkCapabilities.TRANSPORT_WIFI)
            .removeCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
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
        activeNetworkCallback = callbackImpl
    }

    override fun getCurrentWifiInfo(): Pair<String?, Int?> {
        val info = wifiManager.connectionInfo
        val ssid = info?.ssid?.replace("\"", "")
        val networkId = info?.networkId
        return Pair(ssid, networkId)
    }

    override fun reconnectToWifi(ssid: String, callback: (Boolean) -> Unit) {
        Log.d("WifiRepository", "Attempting to reconnect to WiFi: $ssid")
        
        // Clean up any existing network binding first
        activeNetworkCallback?.let {
            try {
                connectivityManager.unregisterNetworkCallback(it)
                connectivityManager.bindProcessToNetwork(null) // Clear existing binding
                Log.d("WifiRepository", "Cleared existing network binding")
            } catch (e: Exception) {
                Log.d("WifiRepository", "Error clearing network binding: ${e.message}")
            }
            activeNetworkCallback = null
        }
        
        val configuredNetworks = wifiManager.configuredNetworks
        val target = configuredNetworks?.find { it.SSID.replace("\"", "") == ssid }
        
        if (target != null) {
            Log.d("WifiRepository", "Found configured network for $ssid, networkId: ${target.networkId}")
            val enabled = wifiManager.enableNetwork(target.networkId, true)
            val reconnected = wifiManager.reconnect()
            Log.d("WifiRepository", "Network enabled: $enabled, reconnect called: $reconnected")
            
            // Wait a bit for connection to establish before calling back
            Handler(Looper.getMainLooper()).postDelayed({
                val currentWifi = getCurrentWifiInfo().first
                Log.d("WifiRepository", "After reconnection, current WiFi: $currentWifi")
                callback(enabled && reconnected)
            }, 1500) // 1.5 second delay
        } else {
            Log.w("WifiRepository", "No configured network found for SSID: $ssid")
            callback(false)
        }
    }

    override fun isInternetAvailable(): Boolean {
        return try {
            val activeNetwork = connectivityManager.activeNetwork
            Log.d("WifiRepository", "Active network: $activeNetwork")
            
            if (activeNetwork == null) {
                Log.d("WifiRepository", "No active network found")
                return false
            }
            
            val networkCapabilities = connectivityManager.getNetworkCapabilities(activeNetwork)
            if (networkCapabilities == null) {
                Log.d("WifiRepository", "No network capabilities found")
                return false
            }
            
            val hasInternet = networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
            val isValidated = networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED)
            
            Log.d("WifiRepository", "Network capabilities - Internet: $hasInternet, Validated: $isValidated")
            
            hasInternet && isValidated
        } catch (e: Exception) {
            Log.e("WifiRepository", "Error checking internet availability: ${e.message}")
            false
        }
    }

    override fun waitForInternetConnectivity(timeoutMs: Long, callback: (Boolean) -> Unit) {
        Log.d("WifiRepository", "Waiting for internet connectivity with timeout: ${timeoutMs}ms")
        
        if (isInternetAvailable()) {
            Log.d("WifiRepository", "Internet already available")
            callback(true)
            return
        }
        
        var callbackInvoked = false
        
        val networkCallback = object : ConnectivityManager.NetworkCallback() {
            override fun onCapabilitiesChanged(network: Network, networkCapabilities: NetworkCapabilities) {
                super.onCapabilitiesChanged(network, networkCapabilities)
                val hasInternet = networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
                val isValidated = networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED)
                
                Log.d("WifiRepository", "Network capabilities changed - Internet: $hasInternet, Validated: $isValidated")
                
                if (hasInternet && isValidated && !callbackInvoked) {
                    callbackInvoked = true
                    Log.d("WifiRepository", "Internet connectivity established")
                    try {
                        connectivityManager.unregisterNetworkCallback(this)
                    } catch (e: Exception) {
                        Log.w("WifiRepository", "Error unregistering network callback: ${e.message}")
                    }
                    callback(true)
                }
            }
            
            override fun onLost(network: Network) {
                super.onLost(network)
                Log.d("WifiRepository", "Network lost")
            }
        }
        
        val request = NetworkRequest.Builder()
            .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
            .addCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED)
            .build()
        
        connectivityManager.registerNetworkCallback(request, networkCallback)
        
        // Set timeout
        Handler(Looper.getMainLooper()).postDelayed({
            if (!callbackInvoked) {
                callbackInvoked = true
                try {
                    connectivityManager.unregisterNetworkCallback(networkCallback)
                } catch (e: Exception) {
                    Log.w("WifiRepository", "Error unregistering timeout callback: ${e.message}")
                }
                Log.d("WifiRepository", "Internet connectivity wait timeout")
                callback(false)
            }
        }, timeoutMs)
    }

    // Clean up when repository is no longer needed
    fun cleanup() {
        unregisterReceiver()
        activeNetworkCallback?.let {
            try {
                connectivityManager.unregisterNetworkCallback(it)
            } catch (e: Exception) {
                // Network callback not registered, ignore
            }

            activeNetworkCallback = null
        }
    }
}
