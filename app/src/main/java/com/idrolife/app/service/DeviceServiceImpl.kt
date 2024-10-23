package com.idrolife.app.service

import com.idrolife.app.data.api.HttpRoutes
import com.idrolife.app.data.api.UnauthorizedException
import com.idrolife.app.data.api.UnprocessableEntityException
import com.idrolife.app.data.api.device.DeviceByIDResponse
import com.idrolife.app.data.api.device.DeviceListResponse
import com.idrolife.app.data.api.irrigation.IrrigationConfigNominalFlowData
import com.idrolife.app.data.api.irrigation.IrrigationConfigNominalFlowResponse
import com.idrolife.app.data.api.sensor.SensorMeteostatResponse
import com.idrolife.app.data.api.sensor.SensorSatstatResponse
import com.idrolife.app.data.api.sensor.SoilMoistureMarkerRequest
import com.idrolife.app.data.api.sensor.SoilMositureHumidityResponse
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.ClientRequestException
import io.ktor.client.plugins.RedirectResponseException
import io.ktor.client.plugins.ServerResponseException
import io.ktor.client.plugins.auth.Auth
import io.ktor.client.plugins.auth.providers.BearerAuthProvider
import io.ktor.client.plugins.plugin
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.request.url
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType.Application.Json
import io.ktor.http.contentType
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.int
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive

