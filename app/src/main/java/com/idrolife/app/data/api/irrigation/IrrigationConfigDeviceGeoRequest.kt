package com.idrolife.app.data.api.irrigation

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable


@Serializable
data class IrrigationConfigDeviceGeoRequest(
	@SerialName("device_code")
	val deviceCode: String? = null,
	@SerialName("ev_serial")
	val evSerial: String? = null,
	val latitude: String? = null,
	val longitude: String? = null,
)
