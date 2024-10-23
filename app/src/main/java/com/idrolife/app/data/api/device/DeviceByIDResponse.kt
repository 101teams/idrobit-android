package com.idrolife.app.data.api.device

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class DeviceByIDResponse(

	@SerialName("data")
	val data: DeviceData? = null,

	@SerialName("status")
	val status: String? = null
)

@Serializable
data class DeviceData(

	@SerialName("device")
	val devices: DevicesItem? = null
)
