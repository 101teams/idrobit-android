package com.idrolife.app.presentation.viewmodel

import android.content.Context
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.idrolife.app.BuildConfig
import com.idrolife.app.R
import com.idrolife.app.data.api.device.CreatePlantRequest
import com.idrolife.app.data.api.device.DeviceRelatedItem
import com.idrolife.app.data.api.device.DevicesItem
import com.idrolife.app.data.api.device.EditPlantRequest
import com.idrolife.app.data.api.device.ResultAlarmsItem
import com.idrolife.app.data.api.irrigation.FertigationProgrammation
import com.idrolife.app.data.api.irrigation.FertigationStatus
import com.idrolife.app.data.api.irrigation.IrrigationConfigAdvanceConfig
import com.idrolife.app.data.api.irrigation.IrrigationConfigDeviceGeoRequest
import com.idrolife.app.data.api.irrigation.IrrigationConfigEVConfigList
import com.idrolife.app.data.api.irrigation.IrrigationConfigEVRadioStatus
import com.idrolife.app.data.api.irrigation.IrrigationConfigGeneralMVConfig
import com.idrolife.app.data.api.irrigation.IrrigationConfigGeneralPumpConfig
import com.idrolife.app.data.api.irrigation.IrrigationConfigGeneralSatConfig
import com.idrolife.app.data.api.irrigation.IrrigationConfigNominalFlowDataProduct
import com.idrolife.app.data.api.irrigation.IrrigationSettingGeneralParameter
import com.idrolife.app.data.api.irrigation.IrrigationSettingScheduleStart
import com.idrolife.app.data.api.irrigation.IrrigationSettingSensorManagement
import com.idrolife.app.data.api.irrigation.IrrigationStatusIdrosatStatus
import com.idrolife.app.data.api.irrigation.IrrigationStatusProgramStatus
import com.idrolife.app.data.api.irrigation.IrrigationStatusStationStatus
import com.idrolife.app.data.api.irrigation.ManualStartProgram
import com.idrolife.app.data.api.irrigation.StationDuration
import com.idrolife.app.data.api.map.DeviceGeoData
import com.idrolife.app.data.api.map.DeviceGeosItem
import com.idrolife.app.data.api.sensor.RhsItem
import com.idrolife.app.data.api.sensor.SensorMeteostatData
import com.idrolife.app.data.api.sensor.SensorSatstatData
import com.idrolife.app.data.api.sensor.SoilMoistureMarkerRequest
import com.idrolife.app.service.DeviceService
import com.idrolife.app.utils.Helper
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

