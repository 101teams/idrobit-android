package com.idrolife.app.presentation.viewmodel

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.idrolife.app.data.api.device.DevicesItem
import com.idrolife.app.data.api.irrigation.IrrigationConfigNominalFlowDataProduct
import com.idrolife.app.data.api.sensor.RhsItem
import com.idrolife.app.data.api.sensor.SensorMeteostatData
import com.idrolife.app.data.api.sensor.SensorSatstatData
import com.idrolife.app.data.api.sensor.SoilMoistureMarkerRequest
import com.idrolife.app.service.DeviceService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import java.text.SimpleDateFormat
import java.util.Date
import javax.inject.Inject

@HiltViewModel
class DeviceViewModel @Inject constructor(
    private val deviceService: DeviceService
): ViewModel() {
    private val _devices = mutableStateOf<List<DevicesItem?>>(emptyList())
    val devices: State<List<DevicesItem?>> = _devices

    private val _selectedDevice = mutableStateOf<DevicesItem?>(null)
    val selectedDevice: State<DevicesItem?> = _selectedDevice

    private val _deviceSensorMeteostat = mutableStateOf<SensorMeteostatData?>(null)
    val deviceSensorMeteostat: State<SensorMeteostatData?> = _deviceSensorMeteostat

    private val _deviceSensorSatstat = mutableStateOf<SensorSatstatData?>(null)
    val deviceSensorSatstat: State<SensorSatstatData?> = _deviceSensorSatstat

    private val _sensorSoilHumidity = mutableStateOf<List<RhsItem?>>(emptyList())
    val sensorSoilHumidity: State<List<RhsItem?>> = _sensorSoilHumidity

    private val _irrigationConfigNominalFlow = mutableStateOf<List<IrrigationConfigNominalFlowDataProduct>>(
        emptyList()
    )
    val irrigationConfigNominalFlow: State<List<IrrigationConfigNominalFlowDataProduct>> = _irrigationConfigNominalFlow

    val isLoading = mutableStateOf(false)
    val setMarkerLoading = mutableStateOf(false)

    fun resetToken() {
        deviceService.resetToken()
    }

    suspend fun getDevices(): Pair<List<DevicesItem?>, String> {
        val result = deviceService.getDevices()
        var deviceData: List<DevicesItem?> = arrayListOf()

        if (result.first != null) {
            deviceData = result.first?.data?.devices!!
        }

        for (data in deviceData) {
            val sdf = SimpleDateFormat("dd/M/yyyy hh:mm:ss")
            val currentDate = sdf.format(Date())

            data?.responseDateTime = currentDate
        }

        _devices.value = deviceData

        return Pair(_devices.value, result.second)
    }


    suspend fun startPeriodicFetchingDevices() {
        delay(5000L)

        while (true) {
            getDevices()

            delay(5000L)
        }
    }

    suspend fun getDeviceByID(id: String): Pair<DevicesItem?, String> {
        val result = deviceService.getDevicesByID(id)
        var deviceData: DevicesItem? = null

        if (result.first != null) {
            deviceData = result.first?.data?.devices
        }

        _selectedDevice.value = deviceData

        return Pair(_selectedDevice.value, result.second)
    }

    suspend fun startPeriodicFetchingDevicesByID(id: String) {
        delay(5000L)

        while (true) {
            getDeviceByID(id)

            delay(5000L)
        }
    }

    suspend fun getSensorMeteostat(deviceCode: String): Pair<SensorMeteostatData?, String> {
        val result = deviceService.getSensorMeteostat(deviceCode)
        var deviceData: SensorMeteostatData? = null

        if (result.first != null) {
            deviceData = result.first?.data
        }

        _deviceSensorMeteostat.value = deviceData

        return Pair(_deviceSensorMeteostat.value, result.second)
    }

    suspend fun startPeriodicFetchingMeteostatByCode(deviceCode: String) {
        delay(5000L)

        while (true) {
            getSensorMeteostat(deviceCode)

            delay(5000L)
        }
    }

    suspend fun getSensorSatstat(deviceCode: String): Pair<SensorSatstatData?, String> {
        val result = deviceService.getSensorSatstat(deviceCode)
        var deviceData: SensorSatstatData? = null

        if (result.first != null) {
            deviceData = result.first?.data
        }

        _deviceSensorSatstat.value = deviceData

        return Pair(_deviceSensorSatstat.value, result.second)
    }

    suspend fun startPeriodicFetchingSatstatByCode(deviceCode: String) {
        delay(5000L)

        while (true) {
            getSensorSatstat(deviceCode)

            delay(5000L)
        }
    }

    suspend fun getSensorSoilHumidity(deviceCode: String): Pair<List<RhsItem?>, String> {
        val result = deviceService.getSensorRH(deviceCode)
        var sensorSoilHumidity: List<RhsItem?> = arrayListOf()

        if (result.first != null) {
            sensorSoilHumidity = result.first?.data?.rhs!!
        }

        _sensorSoilHumidity.value = sensorSoilHumidity

        return Pair(_sensorSoilHumidity.value, result.second)
    }

    suspend fun postSoilMoistureMarker(markerData: SoilMoistureMarkerRequest): String? {
        setMarkerLoading.value = true
        val result = deviceService.postSoilMoistureMarker(markerData)
        setMarkerLoading.value = false
        return if (result.first) null else result.second
    }

    suspend fun getIrrigationConfigNominalFlow(deviceCode: String): Pair<List<IrrigationConfigNominalFlowDataProduct>, String> {
        val result = deviceService.getIrrigationConfigNominalFlow(deviceCode)
        var irrigationConfigNominalFlow: MutableList<IrrigationConfigNominalFlowDataProduct> = arrayListOf()

        if (result.first != null && result.first?.data != null && result.first?.data?.dynamicFields != null) {
            var evIndex = 2000
            val data = result.first!!.data?.dynamicFields!!
            for (i in 0..data.size / 5) {
                if (data.containsKey("S${evIndex}")) {
                    var nominalValue: String? = null
                    var auto = true

                    if (data["S${evIndex + 5}"] != "65535") {
                        nominalValue = data["S${evIndex + 5}"]
                        auto = false
                    }
                    val dataItems = IrrigationConfigNominalFlowDataProduct(
                        evSerial = data["S${evIndex}"],
                        station = data["S${evIndex + 2}"],
                        pump = data["S${evIndex + 3}"],
                        master = data["S${evIndex + 4}"],
                        nominalValue = nominalValue,
                        auto = auto,
                    )

                    irrigationConfigNominalFlow.add(dataItems)
                }

                evIndex += 6
            }
        }

        _irrigationConfigNominalFlow.value = irrigationConfigNominalFlow

        return Pair(_irrigationConfigNominalFlow.value, result.second)
    }
}
