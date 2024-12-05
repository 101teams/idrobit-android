package com.idrolife.app.data.api.device

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class DeviceRelatedResponse(

	@SerialName("data")
	val data: DeviceRelatedData? = null,

	@SerialName("status")
	val status: String? = null
)

@Serializable
data class DeviceRelatedData(

	@SerialName("devices")
	val devices: List<DeviceRelatedItem?>? = null
)

@Serializable
data class DeviceRelatedItem(

	@SerialName("hw_version")
	val hwVersion: String? = null,

	@SerialName("code")
	val code: String? = null,

	@SerialName("coordinate")
	val coordinate: String? = null,

	@SerialName("is_fast_watering")
	val isFastWatering: Boolean? = null,

	@SerialName("created_at")
	val createdAt: String? = null,

	@SerialName("type")
	val type: String? = null,

	@SerialName("fw_esp32")
	val fwEsp32: String? = null,

	@SerialName("fw_idrosat")
	val fwIdrosat: String? = null,

	@SerialName("is_alarm")
	val isAlarm: Boolean? = null,

	@SerialName("password")
	val password: String? = null,

	@SerialName("max_devices")
	val maxDevices: String? = null,

	@SerialName("updated_at")
	val updatedAt: String? = null,

	@SerialName("mac_address")
	val macAddress: String? = null,

	@SerialName("name")
	val name: String? = null,

	@SerialName("company")
	val company: String? = null,

	@SerialName("phone_number")
	val phoneNumber: String? = null,

	@SerialName("id")
	val id: Int? = null,

	@SerialName("client_name")
	val clientName: String? = null
)