@HiltViewModel
class DeviceViewModel @Inject constructor(
    private val deviceService: DeviceService
): ViewModel() {
    private val _devices = MutableStateFlow<List<DevicesItem?>>(emptyList())
    val devices: StateFlow<List<DevicesItem?>> = _devices

    var _filteredDevices = mutableStateOf<List<DevicesItem?>>(emptyList())
    val filteredDevices: State<List<DevicesItem?>> = _filteredDevices

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

    private var _irrigationSettingGeneralParameter = mutableStateOf<IrrigationSettingGeneralParameter?>(null)
    val irrigationSettingGeneralParameter: State<IrrigationSettingGeneralParameter?> = _irrigationSettingGeneralParameter

    var _irrigationSettingScheduleStart = mutableStateOf<List<IrrigationSettingScheduleStart>>(
        emptyList()
    )
    val irrigationSettingScheduleStart: State<List<IrrigationSettingScheduleStart>> = _irrigationSettingScheduleStart

    private val _deviceRelated = MutableStateFlow<List<DeviceRelatedItem?>>(emptyList())
    val deviceRelated: StateFlow<List<DeviceRelatedItem?>> = _deviceRelated

    private var _irrigationSettingSensorManagement = mutableStateOf<IrrigationSettingSensorManagement?>(null)
    val irrigationSettingSensorManagement: State<IrrigationSettingSensorManagement?> = _irrigationSettingSensorManagement

    private val _irrigationStatusProgramStatus = mutableStateOf<List<IrrigationStatusProgramStatus>>(
        emptyList()
    )
    val irrigationStatusProgramStatus: State<List<IrrigationStatusProgramStatus>> = _irrigationStatusProgramStatus

    private val _irrigationStatusStationStatus = mutableStateOf<List<IrrigationStatusStationStatus>>(
        emptyList()
    )
    val irrigationStatusStationStatus: State<List<IrrigationStatusStationStatus>> = _irrigationStatusStationStatus

    private val _irrigationStatusIdrosatStatusInstantConsumption = mutableStateOf<List<IrrigationStatusIdrosatStatus>>(
        emptyList()
    )
    val irrigationStatusIdrosatStatusInstantConsumption: State<List<IrrigationStatusIdrosatStatus>> = _irrigationStatusIdrosatStatusInstantConsumption

    private val _irrigationStatusIdrosatStatusTotalConsumption = mutableStateOf<List<IrrigationStatusIdrosatStatus>>(
        emptyList()
    )
    val irrigationStatusIdrosatStatusTotalConsumption: State<List<IrrigationStatusIdrosatStatus>> = _irrigationStatusIdrosatStatusTotalConsumption

    private val _evStationName = mutableStateOf<List<Pair<String, String>>>(
        emptyList()
    )
    val evStationName: State<List<Pair<String, String>>> = _evStationName

    private val _manualStartProgram = mutableStateOf<List<ManualStartProgram>>(
        emptyList()
    )
    val manualStartProgram: State<List<ManualStartProgram>> = _manualStartProgram

    private val _fertigationStatus = mutableStateOf<FertigationStatus?>(
        null
    )
    val fertigationStatus: State<FertigationStatus?> = _fertigationStatus

    private val _fertigationProgrammation = mutableStateOf<FertigationProgrammation?>(
        null
    )
    val fertigationProgrammation: State<FertigationProgrammation?> = _fertigationProgrammation

    private val _deviceGeoItem = MutableStateFlow<List<DeviceGeosItem?>>(emptyList())
    val deviceGeoItem: StateFlow<List<DeviceGeosItem?>> = _deviceGeoItem

    private val _deviceGeoData = mutableStateOf<DeviceGeoData?>(
        null
    )
    val deviceGeoData: State<DeviceGeoData?> = _deviceGeoData

    var _stationDurationData = mutableStateOf<List<StationDuration>>(
        emptyList()
    )
    val stationDurationData: State<List<StationDuration>> = _stationDurationData

    private val _alarmDevice = MutableStateFlow<List<ResultAlarmsItem?>>(emptyList())
    val alarmDevice: StateFlow<List<ResultAlarmsItem?>> = _alarmDevice

    private val _availableGroup = MutableStateFlow<List<Pair<String, String>>>(emptyList())
    val availableGroup: StateFlow<List<Pair<String, String>>> = _availableGroup

    var _stationDurationDataAll = mutableStateOf<List<StationDuration>>(
        emptyList()
    )
    val stationDurationDataAll: State<List<StationDuration>> = _stationDurationDataAll


    val isLoading = mutableStateOf(false)
    val setMarkerLoading = mutableStateOf(false)
    val postDataLoading = mutableStateOf(false)
    val postData2Loading = mutableStateOf(false)
    val searchDeviceValue = mutableStateOf("")

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
        _filteredDevices.value = deviceData.filter {
            it?.name != null && it.name.lowercase().contains(searchDeviceValue.value.lowercase())
        }

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
        val irrigationConfigNominalFlow: MutableList<IrrigationConfigNominalFlowDataProduct> = arrayListOf()

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
                pressureMin= if (data["S1045"] != null && !data["S1045"].isNullOrEmpty()) data["S1045"]!!.toFloat().toInt().toString() else "0",
                pressureMax= if (data["S1046"] != null && !data["S1046"].isNullOrEmpty()) data["S1046"]!!.toFloat().toInt().toString() else "0",
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
        satConfigData["S70"] = satData.plantOperationStatus ?: ""
        satConfigData["S1000"] = satData.password ?: ""
        satConfigData["S1002"] = satData.solarIntensity ?: ""
        satConfigData["S1003"] = satData.windSpeed ?: ""
        satConfigData["S1005"] = satData.evMaster ?: ""
        satConfigData["S1006"] = satData.ecCommand ?: ""
        satConfigData["S1007"] = satData.pulsesFlow ?: ""
        satConfigData["S1008"] = satData.solarIrradiation ?: ""
        satConfigData["S1009"] = satData.windSensor ?: ""
        satConfigData["S1010"] = satData.temperature ?: ""
        satConfigData["S1011"] = satData.humidity ?: ""
        satConfigData["S1012"] = satData.maxActiveProgram ?: ""
        satConfigData["S1013"] = satData.entry1 ?: ""
        satConfigData["S1014"] = satData.entry2 ?: ""
        satConfigData["S1017"] = satData.entry1forDelay ?: ""
        satConfigData["S1018"] = satData.entry2forDelay ?: ""
        satConfigData["S1019"] = satData.flowOffTolerance ?: ""
        satConfigData["S1020"] = satData.flowOff ?: ""
        satConfigData["S1021"] = satData.flowAlarmDelay ?: ""
        satConfigData["S1032"] = satData.entry3 ?: ""
        satConfigData["S1033"] = satData.entry4 ?: ""
        satConfigData["S1036"] = satData.entry3forDelay ?: ""
        satConfigData["S1037"] = satData.entry4forDelay ?: ""
        satConfigData["S1045"] = satData.pressureMin ?: ""
        satConfigData["S1046"] = satData.pressureMax ?: ""
        satConfigData["S1047"] = satData.delayAlarmTimeLowPressure ?: ""
        satConfigData["S1048"] = satData.delayAlarmTimeHighPressure ?: ""

        val resultSatConfig = deviceService.postIrrigationConfigNominalFlow(deviceCode, "SATCONFIG", satConfigData)

        //post pump data
        val pumpConfigData = mutableMapOf<String, String>()
        pumpConfigData["S1516"] = pumpData.pulses ?: ""
        pumpConfigData["S1524"] = pumpData.pumpDeactivationDelay ?: ""
        val resultPumpConfig = deviceService.postIrrigationConfigNominalFlow(deviceCode, "PUMPCONFIG", pumpConfigData)

        //post mv data
        val mvConfigData = mutableMapOf<String, String>()
        mvConfigData["S1616"] = mvData.delayBetweenMSandEV ?: ""
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

    suspend fun postRainMode(
        deviceCode: String,
        satData: IrrigationConfigGeneralSatConfig,
        ): String? {

        //post sat data
        val satConfigData = mutableMapOf<String, String>()
        satConfigData["S70"] = satData.plantOperationStatus ?: ""

        val resultSatConfig = deviceService.postIrrigationConfigNominalFlow(deviceCode, "SATCONFIG", satConfigData)

        if (!resultSatConfig.first) {
            return resultSatConfig.second
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
        data["I1000"] = postData.openedCircuit ?: ""
        data["I1001"] = postData.acknowledgePulseTime ?: ""
        data["I1002"] = postData.minimumAmpere ?: ""
        data["I1003"] = postData.activationDelayMaster ?: ""
        data["I1004"] = postData.activationDelayEV ?: ""
        data["I1005"] = postData.evHoldingVoltage ?: ""
        data["I1006"] = postData.triggerPulseTime ?: ""
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
                        evSerialKey = "S${evIndex}",
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

    suspend fun getIrrigationSettingGeneralParameter(deviceCode: String, programNum: Int): Pair<IrrigationSettingGeneralParameter?, String> {
        val programNumber = programNum - 1
        val baseReg = (10000 + (programNumber * 1000))
        val programModeCode = "S$baseReg"
        val timeModeCode = "S" + (baseReg + 1)
        val startModeCode = "S" + (baseReg + 2)
        val cycleTimeModeCode = "S" + (baseReg + 3)
        val programNameCode = "S" + (baseReg + 4)
        val flowModeCode = "S" + (baseReg + 6)
        val delayBetweenStationCode = "S" + (baseReg + 12)
        val delaybetweenCycleCode = "S" + (baseReg + 13)
        val calendarBMCode = "S" + (baseReg + 18)
        val minifertProgramCode = "S" + (baseReg + 19)
        val remainingDaysCode = "S" + (baseReg + 21)
        val activeWeekCode = "S71"

        val fields = "$programModeCode,$timeModeCode,$startModeCode,$cycleTimeModeCode,$programNameCode,$flowModeCode,$delayBetweenStationCode,$delaybetweenCycleCode,$calendarBMCode,$minifertProgramCode,$remainingDaysCode,"
        val measurement = "SATPRGCONFIG$programNum"
        val result = deviceService.getIrrigationConfigNominalFlow(deviceCode, fields, measurement)
        var irrigationSettingGeneralParameter: IrrigationSettingGeneralParameter? = null

        if (result.first != null && result.first?.data != null && result.first?.data?.dynamicFields != null) {
            val data = result.first!!.data?.dynamicFields!!

            val returnedData = IrrigationSettingGeneralParameter (
                programName = data[programNameCode],
                programMode = data[programModeCode],
                minifertProgramRelated = data[minifertProgramCode],
                timeMode = data[timeModeCode],
                cycletime = data[cycleTimeModeCode],
                delayBetweenStation = data[delayBetweenStationCode],
                delayBetweenCycle = data[delaybetweenCycleCode],
                startMode = data[startModeCode],
                biweeklyCalendar = data[calendarBMCode],
                skippedDays = data[calendarBMCode],
                daysBeforeStart = data[remainingDaysCode],
                flowMode = data[flowModeCode],
            )

            if (returnedData.programName.isNullOrEmpty()) {
                returnedData.programName = ("P." + (programNum).toString().padStart(2, '0'))
            }

            if (returnedData.programMode.isNullOrEmpty()) {
                returnedData.programMode = "1"
            }

            if (returnedData.startMode.isNullOrEmpty()) {
                returnedData.startMode = "0"
            }

            if (returnedData.biweeklyCalendar.isNullOrEmpty() || returnedData.biweeklyCalendar!!.length < 14) {
                returnedData.biweeklyCalendar = "0000000000000000"
            }

            if (returnedData.skippedDays.isNullOrEmpty() || returnedData.skippedDays!!.length >= 14) {
                returnedData.skippedDays = "0"
            }

            if (returnedData.flowMode.isNullOrEmpty()) {
                returnedData.flowMode = "1"
            }

            if (returnedData.daysBeforeStart.isNullOrEmpty()) {
                returnedData.daysBeforeStart = "0"
            }

            if (returnedData.timeMode.isNullOrEmpty()) {
                returnedData.timeMode = "2"
            }

            if (returnedData.cycletime.isNullOrEmpty()) {
                returnedData.cycletime = "0"
            }

            if (returnedData.delayBetweenCycle.isNullOrEmpty()) {
                returnedData.delayBetweenCycle = "0"
            }

            if (returnedData.delayBetweenStation.isNullOrEmpty()) {
                returnedData.delayBetweenStation = "0"
            }

            if (returnedData.minifertProgramRelated.isNullOrEmpty()) {
                returnedData.minifertProgramRelated = "0"
            }

            if (returnedData.flowMode == "0") {
                returnedData.choiceTimeMode = "0"
            } else {
                if (returnedData.timeMode == "0") {
                    returnedData.choiceTimeMode = "1"
                } else {
                    returnedData.choiceTimeMode = "2"
                }
            }

            irrigationSettingGeneralParameter = returnedData
        }

        if (irrigationSettingGeneralParameter != null) {
            val measurement2 = "SATSTAT"
            val result2 = deviceService.getIrrigationConfigNominalFlow(deviceCode, activeWeekCode, measurement2)

            if (result2.first != null && result2.first?.data != null && result2.first?.data?.dynamicFields != null) {
                val data = result2.first!!.data?.dynamicFields!!
                irrigationSettingGeneralParameter.activeWeek = data[activeWeekCode]
            } else {
                return Pair(null, result2.second)
            }
        }

        _irrigationSettingGeneralParameter.value = irrigationSettingGeneralParameter

        return Pair(_irrigationSettingGeneralParameter.value, result.second)
    }

    suspend fun postIrrigationSettingGeneralParameter(
        deviceCode: String,
        postData: IrrigationSettingGeneralParameter,
        programNum: Int,
    ): String? {
        val programNumber = programNum - 1
        val baseReg = (10000 + (programNumber * 1000))
        val programModeCode = "S$baseReg"
        val timeModeCode = "S" + (baseReg + 1)
        val startModeCode = "S" + (baseReg + 2)
        val cycleTimeModeCode = "S" + (baseReg + 3)
        val programNameCode = "S" + (baseReg + 4)
        val flowModeCode = "S" + (baseReg + 6)
        val delayBetweenStationCode = "S" + (baseReg + 12)
        val delaybetweenCycleCode = "S" + (baseReg + 13)
        val calendarBMCode = "S" + (baseReg + 18)
        val minifertProgramCode = "S" + (baseReg + 19)
        val remainingDaysCode = "S" + (baseReg + 21)
        val activeWeekCode = "S71"

        when(postData.choiceTimeMode) {
            "0" -> {
                postData.flowMode = "0"
            }
            "1" -> {
                postData.flowMode = "1"
                postData.timeMode = "0"
            }
            "2" -> {
                postData.flowMode = "1"
                postData.timeMode = "1"
            }
        }

        val data = mutableMapOf<String, String>()
        data[programNameCode] = postData.programName ?: ""
        data[timeModeCode] = postData.timeMode ?: ""
        data[startModeCode] = postData.startMode ?: ""
        data[cycleTimeModeCode] = postData.cycletime ?: ""
        data[programModeCode] = postData.programMode ?: ""
        data[flowModeCode] = postData.flowMode ?: ""
        data[delayBetweenStationCode] = postData.delayBetweenStation ?: ""
        data[delaybetweenCycleCode] = postData.delayBetweenCycle ?: ""
        data[minifertProgramCode] = postData.minifertProgramRelated ?: ""
        data[calendarBMCode] = if(postData.startMode == "0") postData.biweeklyCalendar ?: "" else postData.skippedDays ?: ""
        data[remainingDaysCode] = postData.daysBeforeStart ?: ""

        val resultSatPRGConfig = deviceService.postIrrigationConfigNominalFlow(deviceCode, "SATPRGCONFIG$programNum", data)

        val data2 = mutableMapOf<String, String>()
        data2[activeWeekCode] = postData.activeWeek ?: ""
        val resultSatStat = deviceService.postIrrigationConfigNominalFlow(deviceCode, "SATSTAT", data2)

        return if (resultSatPRGConfig.first) null else resultSatPRGConfig.second
    }

    suspend fun getIrrigationSettingScheduleStart(deviceCode: String, programNum: Int, isEndTime: Boolean): Pair<List<IrrigationSettingScheduleStart>, String> {
        var fields = ""
        val programNumber = programNum - 1
        val baseReg = (10000 + (programNumber * 1000) + 50)
        for (i in 0..7) {
            fields += "S${baseReg + i},"
        }

        val measurement = "SATPRGSTARTS$programNum"
        val result = deviceService.getIrrigationConfigNominalFlow(deviceCode, fields, measurement)
        var irrigationSettingScheduleStart: List<IrrigationSettingScheduleStart> = emptyList()

        if (result.first != null && result.first?.data != null && result.first?.data?.dynamicFields != null) {
            val data = result.first!!.data?.dynamicFields!!

            val schedules = arrayListOf<IrrigationSettingScheduleStart>()
            for (i in 0..7) {
                if (i%2 == 0) {
                    val time1 = data["S${baseReg+i}"]
                    val time2 = data["S${baseReg+i+1}"]
                    var cycle = "0"

                    var time1Split = listOf("0", "00", "")
                    if (!time1.isNullOrEmpty()) {
                        time1Split = time1.split(".")
                    }

                    var time2Split = listOf("0", "00")
                    if (!time2.isNullOrEmpty()) {
                        if (isEndTime) {
                            time2Split = time2.split(".")
                        } else {
                            cycle = time2
                        }
                    }
                    schedules.add(
                        IrrigationSettingScheduleStart(
                            activate = time1Split.getOrNull(2),
                            startHour = time1Split.getOrNull(0),
                            startMinute = time1Split.getOrNull(1),
                            endHour = time2Split.getOrNull(0),
                            endMinute = time2Split.getOrNull(1),
                            cycle = cycle,
                        )
                    )
                }
            }

            irrigationSettingScheduleStart = schedules
        }

        _irrigationSettingScheduleStart.value = irrigationSettingScheduleStart

        return Pair(_irrigationSettingScheduleStart.value, result.second)
    }


    suspend fun postIrrigationSettingScheduleStart(
        deviceCode: String,
        postData: List<IrrigationSettingScheduleStart>,
        programNum: Int,
        isEndTime: Boolean,
    ): String? {
        val programNumber = programNum - 1
        val baseReg = (10000 + (programNumber * 1000) + 50)

        val data = mutableMapOf<String, String>()
        var idx = 0
        for (i in postData) {
            data["S${baseReg + idx}"] = "${i.startHour}.${i.startMinute}.${i.activate}"
            if (isEndTime) {
                data["S${baseReg + idx + 1}"] = "${i.endHour}.${i.endMinute}"
            } else {
                data["S${baseReg + idx + 1}"] = i.cycle ?: ""
            }
            idx += 2
        }

        val resultSatPRGConfig = deviceService.postIrrigationConfigNominalFlow(deviceCode, "SATPRGSTARTS$programNum", data)

        return if (resultSatPRGConfig.first) null else resultSatPRGConfig.second
    }

    suspend fun postCreatePlant(
        postData: CreatePlantRequest,
    ): Pair<Boolean, String?> {
        val response = deviceService.postCreatePlant(postData)
        return Pair(response.first, response.second)
    }

    suspend fun getRelatedDevice(): Pair<List<DeviceRelatedItem?>, String?> {
        val result = deviceService.getRelatedDevice()
        val deviceRelatedItem: MutableList<DeviceRelatedItem?> = mutableListOf()

        if (result.first != null && result.first?.data != null && result.first?.data?.devices != null) {
            for (i in result.first!!.data!!.devices!!) {
                deviceRelatedItem.add(i)
            }
        }

        _deviceRelated.value = deviceRelatedItem

        return Pair(_deviceRelated.value, result.second)
    }

    suspend fun postEditPlant(
        postData: EditPlantRequest,
        id: Int,
    ): Pair<Boolean, String?> {
        val response = deviceService.editRelatedDevice(postData, id)
        return Pair(response.first, response.second)
    }

    suspend fun postDeletePlant(
        id: Int,
    ): Pair<Boolean, String?> {
        val response = deviceService.deleteRelatedDevice(id)
        return Pair(response.first, response.second)
    }

    suspend fun getIrrigationSettingSensorManagement(deviceCode: String, programNum: Int): Pair<IrrigationSettingSensorManagement?, String> {
        val programNumber = programNum - 1
        val baseReg = (10000 + (programNumber * 1000))
        val humiditySensorTypeCode = "S" + (baseReg + 5)
        val waterBudgetCode = "S" + (baseReg + 7)
        val programStopCode = "S" + (baseReg + 8)
        val programStandByCode = "S" + (baseReg + 9)
        val programStartCode = "S" + (baseReg + 10)
        val programSkipCode = "S" + (baseReg + 11)
        val lowTempCode = "S" + (baseReg + 14)
        val highTempCode = "S" + (baseReg + 15)
        val lowHumidityCode = "S" + (baseReg + 16)
        val highHumidityCode = "S" + (baseReg + 17)
        val humiditySensorLevelCode = "S" + (baseReg + 20)

        val fields = "$humiditySensorTypeCode,$waterBudgetCode,$programStopCode,$programStandByCode,$programStartCode,$programSkipCode,$lowTempCode,$highTempCode,$lowHumidityCode,$highHumidityCode,$humiditySensorLevelCode"
        val measurement = "SATPRGCONFIG$programNum"
        val result = deviceService.getIrrigationConfigNominalFlow(deviceCode, fields, measurement)
        var irrigationSettingSensorManagement: IrrigationSettingSensorManagement? = null

        if (result.first != null && result.first?.data != null && result.first?.data?.dynamicFields != null) {
            val data = result.first!!.data?.dynamicFields!!

            val returnedData = IrrigationSettingSensorManagement (
                humiditySensorType = data[humiditySensorTypeCode],
                waterBudget = data[waterBudgetCode],
                programStop = data[programStopCode],
                programStandBy = data[programStandByCode],
                programStart = data[programStartCode],
                programSkip = data[programSkipCode],
                lowTemp = data[lowTempCode],
                highTemp = data[highTempCode],
                lowHumidity = data[lowHumidityCode],
                highHumidity = data[highHumidityCode],
                humiditySensorLevel = data[humiditySensorLevelCode],
                waterBudgetAuto = data[waterBudgetCode] == "255",
            )

            if (returnedData.waterBudget.isNullOrEmpty()) {
                returnedData.waterBudget = "0"
            }

            if (returnedData.lowTemp.isNullOrEmpty()) {
                returnedData.lowTemp = "0"
            }

            if (returnedData.highTemp.isNullOrEmpty()) {
                returnedData.highTemp = "0"
            }

            if (returnedData.lowHumidity.isNullOrEmpty()) {
                returnedData.lowHumidity = "0"
            }

            if (returnedData.highHumidity.isNullOrEmpty()) {
                returnedData.highHumidity = "0"
            }

            if (returnedData.programStop.isNullOrEmpty()) {
                returnedData.programStop = "000000000000"
            }

            if (returnedData.programStandBy.isNullOrEmpty()) {
                returnedData.programStandBy = "000000000000"
            }

            if (returnedData.programStart.isNullOrEmpty()) {
                returnedData.programStart = "000000000000"
            }

            if (returnedData.programSkip.isNullOrEmpty()) {
                returnedData.programSkip = "000000000000"
            }

            irrigationSettingSensorManagement = returnedData
        }

        _irrigationSettingSensorManagement.value = irrigationSettingSensorManagement

        return Pair(_irrigationSettingSensorManagement.value, result.second)
    }

    suspend fun postIrrigationSettingSensorManagement(
        deviceCode: String,
        postData: IrrigationSettingSensorManagement,
        programNum: Int,
    ): String? {
        val programNumber = programNum - 1
        val baseReg = (10000 + (programNumber * 1000))
        val humiditySensorTypeCode = "S" + (baseReg + 5)
        val waterBudgetCode = "S" + (baseReg + 7)
        val programStopCode = "S" + (baseReg + 8)
        val programStandByCode = "S" + (baseReg + 9)
        val programStartCode = "S" + (baseReg + 10)
        val programSkipCode = "S" + (baseReg + 11)
        val lowTempCode = "S" + (baseReg + 14)
        val highTempCode = "S" + (baseReg + 15)
        val lowHumidityCode = "S" + (baseReg + 16)
        val highHumidityCode = "S" + (baseReg + 17)
        val humiditySensorLevelCode = "S" + (baseReg + 20)

        val data = mutableMapOf<String, String>()
        if (postData.humiditySensorType != null) {
            data[humiditySensorTypeCode] = postData.humiditySensorType!!
        }
        data[waterBudgetCode] = if(postData.waterBudgetAuto) "255" else (postData.waterBudget ?: "")
        data[programStopCode] = postData.programStop ?: ""
        data[programStandByCode] = postData.programStandBy ?: ""
        data[programStartCode] = postData.programStart ?: ""
        data[programSkipCode] = postData.programSkip ?: ""
        data[lowTempCode] = postData.lowTemp ?: ""
        data[highTempCode] = postData.highTemp ?: ""
        data[lowHumidityCode] = postData.lowHumidity ?: ""
        data[highHumidityCode] = postData.highHumidity ?: ""
        if (postData.humiditySensorLevel != null) {
            data[humiditySensorLevelCode] = postData.humiditySensorLevel!!
        }

        val resultSatPRGConfig = deviceService.postIrrigationConfigNominalFlow(deviceCode, "SATPRGCONFIG$programNum", data)

        return if (resultSatPRGConfig.first) null else resultSatPRGConfig.second
    }

    suspend fun getIrrigationStatusProgramStatus(deviceCode: String): Pair<List<IrrigationStatusProgramStatus?>, String> {
        var fields =""
        var lastField = when (BuildConfig.FLAVOR) {
            "idroRes" -> {
                40007
            } else -> {
                40029
            }
        }
        for (i in 40000..lastField) {
            if (i != 40000) {
                fields += ","
            }
            fields += "S${i}"
        }

        val measurement = "SATSTAT"
        val result = deviceService.getIrrigationConfigNominalFlow(deviceCode, fields, measurement)

        val irrigationStatusProgramStatus = arrayListOf<IrrigationStatusProgramStatus>()

        if (result.first != null && result.first?.data != null && result.first?.data?.dynamicFields != null) {
            val data = result.first!!.data?.dynamicFields!!
            var index = 0

            for (i in data) {
                if (i.key.startsWith("S4")) {
                    index += 1

                    val dataSplit = i.value.split(",")

                    irrigationStatusProgramStatus.add(
                        IrrigationStatusProgramStatus(
                            index = index,
                            status = dataSplit.getOrNull(0) ?: "0",
                            stationUsed = dataSplit.getOrNull(1) ?: "0",
                            remainingTime = (dataSplit.getOrNull(2) ?: "0").replace(".", ":"),
                        )
                    )
                }
            }
        }

        _irrigationStatusProgramStatus.value = irrigationStatusProgramStatus

        return Pair(_irrigationStatusProgramStatus.value, result.second)
    }

    suspend fun startPeriodicFetchingIrrigationStatusProgramStatus(deviceCode: String) {
        delay(5000L)

        while (true) {
            getIrrigationStatusProgramStatus(deviceCode)

            delay(5000L)
        }
    }

    suspend fun getIrrigationStatusStationStatus(deviceCode: String): Pair<List<IrrigationStatusStationStatus>, String> {
        //get stations
        var fields =""
        for (i in 2000..3151) {
            if (i != 2000) {
                fields += ","
            }
            fields += "S${i}"
        }
        val measurement = "EVCONFIG"

        val result = deviceService.getIrrigationConfigNominalFlow(deviceCode, fields, measurement)
        val stations: MutableList<IrrigationConfigNominalFlowDataProduct> = arrayListOf()

        if (result.first != null && result.first?.data != null && result.first?.data?.dynamicFields != null) {
            var evIndex = 2000
            val data = result.first!!.data?.dynamicFields!!
            for (i in 0..data.size / 5) {
                if (data.containsKey("S${evIndex}") && data["S${evIndex}"] != "FFFFFF") {
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

                    stations.add(dataItems)
                }

                evIndex += 6
            }
        }

        val groupedData = stations.groupBy { it.station }
        val stationList = groupedData.keys.toList()

        //get satstat
        val satData = getSatStat(deviceCode)
        val activeStation = satData.first
        val satRawData = satData.second

        val activeStationFirstAddress = 40100

        val returnedData: MutableList<IrrigationStatusStationStatus> = arrayListOf()

        for (i in stationList.indices) {
            val tmpStato = activeStation.contains(stationList[i])
            val tmpAzioneTempo = satRawData["S${activeStationFirstAddress + (stationList[i]?.toIntOrNull() ?: 0) - 1}"]?.split(",")

            var tmpAction = "0"
            var tmpRemainingtime = "00:00:00"

            if (tmpAzioneTempo != null) {
                val tmpBoolean = tmpAzioneTempo[0]
                if (tmpBoolean == "0") {
                    tmpAction = "0"
                } else if (tmpBoolean == "1") {
                    tmpAction = "1"
                }
                tmpRemainingtime = tmpAzioneTempo[1].replace(".", ":")
            }

            returnedData.add(IrrigationStatusStationStatus(
                index = i+1,
                action = tmpAction,
                remainingTime = tmpRemainingtime,
                status = tmpStato,
                station = stationList[i],
            ))
        }

        returnedData.sortBy { it.station?.toIntOrNull() ?: 0 }

        _irrigationStatusStationStatus.value = returnedData

        return Pair(returnedData, result.second)
    }

    suspend fun getSatStat(deviceCode: String): Pair<List<String>, Map<String,String>> {
        //get sat stat
        var fields2 ="S18"
        for (i in 40100..40195) {
            fields2 += ",S${i}"
        }
        val measurement2 = "SATSTAT"

        val result2 = deviceService.getIrrigationConfigNominalFlow(deviceCode, fields2, measurement2)
        val satStat: MutableList<Pair<String,String>> = arrayListOf()

        var activeStation = listOf<String>()
        var satRawData = mapOf<String, String>()

        if (result2.first != null && result2.first?.data != null && result2.first?.data?.dynamicFields != null) {
            satRawData = result2.first!!.data?.dynamicFields!!
            for (i in satRawData) {
                if (i.key.startsWith("S18")) {
                    activeStation = i.value.split(",")
                } else if (i.key.startsWith("S40")) {
                    satStat.add(Pair(i.key, i.value))
                }
            }
        }

        return Pair(activeStation, satRawData)
    }

    suspend fun startPeriodicFetchingIrrigationStatusStationStatus(deviceCode: String) {
        delay(5000L)

        while (true) {
            getIrrigationStatusStationStatus(deviceCode)

            delay(5000L)
        }
    }

    suspend fun getIrrigationStatusIdrosatStatus(deviceCode: String, context: Context): Pair<List<IrrigationStatusIdrosatStatus?>, String> {
        val instantConsumptionPump1Code = "S468"
        val instantConsumptionPump2Code = "S470"
        val instantConsumptionPump3Code = "S472"
        val instantConsumptionPump4Code = "S474"
        val instantConsumptionPump5Code = "S476"
        val instantConsumptionPump6Code = "S478"
        val instantConsumptionPump7Code = "S480"
        val instantConsumptionPump8Code = "S482"
        val totalConsumptionPump1Code = "S484"
        val totalConsumptionPump2Code = "S486"
        val totalConsumptionPump3Code = "S488"
        val totalConsumptionPump4Code = "S490"
        val totalConsumptionPump5Code = "S492"
        val totalConsumptionPump6Code = "S494"
        val totalConsumptionPump7Code = "S496"
        val totalConsumptionPump8Code = "S498"

        val fields ="$instantConsumptionPump1Code,$instantConsumptionPump2Code,$instantConsumptionPump3Code,$instantConsumptionPump4Code,$instantConsumptionPump5Code,$instantConsumptionPump6Code,$instantConsumptionPump7Code,$instantConsumptionPump8Code,$totalConsumptionPump1Code,$totalConsumptionPump2Code,$totalConsumptionPump3Code,$totalConsumptionPump4Code,$totalConsumptionPump5Code,$totalConsumptionPump6Code,$totalConsumptionPump7Code,$totalConsumptionPump8Code"

        val measurement = "SATSTAT"
        val result = deviceService.getIrrigationConfigNominalFlow(deviceCode, fields, measurement)

        val irrigationStatusIdrosatStatusInstantConsumption = arrayListOf<IrrigationStatusIdrosatStatus>()
        val irrigationStatusIdrosatStatusTotalConsumption = arrayListOf<IrrigationStatusIdrosatStatus>()

        if (result.first != null && result.first?.data != null && result.first?.data?.dynamicFields != null) {
            val data = result.first!!.data?.dynamicFields!!

            //instant consumption data
            irrigationStatusIdrosatStatusInstantConsumption.add(
                IrrigationStatusIdrosatStatus(
                    name = "${context.getString(R.string.pump)} 1",
                    value = data[instantConsumptionPump1Code] ?: "-",
                )
            )

            //total consumption data
            irrigationStatusIdrosatStatusTotalConsumption.add(
                IrrigationStatusIdrosatStatus(
                    name = "${context.getString(R.string.pump)} 1",
                    value = data[totalConsumptionPump1Code] ?: "-",
                )
            )

            if (BuildConfig.FLAVOR != "idroRes" && BuildConfig.FLAVOR != "irriLife") {
                //instant consumption data
                irrigationStatusIdrosatStatusInstantConsumption.add(
                    IrrigationStatusIdrosatStatus(
                        name = "${context.getString(R.string.pump)} 2",
                        value = data[instantConsumptionPump2Code] ?: "-",
                    )
                )
                irrigationStatusIdrosatStatusInstantConsumption.add(
                    IrrigationStatusIdrosatStatus(
                        name = "${context.getString(R.string.pump)} 3",
                        value = data[instantConsumptionPump3Code] ?: "-",
                    )
                )
                irrigationStatusIdrosatStatusInstantConsumption.add(
                    IrrigationStatusIdrosatStatus(
                        name = "${context.getString(R.string.pump)} 4",
                        value = data[instantConsumptionPump4Code] ?: "-",
                    )
                )
                irrigationStatusIdrosatStatusInstantConsumption.add(
                    IrrigationStatusIdrosatStatus(
                        name = "${context.getString(R.string.pump)} 5",
                        value = data[instantConsumptionPump5Code] ?: "-",
                    )
                )
                irrigationStatusIdrosatStatusInstantConsumption.add(
                    IrrigationStatusIdrosatStatus(
                        name = "${context.getString(R.string.pump)} 6",
                        value = data[instantConsumptionPump6Code] ?: "-",
                    )
                )
                irrigationStatusIdrosatStatusInstantConsumption.add(
                    IrrigationStatusIdrosatStatus(
                        name = "${context.getString(R.string.pump)} 7",
                        value = data[instantConsumptionPump7Code] ?: "-",
                    )
                )
                irrigationStatusIdrosatStatusInstantConsumption.add(
                    IrrigationStatusIdrosatStatus(
                        name = "${context.getString(R.string.pump)} 8",
                        value = data[instantConsumptionPump8Code] ?: "-",
                    )
                )

                //total consumption data
                irrigationStatusIdrosatStatusTotalConsumption.add(
                    IrrigationStatusIdrosatStatus(
                        name = "${context.getString(R.string.pump)} 2",
                        value = data[totalConsumptionPump2Code] ?: "-",
                    )
                )
                irrigationStatusIdrosatStatusTotalConsumption.add(
                    IrrigationStatusIdrosatStatus(
                        name = "${context.getString(R.string.pump)} 3",
                        value = data[totalConsumptionPump3Code] ?: "-",
                    )
                )
                irrigationStatusIdrosatStatusTotalConsumption.add(
                    IrrigationStatusIdrosatStatus(
                        name = "${context.getString(R.string.pump)} 4",
                        value = data[totalConsumptionPump4Code] ?: "-",
                    )
                )
                irrigationStatusIdrosatStatusTotalConsumption.add(
                    IrrigationStatusIdrosatStatus(
                        name = "${context.getString(R.string.pump)} 5",
                        value = data[totalConsumptionPump5Code] ?: "-",
                    )
                )
                irrigationStatusIdrosatStatusTotalConsumption.add(
                    IrrigationStatusIdrosatStatus(
                        name = "${context.getString(R.string.pump)} 6",
                        value = data[totalConsumptionPump6Code] ?: "-",
                    )
                )
                irrigationStatusIdrosatStatusTotalConsumption.add(
                    IrrigationStatusIdrosatStatus(
                        name = "${context.getString(R.string.pump)} 7",
                        value = data[totalConsumptionPump7Code] ?: "-",
                    )
                )
                irrigationStatusIdrosatStatusTotalConsumption.add(
                    IrrigationStatusIdrosatStatus(
                        name = "${context.getString(R.string.pump)} 8",
                        value = data[totalConsumptionPump8Code] ?: "-",
                    )
                )
            }
        }

        _irrigationStatusIdrosatStatusInstantConsumption.value = irrigationStatusIdrosatStatusInstantConsumption
        _irrigationStatusIdrosatStatusTotalConsumption.value = irrigationStatusIdrosatStatusTotalConsumption

        return Pair(_irrigationStatusIdrosatStatusInstantConsumption.value, result.second)
    }

    suspend fun startPeriodicFetchingIrrigationStatusIdrosatStatus(deviceCode: String, context: Context) {
        delay(5000L)

        while (true) {
            getIrrigationStatusIdrosatStatus(deviceCode, context)

            delay(5000L)
        }
    }


    suspend fun getEVStationName(deviceCode: String): Pair<List<Pair<String, String>>, String> {
        var fields = ""
        val baseReg = 6000
        for (i in 0..1051) {
            if (i != 0) {
                fields += ","
            }

            fields += "S${baseReg + i}"
        }

        val measurement = "GROUPCONFIG"
        val result = deviceService.getIrrigationConfigNominalFlow(deviceCode, fields, measurement)
        var evStationName: List<Pair<String, String>> = emptyList()

        if (result.first != null && result.first?.data != null && result.first?.data?.dynamicFields != null) {
            val data = result.first!!.data?.dynamicFields!!

            val stationNames = arrayListOf<Pair<String, String>>()

            for (i in data) {

                if (i.key.startsWith("S", false)) {
                    stationNames.add(
                        Pair(i.key, i.value)
                    )
                }
            }

            evStationName = stationNames
        }

        _evStationName.value = evStationName

        return Pair(_evStationName.value, result.second)
    }

    suspend fun postManualIdrosatStatIrrigationName(deviceCode: String, data: Map<String, String>): String? {
        val result = deviceService.postIrrigationConfigNominalFlow(deviceCode, "SATSTAT", data)
        return if (result.first) null else result.second
    }

    suspend fun getManualStartProgram(deviceCode: String, context: Context): Pair<List<ManualStartProgram?>, String> {
        val fields ="S16"

        val measurement = "SATSTAT"
        val result = deviceService.getIrrigationConfigNominalFlow(deviceCode, fields, measurement)

        val manualStartProgram = arrayListOf<ManualStartProgram>()

        if (result.first != null && result.first?.data != null && result.first?.data?.dynamicFields != null) {
            val data = result.first!!.data?.dynamicFields!!

            val dataPrograms = data[fields]

            if (!dataPrograms.isNullOrEmpty()) {
                val programSplit = dataPrograms.split(",")
                var dataCount = 30

                if (BuildConfig.FLAVOR == "idroRes" || BuildConfig.FLAVOR == "irriLife") {
                    dataCount = 8
                }
                for (i in 1..dataCount) {
                    manualStartProgram.add(
                        ManualStartProgram(
                            name = "${context.getString(R.string.program)} $i",
                            value = if (programSplit.contains("$i")) "1" else "0",
                        )
                    )
                }
            }
        }

        _manualStartProgram.value = manualStartProgram

        return Pair(_manualStartProgram.value, result.second)
    }

    suspend fun postManualStartProgram(deviceCode: String, data: Map<String, String>): String? {
        val result = deviceService.postIrrigationConfigNominalFlow(deviceCode, "SATSTAT", data)
        return if (result.first) null else result.second
    }

    suspend fun getFertigationStatus(deviceCode: String): Pair<FertigationStatus?, String> {
        val counterPrincipalCode = "F12"
        val counter1Code = "F4"
        val counter2Code = "F6"
        val counter3Code = "F8"
        val counter4Code = "F10"
        val ecCode = "F14"
        val phCode = "F15"
        val numberOfAlarm = "F22"
        val activeProgramCode = "F17"

        val fields = "$counterPrincipalCode,$counter1Code,$counter2Code,$counter3Code,$counter4Code,$ecCode,$phCode,$numberOfAlarm,$activeProgramCode"

        val measurement = "FERTSTAT"
        val result = deviceService.getIrrigationConfigNominalFlow(deviceCode, fields, measurement)

        var fertigationStatus : FertigationStatus? = null

        if (result.first != null && result.first?.data != null && result.first?.data?.dynamicFields != null) {
            val data = result.first!!.data?.dynamicFields!!

            val counterPrincipal = data[counterPrincipalCode]
            val counter1 = data[counter1Code]
            val counter2 = data[counter2Code]
            val counter3 = data[counter3Code]
            val counter4 = data[counter4Code]
            val ec = data[ecCode]
            val ph = data[phCode]
            val numberOfAlarm = data[numberOfAlarm]
            val activeProgram = data[activeProgramCode]

            fertigationStatus = FertigationStatus(
                counterPrincipal = counterPrincipal,
                counter1 = counter1,
                counter2 = counter2,
                counter3 = counter3,
                counter4 = counter4,
                ec = ec,
                ph = ph,
                numberOfAlarm = numberOfAlarm,
                activeProgram = activeProgram,
            )
        }

        _fertigationStatus.value = fertigationStatus

        return Pair(_fertigationStatus.value, result.second)
    }

    suspend fun startPeriodicFetchingFertigationStatus(deviceCode: String) {
        delay(5000L)

        while (true) {
            getFertigationStatus(deviceCode)

            delay(5000L)
        }
    }

    suspend fun getFertigationProgrammation(deviceCode: String, programNum: Int): Pair<FertigationProgrammation?, String> {
        val postRawControlData = mutableMapOf<String, String>()
        deviceService.postIrrigationConfigRawControl(deviceCode, "FERTPRGCONFIG${programNum}?", postRawControlData)

        val hysteresisCode = "F1${programNum - 1}015"
        val checkEveryCode = "F1${programNum - 1}018"
        val checkEveryTypeCode = "F1${programNum - 1}017"
        val setpointECCode = "F1${programNum - 1}019"
        val setpointPhCode = "F1${programNum - 1}002"
        val dosagePhCode = "F1${programNum - 1}001"

        val fields = "$hysteresisCode,$checkEveryCode,$checkEveryTypeCode,$setpointECCode,$setpointPhCode,$dosagePhCode"

        val measurement = "FERTPRGCONFIG${programNum}"

        val result = deviceService.getIrrigationConfigNominalFlow(deviceCode, fields, measurement)

        var fertigationProgrammation : FertigationProgrammation? = null

        if (result.first != null && result.first?.data != null && result.first?.data?.dynamicFields != null) {
            val data = result.first!!.data?.dynamicFields!!

            val hysteresis = data[hysteresisCode]
            val checkEvery = data[checkEveryCode]
            val checkEveryType = data[checkEveryTypeCode]
            val setpointEC = data[setpointECCode]
            val setpointPh = data[setpointPhCode]
            val dosagePh = data[dosagePhCode]

            fertigationProgrammation = FertigationProgrammation(
                hysteresis = hysteresis,
                checkEvery = checkEvery,
                checkEveryType = checkEveryType,
                setpointEC = setpointEC,
                setpointPh = setpointPh,
                dosagePh = dosagePh,
            )
        }

        _fertigationProgrammation.value = fertigationProgrammation

        return Pair(_fertigationProgrammation.value, result.second)
    }

    suspend fun postFertigationProgrammation(deviceCode: String, programNum: Int, data: FertigationProgrammation): String? {
        val postData = mutableMapOf<String, String>()

        val hysteresisCode = "F1${programNum - 1}015"
        val checkEveryCode = "F1${programNum - 1}018"
        val checkEveryTypeCode = "F1${programNum - 1}017"
        val setpointECCode = "F1${programNum - 1}019"
        val setpointPhCode = "F1${programNum - 1}002"
        val dosagePhCode = "F1${programNum - 1}001"

        postData[hysteresisCode] = data.hysteresis ?: ""
        postData[checkEveryCode] = data.checkEvery ?: ""
        postData[checkEveryTypeCode] = data.checkEveryType ?: ""
        postData[setpointECCode] = data.setpointEC ?: ""
        postData[setpointPhCode] = data.setpointPh ?: ""
        postData[dosagePhCode] = data.dosagePh ?: ""

        val result = deviceService.postIrrigationConfigNominalFlow(deviceCode, "FERTPRGCONFIG${programNum}", postData)
        return if (result.first) null else result.second
    }
    suspend fun getDeviceGeo(deviceCode: String): Pair<List<DeviceGeosItem?>, String?> {
        val evConfig = getIrrigationConfigNominalFlow(deviceCode)
        val evConfigData = evConfig.first.filter {
            !it.evSerial.isNullOrEmpty() && it.evSerial != "FFFFFF"
        }

        val satData = getSatStat(deviceCode)
        val activeStation = satData.first

        val result = deviceService.getDeviceGeo(deviceCode)
        val deviceGeoItem: MutableList<DeviceGeosItem?> = mutableListOf()

        if (result.first != null && result.first?.data != null && result.first?.data?.device != null) {
            _deviceGeoData.value = result.first!!.data!!.device

            if (!result.first!!.data!!.device!!.deviceGeos.isNullOrEmpty()) {
                for (i in result.first!!.data!!.device?.deviceGeos!!) {
                    val exists = evConfigData.find { it.evSerial == i?.evSerial }
                    if (exists != null) {
                        i?.stationNumber = exists.station

                        i?.status =  if (activeStation.contains(i?.stationNumber)) "1" else "0"
                        deviceGeoItem.add(i)
                    }
                }
            }
        }

        _deviceGeoItem.value = deviceGeoItem

        return Pair(_deviceGeoItem.value, result.second)
    }

    suspend fun getStationDuration(deviceCode: String, programNum: Int): Pair<List<StationDuration?>, String> {
        val stationDurationDataAll = arrayListOf<StationDuration>()

        //get device info
        val deviceInfo = getIrrigationSettingGeneralParameter(deviceCode, programNum)

        //get station name
        val stationName = getEVStationName(deviceCode).first

        val evConfig = getIrrigationConfigNominalFlow(deviceCode)
        val evConfigData = evConfig.first.filter {
            !it.evSerial.isNullOrEmpty() && it.evSerial != "FFFFFF"
        }

        val evAndStationName = arrayListOf<Pair<String,String>>()
        val availableGroup = arrayListOf<Pair<String,String>>()
        for (i in evConfigData) {
            val station = stationName.find {
                it.first == "S${6000 + ((i.station?.toIntOrNull()) ?: 0) - 1}"
            }
            evAndStationName.add(
                Pair(i.station!!, (station?.first ?: ""))
            )

            if (!availableGroup.contains(Pair(station?.second ?: "", station?.first ?: ""))) {
                availableGroup.add(Pair(station?.second ?: "", station?.first ?: ""))

                val evs = evConfigData.filter {
                    it.station == i.station
                }

                var evJoin = ""
                for ((index, ev) in evs.withIndex()) {
                    evJoin += "${ev.evSerial}"
                    if (index != evs.size - 1) {
                        evJoin += "\n"
                    }
                }

                val flowMode = deviceInfo.first?.choiceTimeMode ?: "0"

                stationDurationDataAll.add(
                    StationDuration(
                        station = i.station ?: "",
                        group = station?.first ?: "",
                        ev = evJoin,
                        status = "0",
                        flowMode = flowMode,
                        hour = "0",
                        minute = "0",
                        second = "0",
                        volume = "0",
                    )
                )
            }
        }

        _stationDurationDataAll.value = stationDurationDataAll

        _availableGroup.value = availableGroup

        //get stations
        var fields =""

        val startStation = ((programNum - 1) * 1000) + 10100
        for (i in startStation..startStation + 196) {
            if (i != startStation) {
                fields += ","
            }
            fields += "S${i}"
        }
        val measurement = "SATPRGTIMES${programNum}"

        val result = deviceService.getIrrigationConfigNominalFlow(deviceCode, fields, measurement)
        val programSteps = 95

        val stationDurationData = arrayListOf<StationDuration>()

        if (result.first != null && result.first?.data != null && result.first?.data?.dynamicFields != null) {
            val data = result.first!!.data?.dynamicFields!!

            for (step in 0..programSteps) {
                val orderRegister = "S${startStation + 100 + step}"
                val stationID = (data[orderRegister]?.split(",")?.getOrNull(0)) ?: "0"
                val stationIDInt = (stationID.toIntOrNull()) ?: 0

                if (stationIDInt > 0) {
                    val timeData = data["S${startStation + step}"]

                    var hour = ""
                    var minute = ""
                    var second = ""
                    var volume = ""

                    val flowMode = deviceInfo.first?.choiceTimeMode ?: "0"

                    if (!timeData.isNullOrEmpty()) {
                        when (flowMode) {
                            "0" -> {
                                volume = timeData
                            }
                            "1" -> {
                                val timeDataSplit = timeData.split(".")
                                minute = (timeDataSplit.getOrNull(0)) ?: "0"
                                second = (timeDataSplit.getOrNull(1)) ?: "0"
                            }
                            "2" -> {
                                val timeDataSplit = timeData.split(".")
                                hour = (timeDataSplit.getOrNull(0)) ?: "0"
                                minute = (timeDataSplit.getOrNull(1)) ?: "0"
                            }
                        }
                    }

                    val group = evAndStationName.find {
                        it.first == stationID
                    }

                    if (group != null) {
                        val evs = evConfigData.filter {
                            it.station == stationID
                        }

                        var evJoin = ""
                        for ((index, ev) in evs.withIndex()) {
                            evJoin += "${ev.evSerial}"
                            if (index != evs.size - 1) {
                                evJoin += "\n"
                            }
                        }

                        val stationStatus = (data[orderRegister]?.split(",")?.getOrNull(1)) ?: "0"

                        val addedData = StationDuration(
                            station = stationID,
                            group = group?.second ?: "",
                            ev = evJoin,
                            status = stationStatus,
                            flowMode = flowMode,
                            hour = hour,
                            minute = minute,
                            second = second,
                            volume = volume,
                        )

                        stationDurationData.add(addedData)
                    }
                }
            }
        }

        _stationDurationData.value = stationDurationData

        return Pair(_stationDurationData.value, "")
    }

    suspend fun postStationDuration (deviceCode: String, programNum: Int): String? {
        val command = "SATPRGTIMES${programNum}"
        val postData = mutableMapOf<String, String>()
        val baseReg = (10000 + ((programNum - 1) * 1000))

        val flowMode = _irrigationSettingGeneralParameter.value?.choiceTimeMode ?: "0"

        for (step in 0..<96) {
            val orderRegister = "S${baseReg + 200 + step}"
            val timeRegister = "S${baseReg + 100 + step}"
            var flowValueString = "0"

            if (flowMode == "1" || flowMode == "2") {
                flowValueString = "0.00"
            }

            postData[timeRegister] = flowValueString
            postData[orderRegister] = "0,0"

            val existsData = stationDurationData.value.getOrNull(step)
            if (existsData != null) {
                when (flowMode) {
                    "0" -> {
                        postData[timeRegister] = existsData.volume
                    }
                    "1" -> {
                        postData[timeRegister] = "${existsData.minute}.${existsData.second}"
                    }
                    "2" -> {
                        postData[timeRegister] = "${existsData.hour}.${existsData.minute}"
                    }
                }

                postData[orderRegister] = "${existsData.station},${existsData.status}"
            }
        }

        val chunked = postData.entries.chunked(60)

        var result = Pair(false, "Something went wrong")
        for (chunk in chunked) {
            val map = chunk.associateBy({it.key}, {it.value})
            result = deviceService.postIrrigationConfigNominalFlow(deviceCode, command, map)
        }

        return if (result.first) null else result.second
    }

    suspend fun getAlarmDevice(deviceCode: String, language: String): Pair<List<ResultAlarmsItem?>, String?> {
        val result = deviceService.getDeviceAlarm(deviceCode, language)
        val alarmDeviceItem: MutableList<ResultAlarmsItem?> = mutableListOf()

        if (result.first != null && result.first?.data != null && result.first?.data?.resultAlarms != null) {
            for (i in result.first!!.data!!.resultAlarms!!) {
                alarmDeviceItem.add(i)
            }
        }

        _alarmDevice.value = alarmDeviceItem

        return Pair(_alarmDevice.value, result.second)
    }

    suspend fun postResetListAlarm(
        deviceCode: String
    ): String? {
        //post mv data
        val mvConfigData = mutableMapOf<String, String>()
        mvConfigData["S15"] = "0"
        val resultMVConfig = deviceService.postIrrigationConfigNominalFlow(deviceCode, "SATSTAT", mvConfigData)

        return if (resultMVConfig.first) null else resultMVConfig.second
    }
    suspend fun postFastWatering(deviceCode: String, status: Boolean): String? {
        val result = deviceService.postFastWatering(deviceCode, status)
        return if (result.first) null else result.second
    }
}
