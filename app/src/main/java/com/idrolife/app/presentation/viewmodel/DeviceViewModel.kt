package com.idrolife.app.presentation.viewmodel

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.idrolife.app.data.api.device.DevicesItem
import com.idrolife.app.data.api.irrigation.IrrigationConfigAdvanceConfig
import com.idrolife.app.data.api.irrigation.IrrigationConfigDeviceGeoRequest
import com.idrolife.app.data.api.irrigation.IrrigationConfigEVConfigList
import com.idrolife.app.data.api.irrigation.IrrigationConfigEVRadioStatus
import com.idrolife.app.data.api.irrigation.IrrigationConfigGeneralMVConfig
import com.idrolife.app.data.api.irrigation.IrrigationConfigGeneralPumpConfig
import com.idrolife.app.data.api.irrigation.IrrigationConfigGeneralSatConfig
import com.idrolife.app.data.api.irrigation.IrrigationConfigNominalFlowDataProduct
import com.idrolife.app.data.api.sensor.RhsItem
import com.idrolife.app.data.api.sensor.SensorMeteostatData
import com.idrolife.app.data.api.sensor.SensorSatstatData
import com.idrolife.app.data.api.sensor.SoilMoistureMarkerRequest
import com.idrolife.app.service.DeviceService
import com.idrolife.app.utils.Helper
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
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

    private val _irrigationConfigGeneralSatConfig = mutableStateOf<IrrigationConfigGeneralSatConfig?>(
        null
    )
    val irrigationConfigGeneralSatConfig: State<IrrigationConfigGeneralSatConfig?> = _irrigationConfigGeneralSatConfig

    private val _irrigationConfigGeneralPumpConfig = mutableStateOf<IrrigationConfigGeneralPumpConfig?>(
        null
    )
    val irrigationConfigGeneralPumpConfig: State<IrrigationConfigGeneralPumpConfig?> = _irrigationConfigGeneralPumpConfig

    private val _irrigationConfigGeneralMVConfig = mutableStateOf<IrrigationConfigGeneralMVConfig?>(
        null
    )
    val irrigationConfigGeneralMVConfig: State<IrrigationConfigGeneralMVConfig?> = _irrigationConfigGeneralMVConfig

    private val _irrigationConfigAdvanceConfig = mutableStateOf<IrrigationConfigAdvanceConfig?>(
        null
    )
    val irrigationConfigAdvanceConfig: State<IrrigationConfigAdvanceConfig?> = _irrigationConfigAdvanceConfig

    private val _irrigationConfigEVRadioStatus = mutableStateOf<List<IrrigationConfigEVRadioStatus>>(
        emptyList()
    )
    val irrigationConfigEVRadioStatus: State<List<IrrigationConfigEVRadioStatus>> = _irrigationConfigEVRadioStatus

    var _irrigationConfigEvConfigList = mutableStateOf<List<IrrigationConfigEVConfigList>>(
        emptyList()
    )
    val irrigationConfigEvConfigList: State<List<IrrigationConfigEVConfigList>> = _irrigationConfigEvConfigList


    val isLoading = mutableStateOf(false)
    val setMarkerLoading = mutableStateOf(false)
    val postDataLoading = mutableStateOf(false)

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
            if (data?.lastHeard != null) {
                data.responseDateTime = Helper().dynamicFormatDate(data.lastHeard, "dd/M/yyyy HH:mm:ss") ?: "-"
            }
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
        var fields =""
        for (i in 2000..3151) {
            if (i != 2000) {
                fields += ","
            }
            fields += "S${i}"
        }
        val measurement = "EVCONFIG"

        val result = deviceService.getIrrigationConfigNominalFlow(deviceCode, fields, measurement)
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
                        nominalValue = nominalValue ?: "0",
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

    suspend fun getIrrigationConfigGeneralSatConfig(deviceCode: String): Pair<IrrigationConfigGeneralSatConfig?, String> {
        var fields ="S70,S1000,S1002,S1003,S1005,S1006,S1007,S1008,S1009,S1010,S1011,S1012,S1013,S1014,S1017,S1018,S1019,S1020,S1021,S1032,S1033,S1036,S1037,S1045,S1046,S1047,S1048"

        val measurement = "SATCONFIG"

        val result = deviceService.getIrrigationConfigNominalFlow(deviceCode, fields, measurement)
        var irrigationConfigGeneralSatConfig: IrrigationConfigGeneralSatConfig? = null

        if (result.first != null && result.first?.data != null && result.first?.data?.dynamicFields != null) {
            val data = result.first!!.data?.dynamicFields!!

            val returnedData = IrrigationConfigGeneralSatConfig (
                plantOperationStatus= data["S70"],
                password= data["S1000"],
                solarIntensity= data["S1002"],
                windSpeed= data["S1003"],
                evMaster = data["S1005"],
                ecCommand= data["S1006"],
                pulsesFlow= data["S1007"],
                solarIrradiation= data["S1008"],
                windSensor= data["S1009"],
                temperature= data["S1010"],
                humidity= data["S1011"],
                maxActiveProgram= data["S1012"],
                entry1= data["S1013"],
                entry2= data["S1014"],
                entry1forDelay= data["S1017"],
                entry2forDelay= data["S1018"],
                flowOffTolerance= data["S1019"],
                flowOff= data["S1020"],
                flowAlarmDelay= data["S1021"],
                entry3= data["S1032"],
                entry4= data["S1033"],
                entry3forDelay= data["S1036"],
                entry4forDelay= data["S1037"],
                pressureMin= data["S1045"],
                pressureMax= data["S1046"],
                delayAlarmTimeLowPressure= data["S1047"],
                delayAlarmTimeHighPressure= data["S1048"],
            )


            irrigationConfigGeneralSatConfig = returnedData
        }

        _irrigationConfigGeneralSatConfig.value = irrigationConfigGeneralSatConfig

        return Pair(_irrigationConfigGeneralSatConfig.value, result.second)
    }

    suspend fun getIrrigationConfigGeneralPumpConfig(deviceCode: String): Pair<IrrigationConfigGeneralPumpConfig?, String> {
        var fields ="S1516,1524"

        val measurement = "PUMPCONFIG"

        val result = deviceService.getIrrigationConfigNominalFlow(deviceCode, fields, measurement)
        var irrigationConfigGeneralPumpConfig: IrrigationConfigGeneralPumpConfig? = null

        if (result.first != null && result.first?.data != null && result.first?.data?.dynamicFields != null) {
            val data = result.first!!.data?.dynamicFields!!

            val returnedData = IrrigationConfigGeneralPumpConfig (
                pulses = data["S1516"],
                pumpDeactivationDelay = data["S1524"],
            )


            irrigationConfigGeneralPumpConfig = returnedData
        }

        _irrigationConfigGeneralPumpConfig.value = irrigationConfigGeneralPumpConfig

        return Pair(_irrigationConfigGeneralPumpConfig.value, result.second)
    }

    suspend fun getIrrigationConfigGeneralMVConfig(deviceCode: String): Pair<IrrigationConfigGeneralMVConfig?, String> {
        var fields ="S1616"

        val measurement = "MVCONFIG"

        val result = deviceService.getIrrigationConfigNominalFlow(deviceCode, fields, measurement)
        var irrigationConfigGeneralMVConfig: IrrigationConfigGeneralMVConfig? = null

        if (result.first != null && result.first?.data != null && result.first?.data?.dynamicFields != null) {
            val data = result.first!!.data?.dynamicFields!!

            val returnedData = IrrigationConfigGeneralMVConfig (
                delayBetweenMSandEV = data["S1616"],
            )


            irrigationConfigGeneralMVConfig = returnedData
        }

        _irrigationConfigGeneralMVConfig.value = irrigationConfigGeneralMVConfig

        return Pair(_irrigationConfigGeneralMVConfig.value, result.second)
    }

    suspend fun postIrrigationConfigNominalFlow(deviceCode: String, data: Map<String, String>): String? {
        val result = deviceService.postIrrigationConfigNominalFlow(deviceCode, "EVCONFIG", data)
        return if (result.first) null else result.second
    }

    suspend fun postIrrigationConfigGeneralConfig(
        deviceCode: String,
        satData: IrrigationConfigGeneralSatConfig,
        pumpData: IrrigationConfigGeneralPumpConfig,
        mvData: IrrigationConfigGeneralMVConfig): String? {

        //post sat data
        val satConfigData = mutableMapOf<String, String>()
        satConfigData["S70"] = satData.plantOperationStatus!!
        satConfigData["S1000"] = satData.password!!
        satConfigData["S1002"] = satData.solarIntensity!!
        satConfigData["S1003"] = satData.windSpeed!!
        satConfigData["S1005"] = satData.evMaster!!
        satConfigData["S1006"] = satData.ecCommand!!
        satConfigData["S1007"] = satData.pulsesFlow!!
        satConfigData["S1008"] = satData.solarIrradiation!!
        satConfigData["S1009"] = satData.windSensor!!
        satConfigData["S1010"] = satData.temperature!!
        satConfigData["S1011"] = satData.humidity!!
        satConfigData["S1012"] = satData.maxActiveProgram!!
        satConfigData["S1013"] = satData.entry1!!
        satConfigData["S1014"] = satData.entry2!!
        satConfigData["S1017"] = satData.entry1forDelay!!
        satConfigData["S1018"] = satData.entry2forDelay!!
        satConfigData["S1019"] = satData.flowOffTolerance!!
        satConfigData["S1020"] = satData.flowOff!!
        satConfigData["S1021"] = satData.flowAlarmDelay!!
        satConfigData["S1032"] = satData.entry3!!
        satConfigData["S1033"] = satData.entry4!!
        satConfigData["S1036"] = satData.entry3forDelay!!
        satConfigData["S1037"] = satData.entry4forDelay!!
        satConfigData["S1045"] = satData.pressureMin!!
        satConfigData["S1046"] = satData.pressureMax!!
        satConfigData["S1047"] = satData.delayAlarmTimeLowPressure!!
        satConfigData["S1048"] = satData.delayAlarmTimeHighPressure!!

        val resultSatConfig = deviceService.postIrrigationConfigNominalFlow(deviceCode, "SATCONFIG", satConfigData)

        //post pump data
        val pumpConfigData = mutableMapOf<String, String>()
        pumpConfigData["S1516"] = pumpData.pulses!!
        pumpConfigData["S1524"] = pumpData.pumpDeactivationDelay!!
        val resultPumpConfig = deviceService.postIrrigationConfigNominalFlow(deviceCode, "PUMPCONFIG", pumpConfigData)

        //post mv data
        val mvConfigData = mutableMapOf<String, String>()
        mvConfigData["S1616"] = mvData.delayBetweenMSandEV!!
        val resultMVConfig = deviceService.postIrrigationConfigNominalFlow(deviceCode, "MVCONFIG", mvConfigData)

        if (!resultSatConfig.first) {
            return resultSatConfig.second
        } else if (!resultMVConfig.first) {
            return resultMVConfig.second
        } else if (!resultPumpConfig.first) {
            return resultPumpConfig.second
        }

        return null
    }

    suspend fun postIrrigationConfigGeneralConfigCleanMemory(
        deviceCode: String
    ): String? {
        //post mv data
        val mvConfigData = mutableMapOf<String, String>()
        mvConfigData["S1616"] = "2"
        val resultMVConfig = deviceService.postIrrigationConfigNominalFlow(deviceCode, "MVCONFIG", mvConfigData)

        return if (resultMVConfig.first) null else resultMVConfig.second
    }

    //advance config


    suspend fun getIrrigationConfigAdvanceConfig(deviceCode: String): Pair<IrrigationConfigAdvanceConfig?, String> {
        val fields ="I1000,I1001,I1002,I1003,I1004,I1005,I1006"

        val measurement = "ICODCONFIG"

        val result = deviceService.getIrrigationConfigNominalFlow(deviceCode, fields, measurement)
        var irrigationConfigAdvanceConfig: IrrigationConfigAdvanceConfig? = null

        if (result.first != null && result.first?.data != null && result.first?.data?.dynamicFields != null) {
            val data = result.first!!.data?.dynamicFields!!

            val returnedData = IrrigationConfigAdvanceConfig (
                openedCircuit = data["I1000"],
                acknowledgePulseTime = data["I1001"],
                minimumAmpere = data["I1002"],
                activationDelayMaster = data["I1003"],
                activationDelayEV = data["I1004"],
                evHoldingVoltage = data["I1005"],
                triggerPulseTime = data["I1006"],
            )


            irrigationConfigAdvanceConfig = returnedData
        }

        _irrigationConfigAdvanceConfig.value = irrigationConfigAdvanceConfig

        return Pair(_irrigationConfigAdvanceConfig.value, result.second)
    }

    suspend fun postIrrigationConfigAdvanceConfig(
        deviceCode: String,
        postData: IrrigationConfigAdvanceConfig
    ): String? {
        //post mv data
        val data = mutableMapOf<String, String>()
        data["I1000"] = postData.openedCircuit!!
        data["I1001"] = postData.acknowledgePulseTime!!
        data["I1002"] = postData.minimumAmpere!!
        data["I1003"] = postData.activationDelayMaster!!
        data["I1004"] = postData.activationDelayEV!!
        data["I1005"] = postData.evHoldingVoltage!!
        data["I1006"] = postData.triggerPulseTime!!
        val resultMVConfig = deviceService.postIrrigationConfigNominalFlow(deviceCode, "ICODCONFIG", data)

        return if (resultMVConfig.first) null else resultMVConfig.second
    }

    suspend fun getIrrigationConfigEvRadioStatus(deviceCode: String): Pair<List<IrrigationConfigEVRadioStatus>, String> {
        var fields =""
        for (i in 5700..5795) {
            if (i != 5700) {
                fields += ","
            }
            fields += "S${i}"
        }
        val measurement = "RADIOEVSTAT"

        val result = deviceService.getIrrigationConfigNominalFlow(deviceCode, fields, measurement)
        val irrigationConfigEVRadioStatus: MutableList<IrrigationConfigEVRadioStatus> = arrayListOf()

        if (result.first != null && result.first?.data != null && result.first?.data?.dynamicFields != null) {
            val data = result.first!!.data?.dynamicFields!!
            for (i in data) {
                if (i.key.startsWith("S") && i.value.startsWith("C")) {
                    val splittedValue = i.value.split(",")

                    var percetage = "0"
                    if (splittedValue.size > 4) {
                        val value4 = splittedValue[4].toIntOrNull() ?: 0
                        val value5 = splittedValue[5].toIntOrNull() ?: 0

                        percetage = if (value4 + value5 == 0) {
                            "0"
                        } else {
                            ((value5.toDouble() / (value4 + value5)) * 100).let { String.format("%.1f", it) }
                        }
                    }

                    val returnData = IrrigationConfigEVRadioStatus(
                        serialID = if (splittedValue.isNotEmpty()) splittedValue[0] else "0",
                        group = if (splittedValue.size > 1) splittedValue[1] else "0",
                        batteryLevel = if (splittedValue.size > 3) splittedValue[3] else "0",
                        signal = if (splittedValue.size > 7) splittedValue[7] else "0",
                        goodData = if (splittedValue.size > 4) splittedValue[4] else "0",
                        errorData = if (splittedValue.size > 5) splittedValue[5] else "0",
                        errorPercentage = percetage,
                    )

                    irrigationConfigEVRadioStatus.add(returnData)
                }
            }
        }

        _irrigationConfigEVRadioStatus.value = irrigationConfigEVRadioStatus

        return Pair(_irrigationConfigEVRadioStatus.value, result.second)
    }

    suspend fun postIrrigationConfigEVRadioStatusRefresh(
        deviceCode: String,
    ): String? {
        //post mv data
        val data = mutableMapOf<String, String>()
        val resultMVConfig = deviceService.postIrrigationConfigRawControl(deviceCode, "RADIOEVSTAT?", data)

        return if (resultMVConfig.first) null else resultMVConfig.second
    }


    suspend fun getIrrigationConfigEVConfigList(deviceCode: String): Pair<List<IrrigationConfigEVConfigList>, String> {
        var fields =""
        for (i in 2000..3151) {
            if (i != 2000) {
                fields += ","
            }
            fields += "S${i}"
        }
        val measurement = "EVCONFIG"

        val result = deviceService.getIrrigationConfigNominalFlow(deviceCode, fields, measurement)
        var irrigationConfigEvConfigList: MutableList<IrrigationConfigEVConfigList> = arrayListOf()

        if (result.first != null && result.first?.data != null && result.first?.data?.dynamicFields != null) {
            var evIndex = 2000
            val data = result.first!!.data?.dynamicFields!!
            for (i in 0..data.size / 5) {
                if (data.containsKey("S${evIndex}")) {
                    val dataItems = IrrigationConfigEVConfigList(
                        evSerial = data["S${evIndex}"],
                        station = data["S${evIndex + 2}"],
                        pump = data["S${evIndex + 3}"],
                        master = data["S${evIndex + 4}"],
                        nominalValue = "0",
                        index = irrigationConfigEvConfigList.size,
                    )

                    irrigationConfigEvConfigList.add(dataItems)
                }

                evIndex += 6
            }
        }

        _irrigationConfigEvConfigList.value = irrigationConfigEvConfigList

        return Pair(_irrigationConfigEvConfigList.value, result.second)
    }

    suspend fun postIrrigationConfigDeviceGeo(request: IrrigationConfigDeviceGeoRequest): String? {
        val result = deviceService.postIrrigationConfigDeviceGeo(request)
        return if (result.first) null else result.second
    }
}
