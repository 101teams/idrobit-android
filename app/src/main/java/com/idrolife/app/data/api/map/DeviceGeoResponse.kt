package com.idrolife.app.data.api.map

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class DeviceGeoResponse(

	@SerialName("data")
	val data: Data? = null,

	@SerialName("status")
	val status: String? = null
)

@Serializable
data class DeviceGeosItem(

	@SerialName("device_id")
	val deviceId: Int? = null,

	@SerialName("updated_at")
	val updatedAt: String? = null,

	@SerialName("group_name")
	val groupName: String? = null,

	@SerialName("latitude")
	val latitude: String? = null,

	@SerialName("created_at")
	val createdAt: String? = null,

	@SerialName("id")
	val id: Int? = null,

	@SerialName("ev_serial")
	val evSerial: String? = null,

	@SerialName("longitude")
	val longitude: String? = null,

	var stationNumber: String? = null,

	var status: String? = null,
)

@Serializable
data class DeviceGeoData(

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

	@SerialName("deviceGeos")
	val deviceGeos: List<DeviceGeosItem?>? = null,

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

@Serializable
data class Data(

	@SerialName("device")
	val device: DeviceGeoData? = null
)
