package com.idrolife.app.service

import com.idrolife.app.data.api.device.CreatePlantRequest
import com.idrolife.app.data.api.device.DeviceAlarmsResponse
import com.idrolife.app.data.api.device.DeviceByIDResponse
import com.idrolife.app.data.api.device.DeviceListResponse
import com.idrolife.app.data.api.device.DeviceRelatedResponse
import com.idrolife.app.data.api.device.EditPlantRequest
import com.idrolife.app.data.api.irrigation.IrrigationConfigDeviceGeoRequest
import com.idrolife.app.data.api.irrigation.IrrigationConfigNominalFlowResponse
import com.idrolife.app.data.api.map.DeviceGeoResponse
import com.idrolife.app.data.api.sensor.SensorMeteostatResponse
import com.idrolife.app.data.api.sensor.SensorSatstatResponse
import com.idrolife.app.data.api.sensor.SoilMoistureMarkerRequest
import com.idrolife.app.data.api.sensor.SoilMositureHumidityResponse

interface DeviceService {
    fun resetToken()
    suspend fun getDevices(): Pair<DeviceListResponse?, String>
    suspend fun getDevicesByID(id: String): Pair<DeviceByIDResponse?, String>
    suspend fun getSensorMeteostat(deviceCode: String): Pair<SensorMeteostatResponse?, String>
    suspend fun getSensorSatstat(deviceCode: String): Pair<SensorSatstatResponse?, String>
    suspend fun getSensorRH(deviceCode: String): Pair<SoilMositureHumidityResponse?, String>
    suspend fun postSoilMoistureMarker(markerData: SoilMoistureMarkerRequest): Pair<Boolean, String>
    suspend fun getIrrigationConfigNominalFlow(deviceCode: String, fields: String, measurement: String): Pair<IrrigationConfigNominalFlowResponse?, String>
    suspend fun postIrrigationConfigNominalFlow(deviceCode: String, command: String, data: Map<String, String>): Pair<Boolean, String>
    suspend fun postIrrigationConfigRawControl(deviceCode: String, command: String, data: Map<String, String>): Pair<Boolean, String>
    suspend fun postIrrigationConfigDeviceGeo(data: IrrigationConfigDeviceGeoRequest): Pair<Boolean, String>
    suspend fun getDeviceGeo(deviceCode: String): Pair<DeviceGeoResponse?, String>
    suspend fun postCreatePlant(data: CreatePlantRequest): Pair<Boolean, String>
    suspend fun getRelatedDevice(): Pair<DeviceRelatedResponse?, String>
    suspend fun editRelatedDevice(data: EditPlantRequest, id: Int): Pair<Boolean, String>
    suspend fun deleteRelatedDevice(id: Int): Pair<Boolean, String>
    suspend fun getDeviceAlarm(deviceCode: String, language: String): Pair<DeviceAlarmsResponse?, String>
    suspend fun postFastWatering(deviceCode: String, status:Boolean): Pair<Boolean, String>
}
