package com.idrolife.app.data.api.sensor

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class SoilMoistureMarkerRequest(
    val name: String,
    val latitude: String,
    val longitude: String,
    @SerialName("device_code") val deviceCode: String
)