class DeviceServiceImpl(
    private val client: HttpClient
): DeviceService {
    override fun resetToken() {
        client.plugin(Auth).providers.filterIsInstance<BearerAuthProvider>()
            .first().clearToken()
    }

    override suspend fun getDevices(): Pair<DeviceListResponse?, String> {
        return try {
            val response = client.get { url("${HttpRoutes.DEVICE}?company=idrolife") }.body<DeviceListResponse>()
            Pair(response, "")
        } catch (e: UnauthorizedException) {
            Pair(null, "Unauthorized")
        } catch (e: RedirectResponseException) {
            Pair(null, "Error 3xx: ${e.response.status.description}")
        } catch (e: ClientRequestException) {
            Pair(null, "Error 4xx: ${e.response.status.description}")
        } catch (e: ServerResponseException) {
            Pair(null, "Error 5xx: ${e.response.status.description}")
        } catch (e: Exception) {
            Pair(null, "Error 3xx: ${e.message}")
        }
    }

    override suspend fun getDevicesByID(id: String): Pair<DeviceByIDResponse?, String> {
        return try {
            val response = client.get { url("${HttpRoutes.DEVICE}/${id}?company=idrolife") }.body<DeviceByIDResponse>()
            Pair(response, "")
        } catch (e: UnauthorizedException) {
            Pair(null, "Unauthorized")
        } catch (e: RedirectResponseException) {
            Pair(null, "Error 3xx: ${e.response.status.description}")
        } catch (e: ClientRequestException) {
            Pair(null, "Error 4xx: ${e.response.status.description}")
        } catch (e: ServerResponseException) {
            Pair(null, "Error 5xx: ${e.response.status.description}")
        } catch (e: Exception) {
            Pair(null, "Error 3xx: ${e.message}")
        }
    }

    override suspend fun getSensorMeteostat(deviceCode: String): Pair<SensorMeteostatResponse?, String> {
        return try {
            val fields = "M7,M6,M4,M8,M31,M12,M33,M5"
            val measurement = "METEOSTAT"
            val response = client.get { url("${HttpRoutes.STAT}?fields=${fields}&measurement=${measurement}&device_code=${deviceCode}") }.body<SensorMeteostatResponse>()
            Pair(response, "")
        } catch (e: UnauthorizedException) {
            Pair(null, "Unauthorized")
        } catch (e: RedirectResponseException) {
            Pair(null, "Error 3xx: ${e.response.status.description}")
        } catch (e: ClientRequestException) {
            Pair(null, "Error 4xx: ${e.response.status.description}")
        } catch (e: ServerResponseException) {
            Pair(null, "Error 5xx: ${e.response.status.description}")
        } catch (e: Exception) {
            Pair(null, "Error 3xx: ${e.message}")
        }
    }

    override suspend fun getSensorSatstat(deviceCode: String): Pair<SensorSatstatResponse?, String> {
        return try {
            val fields = "S130,S131,S4"
            val measurement = "SATSTAT"
            val response = client.get { url("${HttpRoutes.STAT}?fields=${fields}&measurement=${measurement}&device_code=${deviceCode}") }.body<SensorSatstatResponse>()
            Pair(response, "")
        } catch (e: UnauthorizedException) {
            Pair(null, "Unauthorized")
        } catch (e: RedirectResponseException) {
            Pair(null, "Error 3xx: ${e.response.status.description}")
        } catch (e: ClientRequestException) {
            Pair(null, "Error 4xx: ${e.response.status.description}")
        } catch (e: ServerResponseException) {
            Pair(null, "Error 5xx: ${e.response.status.description}")
        } catch (e: Exception) {
            Pair(null, "Error 3xx: ${e.message}")
        }
    }

    override suspend fun getSensorRH(deviceCode: String): Pair<SoilMositureHumidityResponse?, String> {
        return try {
            val response = client.get { url("${HttpRoutes.RH}/${deviceCode}") }.body<SoilMositureHumidityResponse>()
            Pair(response, "")
        } catch (e: UnauthorizedException) {
            Pair(null, "Unauthorized")
        } catch (e: RedirectResponseException) {
            Pair(null, "Error 3xx: ${e.response.status.description}")
        } catch (e: ClientRequestException) {
            Pair(null, "Error 4xx: ${e.response.status.description}")
        } catch (e: ServerResponseException) {
            Pair(null, "Error 5xx: ${e.response.status.description}")
        } catch (e: Exception) {
            Pair(null, "Error 3xx: ${e.message}")
        }
    }

    override suspend fun postSoilMoistureMarker(markerData: SoilMoistureMarkerRequest): Pair<Boolean, String> {
        return try {
            client.post {
                url(HttpRoutes.RH)
                contentType(Json)
                setBody(markerData)
            }
            Pair(true, "")
        } catch (e: UnprocessableEntityException) {
            Pair(false, e.message)
        } catch (e: RedirectResponseException) {
            Pair(false, "Error 3xx: ${e.response.status.description}")
        } catch (e: ClientRequestException) {
            Pair(false, "Error 4xx: ${e.response.status.description}")
        } catch (e: ServerResponseException) {
            Pair(false, "Error 5xx: ${e.response.status.description}")
        } catch (e: Exception) {
            Pair(false, "Error: ${e.message}")
        }
    }

    override suspend fun getIrrigationConfigNominalFlow(deviceCode: String): Pair<IrrigationConfigNominalFlowResponse?, String> {
        return try {
            var fields =""
            for (i in 2000..3151) {
                if (i != 2000) {
                    fields += ","
                }
                fields += "S${i}"
            }
            val measurement = "EVCONFIG"

            val response = client.get { url("${HttpRoutes.STAT}?fields=${fields}&measurement=${measurement}&device_code=${deviceCode}") }.bodyAsText()
            val json = kotlinx.serialization.json.Json { ignoreUnknownKeys = true }
            val jsonObject = json.parseToJsonElement(response).jsonObject

            val status = jsonObject["status"]?.jsonPrimitive?.content ?: ""
            val dataObject = jsonObject["data"]?.jsonObject ?: JsonObject(emptyMap())

            val result = dataObject["result"]?.jsonPrimitive?.content ?: ""
            val table = dataObject["table"]?.jsonPrimitive?.int ?: 0
            val _start = dataObject["_start"]?.jsonPrimitive?.content ?: ""
            val _stop = dataObject["_stop"]?.jsonPrimitive?.content ?: ""
            val _measurement = dataObject["_measurement"]?.jsonPrimitive?.content ?: ""
            val device = dataObject["device"]?.jsonPrimitive?.content ?: ""

            val dynamicFields = dataObject.filterKeys { it.startsWith("S") }
                .mapValues { it.value.jsonPrimitive.content }

            val data = IrrigationConfigNominalFlowData(
                result = result,
                table = table,
                start = _start,
                stop = _stop,
                measurement = _measurement,
                device = device,
                dynamicFields = dynamicFields
            )

            val returnedData = IrrigationConfigNominalFlowResponse(
                status = status,
                data = data,
            )
            Pair(returnedData, "")
        } catch (e: UnauthorizedException) {
            Pair(null, "Unauthorized")
        } catch (e: RedirectResponseException) {
            Pair(null, "Error 3xx: ${e.response.status.description}")
        } catch (e: ClientRequestException) {
            Pair(null, "Error 4xx: ${e.response.status.description}")
        } catch (e: ServerResponseException) {
            Pair(null, "Error 5xx: ${e.response.status.description}")
        } catch (e: Exception) {
            Pair(null, "Error 3xx: ${e.message}")
        }
    }
}
